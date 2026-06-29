# Screen Designs

## GymFlow — All Screens & Navigation

**Version:** 1.0

---

## Navigation Structure

```
Bottom Navigation (5 tabs):
├── 🏠 Home
├── 📋 Programs
├── 💪 Workout (Center, prominent)
├── 📊 Progress
└── 👤 Profile

Modal Screens:
├── Active Workout (full screen, replaces nav)
├── Exercise Detail (bottom sheet)
├── Set Logger (bottom sheet)
├── Program Wizard (stepper)
├── Morning Check-in (dialog/bottom sheet)
└── Workout Summary (full screen)
```

---

## 1. Home Screen

### Purpose
Daily entry point — shows today's plan and quick-start options.

### Layout
```
┌─────────────────────────────────────────────┐
│  ☀️ Good evening, Rhythmwave         ⚙️    │
│                                              │
│  ┌─────────────────────────────────────────┐ │
│  │  🟢 How are you today?                  │ │
│  │                                          │ │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐│ │
│  │  │ 💪 PUSH  │ │ 🌤 LIGHT │ │ 😴 REST  ││ │
│  │  │ Full go  │ │ Easy day │ │ Recovery ││ │
│  │  └──────────┘ └──────────┘ └──────────┘│ │
│  └─────────────────────────────────────────┘ │
│                                              │
│  ┌─ Today's Plan ─────────────────────────┐  │
│  │  Day 2 — Lower + Explosive              │  │
│  │  ⏱️ ~55 min  •  🏋️ 7 exercises          │  │
│  │                                          │  │
│  │  1. Barbell Squat          4×6          │  │
│  │  2. Romanian Deadlift      3×10         │  │
│  │  3. Box Jumps              4×5          │  │
│  │  4. Hip Thrusts            3×12         │  │
│  │  5. Walking Lunges         3×12/leg     │  │
│  │  6. Calf Raises            4×15         │  │
│  │  7. HIIT Bike              8×30s        │  │
│  │                                          │  │
│  │  [✏️ Edit]  [▶️ Start Workout]           │  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  ┌─ Weekly Goals ──────────────────────────┐  │
│  │  🫀 Cardio    ████████░░  2/3           │  │
│  │  🧘 Posture   ██████░░░░  2/3           │  │
│  │  💪 Strength  ████████░░  2/3           │  │
│  │  🎯 Core      ████████░░  2/3           │  │
│  │  ⚡ Explosive  ██████████  3/3  ✅       │  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  ┌─ Quick Stats ───────────────────────────┐  │
│  │  🔥 12 day streak   📊 45.2t volume     │  │
│  │  😴 7.1h avg sleep  💪 2 PRs this week  │  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  ┌──────────────────────────────────────────┐  │
│  │  🏠    📋    💪    📊    👤              │  │
│  └──────────────────────────────────────────┘  │
└──────────────────────────────────────────────┘
```

### Interactions
- **PUSH button** → Start today's workout at full intensity
- **LIGHT button** → Load reduced version (−20% weight, fewer sets, shorter finishers)
- **REST button** → Show rest day activities (stretching routine, walk suggestions)
- **Exercise row tap** → Expand to show sets/reps/equipment preview
- **Swipe left on exercise** → Quick swap for alternative
- **Start Workout** → Transition to Active Workout screen
- **Edit** → Opens workout editor (add/remove/reorder exercises)

### States
- **No active program** → Show "Create Your First Program" CTA
- **Rest day** → Show rest day content instead of workout
- **Workout already completed today** → Show summary + "Log Another" option

---

## 2. Active Workout Screen

### Purpose
Primary workout interface — used during exercise with minimal interaction needed.

