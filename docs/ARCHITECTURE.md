# Architecture

## GymFlow — Android App Architecture

**Version:** 1.0
**Pattern:** MVVM + Clean Architecture

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer                           │
│  ┌─────────┐ ┌──────────┐ ┌───────────┐ ┌───────────┐  │
│  │  Home   │ │  Program │ │  Workout  │ │  Progress │  │
│  │  Screen │ │  Screen  │ │  Screen   │ │  Screen   │  │
│  └────┬────┘ └────┬─────┘ └─────┬─────┘ └─────┬─────┘  │
│       │           │             │              │        │
│  ┌────┴────┐ ┌────┴─────┐ ┌────┴──────┐ ┌────┴─────┐  │
│  │  Home   │ │  Program │ │  Workout  │ │ Progress │  │
│  │  ViewM  │ │  ViewM   │ │  ViewM    │ │  ViewM   │  │
│  └────┬────┘ └────┬─────┘ └─────┬─────┘ └────┬─────┘  │
├───────┼───────────┼─────────────┼────────────┼─────────┤
│       │     Domain Layer        │            │         │
│       │           │             │            │         │
│  ┌────┴───────────┴─────────────┴────────────┴──────┐  │
│  │                    Use Cases                      │  │
│  │  ┌──────────────┐ ┌──────────────┐ ┌───────────┐ │  │
│  │  │  Generate    │ │   Log        │ │  Get      │ │  │
│  │  │  Program     │ │   Workout    │ │  Progress │ │  │
│  │  └──────────────┘ └──────────────┘ └───────────┘ │  │
│  │  ┌──────────────┐ ┌──────────────┐ ┌───────────┐ │  │
│  │  │  Adapt Daily │ │  Calculate   │ │  Check    │ │  │
│  │  │  Workout     │ │  Progression │ │  PRs      │ │  │
│  │  └──────────────┘ └──────────────┘ └───────────┘ │  │
│  └──────────────────────┬───────────────────────────┘  │
│                         │                              │
│  ┌──────────────────────┴───────────────────────────┐  │
│  │               Domain Models                      │  │
│  │  Exercise, Program, WorkoutSession, WorkoutSet,  │  │
│  │  PersonalRecord, Goal, UserConfig                │  │
│  └──────────────────────┬───────────────────────────┘  │
├─────────────────────────┼──────────────────────────────┤
│                   Data Layer                           │
│                         │                              │
│  ┌──────────────────────┴───────────────────────────┐  │
│  │               Repositories                       │  │
│  │  ExerciseRepo, ProgramRepo, WorkoutRepo,         │  │
│  │  ProgressRepo, ConfigRepo                        │  │
│  └──────────────────────┬───────────────────────────┘  │
│                         │                              │
│  ┌──────────────────────┴───────────────────────────┐  │
│  │             Local Data Sources                   │  │
│  │  ┌────────────┐ ┌────────────┐ ┌──────────────┐ │  │
│  │  │   Room     │ │  DataStore │ │  Seed Data   │ │  │
│  │  │  Database  │ │  Prefs     │ │  (JSON)      │ │  │
│  │  └────────────┘ └────────────┘ └──────────────┘ │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

---

## Tech Stack

| Component | Technology | Purpose |
|---|---|---|
| Language | Kotlin | Primary language |
| UI Framework | Jetpack Compose | Declarative UI |
| Design System | Material 3 | Theming, components |
| Database | Room | SQLite ORM |
| Preferences | DataStore | Key-value storage |
| DI | Hilt | Dependency injection |
| Navigation | Navigation Compose | Screen navigation |
| Charts | Vico | Data visualization |
| Coroutines | Kotlin Coroutines | Async operations |
| Flow | Kotlin Flow | Reactive data streams |
| JSON | Gson | TypeConverters |
| Testing | JUnit + Compose Testing | Unit & UI tests |

---

## Project Structure

