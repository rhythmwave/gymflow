# UI/UX Design System

## GymFlow Design Language

**Version:** 1.0
**Platform:** Android (Material 3)

---

## Design Philosophy

**"Glance → Tap → Done"**

- Every screen has ONE primary action
- No menu diving during a workout
- Dark mode first (gym-friendly, saves battery, easier on eyes)
- Large touch targets (sweaty hands, mid-set usage)
- Information density balanced with clarity

---

## Color Palette

### Base Theme (Dark)

| Token | Hex | Usage |
|---|---|---|
| `primary` | `#4CAF50` | Primary actions, progress, "go" |
| `primaryDark` | `#388E3C` | Pressed states, emphasis |
| `secondary` | `#FF9800` | Warnings, rest timer |
| `tertiary` | `#00BCD4` | Cardio, heart rate data |
| `surface` | `#121212` | Card backgrounds |
| `surfaceVariant` | `#1E1E1E` | Elevated surfaces |
| `background` | `#0A0A0A` | Screen background |
| `onSurface` | `#E0E0E0` | Primary text |
| `onSurfaceVariant` | `#9E9E9E` | Secondary text |
| `error` | `#CF6679` | Errors, failures (soft red) |
| `success` | `#66BB6A` | Checkmarks, completions |
| `divider` | `#2A2A2A` | Subtle separation |

### Goal-Specific Accent Colors

Each goal has a unique color used for tags, indicators, and progress rings.

| Goal | Color | Hex | Icon |
|---|---|---|---|
| Posture | Light Green | `#8BC34A` | 🧘 `accessibility_rounded` |
| Cardio | Cyan | `#00BCD4` | 🫀 `directions_run_rounded` |
| Burst/Power | Deep Orange | `#FF5722` | ⚡ `flash_on_rounded` |
| Core/Spine | Purple | `#9C27B0` | 🦴 `accessibility_new_rounded` |
| Drive | Pink | `#E91E63` | 💕 `favorite_rounded` |
| Blood Flow | Red | `#F44336` | ❤️ `bloodtype_rounded` |
| Flexibility | Teal | `#009688` | 🤸 `self_improvement_rounded` |

---

## Typography

**Font Family:** Inter (Google Fonts) — clean, readable, modern, excellent for data

| Style | Size | Weight | Usage |
|---|---|---|---|
| Display Large | 32sp | Bold | Screen titles (hero) |
| Display Medium | 28sp | Bold | Section headers |
| Headline Large | 24sp | SemiBold | Card titles |
| Title Large | 20sp | SemiBold | Exercise names |
| Title Medium | 16sp | SemiBold | Subheadings |
| Body Large | 16sp | Regular | Descriptions, instructions |
| Body Medium | 14sp | Regular | Default text |
| Body Small | 12sp | Regular | Labels, captions |
| Label Large | 14sp | Medium | Buttons, tabs |
| Label Medium | 12sp | Medium | Tags, chips |

---

## Iconography

**Set:** Material Symbols Rounded (softer, friendlier than sharp variant)

### Navigation Icons
| Screen | Icon |
|---|---|
| Home | `home_rounded` |
| Programs | `fitness_center_rounded` |
| Workout | `sports_gymnastics_rounded` |
| Progress | `trending_up_rounded` |
| Profile | `person_rounded` |

### Action Icons
| Action | Icon |
|---|---|
| Timer | `timer_rounded` |
| Swap exercise | `swap_horiz_rounded` |
| Add | `add_circle_rounded` |
| Check/Done | `check_circle_rounded` |
| Edit | `edit_rounded` |
| Skip | `skip_next_rounded` |
| Delete | `delete_rounded` |
| Settings | `settings_rounded` |
| Search | `search_rounded` |
| Filter | `filter_list_rounded` |
| Info | `info_rounded` |
| Play | `play_arrow_rounded` |
| Pause | `pause_rounded` |

### Goal Icons
| Goal | Icon |
|---|---|
| Posture | `accessibility_rounded` |
| Cardio | `directions_run_rounded` |
| Explosive | `flash_on_rounded` |
| Core | `accessibility_new_rounded` |
| Drive | `favorite_rounded` |
| Blood Flow | `bloodtype_rounded` |
| Rest | `self_improvement_rounded` |