### Layout
```
┌─────────────────────────────────────────────┐
│  ← Exit    Day 2 — Lower     ⏱️ 34:12       │
│                                              │
│  ┌─ Exercise 3 of 7 ──────────────────────┐  │
│  │                                          │  │
│  │  ⚡ BOX JUMPS                            │  │
│  │  4 sets × 5 reps  •  Explosive           │  │
│  │                                          │  │
│  │  ┌──────────────────────────────────┐   │  │
│  │  │  Set 1  │  ✅ Done  │  —         │   │  │
│  │  │  Set 2  │  ✅ Done  │  —         │   │  │
│  │  │  Set 3  │  🔄 NOW   │  [✅ Done] │   │  │
│  │  │  Set 4  │  ⏳ Next  │  —         │   │  │
│  │  └──────────────────────────────────┘   │  │
│  │                                          │  │
│  │  [ℹ️ Form Guide]  [🔄 Swap]  [⏭️ Skip]  │  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  ┌──────────────────────────────────────────┐  │
│  │  ⏱️ REST TIMER                           │  │
│  │        ┌─────────────┐                   │  │
│  │        │    1:23     │                   │  │
│  │        └─────────────┘                   │  │
│  │  [▶️ +30s]  [⏸️ Pause]  [⏭️ Skip Rest]   │  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  ┌─ Coming Up ─────────────────────────────┐  │
│  │  4. Hip Thrusts    3×12                  │  │
│  │  5. Lunges         3×12/leg              │  │
│  │  6. Calf Raises    4×15                  │  │
│  │  7. HIIT Bike      8×30s                 │  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  [➕ Add Exercise]        [🏁 Finish Workout]  │
└─────────────────────────────────────────────┘
```

### Interactions
- **✅ Done button** (56dp+) → Mark set complete, auto-start rest timer
- **Rest timer** → Auto-counts down, haptic on zero, configurable duration
- **+30s / Skip Rest** → Adjust timer on the fly
- **Form Guide** → Bottom sheet with step-by-step instructions
- **Swap** → Show alternatives ranked by equipment match and goal relevance
- **Skip** → Skip this exercise entirely (with confirmation)
- **Coming Up** → Swipe up to see full list, tap to jump to exercise
- **Finish Workout** → Confirmation dialog → Summary screen

### Key UX Rules
- ✅ Done button is **minimum 56dp height** (easy tap mid-set)
- Timer uses **haptic feedback** when rest completes
- Screen stays **awake** during active workout (FLAG_KEEP_SCREEN_ON)
- **No scrolling needed** for primary actions (everything above fold)
- Auto-advances to next exercise when all sets done
- **Back button** shows "Exit workout?" confirmation

### Background Behavior
- Foreground service keeps timer running if app is backgrounded
- Persistent notification shows: current exercise, sets remaining, timer
- Notification action buttons: "Done" (mark set), "Pause Timer"

---

## 3. Set Logger (Bottom Sheet)

### Purpose
Input weight, reps, and optional RPE after each set.

### Layout
```
┌─────────────────────────────────────────────┐
│  ┌──────────────────────────────────────────┐│
│  │  Log Set 3 — Box Jumps                   ││
│  │                                          ││
│  │  WEIGHT                                  ││
│  │     [-]  [  0  kg  ]  [+]               ││
│  │          (bodyweight)                    ││
│  │                                          ││
│  │  REPS                                    ││
│  │     [-]  [  5  ]  [+]                   ││
│  │                                          ││
│  │  RPE (optional):                         ││
│  │  [1] [2] [3] [4] [5] [6] [7] [8] [9] [10]││
│  │                                          ││
│  │  Notes:                                  ││
│  │  [________________________]              ││
│  │                                          ││
│  │     [✅ LOG SET & REST]                  ││
│  │                                          ││
│  │     [Log without rest timer]             ││
│  └──────────────────────────────────────────┘│
└─────────────────────────────────────────────┘
```

### Interactions
- **Weight +/-** → Increment by 2.5kg (configurable)
- **Reps +/-** → Increment by 1
- **Tap number** → Direct keyboard input
- **RPE chips** → Tap to select (optional, skippable)
- **Notes** → Free text for observations
- **LOG SET & REST** → Save + start rest timer
- **Log without rest timer** → Save only (for supersets)

### Smart Defaults
- Pre-fills with last set's weight/reps for this exercise
- Pre-fills with last session's data if first set today
- Remembers RPE if user typically logs it

