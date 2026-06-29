package com.rhythmwave.gymflow.ui.workout

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
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
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(state.dayName, style = MaterialTheme.typography.titleMedium)
                        Text(
                            formatTime(state.elapsedSeconds),
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(Icons.Rounded.Close, contentDescription = "Exit")
                    }
                },
                actions = {
                    Text(
                        "${state.currentExerciseIndex + 1}/${state.exercises.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = OnSurfaceVariant,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        }
    ) { padding ->
        if (currentExercise == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No exercises", style = MaterialTheme.typography.bodyLarge, color = OnSurfaceVariant)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Exercise info
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = currentExercise.exerciseName,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${currentExercise.targetSets} sets × ${currentExercise.targetReps}" +
                            (currentExercise.targetRepsMax?.let { "-$it" } ?: "") +
                            " reps  •  ${currentExercise.muscleGroup}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                    if (currentExercise.isFinisher) {
                        Spacer(modifier = Modifier.height(4.dp))
                        SuggestionChip(
                            onClick = {},
                            label = { Text("FINISHER") },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = GoalExplosive.copy(alpha = 0.2f),
                                labelColor = GoalExplosive
                            )
                        )
                    }
                }
            }

            // Sets progress
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    (1..currentExercise.targetSets).forEach { setNum ->
                        val completed = currentExercise.completedSets.getOrNull(setNum - 1)
                        val isCurrent = setNum == currentExercise.currentSetNumber && !currentExercise.allSetsDone
                        val isDone = completed != null

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Set number
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
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
                                    Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                } else {
                                    Text(
                                        "$setNum",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }

                            // Status
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    when {
                                        isDone -> "Set $setNum — Done"
                                        isCurrent -> "Set $setNum — Current"
                                        else -> "Set $setNum — Pending"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
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

                            // Action button for current set
                            if (isCurrent) {
                                Button(
                                    onClick = { viewModel.showSetLogger() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                    modifier = Modifier.height(40.dp)
                                ) {
                                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Done")
                                }
                            }
                        }
                    }
                }
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.showSwapSheet() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.SwapHoriz, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Swap")
                }
                OutlinedButton(
                    onClick = { viewModel.skipExercise() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.SkipNext, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Skip")
                }
            }

            // Rest timer (always visible area)
            RestTimerCard()

            // Coming up
            val upcomingExercises = state.exercises.drop(state.currentExerciseIndex + 1).take(4)
            if (upcomingExercises.isNotEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Coming Up", style = MaterialTheme.typography.titleMedium, color = OnSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        upcomingExercises.forEach { ex ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(ex.exerciseName, style = MaterialTheme.typography.bodyMedium)
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

            // Bottom actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* Add exercise */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Exercise")
                }
                Button(
                    onClick = { viewModel.finishWorkout() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Success)
                ) {
                    Icon(Icons.Rounded.Flag, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Finish Workout")
                }
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
            onLog = { weight, reps, rpe, notes ->
                viewModel.logSet(weight, reps, rpe, false, notes)
            },
            onDismiss = { viewModel.hideSetLogger() }
        )
    }
}

@Composable
fun RestTimerCard() {
    // This is a placeholder — actual timer state comes from the service
    Card(colors = CardDefaults.cardColors(containerColor = Surface)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Rest Timer", style = MaterialTheme.typography.titleMedium, color = OnSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            // Timer circle
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { 0f },
                    modifier = Modifier.fillMaxSize(),
                    color = RestTimerColor,
                    trackColor = RestTimerColor.copy(alpha = 0.2f),
                    strokeWidth = 8.dp
                )
                Text(
                    "0:00",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { /* +30s */ }) {
                    Text("+30s")
                }
                OutlinedButton(onClick = { /* Skip */ }) {
                    Text("Skip Rest")
                }
            }
        }
    }
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
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Set & Rest", style = MaterialTheme.typography.titleMedium)
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

fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%d:%02d".format(mins, secs)
}
