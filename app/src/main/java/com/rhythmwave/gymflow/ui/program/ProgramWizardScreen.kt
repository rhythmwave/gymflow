package com.rhythmwave.gymflow.ui.program

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rhythmwave.gymflow.domain.model.*
import com.rhythmwave.gymflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramWizardScreen(navController: NavHostController) {
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 5

    // Wizard state
    var selectedGoals by remember { mutableStateOf(mapOf<GoalProfile, Int>()) }
    var daysPerWeek by remember { mutableIntStateOf(4) }
    var sessionDuration by remember { mutableIntStateOf(60) }
    var selectedRestDays by remember { mutableStateOf(setOf(2, 5, 6)) } // Wed, Sat, Sun
    var selectedEquipment by remember { mutableStateOf(setOf("barbell", "dumbbell", "cable", "pull_up_bar", "bench", "kettlebell")) }
    var selectedExperience by remember { mutableStateOf(ExperienceLevel.INTERMEDIATE) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Program") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 0) currentStep-- else navController.popBackStack()
                    }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { (currentStep + 1).toFloat() / totalSteps },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = Primary,
                trackColor = Primary.copy(alpha = 0.2f)
            )

            // Step indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Step ${currentStep + 1} of $totalSteps",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariant
                )
                Text(
                    when (currentStep) {
                        0 -> "Goals"
                        1 -> "Schedule"
                        2 -> "Equipment"
                        3 -> "Experience"
                        4 -> "Review"
                        else -> ""
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = Primary
                )
            }

            // Step content
            Box(modifier = Modifier.weight(1f)) {
                when (currentStep) {
                    0 -> GoalSelectionStep(selectedGoals) { selectedGoals = it }
                    1 -> ScheduleStep(daysPerWeek, sessionDuration, selectedRestDays,
                        { daysPerWeek = it }, { sessionDuration = it }, { selectedRestDays = it })
                    2 -> EquipmentStep(selectedEquipment) { selectedEquipment = it }
                    3 -> ExperienceStep(selectedExperience) { selectedExperience = it }
                    4 -> ReviewStep(
                        goals = selectedGoals,
                        daysPerWeek = daysPerWeek,
                        sessionDuration = sessionDuration,
                        restDays = selectedRestDays,
                        equipment = selectedEquipment,
                        experience = selectedExperience
                    )
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Previous")
                    }
                }
                Button(
                    onClick = {
                        if (currentStep < totalSteps - 1) {
                            currentStep++
                        } else {
                            // Generate program and navigate back
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentStep == totalSteps - 1) Success else Primary
                    )
                ) {
                    Text(if (currentStep == totalSteps - 1) "Generate Program" else "Next")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        if (currentStep == totalSteps - 1) Icons.Rounded.Check else Icons.Rounded.ArrowForward,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun GoalSelectionStep(
    selectedGoals: Map<GoalProfile, Int>,
    onGoalsChanged: (Map<GoalProfile, Int>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "What do you want to achieve?",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            "Toggle goals and set priority (1-5 stars)",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        GoalProfile.entries.forEach { goal ->
            val isSelected = goal in selectedGoals
            val priority = selectedGoals[goal] ?: 3

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val newGoals = if (isSelected) {
                            selectedGoals - goal
                        } else {
                            selectedGoals + (goal to 3)
                        }
                        onGoalsChanged(newGoals)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) goal.colorHex.copy(alpha = 0.15f) else SurfaceVariant
                ),
                border = if (isSelected) {
                    androidx.compose.foundation.BorderStroke(2.dp, goal.colorHex)
                } else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { checked ->
                            val newGoals = if (checked) {
                                selectedGoals + (goal to 3)
                            } else {
                                selectedGoals - goal
                            }
                            onGoalsChanged(newGoals)
                        },
                        colors = CheckboxDefaults.colors(checkedColor = goal.colorHex)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "${goal.icon} ${goal.displayName}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            when (goal) {
                                GoalProfile.POSTURE_FIX -> "Posterior chain, mobility, alignment"
                                GoalProfile.CARDIO_VASCULAR -> "Blood flow, endurance, circulation"
                                GoalProfile.HEART_RATE -> "Zone 2 cardio + HIIT"
                                GoalProfile.BURST_ENDURANCE -> "Explosive power + muscular endurance"
                                GoalProfile.ABDOMEN_SHAPE -> "Upper & lower abs definition"
                                GoalProfile.CORE_SPINE -> "Anti-rotation, spinal stability"
                                GoalProfile.SEX_DRIVE -> "Compound lifts, recovery, vitality"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }

                // Priority stars (only when selected)
                if (isSelected) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 56.dp, end = 16.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Priority: ", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        Spacer(modifier = Modifier.width(8.dp))
                        (1..5).forEach { star ->
                            Icon(
                                imageVector = if (star <= priority) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                                contentDescription = "Star $star",
                                tint = if (star <= priority) PrColor else OnSurfaceVariant,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable {
                                        onGoalsChanged(selectedGoals + (goal to star))
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleStep(
    daysPerWeek: Int,
    sessionDuration: Int,
    restDays: Set<Int>,
    onDaysChanged: (Int) -> Unit,
    onDurationChanged: (Int) -> Unit,
    onRestDaysChanged: (Set<Int>) -> Unit
) {
    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("When can you train?", style = MaterialTheme.typography.headlineLarge)

        // Days per week
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Days per week", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (2..6).forEach { days ->
                        val selected = days == daysPerWeek
                        FilterChip(
                            selected = selected,
                            onClick = { onDaysChanged(days) },
                            label = {
                                Text(
                                    "$days",
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Session duration
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Session duration", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(30, 45, 60, 90).forEach { dur ->
                        val selected = dur == sessionDuration
                        FilterChip(
                            selected = selected,
                            onClick = { onDurationChanged(dur) },
                            label = { Text("${dur}min") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Rest days
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Rest days", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    dayNames.forEachIndexed { index, name ->
                        val isRest = index in restDays
                        val selected = isRest
                        FilterChip(
                            selected = selected,
                            onClick = {
                                val newRest = if (isRest) restDays - index else restDays + index
                                onRestDaysChanged(newRest)
                            },
                            label = { Text(name, style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Secondary.copy(alpha = 0.3f),
                                selectedLabelColor = Secondary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "${7 - restDays.size} training days selected",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EquipmentStep(
    selectedEquipment: Set<String>,
    onEquipmentChanged: (Set<String>) -> Unit
) {
    val equipmentGroups = mapOf(
        "Free Weights" to listOf("barbell", "dumbbell", "kettlebell"),
        "Machines" to listOf("cable", "leg_press", "chest_press", "lat_pulldown", "rowing_machine"),
        "Bodyweight" to listOf("bodyweight", "pull_up_bar", "dip_bars"),
        "Accessories" to listOf("bench", "squat_rack", "resistance_bands", "foam_roller"),
        "Cardio" to listOf("bike", "treadmill", "rowing_machine", "jump_rope")
    )

    val equipmentLabels = mapOf(
        "barbell" to "🏋️ Barbell",
        "dumbbell" to "💪 Dumbbell",
        "kettlebell" to "🔔 Kettlebell",
        "cable" to "🔗 Cable Machine",
        "bodyweight" to "🤸 Bodyweight",
        "pull_up_bar" to "🏋️ Pull-up Bar",
        "dip_bars" to "💪 Dip Bars",
        "bench" to "🪑 Bench",
        "squat_rack" to "🏗️ Squat Rack",
        "leg_press" to "🦵 Leg Press",
        "chest_press" to "🏋️ Chest Press",
        "lat_pulldown" to "⬇️ Lat Pulldown",
        "rowing_machine" to "🚣 Rowing Machine",
        "bike" to "🚴 Bike",
        "treadmill" to "🏃 Treadmill",
        "jump_rope" to "⚡ Jump Rope",
        "resistance_bands" to "🔴 Resistance Bands",
        "foam_roller" to "🧘 Foam Roller"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("What equipment do you have?", style = MaterialTheme.typography.headlineLarge)
        Text("Select all available", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)

        // Quick select buttons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = {
                onEquipmentChanged(setOf("bodyweight", "pull_up_bar"))
            }) {
                Text("Home Only")
            }
            OutlinedButton(onClick = {
                onEquipmentChanged(equipmentGroups.values.flatten().distinct().toSet())
            }) {
                Text("Full Gym")
            }
        }

        equipmentGroups.forEach { (group, items) ->
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(group, style = MaterialTheme.typography.titleMedium, color = Primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    items.forEach { item ->
                        val isSelected = item in selectedEquipment
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val new = if (isSelected) selectedEquipment - item else selectedEquipment + item
                                    onEquipmentChanged(new)
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    val new = if (checked) selectedEquipment + item else selectedEquipment - item
                                    onEquipmentChanged(new)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                equipmentLabels[item] ?: item,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExperienceStep(
    selected: ExperienceLevel,
    onSelected: (ExperienceLevel) -> Unit
) {
    val levels = listOf(
        ExperienceLevel.BEGINNER to "🟢" to "Beginner" to "< 6 months of consistent training\nLearning basic compound movements",
        ExperienceLevel.INTERMEDIATE to "🔵" to "Intermediate" to "6 months - 2 years of training\nComfortable with main lifts",
        ExperienceLevel.ADVANCED to "🟣" to "Advanced" to "2+ years of consistent training\nProgramming own routines"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("What's your experience level?", style = MaterialTheme.typography.headlineLarge)

        val experienceOptions = listOf(
            Triple(ExperienceLevel.BEGINNER, "🟢 Beginner", "< 6 months of consistent training\nLearning basic compound movements"),
            Triple(ExperienceLevel.INTERMEDIATE, "🔵 Intermediate", "6 months - 2 years of training\nComfortable with main lifts"),
            Triple(ExperienceLevel.ADVANCED, "🟣 Advanced", "2+ years of consistent training\nProgramming own routines")
        )

        experienceOptions.forEach { (level, title, description) ->
            val isSelected = level == selected
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelected(level) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Primary.copy(alpha = 0.15f) else SurfaceVariant
                ),
                border = if (isSelected) {
                    androidx.compose.foundation.BorderStroke(2.dp, Primary)
                } else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelected(level) },
                        colors = RadioButtonDefaults.colors(selectedColor = Primary)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(title, style = MaterialTheme.typography.titleMedium)
                        Text(description, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewStep(
    goals: Map<GoalProfile, Int>,
    daysPerWeek: Int,
    sessionDuration: Int,
    restDays: Set<Int>,
    equipment: Set<String>,
    experience: ExperienceLevel
) {
    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Review Your Program", style = MaterialTheme.typography.headlineLarge)

        // Goals summary
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Goals", style = MaterialTheme.typography.titleMedium, color = Primary)
                Spacer(modifier = Modifier.height(8.dp))
                goals.entries.sortedByDescending { it.value }.forEach { (goal, priority) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${goal.icon} ${goal.displayName}", style = MaterialTheme.typography.bodyMedium)
                        Text("★".repeat(priority), color = PrColor, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Schedule summary
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Schedule", style = MaterialTheme.typography.titleMedium, color = Primary)
                Spacer(modifier = Modifier.height(8.dp))
                ReviewRow("Training days", "$daysPerWeek days/week")
                ReviewRow("Session duration", "$sessionDuration min")
                ReviewRow("Rest days", restDays.sorted().map { dayNames[it] }.joinToString(", "))
                ReviewRow("Estimated exercises/day", "6-8")
            }
        }

        // Equipment summary
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Equipment", style = MaterialTheme.typography.titleMedium, color = Primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    equipment.joinToString(", ") { it.replace("_", " ").replaceFirstChar { c -> c.uppercase() } },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Experience
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Level", style = MaterialTheme.typography.titleMedium, color = Primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    when (experience) {
                        ExperienceLevel.BEGINNER -> "🟢 Beginner"
                        ExperienceLevel.INTERMEDIATE -> "🔵 Intermediate"
                        ExperienceLevel.ADVANCED -> "🟣 Advanced"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Recommendation
        Card(colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f))) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = Primary)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Recommended Split", style = MaterialTheme.typography.titleMedium, color = Primary)
                    Text(
                        when {
                            daysPerWeek <= 2 -> "Full Body A/B"
                            daysPerWeek == 3 -> "Push/Pull/Legs"
                            daysPerWeek == 4 -> "Upper/Lower"
                            daysPerWeek == 5 -> "Bro Split"
                            else -> "PPL × 2"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
