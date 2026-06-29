package com.rhythmwave.gymflow.engine

import com.rhythmwave.gymflow.domain.model.*

object SetsRepsCalculator {

    data class ExerciseConfig(
        val targetSets: Int,
        val targetReps: Int,
        val targetRepsMax: Int? = null,
        val restSeconds: Int,
        val tempo: String,
        val isFinisher: Boolean = false
    )

    fun calculate(
        isCompound: Boolean,
        primaryGoal: GoalProfile,
        sessionDurationMinutes: Int,
        exercisesInSession: Int,
        isFinisher: Boolean = false
    ): ExerciseConfig {
        if (isFinisher) {
            return finisherConfig(primaryGoal)
        }

        val timePerExercise = sessionDurationMinutes / exercisesInSession.coerceAtLeast(1)
        val maxSets = (timePerExercise / 3).coerceIn(2, 5) // ~3 min per set including rest

        return when (primaryGoal) {
            GoalProfile.POSTURE_FIX -> ExerciseConfig(
                targetSets = maxSets.coerceAtMost(4),
                targetReps = 10,
                targetRepsMax = 15,
                restSeconds = 75,
                tempo = "2-1-2"
            )
            GoalProfile.CARDIO_VASCULAR -> ExerciseConfig(
                targetSets = maxSets.coerceAtMost(3),
                targetReps = 12,
                targetRepsMax = 20,
                restSeconds = 40,
                tempo = "1-0-1"
            )
            GoalProfile.HEART_RATE -> ExerciseConfig(
                targetSets = maxSets.coerceAtMost(3),
                targetReps = 12,
                targetRepsMax = 20,
                restSeconds = 40,
                tempo = "1-0-1"
            )
            GoalProfile.BURST_ENDURANCE -> ExerciseConfig(
                targetSets = maxSets.coerceAtMost(4),
                targetReps = 5,
                targetRepsMax = 8,
                restSeconds = 90,
                tempo = "1-0-X"
            )
            GoalProfile.ABDOMEN_SHAPE -> ExerciseConfig(
                targetSets = maxSets.coerceAtMost(4),
                targetReps = 12,
                targetRepsMax = 20,
                restSeconds = 45,
                tempo = "2-1-2"
            )
            GoalProfile.CORE_SPINE -> ExerciseConfig(
                targetSets = maxSets.coerceAtMost(4),
                targetReps = 8,
                targetRepsMax = 15,
                restSeconds = 60,
                tempo = "2-2-2"
            )
            GoalProfile.SEX_DRIVE -> ExerciseConfig(
                targetSets = maxSets.coerceAtMost(4),
                targetReps = 6,
                targetRepsMax = 12,
                restSeconds = 90,
                tempo = "2-1-1"
            )
        }
    }

    private fun finisherConfig(goal: GoalProfile): ExerciseConfig {
        return when (goal) {
            GoalProfile.POSTURE_FIX -> ExerciseConfig(
                targetSets = 2, targetReps = 12, restSeconds = 30,
                tempo = "controlled", isFinisher = true
            )
            GoalProfile.CARDIO_VASCULAR, GoalProfile.HEART_RATE -> ExerciseConfig(
                targetSets = 3, targetReps = 15, restSeconds = 30,
                tempo = "fast", isFinisher = true
            )
            GoalProfile.BURST_ENDURANCE -> ExerciseConfig(
                targetSets = 4, targetReps = 5, restSeconds = 45,
                tempo = "explosive", isFinisher = true
            )
            GoalProfile.ABDOMEN_SHAPE, GoalProfile.CORE_SPINE -> ExerciseConfig(
                targetSets = 3, targetReps = 15, restSeconds = 30,
                tempo = "controlled", isFinisher = true
            )
            GoalProfile.SEX_DRIVE -> ExerciseConfig(
                targetSets = 3, targetReps = 10, restSeconds = 45,
                tempo = "explosive", isFinisher = true
            )
        }
    }

    fun getRestDuration(
        isCompound: Boolean,
        goal: GoalProfile,
        setNumber: Int
    ): Int {
        val base = goal.restSeconds.first
        val max = goal.restSeconds.last
        var rest = base

        if (isCompound) rest += 30
        if (setNumber > 2) rest += 15

        return rest.coerceAtMost(max)
    }

    fun estimateSessionDuration(
        exerciseCount: Int,
        avgSetsPerExercise: Int,
        avgRestSeconds: Int
    ): Int {
        val setTime = 45 // seconds per set (avg)
        val transitionTime = 30 // seconds between exercises
        val totalSetTime = exerciseCount * avgSetsPerExercise * (setTime + avgRestSeconds)
        val totalTransition = exerciseCount * transitionTime
        val warmupTime = 5 * 60 // 5 min warmup
        val finisherTime = 5 * 60 // 5 min finisher
        return ((totalSetTime + totalTransition + warmupTime + finisherTime) / 60)
    }
}