---

## 4. Workout Complete — Summary

### Purpose
Celebrate completion, show stats, capture feedback.

### Layout
```
┌─────────────────────────────────────────────┐
│                                              │
│            🎉 WORKOUT COMPLETE!              │
│                                              │
│  ┌──────────────────────────────────────────┐│
│  │  Day 2 — Lower + Explosive               ││
│  │  ⏱️ Duration:  52:34                     ││
│  │  🏋️ Volume:   12,450 kg                  ││
│  │  📊 Sets:     24/24 completed            ││
│  │  💪 Est. Calories: 380                   ││
│  │                                          ││
│  │  ── Personal Records ──                  ││
│  │  🏆 Squat: 80kg × 6  (NEW PR!)          ││
│  │  🏆 Hip Thrust: 100kg × 12 (NEW PR!)    ││
│  │                                          ││
│  │  ── Goal Progress ──                     ││
│  │  ⚡ Explosive: ██████████ ✅             ││
│  │  💪 Strength:  ████████░░                ││
│  │  🫀 Cardio:    ████████░░                ││
│  └──────────────────────────────────────────┘│
│                                              │
│  ┌──────────────────────────────────────────┐│
│  │  How was this workout?                   ││
│  │  [😊 Too Easy] [👍 Just Right] [😰 Hard] ││
│  └──────────────────────────────────────────┘│
│                                              │
│  [🏠 Home]     [📊 Progress]     [📤 Share]  │
└─────────────────────────────────────────────┘
```

### Interactions
- **PR cards** → Confetti animation + haptic celebration
- **Feedback buttons** → Adjusts future difficulty (too easy → increase, hard → maintain/decrease)
- **Share** → Generate workout summary image (future)
- **Home** → Return to home screen
- **Progress** → Jump to progress charts

---

## 5. Progress Screen

### Purpose
Visual analytics of training history and goal progress.

### Layout
```
┌─────────────────────────────────────────────┐
│  📊 Progress                          ⚙️    │
│                                              │
│  [1W] [1M] [3M] [6M] [1Y] [ALL]            │
│                                              │
│  ┌─ Volume (Total kg lifted) ──────────────┐  │
│  │   50k│                    ╭──╮           │  │
│  │      │              ╭────╯  │           │  │
│  │   30k│         ╭────╯       ╰──╮        │  │
│  │      │    ╭────╯               │        │  │
│  │   10k│────╯                    ╰──      │  │
│  │      └──────────────────────────────     │  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  ┌─ Muscle Group Heatmap ──────────────────┐  │
│  │         ┌───┐                            │  │
│  │         │ ● │ Shoulders                  │  │
│  │      ┌──┤   ├──┐                         │  │
│  │      │● │   │ ●│ Chest                   │  │
│  │      └──┬───┬──┘                         │  │
│  │       ┌─┴───┴─┐                          │  │
│  │       │●     ●│ Legs                     │  │
│  │       └───────┘                          │  │
│  │  ● Trained  ○ Not yet                    │  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  ┌─ Personal Records ──────────────────────┐  │
│  │  Squat        80kg × 6    🏆 2 days ago  │  │
│  │  Deadlift     90kg × 5    🏆 1 week ago  │  │
│  │  Bench        55kg × 8    🏆 3 days ago  │  │
│  │  Plank        70s          🏆 5 days ago  │  │
│  │  [View All PRs]                          │  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  ┌─ Estimated 1RM ─────────────────────────┐  │
│  │  Squat:     95kg   ████████████░░        │  │
│  │  Deadlift:  105kg  ██████████████░       │  │
│  │  Bench:     68kg   ████████░░░░░░        │  │
│  │  OHP:       45kg   ██████░░░░░░░░        │  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  ┌──────────────────────────────────────────┐  │
│  │  🏠    📋    💪    📊    👤              │  │
│  └──────────────────────────────────────────┘  │
└──────────────────────────────────────────────┘
```

