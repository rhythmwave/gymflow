package com.rhythmwave.gymflow.data.local.entity

import androidx.room.*
import com.rhythmwave.gymflow.domain.model.*

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val muscleGroup: MuscleGroup,
    @ColumnInfo(name = "equipment")
    val equipmentJson: String,
    val difficulty: ExperienceLevel,
    val isCompound: Boolean,
    val instructions: String,
    @ColumnInfo(name = "tags")
    val tagsJson: String,
    @ColumnInfo(name = "alternatives")
    val alternativesJson: String,
    val videoUrl: String? = null
)

@Entity(tableName = "programs")
data class ProgramEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val splitType: SplitType,
    val daysPerWeek: Int,
    val sessionDuration: Int,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "goals",
    foreignKeys = [ForeignKey(
        entity = ProgramEntity::class,
        parentColumns = ["id"],
        childColumns = ["programId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val programId: Long,
    val profile: GoalProfile,
    val priority: Int,
    val isEnabled: Boolean = true
)

@Entity(
    tableName = "program_days",
    foreignKeys = [ForeignKey(
        entity = ProgramEntity::class,
        parentColumns = ["id"],
        childColumns = ["programId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ProgramDayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val programId: Long,
    val dayIndex: Int,
    val dayName: String,
    @ColumnInfo(name = "muscleGroups")
    val muscleGroupsJson: String,
    val isRestDay: Boolean = false
)

@Entity(
    tableName = "program_exercises",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProgramExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayId: Long,
    val exerciseId: String,
    val orderIndex: Int,
    val targetSets: Int,
    val targetReps: Int,
    val targetRepsMax: Int? = null,
    val restSeconds: Int,
    val isFinisher: Boolean = false,
    val notes: String? = null
)

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val programId: Long,
    val dayIndex: Int,
    val date: Long,
    val durationMinutes: Int = 0,
    val totalVolume: Double = 0.0,
    val totalSets: Int = 0,
    val completedSets: Int = 0,
    val rating: Int? = null,
    @ColumnInfo(name = "checkIn")
    val checkInJson: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "workout_sets",
    foreignKeys = [ForeignKey(
        entity = WorkoutSessionEntity::class,
        parentColumns = ["id"],
        childColumns = ["sessionId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val exerciseId: String,
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val rpe: Int? = null,
    val isWarmup: Boolean = false,
    val notes: String? = null,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "personal_records")
data class PersonalRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val exerciseId: String,
    val weight: Double,
    val reps: Int,
    val estimatedOneRepMax: Double,
    val date: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_config")
data class UserConfigEntity(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "User",
    val weight: Double? = null,
    val height: Int? = null,
    val age: Int? = null,
    val experience: ExperienceLevel = ExperienceLevel.INTERMEDIATE,
    val units: WeightUnit = WeightUnit.KG,
    @ColumnInfo(name = "equipment")
    val equipmentJson: String,
    val restDays: String,
    val sessionDuration: Int = 60,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
