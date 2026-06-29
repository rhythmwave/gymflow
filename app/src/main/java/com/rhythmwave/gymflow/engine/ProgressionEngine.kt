package com.rhythmwave.gymflow.engine

import com.rhythmwave.gymflow.data.local.entity.WorkoutSetEntity

object ProgressionEngine {

    enum class ProgressionAction {
        MAINTAIN,
        INCREASE_WEIGHT,
        INCREASE_REPS,
        DELOAD,
        SUGGEST_INCREASE,
        SWAP_EXERCISE
    }

    data class ProgressionResult(
        val action: ProgressionAction,
        val weightChange: Double = 0.0,
        val message: String = ""
    )

    fun calculateProgression(
        exerciseId: String,
        recentSets: List<WorkoutSetEntity>,
        isUpperBody: Boolean = true
    ): ProgressionResult {
        if (recentSets.size < 6) { // Need at least 2 sessions worth of data
            return ProgressionResult(ProgressionAction.MAINTAIN, message = "Not enough data yet")
        }

        // Group by session (approximate: sets close together in time)
        val sessionGroups = groupBySession(recentSets)
        if (sessionGroups.size < 2) {
            return ProgressionResult(ProgressionAction.MAINTAIN, message = "Need 2+ sessions")
        }

        val last2Sessions = sessionGroups.takeLast(2)

        // Check if all sets completed at target reps
        val allCompleted = last2Sessions.all { session ->
            session.all { it.reps >= getTargetReps(it) }
        }

        if (allCompleted) {
            val increment = if (isUpperBody) 2.5 else 5.0
            return ProgressionResult(
                ProgressionAction.INCREASE_WEIGHT,
                weightChange = increment,
                message = "All sets completed! Increase by ${increment}kg"
            )
        }

        // Check if consistently failing
        val failedSessions = last2Sessions.count { session ->
            session.any { it.reps < getTargetReps(it) }
        }

        if (failedSessions >= 2) {
            val deloadPercent = 10
            return ProgressionResult(
                ProgressionAction.DELOAD,
                weightChange = -deloadPercent.toDouble(),
                message = "Struggling — deload by ${deloadPercent}%"
            )
        }

        // Check RPE
        val avgRPE = recentSets.mapNotNull { it.rpe }.average()
        if (avgRPE < 6.0 && avgRPE > 0) {
            return ProgressionResult(
                ProgressionAction.SUGGEST_INCREASE,
                message = "RPE is low (${"%.1f".format(avgRPE)}) — consider increasing weight"
            )
        }

        return ProgressionResult(ProgressionAction.MAINTAIN, message = "Keep going!")
    }

    fun estimateOneRepMax(weight: Double, reps: Int): Double {
        if (reps == 1) return weight
        if (reps == 0) return 0.0
        return weight * (1 + reps / 30.0)
    }

    fun isNewPersonalRecord(
        weight: Double,
        reps: Int,
        currentBestEst1RM: Double?
    ): Boolean {
        val newEst1RM = estimateOneRepMax(weight, reps)
        return currentBestEst1RM == null || newEst1RM > currentBestEst1RM
    }

    fun calculateDeloadWeight(currentWeight: Double, deloadPercent: Int = 10): Double {
        val deloaded = currentWeight * (1 - deloadPercent / 100.0)
        // Round to nearest 2.5
        return (Math.round(deloaded / 2.5) * 2.5).coerceAtLeast(0.0)
    }

    private fun groupBySession(sets: List<WorkoutSetEntity>): List<List<WorkoutSetEntity>> {
        if (sets.isEmpty()) return emptyList()

        val sorted = sets.sortedBy { it.completedAt }
        val sessions = mutableListOf<MutableList<WorkoutSetEntity>>()
        var currentSession = mutableListOf(sorted.first())

        for (i in 1 until sorted.size) {
            val gap = sorted[i].completedAt - sorted[i - 1].completedAt
            if (gap > 2 * 60 * 60 * 1000) { // >2 hours gap = new session
                sessions.add(currentSession)
                currentSession = mutableListOf()
            }
            currentSession.add(sorted[i])
        }
        sessions.add(currentSession)

        return sessions
    }

    private fun getTargetReps(set: WorkoutSetEntity): Int {
        // Default target — actual target comes from ProgramExercise
        return 8
    }
}
