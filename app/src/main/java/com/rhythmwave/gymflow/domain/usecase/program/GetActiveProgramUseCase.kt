package com.rhythmwave.gymflow.domain.usecase.program

import com.rhythmwave.gymflow.data.local.dao.*
import com.rhythmwave.gymflow.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class ActiveProgramData(
    val program: ProgramEntity,
    val goals: List<GoalEntity>,
    val days: List<DayWithExercises>
)

data class DayWithExercises(
    val day: ProgramDayEntity,
    val exercises: List<ProgramExerciseEntity>
)

class GetActiveProgramUseCase @Inject constructor(
    private val programDao: ProgramDao,
    private val goalDao: GoalDao,
    private val programDayDao: ProgramDayDao,
    private val programExerciseDao: ProgramExerciseDao
) {
    operator fun invoke(): Flow<ActiveProgramData?> {
        return programDao.getActiveProgram().combine(
            goalDao.getActiveByProgram(0) // Will be filtered by programId
        ) { program, goals ->
            if (program == null) return@combine null

            // Get days for this program
            val days = programDayDao.getByProgram(program.id)
            // For now return basic structure — full combine would need flatMapLatest
            ActiveProgramData(
                program = program,
                goals = goals.map { it.copy(programId = program.id) },
                days = emptyList() // TODO: populate with actual days
            )
        }
    }
}
