# Training Engine

## GymFlow — Program Generation & Adaptation Logic

**Version:** 1.0

---

## Overview

The Training Engine is the core algorithm that:
1. Generates workout programs based on user goals and preferences
2. Adapts daily workouts based on morning check-in
3. Progresses weight/reps based on performance
4. Evolves programs over weeks/months

---

## 1. Goal System

### Goal Profiles

```kotlin
enum class GoalProfile(
    val displayName: String,
    val icon: String,
    val color: Long,
    val repRange: IntRange,
    val restSeconds: IntRange,
    val tempo: String
) {
    POSTURE_FIX("Fix Posture", "🧘", 0xFF8BC34A, 10..15, 60..90, "controlled"),
    CARDIO_VASCULAR("Better Blood Flow", "🫀", 0xFF00BCD4, 12..20, 30..60, "fast"),
    HEART_RATE("Heart Rate", "❤️", 0xFFF44336, 12..20, 30..45, "moderate"),
    BURST_ENDURANCE("Burst + Endurance", "⚡", 0xFFFF5722, 5..15, 45..120, "explosive"),
    ABDOMEN_SHAPE("Abdomen Shape", "🎯", 0xFF9C27B0, 12..20, 30..60, "controlled"),
    CORE_SPINE("Core/Spine Strength", "🦴", 0xFF9C27B0, 8..15, 45..90, "controlled"),
    SEX_DRIVE("Sex Drive", "💕", 0xFFE91E63, 6..12, 60..120, "explosive")
}
```

### Goal Tags (Per Exercise)

```kotlin
enum class GoalTag {
    POSTURE,        // Fixes anterior/posterior imbalance
    CARDIO,         // Elevates sustained heart rate
    EXPLOSIVE,      // Power/burst training
    CORE_SPINE,     // Spinal stability, anti-movement
    DRIVE,          // Compounds that boost testosterone
    BLOOD_FLOW,     // High-rep, short rest, pump-focused
    FLEXIBILITY     // Mobility and range of motion
}
```

### Goal Priority System

Users set priority 1-5 for each active goal. The engine uses priorities to:
- Allocate more exercises to higher-priority goals
- Determine exercise selection order
- Balance weekly volume across goals

```
Priority 5 (Critical): 30% of weekly volume
Priority 4 (High):     25% of weekly volume
Priority 3 (Medium):   20% of weekly volume
Priority 2 (Low):      15% of weekly volume
Priority 1 (Minimal):  10% of weekly volume
```

---

## 2. Split Templates

### Split Selection Logic

```kotlin
fun selectSplit(daysPerWeek: Int, goals: List<GoalProfile>): SplitType {
    return when {
        daysPerWeek <= 2 -> SplitType.FULL_BODY_AB
        daysPerWeek == 3 -> {
            if (goals.contains(GoalProfile.CORE_SPINE)) SplitType.PUSH_PULL_LEGS
            else SplitType.FULL_BODY_ABC
        }
        daysPerWeek == 4 -> SplitType.UPPER_LOWER
        daysPerWeek == 5 -> SplitType.BRO_SPLIT
        daysPerWeek >= 6 -> SplitType.PPL_x2
    }
}
```

### Split Definitions

```kotlin
enum class SplitType(val displayName: String) {
    FULL_BODY_AB("Full Body A/B"),
    FULL_BODY_ABC("Full Body A/B/C"),
    PUSH_PULL_LEGS("Push/Pull/Legs"),
    UPPER_LOWER("Upper/Lower"),
    BRO_SPLIT("Bro Split"),
    PPL_x2("PPL × 2")
}
```

### Day Assignments

**4-Day Upper/Lower (Recommended for this user):**
```
Day 1 (Mon): Upper A — Posture + Push
Day 2 (Tue): Lower A — Explosive + Compound
Day 3 (Wed): REST
Day 4 (Thu): Upper B — Pull + Core
Day 5 (Fri): Lower B — Functional + Cardio
Day 6 (Sat): REST
Day 7 (Sun): REST
```

**3-Day Push/Pull/Legs:**
```
Day 1 (Mon): Push — Chest, Shoulders, Triceps
Day 2 (Wed): Pull — Back, Biceps, Posture
Day 3 (Fri): Legs — Squat, Deadlift, Explosive
Day 4-7: REST or active recovery
```

---

## 3. Exercise Selection Algorithm

### Scoring Function

Each exercise receives a score based on multiple factors:

