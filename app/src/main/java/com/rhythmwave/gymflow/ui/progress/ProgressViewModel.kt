package com.rhythmwave.gymflow.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhythmwave.gymflow.data.local.dao.*
import com.rhythmwave.gymflow.data.local.entity.PersonalRecordEntity
import com.rhythmwave.gymflow.data.local.entity.WorkoutSessionEntity
import com.rhythmwave.gymflow.data.local.entity.WorkoutSetEntity
import com.rhythmwave.gymflow.engine.ProgressionEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class VolumeDataPoint(
    val label: String,
    val value: Double,
    val date: Long
)

data class OneRepMaxEntry(
    val exerciseId: String,
    val exerciseName: String,
    val estimated1RM: Double,
    val weight: Double,
    val reps: Int,
    val date: Long
)

data class MuscleGroupVolume(
    val muscleGroup: String,
    val sets: Int,
    val volume: Double,
    val progress: Float // 0-1, relative to max
)

data class ProgressUiState(
    val selectedTimeRange: String = "1M",
    val volumeData: List<VolumeDataPoint> = emptyList(),
    val personalRecords: List<PersonalRecordEntry> = emptyList(),
    val oneRepMaxes: List<OneRepMaxEntry> = emptyList(),
    val muscleGroupVolumes: List<MuscleGroupVolume> = emptyList(),
    val totalSessions: Int = 0,
    val totalVolume: Double = 0.0,
    val streakDays: Int = 0,
    val isLoading: Boolean = true
)