### Interactions
- **Time range tabs** → Filter all charts to selected period
- **Volume chart** → Tap data point to see that day's workout details
- **Heatmap body part** → Tap to see exercises for that muscle group
- **PR row** → Tap to see PR history chart for that exercise
- **View All PRs** → Full PR list with search/filter

---

## 6. Programs Screen

### Purpose
Manage training programs, view schedule, create new programs.

### Layout
```
┌─────────────────────────────────────────────┐
│  📋 My Programs                       ⚙️    │
│                                              │
│  ┌─ Active Program ────────────────────────┐  │
│  │  🟢 FULL FUNCTIONAL                      │  │
│  │  4 days/week • 60 min sessions           │  │
│  │  Week 4 of 4  ████████████████░░░░ 75%   │  │
│  │  [View Schedule]  [Edit Program]         │  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  ┌─ Weekly Schedule ───────────────────────┐  │
│  │  MON    TUE    WED    THU    FRI    SAT  │  │
│  │  ┌──┐   ┌──┐   ┌──┐   ┌──┐   ┌──┐   ┌──┐│  │
│  │  │UP│   │LO│   │──│   │CO│   │FN│   │──││  │
│  │  │PE│   │WE│   │RE│   │RE│   │CT│   │RE││  │
│  │  │R │   │R │   │ST│   │  │   │  │   │ST││  │
│  │  └──┘   └──┘   └──┘   └──┘   └──┘   └──┘│  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  ┌─ Create New ────────────────────────────┐  │
│  │  [🆕 Quick Start — AI Generated]         │  │
│  │  [📋 From Template]                      │  │
│  │  [✏️ Custom Program]                     │  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  ┌─ Past Programs ─────────────────────────┐  │
│  │  ○ Beginner Strength (Jan-Mar 2026)      │  │
│  │  ○ Fat Loss Phase (Nov-Dec 2025)         │  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  ┌──────────────────────────────────────────┐  │
│  │  🏠    📋    💪    📊    👤              │  │
│  └──────────────────────────────────────────┘  │
└──────────────────────────────────────────────┘
```

### Interactions
- **Active program card** → Expand to show full week view
- **Schedule day tap** → Show that day's exercises in a bottom sheet
- **Quick Start** → Program wizard (goals → preferences → generate)
- **From Template** → Pre-built program templates
- **Custom** → Manual program builder
- **Past programs** → View historical program data

---

## 7. Exercise Library Screen

### Purpose
Browse, search, and filter the exercise database.

### Layout
```
┌─────────────────────────────────────────────┐
│  📖 Exercise Library                  ⚙️    │
│                                              │
│  [🔍 Search exercises...]                    │
│                                              │
│  Muscle: [All] [Chest] [Back] [Shoulders]   │
│          [Legs] [Arms] [Core] [Cardio]      │
│                                              │
│  Goal:   [Posture] [Cardio] [Explosive]     │
│          [Core] [Drive] [Blood Flow]        │
│                                              │
│  ┌─ Back (12 exercises) ───────────────────┐  │
│  │                                          │  │
│  │  Barbell Row              ⚙️ 🔧          │  │
│  │  Compound • Intermediate                 │  │
│  │  [Posture] [Drive]                       │  │
│  │                                          │  │
│  │  Pull-ups                 ⚙️ 🔧          │  │
│  │  Compound • Intermediate                 │  │
│  │  [Posture] [Core]                        │  │
│  │                                          │  │
│  │  Seated Cable Row         ⚙️ 🔧          │  │
│  │  Isolation • Beginner                    │  │
│  │  [Posture]                               │  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  ┌──────────────────────────────────────────┐  │
│  │  🏠    📋    💪    📊    👤              │  │
│  └──────────────────────────────────────────┘  │
└─────────────────────────────────────────────┘
```

### Interactions
- **Search** → Real-time filter by name
- **Muscle filter chips** → Toggle muscle group filters
- **Goal filter chips** → Toggle goal tag filters
- **Exercise card tap** → Open Exercise Detail bottom sheet
- **⚙️ icon** → Shows equipment needed
- **🔧 icon** → Shows alternatives available

---

## 8. Exercise Detail (Bottom Sheet)

