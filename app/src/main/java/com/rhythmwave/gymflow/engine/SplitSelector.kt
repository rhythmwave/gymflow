package com.rhythmwave.gymflow.engine

import com.rhythmwave.gymflow.domain.model.*

object SplitSelector {

    data class SplitConfig(
        val type: SplitType,
        val days: List<DayPlan>
    )

    data class DayPlan(
        val dayIndex: Int,      // 0=Mon, 1=Tue, etc.
        val name: String,       // "Upper A", "Lower B", "REST"
        val muscleGroups: List<MuscleGroup>,
        val isRestDay: Boolean = false
    )

    fun select(
        daysPerWeek: Int,
        goals: List<GoalWithPriority>,
        restDays: List<Int> // 0=Mon, 6=Sun
    ): SplitConfig {
        val splitType = when {
            daysPerWeek <= 2 -> SplitType.FULL_BODY_AB
            daysPerWeek == 3 -> {
                if (goals.any { it.profile == GoalProfile.CORE_SPINE }) SplitType.PUSH_PULL_LEGS
                else SplitType.FULL_BODY_ABC
            }
            daysPerWeek == 4 -> SplitType.UPPER_LOWER
            daysPerWeek == 5 -> SplitType.BRO_SPLIT
            else -> SplitType.PPL_x2
        }

        val trainingDays = (0..6).filter { it !in restDays }.take(daysPerWeek)
        val dayPlans = generateDayPlans(splitType, trainingDays, goals)

        return SplitConfig(type = splitType, days = dayPlans)
    }

    private fun generateDayPlans(
        split: SplitType,
        trainingDays: List<Int>,
        goals: List<GoalWithPriority>
    ): List<DayPlan> {
        val hasPosture = goals.any { it.profile == GoalProfile.POSTURE_FIX }
        val hasCore = goals.any { it.profile in listOf(GoalProfile.CORE_SPINE, GoalProfile.ABDOMEN_SHAPE) }
        val hasExplosive = goals.any { it.profile == GoalProfile.BURST_ENDURANCE }

        return when (split) {
            SplitType.FULL_BODY_AB -> listOf(
                DayPlan(trainingDays.getOrElse(0) { 0 }, "Full Body A",
                    listOf(MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.LEGS, MuscleGroup.CORE)),
                DayPlan(trainingDays.getOrElse(1) { 2 }, "Full Body B",
                    listOf(MuscleGroup.SHOULDERS, MuscleGroup.LEGS, MuscleGroup.ARMS, MuscleGroup.CORE))
            )

            SplitType.FULL_BODY_ABC -> listOf(
                DayPlan(trainingDays[0], "Full Body A — Push + Legs",
                    listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS, MuscleGroup.LEGS, MuscleGroup.CORE)),
                DayPlan(trainingDays[1], "Full Body B — Pull + Legs",
                    listOf(MuscleGroup.BACK, MuscleGroup.LEGS, MuscleGroup.ARMS, MuscleGroup.CORE)),
                DayPlan(trainingDays[2], "Full Body C — Compound + Core",
                    listOf(MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.LEGS, MuscleGroup.CORE))
            )

            SplitType.PUSH_PULL_LEGS -> listOf(
                DayPlan(trainingDays[0], "Push — Chest/Shoulders/Triceps",
                    listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS, MuscleGroup.ARMS)),
                DayPlan(trainingDays[1], "Pull — Back/Biceps/Posture",
                    listOf(MuscleGroup.BACK, MuscleGroup.ARMS, MuscleGroup.CORE)),
                DayPlan(trainingDays[2], "Legs — Squat/Deadlift/Explosive",
                    listOf(MuscleGroup.LEGS, MuscleGroup.CORE))
            )

            SplitType.UPPER_LOWER -> listOf(
                DayPlan(trainingDays[0], "Upper A — Posture + Push",
                    listOf(MuscleGroup.BACK, MuscleGroup.CHEST, MuscleGroup.SHOULDERS)),
                DayPlan(trainingDays[1], "Lower A — Explosive + Compound",
                    listOf(MuscleGroup.LEGS, MuscleGroup.CORE)),
                DayPlan(trainingDays[2], "Upper B — Pull + Arms",
                    listOf(MuscleGroup.BACK, MuscleGroup.SHOULDERS, MuscleGroup.ARMS, MuscleGroup.CORE)),
                DayPlan(trainingDays[3], "Lower B — Functional + Cardio",
                    listOf(MuscleGroup.LEGS, MuscleGroup.CORE, MuscleGroup.CARDIO))
            )

            SplitType.BRO_SPLIT -> listOf(
                DayPlan(trainingDays[0], "Chest", listOf(MuscleGroup.CHEST)),
                DayPlan(trainingDays[1], "Back", listOf(MuscleGroup.BACK)),
                DayPlan(trainingDays[2], "Shoulders", listOf(MuscleGroup.SHOULDERS, MuscleGroup.ARMS)),
                DayPlan(trainingDays[3], "Legs", listOf(MuscleGroup.LEGS)),
                DayPlan(trainingDays[4], "Arms + Core", listOf(MuscleGroup.ARMS, MuscleGroup.CORE))
            )

            SplitType.PPL_x2 -> listOf(
                DayPlan(trainingDays[0], "Push A", listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS)),
                DayPlan(trainingDays[1], "Pull A", listOf(MuscleGroup.BACK, MuscleGroup.ARMS)),
                DayPlan(trainingDays[2], "Legs A", listOf(MuscleGroup.LEGS, MuscleGroup.CORE)),
                DayPlan(trainingDays[3], "Push B", listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS, MuscleGroup.ARMS)),
                DayPlan(trainingDays[4], "Pull B", listOf(MuscleGroup.BACK, MuscleGroup.ARMS, MuscleGroup.CORE)),
                DayPlan(trainingDays[5], "Legs B", listOf(MuscleGroup.LEGS, MuscleGroup.CORE, MuscleGroup.CARDIO))
            )
        }
    }

    fun getRestDayNames(): List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
}