```kotlin
fun scoreExercise(
    exercise: Exercise,
    userGoals: List<GoalWithPriority>,
    userEquipment: List<String>,
    userLevel: ExperienceLevel,
    recentHistory: List<WorkoutSet>
): Double {
    var score = 0.0

    // Goal relevance (highest weight)
    val goalMatch = exercise.tags.intersect(userGoals.map { it.tag }).size
    score += goalMatch * 10.0

    // Priority boost
    for (goal in userGoals) {
        if (goal.tag in exercise.tags) {
            score += goal.priority * 2.0
        }
    }

    // Equipment availability
    if (exercise.equipment.any { it in userEquipment }) {
        score += 5.0
    } else {
        return 0.0 // Can't do this exercise
    }

    // Difficulty match
    score += when {
        exercise.difficulty == userLevel -> 3.0
        exercise.difficulty == ExperienceLevel.BEGINNER && userLevel == ExperienceLevel.INTERMEDIATE -> 1.0
        exercise.difficulty == ExperienceLevel.INTERMEDIATE && userLevel == ExperienceLevel.BEGINNER -> -5.0
        else -> 0.0
    }

    // Variety bonus (penalize recently done exercises)
    val recentExerciseIds = recentHistory.map { it.exerciseId }.toSet()
    if (exercise.id in recentExerciseIds) {
        score -= 5.0
    }

    // Compound bonus (more efficient)
    if (exercise.isCompound) {
        score += 2.0
    }

    return score
}
```

### Selection Process

```
1. Filter exercises by available equipment
2. Filter by difficulty ≤ user level
3. Score each exercise against active goals
4. Select top N exercises per muscle group
5. Ensure muscle group balance
6. Avoid repeating exercises from last 3 sessions
7. Include at least 1 compound per muscle group
```

---

## 4. Workout Generation

### Sets & Reps Calculation

```kotlin
fun calculateSetsReps(
    exercise: Exercise,
    primaryGoal: GoalProfile,
    sessionDuration: Int,      // minutes
    exercisesInSession: Int
): SetsRepsConfig {
    val timePerExercise = sessionDuration / exercisesInSession
    val maxSets = (timePerExercise / 3).coerceIn(2, 5) // ~3 min per set

    return when (primaryGoal) {
        GoalProfile.POSTURE_FIX -> SetsRepsConfig(
            sets = maxSets.coerceAtMost(4),
            reps = 10..15,
            restSeconds = 60..90,
            tempo = "2-1-2"  // 2s eccentric, 1s pause, 2s concentric
        )
        GoalProfile.CARDIO_VASCULAR -> SetsRepsConfig(
            sets = maxSets.coerceAtMost(3),
            reps = 12..20,
            restSeconds = 30..45,
            tempo = "1-0-1"  // Fast, continuous
        )
        GoalProfile.BURST_ENDURANCE -> SetsRepsConfig(
            sets = maxSets.coerceAtMost(4),
            reps = 5..8,      // Explosive reps
            restSeconds = 60..90,
            tempo = "1-0-X"  // Explosive concentric
        )
        GoalProfile.CORE_SPINE -> SetsRepsConfig(
            sets = maxSets.coerceAtMost(4),
            reps = 8..15,
            restSeconds = 45..60,
            tempo = "2-2-2"  // Slow, controlled
        )
        GoalProfile.SEX_DRIVE -> SetsRepsConfig(
            sets = maxSets.coerceAtMost(4),
            reps = 6..12,
            restSeconds = 60..120,
            tempo = "2-1-1"  // Moderate
        )
        else -> SetsRepsConfig(
            sets = maxSets,
            reps = 8..12,
            restSeconds = 60..90,
            tempo = "2-1-2"
        )
    }
}
```

### Rest Timer Logic

```kotlin
fun getRestDuration(
    exercise: Exercise,
    goal: GoalProfile,
    setNumber: Int,
    isCompound: Boolean
): Int {
    val baseRest = goal.restSeconds.first
    val maxRest = goal.restSeconds.last

    var rest = baseRest

    // Compound lifts need more rest
    if (isCompound) rest += 30

    // Later sets need more rest
    if (setNumber > 2) rest += 15

    // Cap at max
    return rest.coerceAtMost(maxRest)
}
```

---

## 5. Daily Adaptation

### Morning Check-in Processing