### Purpose
Full exercise information, form guide, and alternatives.

### Layout
```
┌─────────────────────────────────────────────┐
│  ┌──────────────────────────────────────────┐│
│  │  BARBELL ROW                             ││
│  │  Back • Compound • Intermediate          ││
│  │                                          ││
│  │  Tags: 🧘 Posture  💪 Drive              ││
│  │  Equipment: Barbell                      ││
│  │                                          ││
│  │  ── How To Perform ──                    ││
│  │  1. Stand with feet hip-width apart      ││
│  │  2. Hinge at hips, keep back flat        ││
│  │  3. Grip bar slightly wider than knees   ││
│  │  4. Pull bar to lower chest              ││
│  │  5. Squeeze shoulder blades together     ││
│  │  6. Lower with control                   ││
│  │                                          ││
│  │  ── Common Mistakes ──                   ││
│  │  ⚠️ Rounding the back                    ││
│  │  ⚠️ Using momentum (jerking)             ││
│  │                                          ││
│  │  ── Alternatives ──                      ││
│  │  • Seated Cable Row                      ││
│  │  • Dumbbell Row                          ││
│  │  • T-Bar Row                             ││
│  │                                          ││
│  │  [▶️ Watch Video]  [➕ Add to Workout]    ││
│  └──────────────────────────────────────────┘│
└─────────────────────────────────────────────┘
```

---

## 9. Profile Screen

### Purpose
User settings, goals, preferences, equipment, data management.

### Layout
```
┌─────────────────────────────────────────────┐
│  👤 Profile                         ⚙️    │
│                                              │
│  ┌──────────────────────────────────────────┐│
│  │     🏋️  Rhythmwave                       ││
│  │     Training since Jan 2026              ││
│  │                                          ││
│  │  Weight: 75kg • Height: 175cm            ││
│  │  Level: Intermediate                     ││
│  │  [Edit Profile]                          ││
│  └──────────────────────────────────────────┘│
│                                              │
│  ┌─ My Goals ──────────────────────────────┐  │
│  │  ☑ 🧘 Posture           ★★★★★           │  │
│  │  ☑ 🫀 Blood flow        ★★★★☆           │  │
│  │  ☑ ❤️ Heart rate         ★★★☆☆           │  │
│  │  ☑ ⚡ Burst + endurance ★★★★☆           │  │
│  │  ☑ 🎯 Abdomen shape     ★★★★★           │  │
│  │  ☑ 🦴 Core/spine        ★★★★★           │  │
│  │  ☑ 💕 Sex drive          ★★★☆☆           │  │
│  │  [Edit Goals]                            │  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  ┌─ Preferences ───────────────────────────┐  │
│  │  Days: 4/week • Duration: 60 min         │  │
│  │  Units: kg • Rest: Wed, Sat, Sun         │  │
│  │  [Edit Preferences]                      │  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  ┌─ Equipment ─────────────────────────────┐  │
│  │  ☑ Barbell ☑ Dumbbell ☑ Cable            │  │
│  │  ☑ Pull-up ☑ Bench    ☑ Kettlebell       │  │
│  │  [Edit Equipment]                        │  │
│  └──────────────────────────────────────────┘  │
│                                              │
│  [📤 Export CSV]  [🗑️ Reset Data]            │
│                                              │
│  ┌──────────────────────────────────────────┐  │
│  │  🏠    📋    💪    📊    👤              │  │
│  └──────────────────────────────────────────┘  │
└──────────────────────────────────────────────┘
```

---

## 10. Program Wizard (Stepper)

### Purpose
Guided program creation with goal selection and preferences.

