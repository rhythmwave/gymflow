package com.rhythmwave.gymflow.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rhythmwave.gymflow.data.local.entity.ExerciseEntity
import com.rhythmwave.gymflow.domain.model.ExperienceLevel
import com.rhythmwave.gymflow.domain.model.MuscleGroup
import com.rhythmwave.gymflow.ui.theme.*

data class ExercisePreview(
    val id: String,
    val name: String,
    val muscleGroup: String,
    val isCompound: Boolean,
    val tags: List<String>,
    val equipment: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseLibraryScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMuscleGroup by remember { mutableStateOf<String?>(null) }
    var selectedExercise by remember { mutableStateOf<ExercisePreview?>(null) }

    val exercises = remember {
        listOf(
            ExercisePreview("bench_press", "Barbell Bench Press", "Chest", true, listOf("DRIVE", "BLOOD_FLOW"), listOf("Barbell", "Bench")),
            ExercisePreview("incline_bench", "Incline Barbell Bench", "Chest", true, listOf("DRIVE", "BLOOD_FLOW"), listOf("Barbell", "Incline Bench")),
            ExercisePreview("dumbbell_bench", "Dumbbell Bench Press", "Chest", true, listOf("DRIVE", "BLOOD_FLOW"), listOf("Dumbbell", "Bench")),
            ExercisePreview("push_ups", "Push-ups", "Chest", true, listOf("POSTURE", "CORE_SPINE", "BLOOD_FLOW"), listOf("Bodyweight")),
            ExercisePreview("deadlift", "Conventional Deadlift", "Back", true, listOf("POSTURE", "DRIVE", "CORE_SPINE"), listOf("Barbell")),
            ExercisePreview("romanian_deadlift", "Romanian Deadlift", "Back", true, listOf("POSTURE", "DRIVE", "CORE_SPINE"), listOf("Barbell")),
            ExercisePreview("barbell_row", "Barbell Bent-Over Row", "Back", true, listOf("POSTURE", "DRIVE"), listOf("Barbell")),
            ExercisePreview("pull_ups", "Pull-ups", "Back", true, listOf("POSTURE", "CORE_SPINE", "DRIVE"), listOf("Pull-up Bar")),
            ExercisePreview("overhead_press", "Overhead Press (OHP)", "Shoulders", true, listOf("POSTURE", "DRIVE", "CORE_SPINE"), listOf("Barbell")),
            ExercisePreview("lateral_raises", "Lateral Raises", "Shoulders", false, listOf("POSTURE", "BLOOD_FLOW"), listOf("Dumbbell")),
            ExercisePreview("face_pulls", "Face Pulls", "Shoulders", false, listOf("POSTURE", "FLEXIBILITY"), listOf("Cable", "Rope")),
            ExercisePreview("barbell_squat", "Barbell Back Squat", "Legs", true, listOf("DRIVE", "CORE_SPINE", "EXPLOSIVE"), listOf("Barbell", "Squat Rack")),
            ExercisePreview("hip_thrusts", "Barbell Hip Thrust", "Legs", true, listOf("DRIVE", "EXPLOSIVE", "BLOOD_FLOW"), listOf("Barbell", "Bench")),
            ExercisePreview("box_jumps", "Box Jumps", "Legs", true, listOf("EXPLOSIVE", "CARDIO", "BLOOD_FLOW"), listOf("Plyo Box")),
            ExercisePreview("plank", "Plank", "Core", false, listOf("CORE_SPINE", "POSTURE"), listOf("Bodyweight")),
            ExercisePreview("dead_bug", "Dead Bug", "Core", false, listOf("CORE_SPINE", "POSTURE"), listOf("Bodyweight")),
            ExercisePreview("hanging_leg_raises", "Hanging Leg Raises", "Core", false, listOf("CORE_SPINE", "DRIVE"), listOf("Pull-up Bar")),
            ExercisePreview("jump_rope", "Jump Rope", "Cardio", false, listOf("CARDIO", "BLOOD_FLOW"), listOf("Jump Rope")),
            ExercisePreview("rowing_machine", "Rowing Machine", "Cardio", true, listOf("CARDIO", "BLOOD_FLOW", "POSTURE"), listOf("Rowing Machine")),
            ExercisePreview("kettlebell_swing", "Kettlebell Swing", "Legs", true, listOf("EXPLOSIVE", "CARDIO", "DRIVE", "POSTURE"), listOf("Kettlebell")),
            ExercisePreview("farmer_carry", "Farmer's Carry", "Core", true, listOf("POSTURE", "CORE_SPINE"), listOf("Dumbbell")),
            ExercisePreview("burpees", "Burpees", "Cardio", true, listOf("CARDIO", "BLOOD_FLOW", "EXPLOSIVE", "DRIVE"), listOf("Bodyweight"))
        )
    }

    val muscleGroups = listOf("All", "Chest", "Back", "Shoulders", "Legs", "Arms", "Core", "Cardio")

    val filteredExercises = exercises.filter { ex ->
        (selectedMuscleGroup == null || selectedMuscleGroup == "All" || ex.muscleGroup == selectedMuscleGroup) &&
        (searchQuery.isEmpty() || ex.name.contains(searchQuery, ignoreCase = true))
    }.groupBy { it.muscleGroup }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exercise Library", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search exercises...") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Muscle Group Filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                muscleGroups.forEach { group ->
                    FilterChip(
                        selected = selectedMuscleGroup == group || (selectedMuscleGroup == null && group == "All"),
                        onClick = {
                            selectedMuscleGroup = if (group == "All") null else group
                        },
                        label = { Text(group, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }

            // Exercise List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredExercises.forEach { (group, exercises) ->
                    item {
                        Text(
                            text = "$group (${exercises.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = Primary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(exercises) { exercise ->
                        ExerciseCard(exercise, onClick = { selectedExercise = exercise })
                    }
                }
            }
        }
    }

    // Exercise detail sheet
    selectedExercise?.let { exercise ->
        // Create a minimal ExerciseEntity for the detail sheet
        val entity = ExerciseEntity(
            id = exercise.id,
            name = exercise.name,
            muscleGroup = MuscleGroup.valueOf(exercise.muscleGroup.uppercase()),
            equipmentJson = com.google.gson.Gson().toJson(exercise.equipment),
            difficulty = ExperienceLevel.INTERMEDIATE,
            isCompound = exercise.isCompound,
            instructions = "See full exercise details in the exercise library.",
            tagsJson = com.google.gson.Gson().toJson(exercise.tags),
            alternativesJson = "[]"
        )
        ExerciseDetailSheet(
            exercise = entity,
            onDismiss = { selectedExercise = null }
        )
    }
}

