package com.rhythmwave.gymflow.engine

import com.rhythmwave.gymflow.data.local.entity.*
import com.rhythmwave.gymflow.domain.model.*

object ProgramGenerator {

    data class GeneratedProgram(
        val program: ProgramEntity,
        val goals: List<GoalEntity>,
        val days: List<GeneratedDay>
    )

    data class GeneratedDay(
        val day: ProgramDayEntity,
        val exercises: List<GeneratedExercise>
    )

    data class GeneratedExercise(
        val exercise: ExerciseEntity,
        val config: SetsRepsCalculator.ExerciseConfig,
        val orderIndex: Int
    )

    data class ProgramParams(
        val name: String,
        val goals: List<GoalWithPriority>,
        val daysPerWeek: Int,
        val sessionDuration: Int,
        val restDays: List<Int>,
        val equipment: List<String>,
        val experience: ExperienceLevel,
        val exercisesPerDay: Int = 7,
        val includeFinisher: Boolean = true
    )

    fun generate(
        params: ProgramParams,
        allExercises: List<ExerciseEntity>
    ): GeneratedProgram {
        // 1. Select split
        val split = SplitSelector.select(params.daysPerWeek, params.goals, params.restDays)

        // 2. Generate each training day
        val generatedDays = split.days.mapIndexed { index, dayPlan ->
            generateDay(
                dayPlan = dayPlan,
                dayIndex = dayPlan.dayIndex,
                params = params,
                allExercises = allExercises,
                includeFinisher = params.includeFinisher && dayPlan.muscleGroups.any { it != MuscleGroup.CARDIO }
            )
        }

        // 3. Create program entity
        val program = ProgramEntity(
            name = params.name,
            splitType = split.type,
            daysPerWeek = params.daysPerWeek,
            sessionDuration = params.sessionDuration,
            isActive = true
        )

        // 4. Create goal entities
        val goalEntities = params.goals.map {
            GoalEntity(
                programId = 0, // Will be set after insert
                profile = it.profile,
                priority = it.priority,
                isEnabled = true
            )
        }

        return GeneratedProgram(
            program = program,
            goals = goalEntities,
            days = generatedDays
        )
    }

    private fun generateDay(
        dayPlan: SplitSelector.DayPlan,
        dayIndex: Int,
        params: ProgramParams,
        allExercises: List<ExerciseEntity>,
        includeFinisher: Boolean
    ): GeneratedDay {
        val primaryGoal = params.goals.maxByOrNull { it.priority }?.profile ?: GoalProfile.POSTURE_FIX
        val exercisesPerGroup = calculateExercisesPerGroup(
            dayPlan.muscleGroups.size,
            params.exercisesPerDay,
            includeFinisher
        )

        val selectedExercises = mutableListOf<ExerciseEntity>()
        val recentlyUsed = mutableSetOf<String>()

        // Select exercises for each muscle group
        for (group in dayPlan.muscleGroups) {
            if (group == MuscleGroup.CARDIO && dayPlan.muscleGroups.size > 1) continue // Skip cardio unless it's the main focus

            val count = exercisesPerGroup[group] ?: 2
            val scored = ExerciseScorer.selectForMuscleGroup(
                exercises = allExercises,
                muscleGroup = group,
                count = count,
                userGoals = params.goals,
                userEquipment = params.equipment,
                userLevel = params.experience,
                recentlyUsedIds = recentlyUsed
            )
            selectedExercises.addAll(scored.map { it.exercise })
            recentlyUsed.addAll(scored.map { it.exercise.id })
        }

        // Add cardio finisher if applicable
        if (includeFinisher && MuscleGroup.CARDIO !in dayPlan.muscleGroups) {
            val cardioFinisher = selectFinisher(allExercises, primaryGoal, params.equipment, recentlyUsed)
            if (cardioFinisher != null) {
                selectedExercises.add(cardioFinisher)
            }
        }

        // Create configs for each exercise
        val generatedExercises = selectedExercises.mapIndexed { index, exercise ->
            val isFinisher = index == selectedExercises.lastIndex && exercise.muscleGroup == MuscleGroup.CARDIO
            val config = SetsRepsCalculator.calculate(
                isCompound = exercise.isCompound,
                primaryGoal = primaryGoal,
                sessionDurationMinutes = params.sessionDuration,
                exercisesInSession = selectedExercises.size,
                isFinisher = isFinisher
            )
            GeneratedExercise(
                exercise = exercise,
                config = config,
                orderIndex = index
            )
        }

        val dayEntity = ProgramDayEntity(
            programId = 0, // Will be set after insert
            dayIndex = dayIndex,
            dayName = dayPlan.name,
            muscleGroupsJson = com.google.gson.Gson().toJson(dayPlan.muscleGroups.map { it.name }),
            isRestDay = false
        )

        return GeneratedDay(day = dayEntity, exercises = generatedExercises)
    }

    private fun calculateExercisesPerGroup(
        groupCount: Int,
        totalExercises: Int,
        includeFinisher: Boolean
    ): Map<MuscleGroup, Int> {
        val available = if (includeFinisher) totalExercises - 1 else totalExercises
        val perGroup = (available / groupCount.coerceAtLeast(1)).coerceAtLeast(2)
        return emptyMap() // Will use default per-group selection
    }

    private fun selectFinisher(
        allExercises: List<ExerciseEntity>,
        goal: GoalProfile,
        equipment: List<String>,
        recentlyUsed: Set<String>
    ): ExerciseEntity? {
        val cardioExercises = allExercises.filter {
            it.muscleGroup == MuscleGroup.CARDIO &&
            ExerciseScorer.scoreExercise(it, listOf(GoalWithPriority(goal, 5)), equipment, ExperienceLevel.INTERMEDIATE).score > 0
        }
        return cardioExercises.firstOrNull { it.id !in recentlyUsed }
            ?: cardioExercises.firstOrNull()
    }

    fun toProgramExerciseEntities(
        generatedDay: GeneratedDay,
        dayId: Long
    ): List<ProgramExerciseEntity> {
        return generatedDay.exercises.map { gen ->
            ProgramExerciseEntity(
                dayId = dayId,
                exerciseId = gen.exercise.id,
                orderIndex = gen.orderIndex,
                targetSets = gen.config.targetSets,
                targetReps = gen.config.targetReps,
                targetRepsMax = gen.config.targetRepsMax,
                restSeconds = gen.config.restSeconds,
                isFinisher = gen.config.isFinisher
            )
        }
    }
}
