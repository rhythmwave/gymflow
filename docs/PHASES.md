# Build Phases

## GymFlow — Development Roadmap

**Version:** 1.0
**Start Date:** TBD
**Estimated Total:** 2-3 weeks (part-time)

---

## Phase 1: Foundation (2-3 days)

### Goal
Project scaffold, database, exercise data, basic navigation.

### Tasks
- [ ] Create Android project with Kotlin + Compose
- [ ] Configure build.gradle.kts (Hilt, Room, Navigation, Vico)
- [ ] Set up Material 3 dark theme (colors, typography, shapes)
- [ ] Implement Room database with all entities
- [ ] Create all DAOs
- [ ] Create all TypeConverters
- [ ] Seed exercise database (50+ exercises with tags)
- [ ] Set up Hilt dependency injection
- [ ] Create bottom navigation shell (5 tabs)
- [ ] Create placeholder screens for all tabs
- [ ] Set up Navigation Compose graph

### Deliverable
App launches, shows 5 tabs, exercises load from database.

### Exercise Seed Data (Minimum 50)
```
Chest: 8 exercises (bench, incline, flyes, push-ups, dips, etc.)
Back: 8 exercises (deadlift, rows, pull-ups, lat pulldown, etc.)
Shoulders: 7 exercises (OHP, lateral raises, face pulls, etc.)
Legs: 10 exercises (squat, lunges, leg press, RDL, hip thrusts, etc.)
Arms: 6 exercises (curls, tricep extensions, hammer curls, etc.)
Core: 8 exercises (planks, dead bugs, bird dogs, leg raises, etc.)
Cardio: 5 exercises (rowing, cycling, jump rope, running, etc.)
```

Each exercise includes:
- id, name, muscleGroup, equipment, difficulty, isCompound
- instructions (step-by-step form guide)
- tags (GoalTag set)
- alternatives (list of exercise IDs)

---

## Phase 2: Program Engine (3-4 days)

### Goal
Program generation algorithm and program creation wizard.

### Tasks
- [ ] Implement `SplitSelector` — split type based on days/week and goals
- [ ] Implement `ExerciseScorer` — scoring algorithm for exercise selection
- [ ] Implement `SetsRepsCalculator` — sets/reps/rest per goal
- [ ] Implement `ProgramGenerator` — main generation orchestrator
- [ ] Create Program Wizard UI (5-step stepper)
  - [ ] Step 1: Goal selection (toggle + priority stars)
  - [ ] Step 2: Schedule (days/week, duration, rest days)
  - [ ] Step 3: Equipment selection
  - [ ] Step 4: Experience level
  - [ ] Step 5: Review & generate
- [ ] Create Program screen (active program view, weekly schedule)
- [ ] Wire up `GenerateProgramUseCase`
- [ ] Add goal persistence (Room)
- [ ] Test: Generate program with 7 goals, verify exercise distribution

### Deliverable
User can create a program via wizard, see weekly schedule with exercises.

---

## Phase 3: Active Workout (3-4 days)

### Goal
Workout tracking with set logging, rest timer, exercise swapping.

### Tasks
- [ ] Create Active Workout screen layout
- [ ] Implement set logging (weight, reps, RPE, notes)
- [ ] Create Set Logger bottom sheet
- [ ] Implement rest timer (foreground service)
  - [ ] Auto-start after set completion
  - [ ] Persistent notification
  - [ ] Haptic feedback on complete
  - [ ] +30s / skip controls
- [ ] Create Exercise Detail bottom sheet (form guide)
- [ ] Implement exercise swap (alternative selection)
- [ ] Implement exercise skip
- [ ] Create Workout Complete summary screen
- [ ] Wire up `StartWorkoutUseCase`, `LogSetUseCase`, `CompleteWorkoutUseCase`
- [ ] Track volume (weight × reps) per session
- [ ] Add workout session persistence
- [ ] Test: Complete full workout, verify data saved correctly

### Deliverable
User can start workout, log sets, see rest timer, finish and view summary.

---

## Phase 4: Progress Dashboard (2-3 days)

### Goal
Charts, PR tracking, goal progress visualization.

### Tasks
- [ ] Create Progress screen layout
- [ ] Implement volume chart (Vico — weekly bar chart)
- [ ] Implement muscle group heatmap (custom composable)
- [ ] Implement PR tracking
  - [ ] Auto-detect PRs on set completion
  - [ ] Confetti celebration animation
  - [ ] PR history list