@Composable
fun ExerciseCard(exercise: ExercisePreview, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${if (exercise.isCompound) "Compound" else "Isolation"} • ${exercise.muscleGroup}",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = { /* Info */ }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Rounded.Info, contentDescription = "Info", modifier = Modifier.size(18.dp), tint = OnSurfaceVariant)
                    }
                    IconButton(onClick = { /* Swap */ }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Rounded.SwapHoriz, contentDescription = "Alternatives", modifier = Modifier.size(18.dp), tint = OnSurfaceVariant)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                exercise.tags.forEach { tag ->
                    val color = when (tag) {
                        "POSTURE" -> GoalPosture
                        "CARDIO" -> GoalCardio
                        "EXPLOSIVE" -> GoalExplosive
                        "CORE_SPINE" -> GoalCoreSpine
                        "DRIVE" -> GoalDrive
                        "BLOOD_FLOW" -> GoalBloodFlow
                        "FLEXIBILITY" -> GoalFlexibility
                        else -> OnSurfaceVariant
                    }
                    SuggestionChip(
                        onClick = { },
                        label = { Text(tag, style = MaterialTheme.typography.labelMedium) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = color.copy(alpha = 0.15f),
                            labelColor = color
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Rounded.Build, contentDescription = null, modifier = Modifier.size(14.dp), tint = OnSurfaceVariant)
                Text(
                    exercise.equipment.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}