---

## Spacing System

| Token | Value | Usage |
|---|---|---|
| `xs` | 4dp | Tight spacing (icon gaps) |
| `sm` | 8dp | Component internal padding |
| `md` | 16dp | Standard padding, gaps |
| `lg` | 24dp | Section spacing |
| `xl` | 32dp | Screen edges, major sections |

---

## Corner Radius

| Token | Value | Usage |
|---|---|---|
| `small` | 8dp | Chips, tags, small cards |
| `medium` | 12dp | Standard cards, buttons |
| `large` | 16dp | Bottom sheets, dialogs |
| `full` | 50% | Circular avatars, progress rings |

---

## Elevation

| Level | Value | Usage |
|---|---|---|
| 0 | 0dp | Background, flat surfaces |
| 1 | 2dp | Cards, list items |
| 2 | 4dp | App bar, navigation |
| 3 | 8dp | FAB, bottom sheets |
| 4 | 16dp | Dialogs, modals |

---

## Animation

### Durations
| Token | Value | Usage |
|---|---|---|
| `fast` | 150ms | Button presses, toggles |
| `normal` | 300ms | Screen transitions, reveals |
| `slow` | 500ms | Celebrations, emphasis |

### Motion Patterns
| Pattern | Usage |
|---|---|
| Slide horizontal | Tab/screen navigation |
| Slide up | Bottom sheets |
| Fade + translate up | Card list items appearing |
| Scale bounce | Button taps (0.95 → 1.0) |
| Circular progress | Timer countdown |
| Confetti burst | PR celebrations |
| Green flash | Set completion |

---

## Haptic Feedback

| Action | Haptic Type |
|---|---|
| Mark set done | Medium impact |
| Timer complete | Heavy impact + vibration pattern |
| New PR | Success notification haptic |
| Button tap | Light impact |
| Error/invalid input | Error feedback |
| Swipe action | Selection feedback |

---

## Touch Targets

| Element | Minimum Size | Recommended |
|---|---|---|
| Text buttons | 48dp × 48dp | 48dp × 48dp |
| Icon buttons | 48dp × 48dp | 48dp × 48dp |
| Primary actions (workout) | 56dp × 56dp | 64dp × 56dp |
| List items | 48dp height | 56dp height |
| Chips/tags | 32dp height | 36dp height |

---

## Accessibility

- **Contrast ratio**: Minimum 4.5:1 (WCAG AA)
- **Font scaling**: Support up to 200% system font size
- **Screen reader**: Full TalkBack support with content descriptions
- **Color blind**: Goal tags use icons + colors (never color alone)
- **Touch targets**: Minimum 48dp (Google standard)
- **Focus order**: Logical tab order through all interactive elements

---

## Component Library

### Cards
- **Standard Card**: 12dp radius, level 1 elevation, 16dp padding
- **Action Card**: Same + colored border-left (4dp) for goal indication
- **Exercise Card**: Name + muscle group + tags + equipment chips

### Buttons
- **Primary Filled**: Green background, white text, 12dp radius, 56dp height (workout)
- **Secondary Outlined**: Transparent bg, green border, green text
- **Text Button**: No bg, green text, 48dp height
- **Icon Button**: 48dp × 48dp, ripple effect

### Chips
- **Filter Chip**: 8dp radius, outlined when unselected, filled when selected
- **Goal Chip**: Colored border matching goal color, icon + text
- **Equipment Chip**: Small, 8dp radius, neutral color

### Input Fields
- **Outlined Text Field**: 12dp radius, green focus border
- **Number Picker**: Large +/- buttons (48dp), center editable number
- **Slider**: Green track, 24dp thumb

### Bottom Sheets
- **Standard**: 16dp top radius, scrim overlay, drag-to-dismiss
- **Modal**: Same + handle bar indicator

### Progress Indicators
- **Linear**: Green fill, rounded ends, percentage label
- **Circular**: Green arc, percentage in center (timer)
- **Ring**: Colored by goal, completion animation

### Navigation
- **Bottom Nav**: 5 items, icon + label, green active indicator
- **Top App Bar**: Title left-aligned, actions right-aligned
- **Tabs**: For time range selection (1W, 1M, 3M, etc.)
