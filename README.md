# 🏋️ GymFlow

AI-powered gym & training tracker — fully local, no subscription, no BS.

## What Is This?

A personal Android app that generates customized training programs based on your specific health goals, tracks workouts, adapts over time, and costs $0.

## Features

- **7 Goal Profiles**: Posture fix, blood flow, heart rate, burst+endurance, abdomen shape, core/spine strength, sex drive
- **Adaptive Engine**: Daily/weekly/monthly program adjustments based on your feedback
- **Smart Rest Timer**: Auto-adjusts based on exercise type and goals
- **Progress Tracking**: Volume charts, PR tracking, muscle heatmap, 1RM estimates
- **Exercise Library**: 100+ exercises with form guides, tagged by goal
- **Morning Check-in**: Push/Light/Rest decision based on sleep and energy
- **Full Customization**: Swap exercises, adjust sets/reps/rest, change goals anytime

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Database | Room (SQLite) — local-first |
| DI | Hilt |
| Charts | Vico |
| Architecture | MVVM + Clean Architecture |

## Docs

- [Product Requirements](docs/PRD.md)
- [UI/UX Design System](docs/DESIGN-SYSTEM.md)
- [Screen Designs](docs/SCREENS.md)
- [Training Engine](docs/TRAINING-ENGINE.md)
- [Data Models](docs/DATA-MODELS.md)
- [Architecture](docs/ARCHITECTURE.md)
- [Build Phases](docs/PHASES.md)

## License

MIT
