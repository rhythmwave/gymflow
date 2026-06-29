package com.rhythmwave.gymflow.domain.model

enum class MuscleGroup {
    CHEST, BACK, SHOULDERS, LEGS, ARMS, CORE, CARDIO
}

enum class ExperienceLevel {
    BEGINNER, INTERMEDIATE, ADVANCED
}

enum class WeightUnit {
    KG, LBS
}

enum class SplitType {
    FULL_BODY_AB,
    FULL_BODY_ABC,
    PUSH_PULL_LEGS,
    UPPER_LOWER,
    BRO_SPLIT,
    PPL_x2
}

enum class GoalProfile(
    val displayName: String,
    val icon: String,
    val colorHex: Long,
    val repRange: IntRange,
    val restSeconds: IntRange,
    val tempo: String
) {
    POSTURE_FIX("Fix Posture", "🧘", 0xFF8BC34A, 10..15, 60..90, "controlled"),
    CARDIO_VASCULAR("Better Blood Flow", "🫀", 0xFF00BCD4, 12..20, 30..60, "fast"),
    HEART_RATE("Heart Rate", "❤️", 0xFFF44336, 12..20, 30..45, "moderate"),
    BURST_ENDURANCE("Burst + Endurance", "⚡", 0xFFFF5722, 5..15, 45..120, "explosive"),
    ABDOMEN_SHAPE("Abdomen Shape", "🎯", 0xFF9C27B0, 12..20, 30..60, "controlled"),
    CORE_SPINE("Core/Spine Strength", "🦴", 0xFF7E57C2, 8..15, 45..90, "controlled"),
    SEX_DRIVE("Sex Drive", "💕", 0xFFE91E63, 6..12, 60..120, "explosive")
}

enum class GoalTag {
    POSTURE,
    CARDIO,
    EXPLOSIVE,
    CORE_SPINE,
    DRIVE,
    BLOOD_FLOW,
    FLEXIBILITY
}

enum class SorenessLevel {
    NONE, MILD, MODERATE, SEVERE
}

enum class DailyRecommendation {
    PUSH, LIGHT, REST
}

data class GoalWithPriority(
    val profile: GoalProfile,
    val priority: Int // 1-5
)

data class SetsRepsConfig(
    val sets: Int,
    val reps: IntRange,
    val restSeconds: IntRange,
    val tempo: String
)
