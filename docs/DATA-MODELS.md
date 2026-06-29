# Data Models

## GymFlow — Room Database Schema

**Version:** 1.0
**Storage:** Room (SQLite) — local-first, no backend

---

## Entity Relationship Diagram

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Exercise   │     │   Program    │     │    Goal      │
│──────────────│     │──────────────│     │──────────────│
│ id (PK)      │     │ id (PK)      │     │ id (PK)      │
│ name         │     │ name         │     │ profile      │
│ muscleGroup  │     │ splitType    │     │ priority     │
│ equipment    │     │ daysPerWeek  │     │ programId(FK)│
│ difficulty   │     │ isActive     │     └──────────────┘
│ isCompound   │     │ createdAt    │
│ instructions │     └──────┬───────┘
│ tags         │            │
│ alternatives │            │ 1:N
│ videoUrl     │            │
└──────┬───────┘     ┌──────┴───────┐
       │             │ ProgramDay   │
       │             │──────────────│
       │             │ id (PK)      │
       │             │ programId(FK)│
       │             │ dayIndex     │
       │             │ dayName      │
       │             │ muscleGroups │
       │             └──────┬───────┘
       │                    │
       │                    │ 1:N
       │             ┌──────┴───────┐
       │             │ProgramExercise│
       │             │──────────────│
       │             │ id (PK)      │
       │             │ dayId (FK)   │
       │             │ exerciseId(FK)│
       │             │ orderIndex   │
       │             │ targetSets   │
       │             │ targetReps   │
       │             │ restSeconds  │
       │             │ isFinisher   │
       └──────┬──────┘
              │
              │ N:M (through WorkoutSet)
              │
       ┌──────┴───────┐     ┌──────────────┐
       │WorkoutSession│     │  WorkoutSet  │
       │──────────────│     │──────────────│
       │ id (PK)      │────→│ id (PK)      │
       │ programId(FK)│     │ sessionId(FK)│
       │ dayIndex     │     │ exerciseId(FK)│
       │ date         │     │ setNumber    │
       │ durationMin  │     │ weight       │
       │ totalVolume  │     │ reps         │
       │ rating       │     │ rpe          │
       │ checkIn      │     │ isWarmup     │
       └──────────────┘     │ notes        │
                            │ completedAt  │
                            └──────────────┘

       ┌──────────────┐
       │PersonalRecord│
       │──────────────│
       │ id (PK)      │
       │ exerciseId   │
       │ weight       │
       │ reps         │
       │ estOneRepMax │
       │ date         │
       └──────────────┘

       ┌──────────────┐
       │  UserConfig  │
       │──────────────│
       │ id (PK)      │
       │ name         │
       │ weight       │
       │ height       │
       │ age          │
       │ experience   │
       │ units        │
       │ equipment    │
       │ restDays     │
       │ sessionDuration│
       └──────────────┘
```

---

## Entity Definitions

### Exercise

```kotlin
@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey
    val id: String,                    // e.g., "barbell_squat"
    val name: String,                  // e.g., "Barbell Squat"
    val muscleGroup: MuscleGroup,      // CHEST, BACK, SHOULDERS, LEGS, ARMS, CORE, CARDIO
    @ColumnInfo(name = "equipment")
    val equipmentJson: String,         // JSON array: ["barbell","squat_rack"]
    val difficulty: ExperienceLevel,   // BEGINNER, INTERMEDIATE, ADVANCED
    val isCompound: Boolean,
    val instructions: String,          // Step-by-step form guide
    @ColumnInfo(name = "tags")
    val tagsJson: String,             // JSON array: ["POSTURE","DRIVE"]
    @ColumnInfo(name = "alternatives")
    val alternativesJson: String,      // JSON array: ["goblet_squat","leg_press"]
    val videoUrl: String? = null       // Optional YouTube/video link
)

// Type converters for JSON fields
class ExerciseConverters {
    @TypeConverter
    fun fromStringList(value: List<String>): String = Gson().toJson(value)