### Steps
```
Step 1/5: Select Goals
┌─────────────────────────────────────────────┐
│  What do you want to achieve?               │
│                                              │
│  ☑ 🧘 Fix posture              [★★★★★]     │
│  ☑ 🫀 Better blood flow        [★★★★☆]     │
│  ☐ ❤️ Better heart rate         [★★★☆☆]     │
│  ☑ ⚡ Burst + endurance        [★★★★☆]     │
│  ☑ 🎯 Abdomen shape            [★★★★★]     │
│  ☑ 🦴 Core/spine strength      [★★★★★]     │
│  ☐ 💕 Sex drive                 [★★★☆☆]     │
│                                              │
│  Priority: Drag to reorder, tap stars       │
│                              [Next →]        │
└─────────────────────────────────────────────┘

Step 2/5: Schedule
┌─────────────────────────────────────────────┐
│  When can you train?                         │
│                                              │
│  Days per week:  [2] [3] [4●] [5] [6]       │
│                                              │
│  Session duration:                           │
│  [30 min] [45 min] [60 min●] [90 min]       │
│                                              │
│  Rest days:                                  │
│  [Mon] [Tue] [Wed●] [Thu] [Fri] [Sat●] [Sun●]│
│                              [Next →]        │
└─────────────────────────────────────────────┘

Step 3/5: Equipment
┌─────────────────────────────────────────────┐
│  What equipment do you have?                 │
│                                              │
│  ☑ Barbell      ☑ Dumbbell    ☑ Cable        │
│  ☑ Pull-up bar  ☑ Bench       ☑ Kettlebell   │
│  ☐ Smith machine ☐ Leg press  ☐ Lat pulldown │
│  ☐ Chest press  ☐ Leg curl    ☐ Leg ext      │
│  ☐ Bands        ☐ Rowing mch  ☐ Bike         │
│                                              │
│  [Select All Gym]  [Select Home Only]        │
│                              [Next →]        │
└─────────────────────────────────────────────┘

Step 4/5: Experience
┌─────────────────────────────────────────────┐
│  What's your experience level?               │
│                                              │
│  ┌─────────────────────────────────────────┐ │
│  │  🟢 Beginner                            │ │
│  │  < 6 months of consistent training      │ │
│  │  Learning basic compound movements      │ │
│  └─────────────────────────────────────────┘ │
│  ┌─────────────────────────────────────────┐ │
│  │  🔵 Intermediate  ← SELECTED           │ │
│  │  6 months - 2 years of training         │ │
│  │  Comfortable with main lifts            │ │
│  └─────────────────────────────────────────┘ │
│  ┌─────────────────────────────────────────┐ │
│  │  🟣 Advanced                            │ │
│  │  2+ years of consistent training        │ │
│  │  Programming own routines               │ │
│  └─────────────────────────────────────────┘ │
│                              [Next →]        │
└─────────────────────────────────────────────┘

Step 5/5: Review & Generate
┌─────────────────────────────────────────────┐
│  Your Program Summary                        │
│                                              │
│  Goals: Posture, Blood Flow, Burst,          │
│         Abdomen, Core, Drive                 │
│  Schedule: 4 days/week, 60 min               │
│  Rest days: Wed, Sat, Sun                    │
│  Equipment: Full gym setup                   │
│  Level: Intermediate                         │
│                                              │
│  Recommended Split: Upper/Lower              │
│  Estimated exercises per day: 6-8            │
│  Cardio finishers: 3x/week                  │
│  Core work: 3x/week                          │
│                                              │
│  [Customize Further]  [✅ Generate Program]  │
└─────────────────────────────────────────────┘
```

---

## Morning Check-in (Dialog)

### Purpose
Daily assessment that modifies today's workout.

```
┌─────────────────────────────────────────────┐
│  ☀️ Good morning! How are you today?         │
│                                              │
│  Sleep quality:                              │
│  [😫] [😐] [🙂] [😊] [🤩]                   │
│                                              │
│  Energy level:                               │
│  [😫] [😐] [🙂] [😊] [🤩]                   │
│                                              │
│  Soreness:                                   │
│  [None] [Mild] [Moderate] [Severe]           │
│                                              │
│  Where? (if any)                             │
│  [Shoulders] [Back] [Legs] [Arms] [Other]   │
│                                              │
│  Available time today:                       │
│  [30] [45] [60●] [90] min                   │
│                                              │
│  [😴 Rest Day]  [🌤 Light]  [💪 Let's Go!]   │
└─────────────────────────────────────────────┘
```