```
app/
├── src/main/java/com/rhythmwave/gymflow/
│   │
│   ├── di/                            # Dependency Injection
│   │   ├── AppModule.kt               # App-wide bindings
│   │   ├── DatabaseModule.kt          # Room DB, DAOs
│   │   └── RepositoryModule.kt        # Repository bindings
│   │
│   ├── data/                          # Data Layer
│   │   ├── local/
│   │   │   ├── GymFlowDatabase.kt    # Room database
│   │   │   ├── dao/                   # Data Access Objects
│   │   │   │   ├── ExerciseDao.kt
│   │   │   │   ├── ProgramDao.kt
│   │   │   │   ├── WorkoutSessionDao.kt
│   │   │   │   ├── WorkoutSetDao.kt
│   │   │   │   ├── PersonalRecordDao.kt
│   │   │   │   └── UserConfigDao.kt
│   │   │   ├── entity/                # Room entities
│   │   │   │   ├── ExerciseEntity.kt
│   │   │   │   ├── ProgramEntity.kt
│   │   │   │   ├── GoalEntity.kt
│   │   │   │   ├── ProgramDayEntity.kt
│   │   │   │   ├── ProgramExerciseEntity.kt
│   │   │   │   ├── WorkoutSessionEntity.kt
│   │   │   │   ├── WorkoutSetEntity.kt
│   │   │   │   ├── PersonalRecordEntity.kt
│   │   │   │   └── UserConfigEntity.kt
│   │   │   ├── converter/             # TypeConverters
│   │   │   │   └── Converters.kt
│   │   │   └── seed/                  # Seed data
│   │   │       └── ExerciseSeedData.kt
│   │   ├── repository/                # Repository implementations
│   │   │   ├── ExerciseRepositoryImpl.kt
│   │   │   ├── ProgramRepositoryImpl.kt
│   │   │   ├── WorkoutRepositoryImpl.kt
│   │   │   ├── ProgressRepositoryImpl.kt
│   │   │   └── ConfigRepositoryImpl.kt
│   │   └── model/                     # Data transfer objects
│   │       └── (minimal, prefer domain models)
│   │
│   ├── domain/                        # Domain Layer
│   │   ├── model/                     # Domain models
│   │   │   ├── Exercise.kt
│   │   │   ├── Program.kt
│   │   │   ├── Goal.kt
│   │   │   ├── WorkoutSession.kt
│   │   │   ├── WorkoutSet.kt
│   │   │   ├── PersonalRecord.kt
│   │   │   ├── UserConfig.kt
│   │   │   ├── MuscleGroup.kt
│   │   │   ├── GoalProfile.kt
│   │   │   ├── GoalTag.kt
│   │   │   └── Enums.kt
│   │   ├── repository/                # Repository interfaces
│   │   │   ├── ExerciseRepository.kt
│   │   │   ├── ProgramRepository.kt
│   │   │   ├── WorkoutRepository.kt
│   │   │   ├── ProgressRepository.kt
│   │   │   └── ConfigRepository.kt
│   │   └── usecase/                   # Business logic
│   │       ├── program/
│   │       │   ├── GenerateProgramUseCase.kt
│   │       │   ├── GetActiveProgramUseCase.kt
│   │       │   └── UpdateProgramUseCase.kt
│   │       ├── workout/
│   │       │   ├── StartWorkoutUseCase.kt
│   │       │   ├── LogSetUseCase.kt
│   │       │   ├── CompleteWorkoutUseCase.kt
│   │       │   └── SwapExerciseUseCase.kt
│   │       ├── adaptation/
│   │       │   ├── ProcessCheckInUseCase.kt
│   │       │   ├── DailyAdaptationUseCase.kt
│   │       │   ├── WeeklyAdaptationUseCase.kt
│   │       │   └── MonthlyAdaptationUseCase.kt
│   │       ├── progression/
│   │       │   ├── CalculateProgressionUseCase.kt
│   │       │   ├── EstimateOneRepMaxUseCase.kt
│   │       │   └── CheckPersonalRecordUseCase.kt
│   │       └── exercise/
│   │           ├── GetExercisesUseCase.kt
│   │           ├── FilterExercisesUseCase.kt
│   │           └── GetAlternativesUseCase.kt
│   │
│   ├── engine/                        # Training Engine (pure logic)
│   │   ├── ProgramGenerator.kt        # Main generation algorithm
│   │   ├── ExerciseScorer.kt          # Exercise selection scoring
│   │   ├── SplitSelector.kt           # Split type selection
│   │   ├── SetsRepsCalculator.kt      # Sets/reps/rest calculation
│   │   ├── ProgressionEngine.kt       # Weight progression logic
│   │   ├── DailyAdapter.kt            # Daily workout modification
│   │   ├── WeeklyAdapter.kt           # Weekly analysis
│   │   ├── MonthlyAdapter.kt          # Monthly evolution
│   │   ├── DeloadCalculator.kt        # Deload week logic
│   │   └── HeartRateZoneCalculator.kt # HR zone targets
│   │
│   ├── ui/                            # UI Layer
│   │   ├── theme/
│   │   │   ├── Color.kt               # Color palette
│   │   │   ├── Type.kt                # Typography
│   │   │   ├── Shape.kt               # Corner radius
│   │   │   └── Theme.kt               # Material 3 theme
│   │   ├── navigation/
│   │   │   ├── GymFlowNavHost.kt      # Nav graph
│   │   │   ├── Screen.kt              # Route definitions
│   │   │   └── BottomNavBar.kt        # Bottom navigation
│   │   ├── components/                # Reusable composables
│   │   │   ├── ExerciseCard.kt
│   │   │   ├── GoalChip.kt
│   │   │   ├── ProgressRing.kt
│   │   │   ├── RestTimer.kt
│   │   │   ├── SetLoggerSheet.kt
│   │   │   ├── MuscleHeatmap.kt
│   │   │   ├── VolumeChart.kt
│   │   │   ├── NumberPicker.kt
│   │   │   └── RpeSelector.kt
│   │   ├── home/
│   │   │   ├── HomeScreen.kt
│   │   │   └── HomeViewModel.kt
│   │   ├── program/
│   │   │   ├── ProgramScreen.kt
│   │   │   ├── ProgramViewModel.kt
│   │   │   └── ProgramWizard/
│   │   │       ├── WizardScreen.kt
│   │   │       └── WizardViewModel.kt
│   │   ├── workout/
│   │   │   ├── ActiveWorkoutScreen.kt
│   │   │   ├── WorkoutViewModel.kt
│   │   │   ├── WorkoutSummaryScreen.kt
│   │   │   └── CheckInDialog.kt
│   │   ├── progress/
│   │   │   ├── ProgressScreen.kt
│   │   │   └── ProgressViewModel.kt
│   │   ├── profile/
│   │   │   ├── ProfileScreen.kt
│   │   │   └── ProfileViewModel.kt
│   │   └── library/
│   │       ├── ExerciseLibraryScreen.kt
│   │       ├── ExerciseDetailSheet.kt
│   │       └── LibraryViewModel.kt
│   │
│   ├── service/                       # Background services
│   │   └── WorkoutTimerService.kt     # Foreground service for timer
│   │
│   └── GymFlowApp.kt                 # Application class (Hilt)
│
├── src/main/res/
│   ├── values/
│   │   ├── strings.xml
│   │   ├── colors.xml
│   │   └── themes.xml
│   └── raw/
│       └── exercises.json             # Seed exercise data
│
├── src/test/java/com/rhythmwave/gymflow/
│   ├── engine/
│   │   ├── ProgramGeneratorTest.kt
│   │   ├── ExerciseScorerTest.kt
│   │   ├── ProgressionEngineTest.kt
│   │   └── DailyAdapterTest.kt
│   └── usecase/
│       ├── GenerateProgramUseCaseTest.kt
│       └── LogSetUseCaseTest.kt
│
└── build.gradle.kts
```

