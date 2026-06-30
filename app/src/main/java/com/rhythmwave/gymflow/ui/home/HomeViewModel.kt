package com.rhythmwave.gymflow.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhythmwave.gymflow.data.local.dao.*
import com.rhythmwave.gymflow.data.local.entity.*
import com.rhythmwave.gymflow.domain.model.GoalProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class DayWorkout(
    val dayId: Long,
    val dayName: String,
    val exercises: List<ExercisePreview>,
    val estimatedMinutes: Int
)

data class ExercisePreview(
    val exerciseId: String,
    val name: String,
    val sets: String,
    val muscleGroup: String
)

data class WeeklyGoalProgress(
    val name: String,
    val current: Int,
    val target: Int,
    val color: Long
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val dayWorkout: DayWorkout? = null,
    val streak: Int = 0,
    val weekVolume: Double = 0.0,
    val weekPRs: Int = 0,
    val weeklyGoals: List<WeeklyGoalProgress> = emptyList(),
    val hasProgram: Boolean = false,
    val todayDayIndex: Int = -1,
    val isRestDay: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val programDao: ProgramDao,
    private val programDayDao: ProgramDayDao,
    private val programExerciseDao: ProgramExerciseDao,
    private val exerciseDao: ExerciseDao,
    private val goalDao: GoalDao,
    private val workoutSessionDao: WorkoutSessionDao,
    private val personalRecordDao: PersonalRecordDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Get active program
            val program = programDao.getActiveProgram().first()
            if (program == null) {
                _uiState.value = HomeUiState(isLoading = false, hasProgram = false)
                return@launch
            }

            // Get today's day index (0=Mon, 6=Sun)
            val cal = Calendar.getInstance()
            val todayIndex = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Mon=0, Sun=6

            // Get all days for this program
            val days = programDayDao.getByProgram(program.id).first()

            // Find today's training day (or next training day)
            val todayDay = days.find { it.dayIndex == todayIndex && !it.isRestDay }
            val isRestDay = todayDay == null

            // Get goals
            val goals = goalDao.getActiveByProgram(program.id).first()

            // Build weekly goals
            val weeklyGoals = buildWeeklyGoals(goals)

            // Get this week's sessions
            val weekStart = cal.apply {
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.timeInMillis
            val sessions = workoutSessionDao.getSessionsSince(weekStart).first()

            // Calculate streak
            val streak = calculateStreak(sessions)

            // Calculate week volume
            val weekVolume = sessions.sumOf { it.totalVolume }

            // Count PRs this week
            val prs = personalRecordDao.getRecent(weekStart).first()

            // Build today's workout preview
            var dayWorkout: DayWorkout? = null
            if (todayDay != null) {
                val programExercises = programExerciseDao.getByDay(todayDay.id).first()
                val exercises = programExercises.map { pe ->
                    val ex = exerciseDao.getById(pe.exerciseId).first()
                    ExercisePreview(
                        exerciseId = pe.exerciseId,
                        name = ex?.name ?: pe.exerciseId,
                        sets = "${pe.targetSets}×${pe.targetReps}" +
                            (pe.targetRepsMax?.let { "-$it" } ?: ""),
                        muscleGroup = ex?.muscleGroup?.name ?: ""
                    )
                }
                dayWorkout = DayWorkout(
                    dayId = todayDay.id,
                    dayName = todayDay.dayName,
                    exercises = exercises,
                    estimatedMinutes = program.sessionDuration
                )
            }

            _uiState.value = HomeUiState(
                isLoading = false,
                dayWorkout = dayWorkout,
                streak = streak,
                weekVolume = weekVolume,
                weekPRs = prs.size,
                weeklyGoals = weeklyGoals,
                hasProgram = true,
                todayDayIndex = todayIndex,
                isRestDay = isRestDay
            )
        }
    }

    private fun buildWeeklyGoals(goals: List<GoalEntity>): List<WeeklyGoalProgress> {
        val goalMap = mapOf(
            GoalProfile.POSTURE_FIX to Triple("Posture", 0xFF8BC34A, 3),
            GoalProfile.CARDIO_VASCULAR to Triple("Cardio", 0xFF00BCD4, 3),
            GoalProfile.CORE_SPINE to Triple("Core", 0xFF9C27B0, 3),
            GoalProfile.BURST_ENDURANCE to Triple("Explosive", 0xFFFF5722, 3),
            GoalProfile.SEX_DRIVE to Triple("Strength", 0xFF4CAF50, 3),
            GoalProfile.HEART_RATE to Triple("Heart", 0xFFF44336, 2),
            GoalProfile.ABDOMEN_SHAPE to Triple("Abs", 0xFF9C27B0, 3)
        )

        return goals.mapNotNull { goal ->
            goalMap[goal.profile]?.let { (name, color, target) ->
                WeeklyGoalProgress(
                    name = name,
                    current = 0, // Will be populated from workout data
                    target = target,
                    color = color
                )
            }
        }.take(5)
    }

    private fun calculateStreak(sessions: List<WorkoutSessionEntity>): Int {
        if (sessions.isEmpty()) return 0
        val sorted = sessions.sortedByDescending { it.date }
        val cal = Calendar.getInstance()
        var streak = 0
        var currentDay = cal.apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis

        for (session in sorted) {
            cal.timeInMillis = session.date
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            val sessionDay = cal.timeInMillis

            if (sessionDay == currentDay || sessionDay == currentDay - 24 * 60 * 60 * 1000L) {
                streak++
                currentDay = sessionDay
            } else {
                break
            }
        }
        return streak
    }
}
