package com.rhythmwave.gymflow.data.local.seed

import com.rhythmwave.gymflow.data.local.dao.*
import com.rhythmwave.gymflow.data.local.entity.*
import com.rhythmwave.gymflow.domain.model.*
import com.rhythmwave.gymflow.engine.ProgramGenerator
import kotlinx.coroutines.flow.first

object DefaultProgramSeeder {

    suspend fun createDefaultProgram(
        programDao: ProgramDao,
        goalDao: GoalDao,
        programDayDao: ProgramDayDao,
        programExerciseDao: ProgramExerciseDao,
        exerciseDao: ExerciseDao,
        userConfigDao: UserConfigDao
    ) {
        // Check if program already exists
        val existing = programDao.getActiveProgram().first()
        if (existing != null) return

        // Create default user config if needed
        val config = userConfigDao.getConfig().first()
        if (config == null) {
            userConfigDao.insertOrUpdate(
                UserConfigEntity(
                    name = "Rhythmwave",
                    experience = ExperienceLevel.INTERMEDIATE,
                    equipmentJson = """["barbell","dumbbell","cable","pull_up_bar","bench","kettlebell","bodyweight"]""",
                    restDays = """[2,5,6]""",
                    sessionDuration = 60
                )
            )
        }

        // Get all exercises
        val allExercises = exerciseDao.getAllSync()
        if (allExercises.isEmpty()) return

        // Define default goals matching user's 7 goals
        val goals = listOf(
            GoalWithPriority(GoalProfile.POSTURE_FIX, 5),
            GoalWithPriority(GoalProfile.CARDIO_VASCULAR, 4),
            GoalWithPriority(GoalProfile.BURST_ENDURANCE, 4),
            GoalWithPriority(GoalProfile.ABDOMEN_SHAPE, 5),
            GoalWithPriority(GoalProfile.CORE_SPINE, 5),
            GoalWithPriority(GoalProfile.SEX_DRIVE, 3),
            GoalWithPriority(GoalProfile.HEART_RATE, 3)
        )

        // Generate program
        val params = ProgramGenerator.ProgramParams(
            name = "Full Functional",
            goals = goals,
            daysPerWeek = 4,
            sessionDuration = 60,
            restDays = listOf(2, 5, 6), // Wed, Sat, Sun
            equipment = listOf("barbell", "dumbbell", "cable", "pull_up_bar", "bench", "kettlebell", "bodyweight"),
            experience = ExperienceLevel.INTERMEDIATE,
            exercisesPerDay = 7,
            includeFinisher = true
        )

        val generated = ProgramGenerator.generate(params, allExercises)

        // Deactivate existing programs
        programDao.deactivateAll()

        // Insert program
        val programId = programDao.insert(generated.program)

        // Insert goals
        val goalsWithId = generated.goals.map { it.copy(programId = programId) }
        goalDao.insertAll(goalsWithId)

        // Insert days and exercises
        for (day in generated.days) {
            val dayWithId = day.day.copy(programId = programId)
            val dayId = programDayDao.insertAll(listOf(dayWithId)).first()
            val exerciseEntities = ProgramGenerator.toProgramExerciseEntities(day, dayId)
            programExerciseDao.insertAll(exerciseEntities)
        }
    }
}
