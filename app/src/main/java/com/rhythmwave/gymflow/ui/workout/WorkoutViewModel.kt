package com.rhythmwave.gymflow.ui.workout

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rhythmwave.gymflow.data.local.dao.*
import com.rhythmwave.gymflow.data.local.entity.*
import com.rhythmwave.gymflow.engine.ProgressionEngine
import com.rhythmwave.gymflow.service.TimerState
import com.rhythmwave.gymflow.service.WorkoutTimerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutExercise(
    val exerciseId: String,
    val exerciseName: String,
    val muscleGroup: String,
    val targetSets: Int,
    val targetReps: Int,
    val targetRepsMax: Int?,
    val restSeconds: Int,
    val isFinisher: Boolean,
    val completedSets: List<CompletedSet> = emptyList(),
    val isCurrent: Boolean = false,
    val isSkipped: Boolean = false
) {
    val isCompleted: Boolean get() = completedSets.size >= targetSets && !isSkipped
    val currentSetNumber: Int get() = completedSets.size + 1
    val allSetsDone: Boolean get() = completedSets.size >= targetSets
}

data class CompletedSet(
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val rpe: Int?,
    val isWarmup: Boolean = false,
    val notes: String? = null
)

data class WorkoutUiState(
    val sessionId: Long? = null,
    val dayName: String = "",
    val exercises: List<WorkoutExercise> = emptyList(),
    val currentExerciseIndex: Int = 0,
    val elapsedSeconds: Int = 0,
    val isWorkoutActive: Boolean = false,
    val isWorkoutComplete: Boolean = false,
    val totalVolume: Double = 0.0,
    val completedSets: Int = 0,
    val totalSets: Int = 0,
    val showSetLogger: Boolean = false,
    val showSwapSheet: Boolean = false,
    val timerState: TimerState = TimerState()
)

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    application: Application,
    private val workoutSessionDao: WorkoutSessionDao,
    private val workoutSetDao: WorkoutSetDao,
    private val programExerciseDao: ProgramExerciseDao,
    private val programDayDao: ProgramDayDao,
    private val exerciseDao: ExerciseDao,
    private val personalRecordDao: PersonalRecordDao
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private var timerService: WorkoutTimerService? = null
    private var timerJob: kotlinx.coroutines.Job? = null

    fun startWorkout(dayId: Long, dayName: String) {
        viewModelScope.launch {
            // Create workout session
            val session = WorkoutSessionEntity(
                programId = 0, // TODO: get from day
                dayIndex = 0,
                date = System.currentTimeMillis()
            )
            val sessionId = workoutSessionDao.insert(session)

            // Load exercises for this day
            val programExercises = programExerciseDao.getByDaySync(dayId)
            val exercises = programExercises.map { pe ->
                val exercise = exerciseDao.getById(pe.exerciseId).first()
                WorkoutExercise(
                    exerciseId = pe.exerciseId,
                    exerciseName = exercise?.name ?: pe.exerciseId,
                    muscleGroup = exercise?.muscleGroup?.name ?: "",
                    targetSets = pe.targetSets,
                    targetReps = pe.targetReps,
                    targetRepsMax = pe.targetRepsMax,
                    restSeconds = pe.restSeconds,
                    isFinisher = pe.isFinisher
                )
            }

            _uiState.value = _uiState.value.copy(
                sessionId = sessionId,
                dayName = dayName,
                exercises = exercises,
                currentExerciseIndex = 0,
                isWorkoutActive = true,
                totalSets = exercises.sumOf { it.targetSets }
            )

            // Start elapsed timer
            startElapsedTimer()
        }
    }

    fun logSet(weight: Double, reps: Int, rpe: Int?, isWarmup: Boolean, notes: String?) {
        val state = _uiState.value
        val currentExercise = state.exercises.getOrNull(state.currentExerciseIndex) ?: return
        val sessionId = state.sessionId ?: return

        viewModelScope.launch {
            // Save to DB
            val set = WorkoutSetEntity(
                sessionId = sessionId,
                exerciseId = currentExercise.exerciseId,
                setNumber = currentExercise.completedSets.size + 1,
                weight = weight,
                reps = reps,
                rpe = rpe,
                isWarmup = isWarmup,
                notes = notes
            )
            workoutSetDao.insert(set)

            // Update UI state
            val completedSet = CompletedSet(
                setNumber = set.setNumber,
                weight = weight,
                reps = reps,
                rpe = rpe,
                isWarmup = isWarmup,
                notes = notes
            )

            val updatedExercises = state.exercises.toMutableList()
            val updatedExercise = currentExercise.copy(
                completedSets = currentExercise.completedSets + completedSet
            )
            updatedExercises[state.currentExerciseIndex] = updatedExercise

            val newVolume = state.totalVolume + (weight * reps)
            val newCompletedSets = state.completedSets + 1

            // Check for PR
            checkForPR(currentExercise.exerciseId, weight, reps)

            _uiState.value = state.copy(
                exercises = updatedExercises,
                totalVolume = newVolume,
                completedSets = newCompletedSets,
                showSetLogger = false
            )

            // Auto-start rest timer
            if (!updatedExercise.allSetsDone) {
                startRestTimer(updatedExercise.restSeconds)
            } else {
                // Move to next exercise
                moveToNextExercise()
            }
        }
    }

    fun skipExercise() {
        val state = _uiState.value
        val updatedExercises = state.exercises.toMutableList()
        val current = updatedExercises.getOrNull(state.currentExerciseIndex) ?: return
        updatedExercises[state.currentExerciseIndex] = current.copy(isSkipped = true)
        _uiState.value = state.copy(exercises = updatedExercises)
        moveToNextExercise()
    }

    fun moveToNextExercise() {
        val state = _uiState.value
        val nextIndex = state.currentExerciseIndex + 1
        if (nextIndex < state.exercises.size) {
            _uiState.value = state.copy(
                currentExerciseIndex = nextIndex,
                showSetLogger = false
            )
        } else {
            // Workout complete
            completeWorkout()
        }
    }

    fun moveToPreviousExercise() {
        val state = _uiState.value
        if (state.currentExerciseIndex > 0) {
            _uiState.value = state.copy(
                currentExerciseIndex = state.currentExerciseIndex - 1,
                showSetLogger = false
            )
        }
    }

    fun showSetLogger() {
        _uiState.value = _uiState.value.copy(showSetLogger = true)
    }

    fun hideSetLogger() {
        _uiState.value = _uiState.value.copy(showSetLogger = false)
    }

    fun showSwapSheet() {
        _uiState.value = _uiState.value.copy(showSwapSheet = true)
    }

    fun hideSwapSheet() {
        _uiState.value = _uiState.value.copy(showSwapSheet = false)
    }

    fun swapExercise(newExerciseId: String) {
        val state = _uiState.value
        viewModelScope.launch {
            val newExercise = exerciseDao.getById(newExerciseId).first() ?: return@launch
            val updatedExercises = state.exercises.toMutableList()
            val current = updatedExercises[state.currentExerciseIndex]
            updatedExercises[state.currentExerciseIndex] = current.copy(
                exerciseId = newExerciseId,
                exerciseName = newExercise.name,
                muscleGroup = newExercise.muscleGroup.name,
                completedSets = emptyList()
            )
            _uiState.value = state.copy(
                exercises = updatedExercises,
                showSwapSheet = false
            )
        }
    }

    fun setWorkoutRating(rating: Int) {
        val state = _uiState.value
        val sessionId = state.sessionId ?: return
        viewModelScope.launch {
            val session = workoutSessionDao.getById(sessionId).first() ?: return@launch
            workoutSessionDao.update(session.copy(rating = rating))
        }
    }

    fun finishWorkout() {
        _uiState.value = _uiState.value.copy(isWorkoutComplete = true)
    }

    private fun completeWorkout() {
        val state = _uiState.value
        val sessionId = state.sessionId ?: return
        viewModelScope.launch {
            val session = workoutSessionDao.getById(sessionId).first() ?: return@launch
            workoutSessionDao.update(
                session.copy(
                    durationMinutes = state.elapsedSeconds / 60,
                    totalVolume = state.totalVolume,
                    totalSets = state.totalSets,
                    completedSets = state.completedSets
                )
            )
            _uiState.value = state.copy(isWorkoutComplete = true)
            stopElapsedTimer()
        }
    }

    private fun checkForPR(exerciseId: String, weight: Double, reps: Int) {
        viewModelScope.launch {
            val currentBest = personalRecordDao.getBestForExercise(exerciseId).first()
            if (ProgressionEngine.isNewPersonalRecord(weight, reps, currentBest?.estimatedOneRepMax)) {
                val est1RM = ProgressionEngine.estimateOneRepMax(weight, reps)
                personalRecordDao.insert(
                    PersonalRecordEntity(
                        exerciseId = exerciseId,
                        weight = weight,
                        reps = reps,
                        estimatedOneRepMax = est1RM
                    )
                )
                // TODO: trigger PR celebration UI
            }
        }
    }

    private fun startRestTimer(seconds: Int) {
        val context = getApplication<Application>()
        val intent = Intent(context, WorkoutTimerService::class.java).apply {
            action = WorkoutTimerService.ACTION_START
            putExtra(WorkoutTimerService.EXTRA_SECONDS, seconds)
        }
        context.startService(intent)
    }

    private fun startElapsedTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.isWorkoutActive && !_uiState.value.isWorkoutComplete) {
                kotlinx.coroutines.delay(1000)
                _uiState.value = _uiState.value.copy(
                    elapsedSeconds = _uiState.value.elapsedSeconds + 1
                )
            }
        }
    }

    private fun stopElapsedTimer() {
        timerJob?.cancel()
    }

    override fun onCleared() {
        stopElapsedTimer()
        super.onCleared()
    }
}
