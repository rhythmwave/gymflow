package com.rhythmwave.gymflow.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rhythmwave.gymflow.domain.model.*

class Converters {
    private val gson = Gson()

    // String List
    @TypeConverter
    fun fromStringList(value: List<String>): String = gson.toJson(value)

    @TypeConverter
    fun toStringList(value: String): List<String> =
        gson.fromJson(value, object : TypeToken<List<String>>() {}.type)

    // GoalTag Set
    @TypeConverter
    fun fromGoalTagSet(value: Set<GoalTag>): String = gson.toJson(value.map { it.name })

    @TypeConverter
    fun toGoalTagSet(value: String): Set<GoalTag> {
        val list: List<String> = gson.fromJson(value, object : TypeToken<List<String>>() {}.type)
        return list.map { GoalTag.valueOf(it) }.toSet()
    }

    // MuscleGroup
    @TypeConverter
    fun fromMuscleGroup(value: MuscleGroup): String = value.name

    @TypeConverter
    fun toMuscleGroup(value: String): MuscleGroup = MuscleGroup.valueOf(value)

    // ExperienceLevel
    @TypeConverter
    fun fromExperienceLevel(value: ExperienceLevel): String = value.name

    @TypeConverter
    fun toExperienceLevel(value: String): ExperienceLevel = ExperienceLevel.valueOf(value)

    // WeightUnit
    @TypeConverter
    fun fromWeightUnit(value: WeightUnit): String = value.name

    @TypeConverter
    fun toWeightUnit(value: String): WeightUnit = WeightUnit.valueOf(value)

    // SplitType
    @TypeConverter
    fun fromSplitType(value: SplitType): String = value.name

    @TypeConverter
    fun toSplitType(value: String): SplitType = SplitType.valueOf(value)

    // GoalProfile
    @TypeConverter
    fun fromGoalProfile(value: GoalProfile): String = value.name

    @TypeConverter
    fun toGoalProfile(value: String): GoalProfile = GoalProfile.valueOf(value)

    // Int Range (for rep ranges in seed data)
    @TypeConverter
    fun fromIntRange(value: IntRange): String = "${value.first}-${value.last}"

    @TypeConverter
    fun toIntRange(value: String): IntRange {
        val parts = value.split("-")
        return parts[0].toInt()..parts[1].toInt()
    }
}
