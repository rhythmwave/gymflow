package com.rhythmwave.gymflow.engine

import com.rhythmwave.gymflow.data.local.entity.WorkoutSessionEntity
import com.rhythmwave.gymflow.data.local.entity.WorkoutSetEntity
import com.rhythmwave.gymflow.domain.model.GoalProfile
import com.rhythmwave.gymflow.domain.model.GoalWithPriority

object AdaptationEngine {

    data class WeeklyAnalysis(
        val totalVolume: Double,
        val targetVolume: Double,
        val sessionsCompleted: Int,
        val targetSessions: Int,
        val avgSleepHours: Double,
        val recommendations: List<Recommendation>
    )

    data class MonthlyAnalysis(
        val weeksCompleted: Int,
        val totalPRs: Int,
        val goalCompletionRates: Map<GoalProfile, Double>,
        val phaseChangeSuggested: Boolean,
        val suggestedChanges: List<String>,
        val forceDeloadWeek: Boolean
    )

    sealed class Recommendation {
        data class IncreaseWeight(val exerciseId: String, val amount: Double) : Recommendation()
        data class Deload(val exerciseId: String, val percent: Int) : Recommendation()
        data class AddGoalExercise(val goal: GoalProfile) : Recommendation()
        data class ReduceIntensity(val percent: Int) : Recommendation()
        object IncreaseVolume : Recommendation()
        object PrioritizeSleep : Recommendation()
        data class Custom(val message: String) : Recommendation()
    }

    fun analyzeWeek(
        sessions: List<WorkoutSessionEntity>,
        sets: Map<Long, List<WorkoutSetEntity>>,
        activeGoals: List<GoalWithPriority>,
        targetSessions: Int,
        avgSleepHours: Double
    ): WeeklyAnalysis {
        val totalVolume = sessions.sumOf { it.totalVolume }
        val targetVolume = targetSessions * 15000.0 // ~15k kg per session target
        val recommendations = mutableListOf<Recommendation>()

        // Volume check
        if (totalVolume < targetVolume * 0.8) {
            recommendations.add(Recommendation.IncreaseVolume)
        }

        // Sleep check
        if (avgSleepHours < 6.5) {
            recommendations.add(Recommendation.ReduceIntensity(10))
            recommendations.add(Recommendation.PrioritizeSleep)
        }

        // Session count check
        if (sessions.size < targetSessions) {
            recommendations.add(Recommendation.Custom(
                "Only ${sessions.size}/$targetSessions sessions this week. Try to hit your target."
            ))
        }

        // Check progression per exercise
        val exerciseSets = mutableMapOf<String, MutableList<WorkoutSetEntity>>()
        sets.values.flatten().forEach { set ->
            exerciseSets.getOrPut(set.exerciseId) { mutableListOf() }.add(set)
        }

        for ((exerciseId, exerciseSetList) in exerciseSets) {
            val progression = ProgressionEngine.calculateProgression(exerciseId, exerciseSetList)
            when (progression.action) {
                ProgressionEngine.ProgressionAction.INCREASE_WEIGHT -> {
                    recommendations.add(Recommendation.IncreaseWeight(exerciseId, progression.weightChange))
                }
                ProgressionEngine.ProgressionAction.DELOAD -> {
                    recommendations.add(Recommendation.Deload(exerciseId, 10))
                }
                else -> {}
            }
        }

        return WeeklyAnalysis(
            totalVolume = totalVolume,
            targetVolume = targetVolume,
            sessionsCompleted = sessions.size,
            targetSessions = targetSessions,
            avgSleepHours = avgSleepHours,
            recommendations = recommendations
        )
    }

    fun analyzeMonth(
        weeklyAnalyses: List<WeeklyAnalysis>,
        totalPRs: Int,
        goalCompletionRates: Map<GoalProfile, Double>
    ): MonthlyAnalysis {
        val suggestions = mutableListOf<String>()

        // Phase change after 4 weeks
        val phaseChange = weeklyAnalyses.size >= 4

        if (phaseChange) {
            suggestions.add("Increase compound lift volume (4×6 → 4×8)")
            suggestions.add("Add advanced core exercises (dragon flags, ab wheel)")
            suggestions.add("Upgrade HIIT to 10 rounds (from 8)")
            suggestions.add("Consider adding a 5th training day")
        }

        // PR frequency
        if (totalPRs < 2) {
            suggestions.add("Try different exercise variations to break plateaus")
        }

        // Goal completion
        for ((goal, rate) in goalCompletionRates) {
            if (rate < 0.7) {
                suggestions.add("Adjust ${goal.displayName} exercises — completion rate low (${"%.0f".format(rate * 100)}%)")
            }
        }

        // Deload week
        val forceDeload = weeklyAnalyses.size % 4 == 0

        return MonthlyAnalysis(
            weeksCompleted = weeklyAnalyses.size,
            totalPRs = totalPRs,
            goalCompletionRates = goalCompletionRates,
            phaseChangeSuggested = phaseChange,
            suggestedChanges = suggestions,
            forceDeloadWeek = forceDeload
        )
    }
}
