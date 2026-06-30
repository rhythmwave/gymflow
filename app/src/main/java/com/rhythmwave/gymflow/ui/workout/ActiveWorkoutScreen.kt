package com.rhythmwave.gymflow.ui.workout

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rhythmwave.gymflow.service.TimerState
import com.rhythmwave.gymflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    onExit: () -> Unit,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isWorkoutComplete) {
        WorkoutSummaryScreen(
            state = state,
            onRatingSelected = { viewModel.setWorkoutRating(it) },
            onFinish = onExit
        )
        return
    }

    val currentExercise = state.exercises.getOrNull(state.currentExerciseIndex)

    Scaffold(
        containerColor = Background
    ) { padding ->
        if (currentExercise == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.FitnessCenter, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No exercises", style = MaterialTheme.typography.bodyLarge, color = OnSurfaceVariant)
                }
            }
            return@Scaffold
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onExit) {
                    Icon(Icons.Rounded.Close, contentDescription = "Exit", tint = OnSurface)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        state.dayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        formatTime(state.elapsedSeconds),
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
                Text(
                    "${state.currentExerciseIndex + 1}/${state.exercises.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Progress bar
            LinearProgressIndicator(
                progress = { (state.currentExerciseIndex + 1).toFloat() / state.exercises.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = Primary,
                trackColor = Primary.copy(alpha = 0.15f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Exercise Hero Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Primary.copy(alpha = 0.2f),
                                        PrimaryDark.copy(alpha = 0.1f)
                                    )
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (currentExercise.isFinisher) {
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text("FINISHER", fontWeight = FontWeight.Bold) },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = GoalExplosive.copy(alpha = 0.3f),
                                            labelColor = GoalExplosive
                                        )
                                    )
                                }
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(currentExercise.muscleGroup) },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = OnSurfaceVariant.copy(alpha = 0.2f),
                                        labelColor = OnSurfaceVariant
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                currentExercise.exerciseName,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "${currentExercise.targetSets} sets × ${currentExercise.targetReps}" +
                                    (currentExercise.targetRepsMax?.let { "-$it" } ?: "") + " reps",
                                style = MaterialTheme.typography.bodyLarge,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }

                // Sets Progress
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Sets",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        (1..currentExercise.targetSets).forEach { setNum ->
                            val completed = currentExercise.completedSets.getOrNull(setNum - 1)
                            val isCurrent = setNum == currentExercise.currentSetNumber && !currentExercise.allSetsDone
                            val isDone = completed != null

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when {
                                            isCurrent -> Primary.copy(alpha = 0.1f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Set indicator
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isDone -> Success
                                                isCurrent -> Primary
                                                else -> Divider
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text(
                                            "$setNum",
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Info
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Set $setNum",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (isDone) {
                                        Text(
                                            "${completed!!.weight}kg × ${completed.reps}" +
                                                (completed.rpe?.let { "  RPE $it" } ?: ""),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = OnSurfaceVariant
                                        )
                                    }
                                }

                                // Action
                                if (isCurrent) {
                                    Button(
                                        onClick = { viewModel.showSetLogger() },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                        modifier = Modifier.height(44.dp)
                                    ) {
                                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Done", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            if (setNum < currentExercise.targetSets) {
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }

                // Rest Timer
                RestTimerCard()

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ActionButton(
                        icon = Icons.Rounded.SwapHoriz,
                        label = "Swap",
                        onClick = { viewModel.showSwapSheet() },
                        modifier = Modifier.weight(1f)
                    )
                    ActionButton(
                        icon = Icons.Rounded.SkipNext,
                        label = "Skip",
                        onClick = { viewModel.skipExercise() },
                        modifier = Modifier.weight(1f)
                    )
                    ActionButton(
                        icon = Icons.Rounded.Add,
                        label = "Add",
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Coming Up
                val upcoming = state.exercises.drop(state.currentExerciseIndex + 1).take(3)
                if (upcoming.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "Coming Up",
                                style = MaterialTheme.typography.titleMedium,
                                color = OnSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            upcoming.forEachIndexed { index, ex ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(Divider),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "${state.currentExerciseIndex + index + 2}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = OnSurfaceVariant
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(ex.exerciseName, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Text(
                                        "${ex.targetSets}×${ex.targetReps}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Finish button
                Button(
                    onClick = { viewModel.finishWorkout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Success)
                ) {
                    Icon(Icons.Rounded.Flag, contentDescription = null, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Finish Workout",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Set Logger Bottom Sheet
    if (state.showSetLogger) {
        SetLoggerSheet(
            exerciseName = currentExercise?.exerciseName ?: "",
            setNumber = currentExercise?.currentSetNumber ?: 1,
            lastWeight = currentExercise?.completedSets?.lastOrNull()?.weight,
            lastReps = currentExercise?.completedSets?.lastOrNull()?.reps,
            onLog = { weight: Double, reps: Int, rpe: Int?, notes: String? ->
                viewModel.logSet(weight, reps, rpe, false, notes)
            },
            onDismiss = { viewModel.hideSetLogger() }
        )
    }
}

@Composable
fun RestTimerCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Rest Timer",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Timer circle
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 8.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Background circle
                    drawCircle(
                        color = RestTimerColor.copy(alpha = 0.15f),
                        radius = radius,
                        center = center,
                        style = Stroke(strokeWidth)
                    )
                }
                Text(
                    "0:00",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+30s")
                }
                OutlinedButton(
                    onClick = { },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Skip Rest")
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}

fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%d:%02d".format(mins, secs)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetLoggerSheet(
    exerciseName: String,
    setNumber: Int,
    lastWeight: Double?,
    lastReps: Int?,
    onLog: (Double, Int, Int?, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var weight by remember { mutableStateOf(lastWeight?.toInt()?.toString() ?: "0") }
    var reps by remember { mutableStateOf(lastReps?.toString() ?: "8") }
    var selectedRpe by remember { mutableStateOf<Int?>(null) }
    var notes by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Log Set $setNumber — $exerciseName",
                style = MaterialTheme.typography.titleMedium
            )

            // Weight
            Column {
                Text("WEIGHT (kg)", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = {
                            val w = (weight.toDoubleOrNull() ?: 0.0) - 2.5
                            weight = w.coerceAtLeast(0.0).toInt().toString()
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Rounded.Remove, contentDescription = "Decrease")
                    }
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        modifier = Modifier.width(100.dp),
                        textStyle = MaterialTheme.typography.headlineLarge.copy(textAlign = TextAlign.Center),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            val w = (weight.toDoubleOrNull() ?: 0.0) + 2.5
                            weight = w.toInt().toString()
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = "Increase")
                    }
                }
            }

            // Reps
            Column {
                Text("REPS", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = {
                            val r = (reps.toIntOrNull() ?: 0) - 1
                            reps = r.coerceAtLeast(0).toString()
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Rounded.Remove, contentDescription = "Decrease")
                    }
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it },
                        modifier = Modifier.width(100.dp),
                        textStyle = MaterialTheme.typography.headlineLarge.copy(textAlign = TextAlign.Center),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            val r = (reps.toIntOrNull() ?: 0) + 1
                            reps = r.toString()
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = "Increase")
                    }
                }
            }

            // RPE
            Column {
                Text("RPE (optional)", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    (4..10).forEach { rpe ->
                        val selected = rpe == selectedRpe
                        FilterChip(
                            selected = selected,
                            onClick = { selectedRpe = if (selected) null else rpe },
                            label = { Text("$rpe", style = MaterialTheme.typography.labelMedium) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when {
                                    rpe <= 6 -> Success
                                    rpe <= 8 -> Secondary
                                    else -> Error
                                }.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Notes (optional)") },
                singleLine = true
            )

            // Log button
            Button(
                onClick = {
                    val w = weight.toDoubleOrNull() ?: 0.0
                    val r = reps.toIntOrNull() ?: 0
                    onLog(w, r, selectedRpe, notes.ifBlank { null })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Set & Rest", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            // Log without rest
            TextButton(
                onClick = {
                    val w = weight.toDoubleOrNull() ?: 0.0
                    val r = reps.toIntOrNull() ?: 0
                    onLog(w, r, selectedRpe, notes.ifBlank { null })
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log without rest timer", color = OnSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
