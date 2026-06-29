package com.rhythmwave.gymflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rhythmwave.gymflow.data.local.converter.Converters
import com.rhythmwave.gymflow.data.local.dao.*
import com.rhythmwave.gymflow.data.local.entity.*

@Database(
    entities = [
        ExerciseEntity::class,
        ProgramEntity::class,
        GoalEntity::class,
        ProgramDayEntity::class,
        ProgramExerciseEntity::class,
        WorkoutSessionEntity::class,
        WorkoutSetEntity::class,
        PersonalRecordEntity::class,
        UserConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GymFlowDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun programDao(): ProgramDao
    abstract fun goalDao(): GoalDao
    abstract fun programDayDao(): ProgramDayDao
    abstract fun programExerciseDao(): ProgramExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun workoutSetDao(): WorkoutSetDao
    abstract fun personalRecordDao(): PersonalRecordDao
    abstract fun userConfigDao(): UserConfigDao
}
