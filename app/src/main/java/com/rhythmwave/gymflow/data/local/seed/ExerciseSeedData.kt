package com.rhythmwave.gymflow.data.local.seed

import com.rhythmwave.gymflow.data.local.entity.ExerciseEntity
import com.rhythmwave.gymflow.domain.model.ExperienceLevel
import com.rhythmwave.gymflow.domain.model.MuscleGroup

object ExerciseSeedData {

    fun getAll(): List<ExerciseEntity> = listOf(
        // ═══════════════════════════════════════════
        // CHEST (8 exercises)
        // ═══════════════════════════════════════════
        exercise(
            id = "bench_press",
            name = "Barbell Bench Press",
            group = MuscleGroup.CHEST,
            equipment = listOf("barbell", "bench"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("DRIVE", "BLOOD_FLOW"),
            instructions = "1. Lie on bench with feet flat on floor\n2. Grip bar slightly wider than shoulders\n3. Unrack and lower to mid-chest\n4. Press up to full lockout\n5. Keep shoulder blades pinched throughout",
            alternatives = listOf("dumbbell_bench", "push_ups", "chest_press_machine")
        ),
        exercise(
            id = "incline_bench",
            name = "Incline Barbell Bench",
            group = MuscleGroup.CHEST,
            equipment = listOf("barbell", "incline_bench"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("DRIVE", "BLOOD_FLOW"),
            instructions = "1. Set bench to 30-45 degree angle\n2. Grip bar slightly wider than shoulders\n3. Lower to upper chest\n4. Press up and slightly back\n5. Maintain controlled tempo",
            alternatives = listOf("incline_dumbbell", "incline_push_ups")
        ),
        exercise(
            id = "dumbbell_bench",
            name = "Dumbbell Bench Press",
            group = MuscleGroup.CHEST,
            equipment = listOf("dumbbell", "bench"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = true,
            tags = listOf("DRIVE", "BLOOD_FLOW"),
            instructions = "1. Lie on bench holding dumbbells at chest level\n2. Press up bringing dumbbells together\n3. Lower with control to chest level\n4. Keep wrists neutral\n5. Squeeze chest at the top",
            alternatives = listOf("bench_press", "push_ups")
        ),
        exercise(
            id = "push_ups",
            name = "Push-ups",
            group = MuscleGroup.CHEST,
            equipment = listOf("bodyweight"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = true,
            tags = listOf("POSTURE", "CORE_SPINE", "BLOOD_FLOW"),
            instructions = "1. Start in plank position, hands shoulder-width\n2. Lower body until chest nearly touches floor\n3. Keep core tight, body in straight line\n4. Push back up to full extension\n5. Breathe in on the way down, out on the way up",
            alternatives = listOf("bench_press", "dumbbell_bench", "knee_push_ups")
        ),
        exercise(
            id = "dumbbell_flyes",
            name = "Dumbbell Flyes",
            group = MuscleGroup.CHEST,
            equipment = listOf("dumbbell", "bench"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("BLOOD_FLOW"),
            instructions = "1. Lie on bench with dumbbells above chest\n2. Lower arms out to sides with slight elbow bend\n3. Feel stretch in chest at bottom\n4. Squeeze arms back together at top\n5. Keep movement controlled, don't go too heavy",
            alternatives = listOf("cable_flyes", "pec_deck")
        ),
        exercise(
            id = "cable_flyes",
            name = "Cable Flyes",
            group = MuscleGroup.CHEST,
            equipment = listOf("cable"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("BLOOD_FLOW"),
            instructions = "1. Set cables at chest height\n2. Step forward for tension\n3. Bring hands together in hugging motion\n4. Squeeze chest at peak contraction\n5. Control the negative portion",
            alternatives = listOf("dumbbell_flyes", "pec_deck")
        ),
        exercise(
            id = "dips",
            name = "Chest Dips",
            group = MuscleGroup.CHEST,
            equipment = listOf("dip_bars"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("DRIVE", "BLOOD_FLOW"),
            instructions = "1. Lean forward on dip bars\n2. Lower body until shoulders are below elbows\n3. Keep elbows flared slightly outward\n4. Press back up without locking elbows\n5. Lean forward more to target chest",
            alternatives = listOf("push_ups", "bench_press")
        ),
        exercise(
            id = "chest_press_machine",
            name = "Chest Press Machine",
            group = MuscleGroup.CHEST,
            equipment = listOf("machine"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = true,
            tags = listOf("BLOOD_FLOW"),
            instructions = "1. Adjust seat so handles are at chest level\n2. Grip handles with full palm\n3. Press forward to full extension\n4. Return slowly to start\n5. Keep back flat against pad",
            alternatives = listOf("bench_press", "dumbbell_bench")
        ),

        // ═══════════════════════════════════════════
        // BACK (8 exercises)
        // ═══════════════════════════════════════════
        exercise(
            id = "deadlift",
            name = "Conventional Deadlift",
            group = MuscleGroup.BACK,
            equipment = listOf("barbell"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("POSTURE", "DRIVE", "CORE_SPINE"),
            instructions = "1. Stand with feet hip-width, bar over mid-foot\n2. Hinge at hips, grip bar just outside knees\n3. Flatten back, brace core, chest up\n4. Drive through heels, extend hips and knees\n5. Lock out at top, reverse the movement\n6. Keep bar close to body throughout",
            alternatives = listOf("romanian_deadlift", "rack_pulls")
        ),
        exercise(
            id = "romanian_deadlift",
            name = "Romanian Deadlift",
            group = MuscleGroup.BACK,
            equipment = listOf("barbell"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("POSTURE", "DRIVE", "CORE_SPINE"),
            instructions = "1. Hold barbell at hip level\n2. Push hips back, slight knee bend\n3. Lower bar along legs until stretch in hamstrings\n4. Keep back flat, chest up\n5. Drive hips forward to return to start",
            alternatives = listOf("deadlift", "good_mornings")
        ),
        exercise(
            id = "barbell_row",
            name = "Barbell Bent-Over Row",
            group = MuscleGroup.BACK,
            equipment = listOf("barbell"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("POSTURE", "DRIVE"),
            instructions = "1. Hinge at hips, grip bar shoulder-width\n2. Keep back flat, core braced\n3. Pull bar to lower chest\n4. Squeeze shoulder blades together at top\n5. Lower with control, don't use momentum",
            alternatives = listOf("dumbbell_row", "cable_row", "t_bar_row")
        ),
        exercise(
            id = "dumbbell_row",
            name = "Single-Arm Dumbbell Row",
            group = MuscleGroup.BACK,
            equipment = listOf("dumbbell", "bench"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = true,
            tags = listOf("POSTURE"),
            instructions = "1. Place one knee and hand on bench\n2. Hold dumbbell in free hand, arm extended\n3. Pull dumbbell to hip, elbow close to body\n4. Squeeze back muscles at top\n5. Lower with control, keep hips square",
            alternatives = listOf("barbell_row", "cable_row")
        ),
        exercise(
            id = "pull_ups",
            name = "Pull-ups",
            group = MuscleGroup.BACK,
            equipment = listOf("pull_up_bar"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("POSTURE", "CORE_SPINE", "DRIVE"),
            instructions = "1. Hang from bar with overhand grip, shoulder-width\n2. Engage lats, pull chest toward bar\n3. Lead with elbows, squeeze back at top\n4. Lower with control to full extension\n5. Avoid swinging or kipping",
            alternatives = listOf("lat_pulldown", "assisted_pull_ups")
        ),
        exercise(
            id = "lat_pulldown",
            name = "Lat Pulldown",
            group = MuscleGroup.BACK,
            equipment = listOf("cable"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = true,
            tags = listOf("POSTURE"),
            instructions = "1. Sit with thighs secured under pads\n2. Grip bar wide, overhand\n3. Pull bar to upper chest\n4. Squeeze lats at bottom\n5. Control the return to full stretch",
            alternatives = listOf("pull_ups", "straight_arm_pulldown")
        ),
        exercise(
            id = "cable_row",
            name = "Seated Cable Row",
            group = MuscleGroup.BACK,
            equipment = listOf("cable"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = true,
            tags = listOf("POSTURE", "BLOOD_FLOW"),
            instructions = "1. Sit with feet on platform, knees slightly bent\n2. Grip handle with arms extended\n3. Pull handle to abdomen, squeezing shoulder blades\n4. Keep chest up, don't lean back excessively\n5. Return slowly to start position",
            alternatives = listOf("barbell_row", "dumbbell_row")
        ),
        exercise(
            id = "t_bar_row",
            name = "T-Bar Row",
            group = MuscleGroup.BACK,
            equipment = listOf("barbell", "landmine"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("POSTURE", "DRIVE"),
            instructions = "1. Straddle bar, grip handles\n2. Hinge at hips, keep back flat\n3. Pull to chest, squeeze shoulder blades\n4. Lower with control\n5. Keep core braced throughout",
            alternatives = listOf("barbell_row", "cable_row")
        ),

        // ═══════════════════════════════════════════
        // SHOULDERS (7 exercises)
        // ═══════════════════════════════════════════
        exercise(
            id = "overhead_press",
            name = "Overhead Press (OHP)",
            group = MuscleGroup.SHOULDERS,
            equipment = listOf("barbell"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("POSTURE", "DRIVE", "CORE_SPINE"),
            instructions = "1. Stand with feet hip-width, bar at shoulders\n2. Brace core, squeeze glutes\n3. Press bar overhead to lockout\n4. Move head slightly forward at top\n5. Lower under control to shoulders",
            alternatives = listOf("dumbbell_ohp", "arnold_press")
        ),
        exercise(
            id = "dumbbell_ohp",
            name = "Dumbbell Shoulder Press",
            group = MuscleGroup.SHOULDERS,
            equipment = listOf("dumbbell"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = true,
            tags = listOf("POSTURE", "DRIVE"),
            instructions = "1. Sit or stand with dumbbells at shoulder height\n2. Press up and slightly inward\n3. Full extension without locking elbows\n4. Lower to ear level\n5. Keep core engaged",
            alternatives = listOf("overhead_press", "arnold_press")
        ),
        exercise(
            id = "lateral_raises",
            name = "Lateral Raises",
            group = MuscleGroup.SHOULDERS,
            equipment = listOf("dumbbell"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("POSTURE", "BLOOD_FLOW"),
            instructions = "1. Stand with dumbbells at sides\n2. Raise arms out to sides until parallel to floor\n3. Slight bend in elbows\n4. Lead with elbows, not hands\n5. Lower slowly, don't swing",
            alternatives = listOf("cable_lateral_raises", "machine_lateral_raises")
        ),
        exercise(
            id = "face_pulls",
            name = "Face Pulls",
            group = MuscleGroup.SHOULDERS,
            equipment = listOf("cable", "rope"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("POSTURE", "FLEXIBILITY"),
            instructions = "1. Set cable at face height with rope attachment\n2. Pull rope toward face, elbows high\n3. Separate rope ends past ears\n4. Squeeze rear delts and upper back\n5. Return slowly to start",
            alternatives = listOf("band_pull_aparts", "reverse_flyes")
        ),
        exercise(
            id = "reverse_flyes",
            name = "Reverse Flyes",
            group = MuscleGroup.SHOULDERS,
            equipment = listOf("dumbbell"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("POSTURE"),
            instructions = "1. Bend at hips, dumbbells hanging below\n2. Raise arms out to sides\n3. Squeeze shoulder blades together\n4. Lower with control\n5. Keep slight bend in elbows",
            alternatives = listOf("face_pulls", "cable_reverse_flyes")
        ),
        exercise(
            id = "arnold_press",
            name = "Arnold Press",
            group = MuscleGroup.SHOULDERS,
            equipment = listOf("dumbbell"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("POSTURE", "DRIVE"),
            instructions = "1. Start with dumbbells in front of shoulders, palms facing you\n2. Press up while rotating palms outward\n3. Full lockout with palms forward\n4. Reverse the rotation on the way down\n5. Smooth, continuous motion",
            alternatives = listOf("overhead_press", "dumbbell_ohp")
        ),
        exercise(
            id = "cable_lateral_raises",
            name = "Cable Lateral Raises",
            group = MuscleGroup.SHOULDERS,
            equipment = listOf("cable"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("POSTURE", "BLOOD_FLOW"),
            instructions = "1. Stand next to cable machine, low setting\n2. Grip cable with far hand across body\n3. Raise arm out to side until parallel\n4. Control the negative\n5. Constant tension throughout",
            alternatives = listOf("lateral_raises", "machine_lateral_raises")
        ),

        // ═══════════════════════════════════════════
        // LEGS (10 exercises)
        // ═══════════════════════════════════════════
        exercise(
            id = "barbell_squat",
            name = "Barbell Back Squat",
            group = MuscleGroup.LEGS,
            equipment = listOf("barbell", "squat_rack"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("DRIVE", "CORE_SPINE", "EXPLOSIVE"),
            instructions = "1. Bar on upper back, feet shoulder-width\n2. Brace core, take a breath\n3. Sit back and down, knees tracking toes\n4. Go to parallel or below\n5. Drive up through heels\n6. Exhale at the top",
            alternatives = listOf("goblet_squat", "front_squat", "leg_press")
        ),
        exercise(
            id = "front_squat",
            name = "Front Squat",
            group = MuscleGroup.LEGS,
            equipment = listOf("barbell", "squat_rack"),
            difficulty = ExperienceLevel.ADVANCED,
            compound = true,
            tags = listOf("DRIVE", "CORE_SPINE"),
            instructions = "1. Bar on front of shoulders, elbows high\n2. Feet shoulder-width, toes slightly out\n3. Squat down keeping torso upright\n4. Go to parallel or below\n5. Drive up, keep elbows high throughout",
            alternatives = listOf("barbell_squat", "goblet_squat")
        ),
        exercise(
            id = "goblet_squat",
            name = "Goblet Squat",
            group = MuscleGroup.LEGS,
            equipment = listOf("dumbbell"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = true,
            tags = listOf("POSTURE", "CORE_SPINE"),
            instructions = "1. Hold dumbbell at chest level\n2. Feet shoulder-width, toes slightly out\n3. Squat down between your legs\n4. Keep elbows inside knees\n5. Drive up through heels",
            alternatives = listOf("barbell_squat", "leg_press")
        ),
        exercise(
            id = "leg_press",
            name = "Leg Press",
            group = MuscleGroup.LEGS,
            equipment = listOf("machine"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = true,
            tags = listOf("DRIVE", "BLOOD_FLOW"),
            instructions = "1. Sit in machine, feet shoulder-width on platform\n2. Release safety, lower weight\n3. Bend knees to 90 degrees\n4. Press through heels to extend\n5. Don't lock knees at top",
            alternatives = listOf("barbell_squat", "goblet_squat")
        ),
        exercise(
            id = "walking_lunges",
            name = "Walking Lunges",
            group = MuscleGroup.LEGS,
            equipment = listOf("dumbbell"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = true,
            tags = listOf("CORE_SPINE", "BLOOD_FLOW", "FLEXIBILITY"),
            instructions = "1. Hold dumbbells at sides\n2. Step forward into lunge\n3. Lower back knee toward floor\n4. Push off front foot, step forward\n5. Alternate legs, keep torso upright",
            alternatives = listOf("stationary_lunges", "bulgarian_split_squat")
        ),
        exercise(
            id = "bulgarian_split_squat",
            name = "Bulgarian Split Squat",
            group = MuscleGroup.LEGS,
            equipment = listOf("dumbbell", "bench"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("DRIVE", "CORE_SPINE", "EXPLOSIVE"),
            instructions = "1. Place rear foot on bench behind you\n2. Hold dumbbells at sides\n3. Lower until front thigh is parallel\n4. Drive up through front heel\n5. Keep torso upright, core braced",
            alternatives = listOf("walking_lunges", "step_ups")
        ),
        exercise(
            id = "hip_thrusts",
            name = "Barbell Hip Thrust",
            group = MuscleGroup.LEGS,
            equipment = listOf("barbell", "bench"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("DRIVE", "EXPLOSIVE", "BLOOD_FLOW"),
            instructions = "1. Sit on floor, back against bench\n2. Roll bar over legs to hips (use pad)\n3. Feet flat, shoulder-width\n4. Drive hips up to full extension\n5. Squeeze glutes hard at top\n6. Lower with control",
            alternatives = listOf("glute_bridges", "cable_kickbacks")
        ),
        exercise(
            id = "calf_raises",
            name = "Standing Calf Raises",
            group = MuscleGroup.LEGS,
            equipment = listOf("machine"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("BLOOD_FLOW"),
            instructions = "1. Stand on platform, balls of feet on edge\n2. Lower heels below platform level\n3. Rise up on toes as high as possible\n4. Squeeze at top for 1 second\n5. Lower slowly for full stretch",
            alternatives = listOf("seated_calf_raises", "donkey_calf_raises")
        ),
        exercise(
            id = "box_jumps",
            name = "Box Jumps",
            group = MuscleGroup.LEGS,
            equipment = listOf("plyo_box"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("EXPLOSIVE", "CARDIO", "BLOOD_FLOW"),
            instructions = "1. Stand facing box, feet shoulder-width\n2. Swing arms, bend knees\n3. Jump onto box, land softly with bent knees\n4. Stand up fully on box\n5. Step back down (don't jump down)\n6. Reset and repeat",
            alternatives = listOf("jump_squats", "broad_jumps")
        ),
        exercise(
            id = "jump_squats",
            name = "Jump Squats",
            group = MuscleGroup.LEGS,
            equipment = listOf("bodyweight"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("EXPLOSIVE", "CARDIO", "BLOOD_FLOW"),
            instructions = "1. Stand with feet shoulder-width\n2. Squat down to parallel\n3. Explode upward, jumping as high as possible\n4. Land softly with bent knees\n5. Immediately go into next squat\n6. Maintain form throughout",
            alternatives = listOf("box_jumps", "broad_jumps")
        ),

        // ═══════════════════════════════════════════
        // ARMS (6 exercises)
        // ═══════════════════════════════════════════
        exercise(
            id = "barbell_curl",
            name = "Barbell Curl",
            group = MuscleGroup.ARMS,
            equipment = listOf("barbell"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("BLOOD_FLOW"),
            instructions = "1. Stand with barbell, shoulder-width grip\n2. Keep elbows at sides\n3. Curl bar up to shoulder level\n4. Squeeze biceps at top\n5. Lower with control, don't swing",
            alternatives = listOf("dumbbell_curl", "hammer_curl")
        ),
        exercise(
            id = "dumbbell_curl",
            name = "Dumbbell Bicep Curl",
            group = MuscleGroup.ARMS,
            equipment = listOf("dumbbell"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("BLOOD_FLOW"),
            instructions = "1. Stand with dumbbells at sides\n2. Curl one or both arms up\n3. Rotate wrist to supinate at top\n4. Squeeze biceps\n5. Lower with control",
            alternatives = listOf("barbell_curl", "hammer_curl")
        ),
        exercise(
            id = "hammer_curl",
            name = "Hammer Curl",
            group = MuscleGroup.ARMS,
            equipment = listOf("dumbbell"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("BLOOD_FLOW"),
            instructions = "1. Stand with dumbbells at sides, palms facing in\n2. Curl up keeping neutral wrist position\n3. Don't rotate wrist\n4. Squeeze at top\n5. Lower with control",
            alternatives = listOf("dumbbell_curl", "barbell_curl")
        ),
        exercise(
            id = "tricep_pushdown",
            name = "Cable Tricep Pushdown",
            group = MuscleGroup.ARMS,
            equipment = listOf("cable"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("BLOOD_FLOW"),
            instructions = "1. Stand at cable machine, high setting\n2. Grip bar or rope, elbows at sides\n3. Push down to full extension\n4. Squeeze triceps at bottom\n5. Return slowly to 90 degrees",
            alternatives = listOf("overhead_tricep_ext", "skull_crushers")
        ),
        exercise(
            id = "overhead_tricep_ext",
            name = "Overhead Tricep Extension",
            group = MuscleGroup.ARMS,
            equipment = listOf("dumbbell"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("BLOOD_FLOW", "POSTURE"),
            instructions = "1. Hold dumbbell overhead with both hands\n2. Lower behind head by bending elbows\n3. Keep elbows pointing forward\n4. Extend back to overhead\n5. Squeeze triceps at top",
            alternatives = listOf("tricep_pushdown", "skull_crushers")
        ),
        exercise(
            id = "skull_crushers",
            name = "Skull Crushers (Lying Tricep Ext)",
            group = MuscleGroup.ARMS,
            equipment = listOf("barbell", "ez_bar", "bench"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = false,
            tags = listOf("BLOOD_FLOW"),
            instructions = "1. Lie on bench holding bar above chest\n2. Lower bar toward forehead by bending elbows\n3. Keep upper arms stationary\n4. Extend back to start\n5. Don't flare elbows outward",
            alternatives = listOf("tricep_pushdown", "overhead_tricep_ext")
        ),

        // ═══════════════════════════════════════════
        // CORE (8 exercises)
        // ═══════════════════════════════════════════
        exercise(
            id = "plank",
            name = "Plank",
            group = MuscleGroup.CORE,
            equipment = listOf("bodyweight"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("CORE_SPINE", "POSTURE"),
            instructions = "1. Forearms on floor, body in straight line\n2. Engage core, squeeze glutes\n3. Don't let hips sag or pike up\n4. Breathe normally\n5. Hold for target time",
            alternatives = listOf("side_plank", "dead_bug")
        ),
        exercise(
            id = "side_plank",
            name = "Side Plank",
            group = MuscleGroup.CORE,
            equipment = listOf("bodyweight"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("CORE_SPINE", "POSTURE"),
            instructions = "1. Lie on side, forearm on floor\n2. Lift hips off ground, body in straight line\n3. Stack feet or stagger them\n4. Hold, then switch sides\n5. Keep core engaged throughout",
            alternatives = listOf("plank", "pallof_press")
        ),
        exercise(
            id = "dead_bug",
            name = "Dead Bug",
            group = MuscleGroup.CORE,
            equipment = listOf("bodyweight"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("CORE_SPINE", "POSTURE"),
            instructions = "1. Lie on back, arms up, knees at 90 degrees\n2. Press lower back into floor\n3. Extend opposite arm and leg slowly\n4. Return to start, repeat other side\n5. Keep lower back pressed down throughout",
            alternatives = listOf("bird_dog", "plank")
        ),
        exercise(
            id = "bird_dog",
            name = "Bird Dog",
            group = MuscleGroup.CORE,
            equipment = listOf("bodyweight"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("CORE_SPINE", "POSTURE"),
            instructions = "1. Start on hands and knees\n2. Extend opposite arm and leg simultaneously\n3. Keep back flat, hips square\n4. Hold briefly at full extension\n5. Return and switch sides\n6. Don't rotate hips",
            alternatives = listOf("dead_bug", "plank")
        ),
        exercise(
            id = "pallof_press",
            name = "Pallof Press",
            group = MuscleGroup.CORE,
            equipment = listOf("cable", "band"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = false,
            tags = listOf("CORE_SPINE", "POSTURE"),
            instructions = "1. Stand perpendicular to cable, chest height\n2. Hold handle at chest with both hands\n3. Press hands straight out from chest\n4. Resist the rotation\n5. Return to chest and repeat\n6. Switch sides",
            alternatives = listOf("plank", "anti_rotation_hold")
        ),
        exercise(
            id = "hanging_leg_raises",
            name = "Hanging Leg Raises",
            group = MuscleGroup.CORE,
            equipment = listOf("pull_up_bar"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = false,
            tags = listOf("CORE_SPINE", "DRIVE"),
            instructions = "1. Hang from bar with straight arms\n2. Keep legs straight or slightly bent\n3. Raise legs to 90 degrees or higher\n4. Control the lowering\n5. Avoid swinging\n6. Focus on curling pelvis up",
            alternatives = listOf("lying_leg_raises", "knee_raises")
        ),
        exercise(
            id = "cable_crunches",
            name = "Cable Crunches",
            group = MuscleGroup.CORE,
            equipment = listOf("cable"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("CORE_SPINE"),
            instructions = "1. Kneel facing cable machine, rope attachment\n2. Hold rope behind head\n3. Crunch down, bringing elbows toward knees\n4. Focus on contracting abs\n5. Return slowly to start\n6. Don't pull with arms",
            alternatives = listOf("hanging_leg_raises", "crunches")
        ),
        exercise(
            id = "back_extension",
            name = "Back Extension (Hyperextension)",
            group = MuscleGroup.CORE,
            equipment = listOf("machine", "bench"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("CORE_SPINE", "POSTURE"),
            instructions = "1. Position yourself on back extension bench\n2. Cross arms over chest or behind head\n3. Lower upper body by hinging at hips\n4. Raise back to neutral (don't hyperextend)\n5. Squeeze glutes and lower back at top\n6. Controlled movement throughout",
            alternatives = listOf("good_mornings", "superman")
        ),

        // ═══════════════════════════════════════════
        // CARDIO (5 exercises)
        // ═══════════════════════════════════════════
        exercise(
            id = "jump_rope",
            name = "Jump Rope",
            group = MuscleGroup.CARDIO,
            equipment = listOf("jump_rope"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("CARDIO", "BLOOD_FLOW"),
            instructions = "1. Hold rope handles at hip height\n2. Rotate wrists to swing rope\n3. Jump just high enough to clear rope\n4. Land softly on balls of feet\n5. Keep elbows close to body\n6. Maintain steady rhythm",
            alternatives = listOf("high_knees", "burpees")
        ),
        exercise(
            id = "rowing_machine",
            name = "Rowing Machine",
            group = MuscleGroup.CARDIO,
            equipment = listOf("rowing_machine"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = true,
            tags = listOf("CARDIO", "BLOOD_FLOW", "POSTURE"),
            instructions = "1. Strap feet in, grip handle\n2. Drive with legs first\n3. Lean back slightly, pull handle to chest\n4. Reverse: arms out, lean forward, bend knees\n5. Maintain steady rhythm\n6. Focus on leg drive, not arm pull",
            alternatives = listOf("bike", "running")
        ),
        exercise(
            id = "bike_intervals",
            name = "Stationary Bike HIIT",
            group = MuscleGroup.CARDIO,
            equipment = listOf("bike"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = false,
            tags = listOf("CARDIO", "BLOOD_FLOW", "DRIVE"),
            instructions = "1. Warm up 2-3 minutes easy pace\n2. Sprint 30 seconds at max effort\n3. Recover 30 seconds easy pace\n4. Repeat for target rounds\n5. Cool down 2-3 minutes\n6. Adjust resistance as needed",
            alternatives = listOf("treadmill_intervals", "jump_rope")
        ),
        exercise(
            id = "battle_ropes",
            name = "Battle Ropes",
            group = MuscleGroup.CARDIO,
            equipment = listOf("battle_ropes"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("CARDIO", "BLOOD_FLOW", "EXPLOSIVE", "POSTURE"),
            instructions = "1. Stand facing rope anchor, athletic stance\n2. Grip one rope in each hand\n3. Alternate arms making waves\n4. Keep core tight, don't lean forward\n5. Maintain intensity for target duration\n6. Can do slams, spirals, or alternating waves",
            alternatives = listOf("jump_rope", "burpees")
        ),
        exercise(
            id = "burpees",
            name = "Burpees",
            group = MuscleGroup.CARDIO,
            equipment = listOf("bodyweight"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("CARDIO", "BLOOD_FLOW", "EXPLOSIVE", "DRIVE"),
            instructions = "1. Stand, then squat and place hands on floor\n2. Jump feet back to push-up position\n3. Do a push-up (optional)\n4. Jump feet back to hands\n5. Explode up with arms overhead\n6. Land softly and repeat immediately",
            alternatives = listOf("jump_rope", "mountain_climbers")
        ),

        // ═══════════════════════════════════════════
        // ADDITIONAL COMPOUND / FUNCTIONAL (4 exercises)
        // ═══════════════════════════════════════════
        exercise(
            id = "kettlebell_swing",
            name = "Kettlebell Swing",
            group = MuscleGroup.LEGS,
            equipment = listOf("kettlebell"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("EXPLOSIVE", "CARDIO", "DRIVE", "POSTURE"),
            instructions = "1. Stand with feet wider than shoulder-width\n2. Hinge at hips, grip kettlebell\n3. Hike bell between legs\n4. Drive hips forward explosively\n5. Bell floats to chest height\n6. Let it swing back between legs\n7. Power comes from hips, not arms",
            alternatives = listOf("hip_thrusts", "deadlift")
        ),
        exercise(
            id = "farmer_carry",
            name = "Farmer's Carry",
            group = MuscleGroup.CORE,
            equipment = listOf("dumbbell"),
            difficulty = ExperienceLevel.BEGINNER,
            compound = true,
            tags = listOf("POSTURE", "CORE_SPINE", "GRIP"),
            instructions = "1. Hold heavy dumbbells at sides\n2. Stand tall, shoulders back\n3. Walk with controlled steps\n4. Keep core braced\n5. Don't let weights swing\n6. Walk for target distance or time",
            alternatives = listOf("suitcase_carry", "trap_bar_carry")
        ),
        exercise(
            id = "turkish_getup",
            name = "Turkish Get-Up",
            group = MuscleGroup.CORE,
            equipment = listOf("kettlebell", "dumbbell"),
            difficulty = ExperienceLevel.ADVANCED,
            compound = true,
            tags = listOf("CORE_SPINE", "POSTURE", "FLEXIBILITY", "DRIVE"),
            instructions = "1. Lie on back holding weight overhead in one hand\n2. Bend same-side knee, foot flat\n3. Roll to forearm, then hand\n4. Lift hips, sweep leg through to kneeling\n5. Stand up while keeping weight overhead\n6. Reverse each step to return to floor\n7. Keep eyes on weight throughout",
            alternatives = listOf("get_up_sit_up", "windmill")
        ),
        exercise(
            id = "good_mornings",
            name = "Good Mornings",
            group = MuscleGroup.BACK,
            equipment = listOf("barbell"),
            difficulty = ExperienceLevel.INTERMEDIATE,
            compound = true,
            tags = listOf("POSTURE", "CORE_SPINE", "FLEXIBILITY"),
            instructions = "1. Bar on upper back (like squat position)\n2. Feet shoulder-width, slight knee bend\n3. Hinge at hips, pushing hips back\n4. Lower torso until parallel to floor\n5. Reverse to standing\n6. Keep back flat throughout",
            alternatives = listOf("romanian_deadlift", "back_extension")
        )
    )

    private fun exercise(
        id: String,
        name: String,
        group: MuscleGroup,
        equipment: List<String>,
        difficulty: ExperienceLevel,
        compound: Boolean,
        tags: List<String>,
        instructions: String,
        alternatives: List<String>
    ) = ExerciseEntity(
        id = id,
        name = name,
        muscleGroup = group,
        equipmentJson = com.google.gson.Gson().toJson(equipment),
        difficulty = difficulty,
        isCompound = compound,
        instructions = instructions,
        tagsJson = com.google.gson.Gson().toJson(tags),
        alternativesJson = com.google.gson.Gson().toJson(alternatives)
    )
}