```kotlin
data class MorningCheckIn(
    val sleepQuality: Int,      // 1-5
    val energyLevel: Int,       // 1-5
    val soreness: SorenessLevel, // NONE, MILD, MODERATE, SEVERE
    val sorenessLocation: List<MuscleGroup>,
    val availableMinutes: Int
)

enum class DailyRecommendation {
    PUSH,       // Full intensity
    LIGHT,      // Reduced intensity
    REST        // Rest day
}

fun processCheckIn(checkIn: MorningCheckIn): DailyRecommendation {
    val score = checkIn.sleepQuality + checkIn.energyLevel

    return when {
        checkIn.soreness == SorenessLevel.SEVERE -> DailyRecommendation.REST
        checkIn.sleepQuality <= 2 -> DailyRecommendation.REST
        score <= 4 -> DailyRecommendation.LIGHT
        score <= 6 && checkIn.soreness == SorenessLevel.MODERATE -> DailyRecommendation.LIGHT
        checkIn.availableMinutes < 20 -> DailyRecommendation.LIGHT
        else -> DailyRecommendation.PUSH
    }
}
```

### Workout Modification

```kotlin
fun modifyWorkoutForLightDay(
    workout: Workout,
    checkIn: MorningCheckIn
): Workout {
    var modified = workout

    // Reduce weight by 20%
    modified = modified.copy(
        exercises = modified.exercises.map { ex ->
            ex.copy(
                targetWeight = ex.targetWeight * 0.8,
                targetSets = (ex.targetSets - 1).coerceAtLeast(2)
            )
        }
    )

    // Remove finishers (cardio/HIIT)
    modified = modified.copy(
        exercises = modified.exercises.filter { !it.isFinisher }
    )

    // Swap explosive exercises for controlled versions
    if (checkIn.soreness != SorenessLevel.NONE) {
        modified = modified.copy(
            exercises = modified.exercises.map { ex ->
                if (ex.isExplosive && ex.muscleGroup in checkIn.sorenessLocation) {
                    swapForAlternative(ex, preferControlled = true)
                } else ex
            }
        )
    }

    // Add extra warmup for sore areas
    if (checkIn.sorenessLocation.isNotEmpty()) {
        val warmupExercises = checkIn.sorenessLocation.flatMap { muscle ->
            getMobilityExercises(muscle)
        }
        modified = modified.copy(
            warmup = modified.warmup + warmupExercises
        )
    }

    // Cap duration
    val totalSets = modified.exercises.sumOf { it.targetSets }
    val estimatedMinutes = totalSets * 3 + modified.warmup.size * 2
    if (estimatedMinutes > checkIn.availableMinutes) {
        // Remove lowest-priority exercises until it fits
        modified = trimWorkoutToDuration(modified, checkIn.availableMinutes)
    }

    return modified
}
```

---

## 6. Progression Engine

### Weight Progression

```kotlin
fun calculateProgression(
    exerciseId: String,
    recentSessions: List<ExerciseSession>
): ProgressionAction {
    if (recentSessions.size < 2) return ProgressionAction.MAINTAIN

    val last2 = recentSessions.takeLast(2)

    // Check if all sets completed at target reps
    val allCompleted = last2.all { session ->
        session.sets.all { it.repsCompleted >= it.targetReps }
    }

    if (allCompleted) {
        // Increase weight
        val increment = if (isUpperBodyExercise(exerciseId)) 2.5 else 5.0
        return ProgressionAction.INCREASE_WEIGHT(increment)
    }

    // Check if consistently failing
    val failedSessions = last2.count { session ->
        session.sets.any { it.repsCompleted < it.targetReps }
    }

    if (failedSessions >= 2) {
        // Deload
        return ProgressionAction.DELOAD(percent = 10)
    }

    // Check RPE
    val avgRPE = recentSessions.flatMap { it.sets }
        .mapNotNull { it.rpe }
        .average()

    if (avgRPE < 6.0) {
        return ProgressionAction.SUGGEST_INCREASE
    }

    return ProgressionAction.MAINTAIN
}

enum class ProgressionAction {
    MAINTAIN,
    INCREASE_WEIGHT(Double),
    INCREASE_REPS,
    DELOAD(percent: Int),
    SUGGEST_INCREASE,
    SWAP_EXERCISE
}
```

### 1RM Estimation (Epley Formula)

```kotlin
fun estimateOneRepMax(weight: Double, reps: Int): Double {
    if (reps == 1) return weight
    return weight * (1 + reps / 30.0)
}
```

---

## 7. Weekly Adaptation

