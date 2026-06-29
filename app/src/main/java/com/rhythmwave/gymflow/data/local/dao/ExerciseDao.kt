package com.rhythmwave.gymflow.data.local.dao

import androidx.room.*
import com.rhythmwave.gymflow.data.local.entity.ExerciseEntity
import com.rhythmwave.gymflow.domain.model.ExperienceLevel
import com.rhythmwave.gymflow.domain.model.MuscleGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercises ORDER BY muscleGroup, name")
    fun getAll(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getById(id: String): Flow<ExerciseEntity?>

    @Query("SELECT * FROM exercises WHERE muscleGroup = :group ORDER BY name")
    fun getByMuscleGroup(group: MuscleGroup): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE tags LIKE '%' || :tag || '%' ORDER BY name")
    fun getByGoalTag(tag: String): Flow<List<ExerciseEntity>>

    @Query("""
        SELECT * FROM exercises
        WHERE (:muscleGroup IS NULL OR muscleGroup = :muscleGroup)
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        AND (:isCompound IS NULL OR isCompound = :isCompound)
        ORDER BY name
    """)
    fun filter(
        muscleGroup: MuscleGroup? = null,
        difficulty: ExperienceLevel? = null,
        isCompound: Boolean? = null
    ): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE name LIKE '%' || :query || '%' OR id LIKE '%' || :query || '%' ORDER BY name")
    fun search(query: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id IN (:ids)")
    fun getByIds(ids: List<String>): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: ExerciseEntity)

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun count(): Int
}