- [ ] Implement 1RM estimation (Epley formula)
- [ ] Create estimated 1RM chart
- [ ] Implement goal completion rings (weekly)
- [ ] Add time range filters (1W, 1M, 3M, 6M, 1Y, ALL)
- [ ] Wire up `ProgressRepository`, `CheckPersonalRecordUseCase`
- [ ] Test: Log multiple workouts, verify charts and PR detection

### Deliverable
User can view volume trends, muscle heatmap, PRs, and goal progress.

---

## Phase 5: Polish & Features (2-3 days)

### Goal
Profile, exercise library, daily check-in, settings.

### Tasks
- [ ] Create Profile screen
  - [ ] User stats (weight, height, age)
  - [ ] Goal display and editing
  - [ ] Preferences display and editing
  - [ ] Equipment management
  - [ ] Export to CSV
  - [ ] Reset data
- [ ] Create Exercise Library screen
  - [ ] List all exercises grouped by muscle
  - [ ] Search by name
  - [ ] Filter by muscle group, goal tag, equipment, difficulty
  - [ ] Exercise detail bottom sheet
- [ ] Implement Morning Check-in dialog
  - [ ] Sleep quality, energy, soreness, available time
  - [ ] PUSH / LIGHT / REST recommendation
- [ ] Implement Daily Adapter
  - [ ] Light day: reduced weight, fewer sets, skip finishers
  - [ ] Rest day: show rest day content
- [ ] Add Home screen state management
  - [ ] Today's workout preview
  - [ ] Weekly goal rings
  - [ ] Quick stats
  - [ ] Streak counter
- [ ] Polish animations and transitions
- [ ] Add haptic feedback throughout

### Deliverable
Complete app with all screens functional, check-in system working.

---

## Phase 6: Testing & Release (1-2 days)

### Goal
Testing, bug fixes, APK build, release.

### Tasks
- [ ] Unit tests for engine logic
  - [ ] ProgramGenerator
  - [ ] ExerciseScorer
  - [ ] ProgressionEngine
  - [ ] DailyAdapter
- [ ] Integration tests for DAOs
- [ ] UI smoke test (all screens)
- [ ] Test on physical device
- [ ] Fix bugs found during testing
- [ ] Optimize performance (lazy lists, DB queries)
- [ ] Add app icon
- [ ] Add splash screen
- [ ] Configure ProGuard/R8
- [ ] Build release APK/AAB
- [ ] Create GitHub release
- [ ] Document README with screenshots

### Deliverable
Release APK ready for sideloading.

---

## Future Phases (Post v1.0)

### Phase 7: Advanced Features
- [ ] Bodyweight tracking (daily weight log, trend chart)
- [ ] Workout templates (save custom workouts)
- [ ] Share workout as image
- [ ] Backup/restore to file
- [ ] Exercise video links integration

### Phase 8: Intelligence
- [ ] Adaptive rest timer (based on RPE and exercise type)
- [ ] Plateau detection and exercise rotation suggestions
- [ ] Fatigue accumulation tracking
- [ ] Optimal training time suggestion (based on performance history)

### Phase 9: Wearable Integration
- [ ] Heart rate from Wear OS
- [ ] Rep counting from accelerometer
- [ ] Rest detection from motion sensors

### Phase 10: Social & Cloud
- [ ] Optional cloud sync (Firebase/supabase)
- [ ] Workout sharing
- [ ] Program templates marketplace

---

## Time Estimates Summary

| Phase | Duration | Cumulative |
|---|---|---|
| Phase 1: Foundation | 2-3 days | 2-3 days |
| Phase 2: Program Engine | 3-4 days | 5-7 days |
| Phase 3: Active Workout | 3-4 days | 8-11 days |
| Phase 4: Progress Dashboard | 2-3 days | 10-14 days |
| Phase 5: Polish & Features | 2-3 days | 12-17 days |
| Phase 6: Testing & Release | 1-2 days | 13-19 days |

**Total: ~2-3 weeks part-time**

---

## Definition of Done

Each phase is complete when:
1. All tasks checked off
2. No compile errors
3. Manual testing on device
4. Code committed to GitHub
5. README updated with progress
