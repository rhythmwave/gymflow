package com.rhythmwave.gymflow.engine

import com.rhythmwave.gymflow.data.local.entity.ExerciseEntity
import com.rhythmwave.gymflow.domain.model.*

object ExerciseScorer {

    data class ScoredExercise(
        val exercise: ExerciseEntity,
        val score: Double,
        val matchedGoals: List<GoalTag>
    )

    fun scoreAll(
        exercises: List<ExerciseEntity>,
        userGoals: List<GoalWithPriority>,
        userEquipment: List<String>,
        userLevel: ExperienceLevel,
        recentlyUsedIds: Set<String> = emptySet()
    ): List<ScoredExercise> {
        return exercises.map { exercise ->
            scoreExercise(exercise, userGoals, userEquipment, userLevel, recentlyUsedIds)
        }.sortedByDescending { it.score }
    }

    fun scoreExercise(
        exercise: ExerciseEntity,
        userGoals: List<GoalWithPriority>,
        userEquipment: List<String>,
        userLevel: ExperienceLevel,
        recentlyUsedIds: Set<String> = emptySet()
    ): ScoredExercise {
        var score = 0.0
        val exerciseTags = parseTags(exercise.tagsJson)
        val exerciseEquipment = parseStringList(exercise.equipmentJson)

        // 1. Equipment check — must have at least one
        val hasEquipment = exerciseEquipment.any { it.lowercase() in userEquipment.map { e -> e.lowercase() } }
        if (!hasEquipment) {
            return ScoredExercise(exercise, 0.0, emptyList())
        }
        score += 5.0

        // 2. Goal relevance (highest weight)
        val matchedGoals = mutableListOf<GoalTag>()
        for (goal in userGoals) {
            val goalTags = mapGoalToTags(goal.profile)
            val overlap = exerciseTags.intersect(goalTags)
            if (overlap.isNotEmpty()) {
                matchedGoals.addAll(overlap)
                score += goal.priority * 3.0 * overlap.size
            }
        }

        // 3. Difficulty match
        score += when {
            exercise.difficulty == userLevel -> 3.0
            exercise.difficulty == ExperienceLevel.BEGINNER && userLevel != ExperienceLevel.BEGINNER -> 1.0
            exercise.difficulty == ExperienceLevel.ADVANCED && userLevel != ExperienceLevel.ADVANCED -> -3.0
            else -> 0.0
        }

        // 4. Compound bonus (more efficient, hits multiple muscles)
        if (exercise.isCompound) {
            score += 2.0
        }

        // 5. Variety penalty (avoid recently used)
        if (exercise.id in recentlyUsedIds) {
            score -= 8.0
        }

        // 6. Equipment simplicity bonus (bodyweight always available)
        if (exerciseEquipment.any { it.lowercase() == "bodyweight" }) {
            score += 1.0
        }

        return ScoredExercise(
            exercise = exercise,
            score = score.coerceAtLeast(0.0),
            matchedGoals = matchedGoals.distinct()
        )
    }

    fun selectForMuscleGroup(
        exercises: List<ExerciseEntity>,
        muscleGroup: MuscleGroup,
        count: Int,
        userGoals: List<GoalWithPriority>,
        userEquipment: List<String>,
        userLevel: ExperienceLevel,
        recentlyUsedIds: Set<String> = emptySet()
    ): List<ScoredExercise> {
        val filtered = exercises.filter { it.muscleGroup == muscleGroup }
        val scored = scoreAll(filtered, userGoals, userEquipment, userLevel, recentlyUsedIds)

        // Ensure at least 1 compound if available
        val compounds = scored.filter { it.exercise.isCompound }
        val isolations = scored.filter { !it.exercise.isCompound }

        val result = mutableListOf<ScoredExercise>()
        if (compounds.isNotEmpty()) result.add(compounds.first())
        result.addAll(isolations.take(count - result.size))
        result.addAll(compounds.drop(1).take(count - result.size))

        return result.take(count)
    }

    private fun mapGoalToTags(goal: GoalProfile): Set<GoalTag> {
        return when (goal) {
            GoalProfile.POSTURE_FIX -> setOf(GoalTag.POSTURE, GoalTag.FLEXIBILITY)
            GoalProfile.CARDIO_VASCULAR -> setOf(GoalTag.CARDIO, GoalTag.BLOOD_FLOW)
            GoalProfile.HEART_RATE -> setOf(GoalTag.CARDIO, GoalTag.BLOOD_FLOW)
            GoalProfile.BURST_ENDURANCE -> setOf(GoalTag.EXPLOSIVE, GoalTag.BLOOD_FLOW, GoalTag.CARDIO)
            GoalProfile.ABDOMEN_SHAPE -> setOf(GoalTag.CORE_SPINE, GoalTag.BLOOD_FLOW)
            GoalProfile.CORE_SPINE -> setOf(GoalTag.CORE_SPINE, GoalTag.POSTURE)
            GoalProfile.SEX_DRIVE -> setOf(GoalTag.DRIVE, GoalTag.EXPLOSIVE, GoalTag.BLOOD_FLOW)
        }
    }

    private fun parseTags(json: String): Set<GoalTag> {
        return try {
            val list: List<String> = com.google.gson.Gson().fromJson(json, object : com.google.gson.reflect.TypeToken<List<String>>() {}.type)
            list.mapNotNull { runCatching { GoalTag.valueOf(it) }.getOrNull() }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    private fun parseStringList(json: String): List<String> {
        return try {
            com.google.gson.Gson().fromJson(json, object : com.google.gson.reflect.TypeToken<List<String>>() {}.type)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
