package com.rhythmwave.gymflow.domain.usecase.program

import com.rhythmwave.gymflow.data.local.dao.*
import com.rhythmwave.gymflow.domain.model.*
import com.rhythmwave.gymflow.engine.ProgramGenerator
import javax.inject.Inject

class GenerateProgramUseCase @Inject constructor(
    private val programDao: ProgramDao,
    private val goalDao: GoalDao,
    private val programDayDao: ProgramDayDao,
    private val programExerciseDao: ProgramExerciseDao,
    private val exerciseDao: ExerciseDao
) {
    suspend operator fun invoke(params: ProgramGenerator.ProgramParams): Result<Long> {
        return try {
            // Get all exercises from DB
            val allExercises = exerciseDao.getAllSync()

            // Generate program
            val generated = ProgramGenerator.generate(params, allExercises)

            // Deactivate existing programs
            programDao.deactivateAll()

            // Insert program
            val programId = programDao.insert(generated.program)

            // Insert goals
            val goalsWithProgramId = generated.goals.map { it.copy(programId = programId) }
            goalDao.insertAll(goalsWithProgramId)

            // Insert days and exercises
            for (day in generated.days) {
                val dayWithProgramId = day.day.copy(programId = programId)
                val dayId = programDayDao.insertAll(listOf(dayWithProgramId)).first()

                val exerciseEntities = ProgramGenerator.toProgramExerciseEntities(day, dayId)
                programExerciseDao.insertAll(exerciseEntities)
            }

            Result.success(programId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