data class PersonalRecordEntry(
    val exerciseId: String,
    val exerciseName: String,
    val weight: Double,
    val reps: Int,
    val estimated1RM: Double,
    val date: Long,
    val daysAgo: Int
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val workoutSessionDao: WorkoutSessionDao,
    private val workoutSetDao: WorkoutSetDao,
    private val personalRecordDao: PersonalRecordDao,
    private val exerciseDao: ExerciseDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        loadProgress("1M")
    }

    fun setTimeRange(range: String) {
        _uiState.value = _uiState.value.copy(selectedTimeRange = range)
        loadProgress(range)
    }

    private fun loadProgress(range: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val startDate = getStartDate(range)
            val now = System.currentTimeMillis()

            // Load sessions
            val sessions = workoutSessionDao.getSessionsSince(startDate).first()

            // Volume data (grouped by week or day)
            val volumeData = groupVolumeData(sessions, range)

            // Personal records
            val prs = personalRecordDao.getRecent(startDate).first()
            val exerciseNames = mutableMapOf<String, String>()
            prs.forEach { pr ->
                if (pr.exerciseId !in exerciseNames) {
                    val ex = exerciseDao.getById(pr.exerciseId).first()
                    exerciseNames[pr.exerciseId] = ex?.name ?: pr.exerciseId
                }
            }
            val prEntries = prs.map { pr ->
                PersonalRecordEntry(
                    exerciseId = pr.exerciseId,
                    exerciseName = exerciseNames[pr.exerciseId] ?: pr.exerciseId,
                    weight = pr.weight,
                    reps = pr.reps,
                    estimated1RM = pr.estimatedOneRepMax,
                    date = pr.date,
                    daysAgo = ((now - pr.date) / (24 * 60 * 60 * 1000)).toInt()
                )
            }.sortedByDescending { it.date }.take(10)

            // Estimated 1RMs (best per exercise)
            val allPRs = personalRecordDao.getAll().first()
            val bestByExercise = allPRs.groupBy { it.exerciseId }
                .mapValues { (_, prs) -> prs.maxByOrNull { it.estimatedOneRepMax }!! }
            val oneRepMaxes = bestByExercise.values.mapNotNull { pr ->
                val ex = exerciseDao.getById(pr.exerciseId).first()
                if (ex != null) {
                    OneRepMaxEntry(
                        exerciseId = pr.exerciseId,
                        exerciseName = ex.name,
                        estimated1RM = pr.estimatedOneRepMax,
                        weight = pr.weight,
                        reps = pr.reps,
                        date = pr.date
                    )
                } else null
            }.sortedByDescending { it.estimated1RM }.take(8)

            // Muscle group volume
            val muscleGroupVolumes = calculateMuscleGroupVolume(sessions, startDate)

            // Streak
            val streak = calculateStreak(sessions)

            _uiState.value = _uiState.value.copy(
                volumeData = volumeData,
                personalRecords = prEntries,
                oneRepMaxes = oneRepMaxes,
                muscleGroupVolumes = muscleGroupVolumes,
                totalSessions = sessions.size,
                totalVolume = sessions.sumOf { it.totalVolume },
                streakDays = streak,
                isLoading = false
            )
        }
    }

    private fun groupVolumeData(sessions: List<WorkoutSessionEntity>, range: String): List<VolumeDataPoint> {
        val cal = Calendar.getInstance()
        return when (range) {
            "1W" -> {
                // Group by day
                (0..6).map { dayOffset ->
                    cal.timeInMillis = System.currentTimeMillis() - (6 - dayOffset) * 24 * 60 * 60 * 1000L
                    val dayStart = cal.apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                    }.timeInMillis
                    val dayEnd = dayStart + 24 * 60 * 60 * 1000L
                    val dayVolume = sessions.filter { it.date in dayStart until dayEnd }.sumOf { it.totalVolume }
                    val dayName = when (dayOffset) {
                        0 -> "Mon"
                        1 -> "Tue"
                        2 -> "Wed"
                        3 -> "Thu"
                        4 -> "Fri"
                        5 -> "Sat"
                        6 -> "Sun"
                        else -> ""
                    }
                    VolumeDataPoint(dayName, dayVolume, dayStart)
                }
            }
            "1M" -> {
                // Group by week
                (0..3).map { weekOffset ->
                    val weekStart = System.currentTimeMillis() - (3 - weekOffset) * 7 * 24 * 60 * 60 * 1000L
                    val weekEnd = weekStart + 7 * 24 * 60 * 60 * 1000L
                    val weekVolume = sessions.filter { it.date in weekStart until weekEnd }.sumOf { it.totalVolume }
                    VolumeDataPoint("W${weekOffset + 1}", weekVolume, weekStart)
                }
            }
            "3M", "6M" -> {
                // Group by month
                val months = if (range == "3M") 3 else 6
                (0 until months).map { monthOffset ->
                    cal.timeInMillis = System.currentTimeMillis()
                    cal.add(Calendar.MONTH, -(months - 1 - monthOffset))
                    val monthStart = cal.apply {
                        set(Calendar.DAY_OF_MONTH, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                    }.timeInMillis
                    cal.add(Calendar.MONTH, 1)
                    val monthEnd = cal.timeInMillis
                    val monthVolume = sessions.filter { it.date in monthStart until monthEnd }.sumOf { it.totalVolume }
                    val monthName = cal.apply { add(Calendar.MONTH, -1) }.get(Calendar.MONTH).let {
                        listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")[it]
                    }
                    VolumeDataPoint(monthName, monthVolume, monthStart)
                }
            }
            else -> {
                // ALL — group by month
                val oldestSession = sessions.minByOrNull { it.date }?.date ?: System.currentTimeMillis()
                val monthsDiff = ((System.currentTimeMillis() - oldestSession) / (30L * 24 * 60 * 60 * 1000)).toInt().coerceAtLeast(1)
                (0 until monthsDiff.coerceAtMost(12)).map { monthOffset ->
                    cal.timeInMillis = System.currentTimeMillis()
                    cal.add(Calendar.MONTH, -(monthsDiff - 1 - monthOffset))
                    val monthStart = cal.apply {
                        set(Calendar.DAY_OF_MONTH, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                    }.timeInMillis
                    cal.add(Calendar.MONTH, 1)
                    val monthEnd = cal.timeInMillis
                    val monthVolume = sessions.filter { it.date in monthStart until monthEnd }.sumOf { it.totalVolume }
                    val monthName = cal.apply { add(Calendar.MONTH, -1) }.get(Calendar.MONTH).let {
                        listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")[it]
                    }
                    VolumeDataPoint(monthName, monthVolume, monthStart)
                }
            }
        }
    }

    private suspend fun calculateMuscleGroupVolume(
        sessions: List<WorkoutSessionEntity>,
        startDate: Long
    ): List<MuscleGroupVolume> {
        val groupMap = mutableMapOf<String, Pair<Int, Double>>()

        for (session in sessions) {
            val sets = workoutSetDao.getBySession(session.id).first()
            for (set in sets) {
                val exercise = exerciseDao.getById(set.exerciseId).first() ?: continue
                val group = exercise.muscleGroup.name
                val (count, volume) = groupMap[group] ?: (0 to 0.0)
                groupMap[group] = (count + 1) to (volume + set.weight * set.reps)
            }
        }

        val maxSets = groupMap.values.maxOfOrNull { it.first } ?: 1
        return groupMap.map { (group, data) ->
            MuscleGroupVolume(
                muscleGroup = group,
                sets = data.first,
                volume = data.second,
                progress = data.first.toFloat() / maxSets
            )
        }.sortedByDescending { it.sets }
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

    private fun getStartDate(range: String): Long {
        val cal = Calendar.getInstance()
        return when (range) {
            "1W" -> cal.apply { add(Calendar.WEEK_OF_YEAR, -1) }.timeInMillis
            "1M" -> cal.apply { add(Calendar.MONTH, -1) }.timeInMillis
            "3M" -> cal.apply { add(Calendar.MONTH, -3) }.timeInMillis
            "6M" -> cal.apply { add(Calendar.MONTH, -6) }.timeInMillis
            "1Y" -> cal.apply { add(Calendar.YEAR, -1) }.timeInMillis
            else -> 0L
        }
    }
}