---

## Dependency Injection (Hilt)

```kotlin
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
        .addCallback(ExerciseSeedCallback(context))
        .build()
    }

    @Provides fun provideExerciseDao(db: GymFlowDatabase) = db.exerciseDao()
    @Provides fun provideProgramDao(db: GymFlowDatabase) = db.programDao()
    @Provides fun provideWorkoutSessionDao(db: GymFlowDatabase) = db.workoutSessionDao()
    @Provides fun provideWorkoutSetDao(db: GymFlowDatabase) = db.workoutSetDao()
    @Provides fun providePersonalRecordDao(db: GymFlowDatabase) = db.personalRecordDao()
    @Provides fun provideUserConfigDao(db: GymFlowDatabase) = db.userConfigDao()
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideExerciseRepository(dao: ExerciseDao): ExerciseRepository =
        ExerciseRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideProgramRepository(
        programDao: ProgramDao,
        goalDao: GoalDao,
        dayDao: ProgramDayDao,
        exerciseDao: ProgramExerciseDao
    ): ProgramRepository = ProgramRepositoryImpl(programDao, goalDao, dayDao, exerciseDao)

    // ... other repositories
}
```

---

## Data Flow Example: Logging a Set

```
User taps "✅ Done" on ActiveWorkoutScreen
        │
        ▼
WorkoutViewModel.logSet(weight, reps, rpe)
        │
        ▼
LogSetUseCase.execute(sessionId, exerciseId, setNumber, weight, reps, rpe)
        │
        ├──→ WorkoutSetDao.insert(set)          // Save to DB
        │
        ├──→ CheckPersonalRecordUseCase         // Check if new PR
        │        │
        │        └──→ PersonalRecordDao          // Save PR if applicable
        │
        ├──→ CalculateVolumeUseCase             // Update session volume
        │        │
        │        └──→ WorkoutSessionDao.update   // Update session total
        │
        └──→ Start Rest Timer                   // Trigger UI update
                 │
                 └──→ TimerState (StateFlow)     // UI observes countdown
```

