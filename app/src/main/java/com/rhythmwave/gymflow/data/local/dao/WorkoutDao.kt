package com.rhythmwave.gymflow.data.local.dao

import androidx.room.*
import com.rhythmwave.gymflow.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {

    @Query("SELECT * FROM workout_sessions WHERE date >= :startDate ORDER BY date DESC")
    fun getSessionsSince(startDate: Long): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    fun getById(id: Long): Flow<WorkoutSessionEntity?>

    @Query("SELECT * FROM workout_sessions ORDER BY date DESC LIMIT 1")
    fun getLatest(): Flow<WorkoutSessionEntity?>

    @Query("SELECT * FROM workout_sessions WHERE date >= :startOfDay AND date < :endOfDay LIMIT 1")
    fun getTodaySession(startOfDay: Long, endOfDay: Long): Flow<WorkoutSessionEntity?>

    @Insert
    suspend fun insert(session: WorkoutSessionEntity): Long

    @Update
    suspend fun update(session: WorkoutSessionEntity)

    @Delete
    suspend fun delete(session: WorkoutSessionEntity)

    @Query("SELECT SUM(totalVolume) FROM workout_sessions WHERE date >= :startDate AND date <= :endDate")
    fun getTotalVolume(startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE date >= :startDate")
    fun getSessionCount(startDate: Long): Flow<Int>

    @Query("SELECT * FROM workout_sessions ORDER BY date DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<WorkoutSessionEntity>>
}

@Dao
interface WorkoutSetDao {

    @Query("SELECT * FROM workout_sets WHERE sessionId = :sessionId ORDER BY completedAt")
    fun getBySession(sessionId: Long): Flow<List<WorkoutSetEntity>>

    @Query("SELECT * FROM workout_sets WHERE sessionId = :sessionId AND exerciseId = :exerciseId ORDER BY setNumber")
    fun getBySessionAndExercise(sessionId: Long, exerciseId: String): Flow<List<WorkoutSetEntity>>

    @Query("""
        SELECT * FROM workout_sets
        WHERE exerciseId = :exerciseId
        ORDER BY completedAt DESC
        LIMIT :limit
    """)
    fun getRecentForExercise(exerciseId: String, limit: Int): Flow<List<WorkoutSetEntity>>

    @Query("""
        SELECT * FROM workout_sets
        WHERE exerciseId = :exerciseId
        ORDER BY (weight * reps) DESC
        LIMIT 1
    """)
    fun getBestSet(exerciseId: String): Flow<WorkoutSetEntity?>

    @Insert
    suspend fun insert(set: WorkoutSetEntity): Long

    @Insert
    suspend fun insertAll(sets: List<WorkoutSetEntity>)

    @Update
    suspend fun update(set: WorkoutSetEntity)

    @Delete
    suspend fun delete(set: WorkoutSetEntity)
}

@Dao
interface PersonalRecordDao {

    @Query("SELECT * FROM personal_records ORDER BY date DESC")
    fun getAll(): Flow<List<PersonalRecordEntity>>

    @Query("""
        SELECT * FROM personal_records
        WHERE exerciseId = :exerciseId
        ORDER BY estimatedOneRepMax DESC
        LIMIT 1
    """)
    fun getBestForExercise(exerciseId: String): Flow<PersonalRecordEntity?>

    @Query("""
        SELECT * FROM personal_records
        WHERE date >= :startDate
        ORDER BY date DESC
    """)
    fun getRecent(startDate: Long): Flow<List<PersonalRecordEntity>>

    @Query("""
        SELECT * FROM personal_records
        WHERE exerciseId = :exerciseId
        ORDER BY estimatedOneRepMax DESC
    """)
    fun getAllForExercise(exerciseId: String): Flow<List<PersonalRecordEntity>>

    @Insert
    suspend fun insert(record: PersonalRecordEntity): Long

    @Delete
    suspend fun delete(record: PersonalRecordEntity)
}

@Dao
interface UserConfigDao {

    @Query("SELECT * FROM user_config WHERE id = 1")
    fun getConfig(): Flow<UserConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(config: UserConfigEntity)

    @Query("DELETE FROM user_config")
    suspend fun clear()
}