```kotlin
fun weeklyAdaptation(weekData: WeekSummary): List<Recommendation> {
    val recommendations = mutableListOf<Recommendation>()

    // Volume check
    if (weekData.totalVolume < weekData.targetVolume * 0.8) {
        recommendations.add(Recommendation.IncreaseVolume)
    }

    // Goal coverage
    for (goal in weekData.activeGoals) {
        val completed = weekData.goalCompletion[goal] ?: 0
        val target = weekData.goalTargets[goal] ?: 0
        if (completed < target) {
            recommendations.add(Recommendation.AddGoalExercise(goal))
        }
    }

    // Sleep pattern
    if (weekData.avgSleepHours < 6.5) {
        recommendations.add(Recommendation.ReduceIntensity(10))
        recommendations.add(Recommendation.PrioritizeSleep)
    }

    // Progression check
    for (exercise in weekData.exercises) {
        when (val action = calculateProgression(exercise.id, exercise.sessions)) {
            is ProgressionAction.INCREASE_WEIGHT -> {
                recommendations.add(Recommendation.IncreaseWeight(exercise.id, action.amount))
            }
            is ProgressionAction.DELOAD -> {
                recommendations.add(Recommendation.Deload(exercise.id, action.percent))
            }
            else -> {}
        }
    }

    return recommendations
}
```

---

## 8. Monthly Adaptation

```kotlin
fun monthlyAdaptation(monthData: MonthSummary): ProgramEvolution {
    val evolution = ProgramEvolution()

    // Phase progression
    if (monthData.weeksCompleted >= 4) {
        evolution.suggestPhaseChange = true
        evolution.suggestedChanges = listOf(
            "Increase compound lift volume (4×6 → 4×8)",
            "Add advanced core exercises (dragon flags, ab wheel)",
            "Upgrade HIIT to 10 rounds (from 8)",
            "Consider adding a 5th training day"
        )
    }

    // PR frequency analysis
    val prFrequency = monthData.personalRecords.size
    if (prFrequency < 2) {
        evolution.suggestions.add("Try different exercise variations to break plateaus")
    }

    // Goal completion trends
    for (goal in monthData.activeGoals) {
        val completionRate = monthData.goalCompletionRate[goal] ?: 0.0
        if (completionRate < 0.7) {
            evolution.suggestions.add("Adjust ${goal.displayName} exercises — completion rate low")
        }
    }

    // Deload week
    if (monthData.weeksCompleted % 4 == 0) {
        evolution.forceDeloadWeek = true
    }

    return evolution
}
```

---

## 9. Cardio & Finisher Protocol

### Built-in Cardio Finishers

Every session includes a goal-appropriate finisher:

| Goal | Finisher Type | Duration | Example |
|---|---|---|---|
| Posture | Mobility circuit | 5 min | Band pull-aparts, wall angels, thoracic rotations |
| Blood Flow | High-rep circuit | 5 min | Jump rope, kettlebell swings, battle ropes |
| Heart Rate | HIIT intervals | 8-10 min | Bike sprints (30s on/30s off) |
| Burst | Explosive circuit | 5 min | Box jumps, medicine ball slams |
| Core | Core burnout | 5 min | Plank variations, dead bugs |
| Drive | Compound burnout | 5 min | Kettlebell swings, hip thrusts |

### Heart Rate Zone Targets

```kotlin
enum class HeartRateZone(val displayName: String, val percentMax: IntRange) {
    ZONE_1("Recovery", 50..60),
    ZONE_2("Aerobic", 60..70),
    ZONE_3("Tempo", 70..80),
    ZONE_4("Threshold", 80..90),
    ZONE_5("Max", 90..100)
}

// Zone 2 cardio: 2×/week for heart health
// Zone 4-5 HIIT: 1×/week for testosterone/capacity
```

---

## 10. Deload Protocol

### Automatic Deload (Every 4th Week)

```kotlin
fun applyDeload(weekPlan: WorkoutWeek): WorkoutWeek {
    return weekPlan.copy(
        exercises = weekPlan.exercises.map { day ->
            day.map { exercise ->
                exercise.copy(
                    targetSets = (exercise.targetSets * 0.6).toInt().coerceAtLeast(2),
                    targetWeight = exercise.targetWeight * 0.7,
                    targetReps = (exercise.targetReps * 0.8).toInt()
                )
            }
        },
        finishers = weekPlan.finishers.map { it.copy(duration = it.duration / 2) }
    )
}
```

### Deload Rules
- Weight: 70% of working weight
- Sets: 60% of normal volume
- Reps: 80% of target reps
- Finishers: Half duration
- No PR attempts
- Focus on form and recovery
