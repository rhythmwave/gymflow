package com.rhythmwave.gymflow.data.local.dao

import androidx.room.*
import com.rhythmwave.gymflow.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramDao {

    @Query("SELECT * FROM programs WHERE isActive = 1 LIMIT 1")
    fun getActiveProgram(): Flow<ProgramEntity?>

    @Query("SELECT * FROM programs ORDER BY createdAt DESC")
    fun getAll(): Flow<List<ProgramEntity>>

    @Query("SELECT * FROM programs WHERE id = :id")
    fun getById(id: Long): Flow<ProgramEntity?>

    @Insert
    suspend fun insert(program: ProgramEntity): Long

    @Update
    suspend fun update(program: ProgramEntity)

    @Query("UPDATE programs SET isActive = 0")
    suspend fun deactivateAll()

    @Query("UPDATE programs SET isActive = 1 WHERE id = :id")
    suspend fun activate(id: Long)

    @Delete
    suspend fun delete(program: ProgramEntity)
}

@Dao
interface GoalDao {

    @Query("SELECT * FROM goals WHERE programId = :programId")
    fun getByProgram(programId: Long): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE programId = :programId AND isEnabled = 1 ORDER BY priority DESC")
    fun getActiveByProgram(programId: Long): Flow<List<GoalEntity>>

    @Insert
    suspend fun insertAll(goals: List<GoalEntity>)

    @Update
    suspend fun update(goal: GoalEntity)

    @Query("DELETE FROM goals WHERE programId = :programId")
    suspend fun deleteByProgram(programId: Long)
}

@Dao
interface ProgramDayDao {

    @Query("SELECT * FROM program_days WHERE programId = :programId ORDER BY dayIndex")
    fun getByProgram(programId: Long): Flow<List<ProgramDayEntity>>

    @Query("SELECT * FROM program_days WHERE id = :id")
    fun getById(id: Long): Flow<ProgramDayEntity?>

    @Insert
    suspend fun insertAll(days: List<ProgramDayEntity>): List<Long>

    @Update
    suspend fun update(day: ProgramDayEntity)

    @Query("DELETE FROM program_days WHERE programId = :programId")
    suspend fun deleteByProgram(programId: Long)
}

@Dao
interface ProgramExerciseDao {

    @Query("SELECT * FROM program_exercises WHERE dayId = :dayId ORDER BY orderIndex")
    fun getByDay(dayId: Long): Flow<List<ProgramExerciseEntity>>

    @Query("SELECT * FROM program_exercises WHERE dayId = :dayId ORDER BY orderIndex")
    suspend fun getByDaySync(dayId: Long): List<ProgramExerciseEntity>

    @Insert
    suspend fun insertAll(exercises: List<ProgramExerciseEntity>)

    @Update
    suspend fun update(exercise: ProgramExerciseEntity)

    @Delete
    suspend fun delete(exercise: ProgramExerciseEntity)

    @Query("DELETE FROM program_exercises WHERE dayId = :dayId")
    suspend fun deleteByDay(dayId: Long)
}