---

## Data Flow Example: Generating a Program

```
User completes Program Wizard
        │
        ▼
WizardViewModel.generateProgram(goals, preferences, equipment)
        │
        ▼
GenerateProgramUseCase.execute(params)
        │
        ├──→ SplitSelector.select(daysPerWeek, goals)     // Choose split type
        │
        ├──→ ProgramGenerator.generate()                   // Main algorithm
        │        │
        │        ├──→ ExerciseScorer.scoreAll()            // Score exercises
        │        ├──→ Select top exercises per muscle group
        │        ├──→ SetsRepsCalculator.calculate()       // Sets/reps/rest
        │        └──→ Assign to days based on split
        │
        ├──→ ProgramDao.insert(program)                    // Save program
        ├──→ GoalDao.insertAll(goals)                      // Save goals
        ├──→ ProgramDayDao.insertAll(days)                 // Save days
        └──→ ProgramExerciseDao.insertAll(exercises)       // Save exercises
                 │
                 └──→ UI navigates to ProgramScreen        // Show result
```

---

## Foreground Service (Timer)

```kotlin
@AndroidEntryPoint
class WorkoutTimerService : LifecycleService() {

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState

    private var timerJob: Job? = null

    fun startTimer(durationSeconds: Int) {
        timerJob?.cancel()
        timerJob = lifecycleScope.launch {
            var remaining = durationSeconds
            while (remaining > 0) {
                _timerState.value = TimerState(
                    isRunning = true,
                    totalSeconds = durationSeconds,
                    remainingSeconds = remaining
                )
                delay(1000)
                remaining--
            }
            // Timer complete
            _timerState.value = TimerState(isComplete = true)
            vibrate()
            playSound()
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState()
    }

    private fun createNotification(): Notification {
        // Persistent notification with timer and action buttons
        // ...
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "workout_timer"
    }
}

data class TimerState(
    val isRunning: Boolean = false,
    val isComplete: Boolean = false,
    val totalSeconds: Int = 0,
    val remainingSeconds: Int = 0
)
```

---

## Navigation

```kotlin
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Rounded.Home)
    object Programs : Screen("programs", "Programs", Icons.Rounded.FitnessCenter)
    object Workout : Screen("workout", "Workout", Icons.Rounded.SportsGymnastics)
    object Progress : Screen("progress", "Progress", Icons.Rounded.TrendingUp)
    object Profile : Screen("profile", "Profile", Icons.Rounded.Person)

    // Modal routes
    object ActiveWorkout : Screen("active_workout", "Active Workout", Icons.Rounded.SportsGymnastics)
    object WorkoutSummary : Screen("workout_summary/{sessionId}", "Summary", Icons.Rounded.CheckCircle)
    object ProgramWizard : Screen("program_wizard", "New Program", Icons.Rounded.AddCircle)
    object ExerciseDetail : Screen("exercise/{exerciseId}", "Exercise", Icons.Rounded.Info)
}

@Composable
fun GymFlowNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Programs.route) { ProgramScreen(navController) }
        composable(Screen.Progress.route) { ProgressScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }

        // Full screen (no bottom nav)
        composable(Screen.ActiveWorkout.route) { ActiveWorkoutScreen(navController) }
        composable(Screen.WorkoutSummary.route) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId")?.toLongOrNull()
            WorkoutSummaryScreen(sessionId, navController)
        }
        composable(Screen.ProgramWizard.route) { ProgramWizardScreen(navController) }
    }
}
```

---

## Testing Strategy

### Unit Tests (Engine Logic)
- ProgramGenerator: correct split selection, exercise scoring, volume targets
- ProgressionEngine: weight increases, deload triggers, 1RM calculations
- DailyAdapter: check-in processing, workout modifications
- SetsRepsCalculator: correct sets/reps for each goal

### Integration Tests (DAOs)
- CRUD operations for all entities
- Cascade deletes (program → days → exercises)
- Query correctness (filters, joins)

### UI Tests (Compose)
- Screen rendering with mock data
- Navigation flows
- Button interactions
- Bottom sheet behavior

---

## Build Configuration

```kotlin
// build.gradle.kts (app)
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.rhythmwave.gymflow"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rhythmwave.gymflow"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2025.01.00")
    implementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    ksp("com.google.dagger:hilt-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Charts
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-beta.2")

    // Gson
    implementation("com.google.code.gson:gson:2.11.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
```