    @TypeConverter
    fun toStringList(value: String): List<String> =
        Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)

    @TypeConverter
    fun fromGoalTagSet(value: Set<GoalTag>): String = Gson().toJson(value)

    @TypeConverter
    fun toGoalTagSet(value: String): Set<GoalTag> =
        Gson().fromJson(value, object : TypeToken<Set<GoalTag>>() {}.type)
}
```

### Program

```kotlin
@Entity(tableName = "programs")
data class Program(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,                  // e.g., "Full Functional"
    val splitType: SplitType,          // UPPER_LOWER, PPL, etc.
    val daysPerWeek: Int,              // 2-6
    val sessionDuration: Int,          // Minutes (30, 45, 60, 90)
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### Goal

```kotlin
@Entity(
    tableName = "goals",
    foreignKeys = [ForeignKey(
        entity = Program::class,
        parentColumns = ["id"],
        childColumns = ["programId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val programId: Long,
    val profile: GoalProfile,          // POSTURE_FIX, CARDIO_VASCULAR, etc.
    val priority: Int,                 // 1-5
    val isEnabled: Boolean = true
)
```

### ProgramDay

```kotlin
@Entity(
    tableName = "program_days",
    foreignKeys = [ForeignKey(
        entity = Program::class,
        parentColumns = ["id"],
        childColumns = ["programId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ProgramDay(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val programId: Long,
    val dayIndex: Int,                 // 0-based (Mon=0, Tue=1, etc.)
    val dayName: String,               // "Upper A", "Lower B", "REST"
    @ColumnInfo(name = "muscleGroups")
    val muscleGroupsJson: String,      // JSON array: ["CHEST","BACK","SHOULDERS"]
    val isRestDay: Boolean = false
)
```

### ProgramExercise

```kotlin
@Entity(
    tableName = "program_exercises",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDay::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProgramExercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayId: Long,
    val exerciseId: String,
    val orderIndex: Int,               // Display order
    val targetSets: Int,               // e.g., 4
    val targetReps: Int,               // e.g., 8 (or min of range)
    val targetRepsMax: Int? = null,    // e.g., 12 (if range 8-12)
    val restSeconds: Int,              // e.g., 90
    val isFinisher: Boolean = false,   // Cardio/conditioning
    val notes: String? = null          // User notes
)
```

### WorkoutSession

```kotlin
@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val programId: Long,
    val dayIndex: Int,
    val date: Long,                    // Start timestamp
    val durationMinutes: Int = 0,
    val totalVolume: Double = 0.0,     // Sum of weight × reps
    val totalSets: Int = 0,
    val completedSets: Int = 0,
    val rating: Int? = null,           // 1-5 (too easy → too hard)
    @ColumnInfo(name = "checkIn")
    val checkInJson: String? = null,   // Serialized MorningCheckIn
    val createdAt: Long = System.currentTimeMillis()
)
```

### WorkoutSet

```kotlin
@Entity(
    tableName = "workout_sets",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkoutSet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val exerciseId: String,
    val setNumber: Int,
    val weight: Double,                // kg or lbs
    val reps: Int,
    val rpe: Int? = null,              // 1-10 (optional)
    val isWarmup: Boolean = false,
    val notes: String? = null,
    val completedAt: Long = System.currentTimeMillis()
)
```

### PersonalRecord

```kotlin
@Entity(tableName = "personal_records")
data class PersonalRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val exerciseId: String,
    val weight: Double,
    val reps: Int,
    val estimatedOneRepMax: Double,    // Calculated via Epley formula
    val date: Long = System.currentTimeMillis()
)
```

### UserConfig

```kotlin
@Entity(tableName = "user_config")
data class UserConfig(
    @PrimaryKey
    val id: Int = 1,                   // Singleton
    val name: String = "User",
    val weight: Double? = null,        // kg
    val height: Int? = null,           // cm
    val age: Int? = null,
    val experience: ExperienceLevel = ExperienceLevel.INTERMEDIATE,
    val units: WeightUnit = WeightUnit.KG,
    @ColumnInfo(name = "equipment")
    val equipmentJson: String,         // JSON array of available equipment
    val restDays: String,              // JSON array: [2,5,6] (Wed,Sat,Sun)
    val sessionDuration: Int = 60,     // Default session duration
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

---

## Enums

```kotlin
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

enum class GoalProfile {
    POSTURE_FIX,
    CARDIO_VASCULAR,
    HEART_RATE,
    BURST_ENDURANCE,
    ABDOMEN_SHAPE,
    CORE_SPINE,
    SEX_DRIVE
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
```

---

## DAO Interfaces

### ExerciseDao

```kotlin
@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises")
    fun getAll(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getById(id: String): Flow<Exercise?>

    @Query("SELECT * FROM exercises WHERE muscleGroup = :group")
    fun getByMuscleGroup(group: MuscleGroup): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE tags LIKE '%' || :tag || '%'")
    fun getByGoalTag(tag: String): Flow<List<Exercise>>

    @Query("""
        SELECT * FROM exercises
        WHERE (:muscleGroup IS NULL OR muscleGroup = :muscleGroup)
        AND (:difficulty IS NULL OR difficulty = :difficulty)
        AND (:isCompound IS NULL OR isCompound = :isCompound)
    """)
    fun filter(
        muscleGroup: MuscleGroup?,
        difficulty: ExperienceLevel?,
        isCompound: Boolean?
    ): Flow<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<Exercise>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: Exercise)
}
```

### ProgramDao

```kotlin
@Dao
interface ProgramDao {
    @Query("SELECT * FROM programs WHERE isActive = 1 LIMIT 1")
    fun getActiveProgram(): Flow<Program?>

    @Query("SELECT * FROM programs ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Program>>

    @Insert
    suspend fun insert(program: Program): Long

    @Update
    suspend fun update(program: Program)

    @Query("UPDATE programs SET isActive = 0")
    suspend fun deactivateAll()

    @Query("UPDATE programs SET isActive = 1 WHERE id = :id")
    suspend fun activate(id: Long)
}
```

### WorkoutSessionDao

```kotlin
@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_sessions WHERE date >= :startDate ORDER BY date DESC")
    fun getSessionsSince(startDate: Long): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    fun getById(id: Long): Flow<WorkoutSession?>

    @Query("SELECT * FROM workout_sessions ORDER BY date DESC LIMIT 1")
    fun getLatest(): Flow<WorkoutSession?>

    @Insert
    suspend fun insert(session: WorkoutSession): Long

    @Update
    suspend fun update(session: WorkoutSession)

    @Query("""
        SELECT SUM(totalVolume) FROM workout_sessions
        WHERE date >= :startDate AND date <= :endDate
    """)
    fun getTotalVolume(startDate: Long, endDate: Long): Flow<Double?>

    @Query("""
        SELECT COUNT(*) FROM workout_sessions
        WHERE date >= :startDate
    """)
    fun getSessionCount(startDate: Long): Flow<Int>
}
```

### WorkoutSetDao

```kotlin
@Dao
interface WorkoutSetDao {
    @Query("SELECT * FROM workout_sets WHERE sessionId = :sessionId ORDER BY completedAt")
    fun getBySession(sessionId: Long): Flow<List<WorkoutSet>>

    @Query("""
        SELECT * FROM workout_sets
        WHERE exerciseId = :exerciseId
        ORDER BY completedAt DESC
        LIMIT :limit
    """)
    fun getRecentForExercise(exerciseId: String, limit: Int): Flow<List<WorkoutSet>>

    @Insert
    suspend fun insert(set: WorkoutSet): Long

    @Insert
    suspend fun insertAll(sets: List<WorkoutSet>)

    @Update
    suspend fun update(set: WorkoutSet)

    @Query("""
        SELECT * FROM workout_sets
        WHERE exerciseId = :exerciseId AND sessionId = :sessionId
        ORDER BY setNumber
    """)
    fun getByExerciseAndSession(exerciseId: String, sessionId: Long): Flow<List<WorkoutSet>>
}
```

### PersonalRecordDao

```kotlin
@Dao
interface PersonalRecordDao {
    @Query("SELECT * FROM personal_records ORDER BY date DESC")
    fun getAll(): Flow<List<PersonalRecord>>

    @Query("""
        SELECT * FROM personal_records
        WHERE exerciseId = :exerciseId
        ORDER BY estimatedOneRepMax DESC
        LIMIT 1
    """)
    fun getBestForExercise(exerciseId: String): Flow<PersonalRecord?>

    @Query("""
        SELECT * FROM personal_records
        WHERE date >= :startDate
        ORDER BY date DESC
    """)
    fun getRecent(startDate: Long): Flow<List<PersonalRecord>>

    @Insert
    suspend fun insert(record: PersonalRecord): Long

    @Query("""
        SELECT * FROM personal_records
        WHERE exerciseId = :exerciseId
        ORDER BY estimatedOneRepMax DESC
    """)
    fun getAllForExercise(exerciseId: String): Flow<List<PersonalRecord>>
}
```
