# Product Requirements Document (PRD)

## GymFlow — AI-Powered Training Tracker

**Version:** 1.0
**Date:** 2026-06-29
**Author:** Rhythmwave

---

## 1. Problem Statement

Commercial gym apps (Strong, JEFIT, Fitbod) lock essential features behind subscriptions ($60-120/year). As a developer with all the resources needed, building a personalized training tracker is both cost-effective and a portfolio piece.

## 2. Target User

**Primary:** Rhythmwave (personal use)
**Secondary (future):** Anyone who wants a free, customizable gym tracker

### User Profile
- Intermediate lifter
- Health-focused (not bodybuilding competition)
- Has gym access with barbells, dumbbells, cables, pull-up bar, kettlebells
- Trains 4 days/week
- Values posture, core strength, cardiovascular health

## 3. Goals (User's 7 Requirements)

| # | Goal | Training Response | Priority |
|---|---|---|---|
| 1 | Fix posture | Posterior chain strengthening, anterior chain stretching | High |
| 2 | Better blood flow | High-rep circuits, supersets, minimal rest | Medium |
| 3 | Better heart rate | Zone 2 cardio + HIIT | Medium |
| 4 | Burst + endurance | Explosive power + high-rep sets | High |
| 5 | Abdomen shape (lower + upper) | Targeted core + body fat reduction support | High |
| 6 | Core strength for spine | Anti-rotation, anti-extension, spinal erectors | High |
| 7 | Sex drive | Compound lifts, HIIT, recovery optimization | Medium |

## 4. Core Features

### 4.1 Program Generation
- User selects goals (toggle ON/OFF with priority 1-5)
- User sets preferences (days/week, duration, equipment, experience level)
- Engine generates weekly split with exercises, sets, reps, rest times
- Each exercise tagged with goal relevance

### 4.2 Daily Check-in
- Morning assessment: sleep quality, energy level, soreness, available time
- Engine recommends: PUSH (full intensity), LIGHT (reduced), or REST
- Adjusts today's workout based on input

### 4.3 Active Workout Tracking
- Log sets (weight, reps, RPE, notes)
- Auto-start rest timer after each set
- Exercise swap with alternatives
- Add/remove exercises mid-workout
- Persistent notification with timer during workout

### 4.4 Progress Analytics
- Volume chart (weight × reps per week)
- Personal records tracking
- Estimated 1RM (Epley formula)
- Muscle group heatmap
- Streak counter
- Goal completion rings per week

### 4.5 Exercise Library
- 100+ exercises across all muscle groups
- Each exercise: name, muscle group, equipment, difficulty, compound/isolation, instructions, alternatives
- Goal tags: POSTURE, CARDIO, EXPLOSIVE, CORE_SPINE, DRIVE, BLOOD_FLOW, FLEXIBILITY
- Filter by muscle group, equipment, goal, difficulty

### 4.6 Adaptive Engine

#### Daily Adaptation
- Input: sleep quality, energy, soreness, available time
- Output: modified workout (reduced weight, swapped exercises, shortened finisher)

#### Weekly Adaptation
- Analyze: volume trends, goal coverage, progression rate
- Output: weight increases, exercise additions, deload suggestions

#### Monthly Adaptation
- Analyze: 4-week trends, PR frequency, goal completion rates
- Output: program evolution (phase 2 recommendations, volume/intensity adjustments)

### 4.7 Progression Algorithm
```
IF completed all sets at target reps for 2 consecutive sessions:
  THEN increase weight by smallest increment (2.5kg upper / 5kg lower)

IF failed to hit target reps for 2 consecutive sessions:
  THEN deload 10% and rebuild

IF RPE consistently < 6 for a week:
  THEN suggest increasing volume or intensity
```

## 5. Non-Features (Explicitly Out of Scope)

- ❌ Social features (sharing, leaderboards)
- ❌ Nutrition tracking
- ❌ Backend/server component
- ❌ User accounts / authentication
- ❌ Wearable integration (future)
- ❌ AI/ML model inference (future)
- ❌ iOS support

## 6. Success Metrics

| Metric | Target |
|---|---|
| App launches/week | ≥ 4 (match training frequency) |
| Workout completion rate | ≥ 90% |
| Program adherence (4 weeks) | ≥ 80% |
| PR frequency | ≥ 1 per week |
| Time to log a set | ≤ 3 seconds |

## 7. Constraints

- **Offline-first**: No internet required
- **Local storage**: All data on device (Room/SQLite)
- **No subscriptions**: Free forever
- **Battery efficient**: No background processes except rest timer
- **Privacy**: No data leaves the device

## 8. Risks

| Risk | Mitigation |
|---|---|
| Exercise database curation takes time | Start with 50 core exercises, expand over time |
| Progression algorithm too aggressive | Conservative defaults, user-adjustable |
| Overtraining from poor program design | Mandatory deload weeks, volume caps |
| User motivation drops | Streak tracking, PR celebrations, progress visibility |
