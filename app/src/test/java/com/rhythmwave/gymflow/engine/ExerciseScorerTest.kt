package com.rhythmwave.gymflow.engine

import com.rhythmwave.gymflow.data.local.entity.ExerciseEntity
import com.rhythmwave.gymflow.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class ExerciseScorerTest {

    private fun createExercise(
        id: String,
        muscleGroup: MuscleGroup,
        equipment: List<String>,
        difficulty: ExperienceLevel,
        isCompound: Boolean,
        tags: List<String>
    ) = ExerciseEntity(
        id = id,
        name = id.replace("_", " ").replaceFirstChar { it.uppercase() },
        muscleGroup = muscleGroup,
        equipmentJson = com.google.gson.Gson().toJson(equipment),
        difficulty = difficulty,
        isCompound = isCompound,
        instructions = "Test instructions",
        tagsJson = com.google.gson.Gson().toJson(tags),
        alternativesJson = "[]"
    )

    @Test
    fun `exercise with matching equipment gets positive score`() {
        val exercise = createExercise(
            "bench_press", MuscleGroup.CHEST,
            listOf("barbell", "bench"), ExperienceLevel.INTERMEDIATE,
            true, listOf("DRIVE")
        )
        val result = ExerciseScorer.scoreExercise(
            exercise,
            listOf(GoalWithPriority(GoalProfile.SEX_DRIVE, 5)),
            listOf("barbell", "bench"),
            ExperienceLevel.INTERMEDIATE
        )
        assertTrue(result.score > 0)
    }

    @Test
    fun `exercise without matching equipment gets zero score`() {
        val exercise = createExercise(
            "bench_press", MuscleGroup.CHEST,
            listOf("barbell", "bench"), ExperienceLevel.INTERMEDIATE,
            true, listOf("DRIVE")
        )
        val result = ExerciseScorer.scoreExercise(
            exercise,
            listOf(GoalWithPriority(GoalProfile.SEX_DRIVE, 5)),
            listOf("dumbbell"), // No barbell
            ExperienceLevel.INTERMEDIATE
        )
        assertEquals(0.0, result.score, 0.01)
    }

    @Test
    fun `compound exercises score higher than isolation`() {
        val compound = createExercise(
            "squat", MuscleGroup.LEGS,
            listOf("barbell"), ExperienceLevel.INTERMEDIATE,
            true, listOf("DRIVE")
        )
        val isolation = createExercise(
            "leg_curl", MuscleGroup.LEGS,
            listOf("machine"), ExperienceLevel.INTERMEDIATE,
            false, listOf("DRIVE")
        )
        val goals = listOf(GoalWithPriority(GoalProfile.SEX_DRIVE, 5))
        val equipment = listOf("barbell", "machine")

        val compoundScore = ExerciseScorer.scoreExercise(compound, goals, equipment, ExperienceLevel.INTERMEDIATE)
        val isolationScore = ExerciseScorer.scoreExercise(isolation, goals, equipment, ExperienceLevel.INTERMEDIATE)

        assertTrue(compoundScore.score > isolationScore.score)
    }

    @Test
    fun `recently used exercises get penalized`() {
        val exercise = createExercise(
            "bench_press", MuscleGroup.CHEST,
            listOf("barbell"), ExperienceLevel.INTERMEDIATE,
            true, listOf("DRIVE")
        )
        val goals = listOf(GoalWithPriority(GoalProfile.SEX_DRIVE, 5))
        val equipment = listOf("barbell")

        val normal = ExerciseScorer.scoreExercise(exercise, goals, equipment, ExperienceLevel.INTERMEDIATE)
        val penalized = ExerciseScorer.scoreExercise(
            exercise, goals, equipment, ExperienceLevel.INTERMEDIATE,
            recentlyUsedIds = setOf("bench_press")
        )

        assertTrue(normal.score > penalized.score)
    }

    @Test
    fun `higher priority goals contribute more to score`() {
        val exercise = createExercise(
            "face_pulls", MuscleGroup.SHOULDERS,
            listOf("cable"), ExperienceLevel.BEGINNER,
            false, listOf("POSTURE")
        )
        val equipment = listOf("cable")

        val highPriority = ExerciseScorer.scoreExercise(
            exercise,
            listOf(GoalWithPriority(GoalProfile.POSTURE_FIX, 5)),
            equipment, ExperienceLevel.BEGINNER
        )
        val lowPriority = ExerciseScorer.scoreExercise(
            exercise,
            listOf(GoalWithPriority(GoalProfile.POSTURE_FIX, 1)),
            equipment, ExperienceLevel.BEGINNER
        )

        assertTrue(highPriority.score > lowPriority.score)
    }
}
