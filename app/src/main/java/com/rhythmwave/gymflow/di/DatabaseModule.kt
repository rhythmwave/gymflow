package com.rhythmwave.gymflow.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rhythmwave.gymflow.data.local.GymFlowDatabase
import com.rhythmwave.gymflow.data.local.dao.*
import com.rhythmwave.gymflow.data.local.seed.ExerciseSeedData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GymFlowDatabase {
        return Room.databaseBuilder(
            context,
            GymFlowDatabase::class.java,
            "gymflow.db"
        )
        .addCallback(object : androidx.room.RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Seed exercises on first creation
                CoroutineScope(Dispatchers.IO).launch {
                    val database = Room.databaseBuilder(
                        context,
                        GymFlowDatabase::class.java,
                        "gymflow.db"
                    ).build()
                    val exercises = ExerciseSeedData.getAll()
                    database.exerciseDao().insertAll(exercises)
                }
            }
        })
        .build()
    }

    @Provides
    fun provideExerciseDao(db: GymFlowDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    fun provideProgramDao(db: GymFlowDatabase): ProgramDao = db.programDao()

    @Provides
    fun provideGoalDao(db: GymFlowDatabase): GoalDao = db.goalDao()

    @Provides
    fun provideProgramDayDao(db: GymFlowDatabase): ProgramDayDao = db.programDayDao()

    @Provides
    fun provideProgramExerciseDao(db: GymFlowDatabase): ProgramExerciseDao = db.programExerciseDao()

    @Provides
    fun provideWorkoutSessionDao(db: GymFlowDatabase): WorkoutSessionDao = db.workoutSessionDao()

    @Provides
    fun provideWorkoutSetDao(db: GymFlowDatabase): WorkoutSetDao = db.workoutSetDao()

    @Provides
    fun providePersonalRecordDao(db: GymFlowDatabase): PersonalRecordDao = db.personalRecordDao()

    @Provides
    fun provideUserConfigDao(db: GymFlowDatabase): UserConfigDao = db.userConfigDao()
}
