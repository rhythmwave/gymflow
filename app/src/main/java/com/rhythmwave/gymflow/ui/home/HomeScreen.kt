package com.rhythmwave.gymflow.ui.home

import androidx.compose.foundation.background
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
import com.rhythmwave.gymflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    var selectedCheckIn by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Good evening, Rhythmwave",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Settings */ }) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Check-in Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "How are you today?",
                        style = MaterialTheme.typography.titleMedium,
                        color = Success
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CheckInButton(
                            label = "PUSH",
                            subtitle = "Full go",
                            icon = Icons.Rounded.FitnessCenter,
                            color = Primary,
                            selected = selectedCheckIn == "push",
                            onClick = { selectedCheckIn = "push" },
                            modifier = Modifier.weight(1f)
                        )
                        CheckInButton(
                            label = "LIGHT",
                            subtitle = "Easy day",
                            icon = Icons.Rounded.WbSunny,
                            color = Secondary,
                            selected = selectedCheckIn == "light",
                            onClick = { selectedCheckIn = "light" },
                            modifier = Modifier.weight(1f)
                        )
                        CheckInButton(
                            label = "REST",
                            subtitle = "Recovery",
                            icon = Icons.Rounded.SelfImprovement,
                            color = OnSurfaceVariant,
                            selected = selectedCheckIn == "rest",
                            onClick = { selectedCheckIn = "rest" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Today's Plan Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Day 2 — Lower + Explosive",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "~55 min  •  7 exercises",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Exercise preview list
                    val exercises = listOf(
                        "Barbell Squat" to "4×6",
                        "Romanian Deadlift" to "3×10",
                        "Box Jumps" to "4×5",
                        "Hip Thrusts" to "3×12",
                        "Walking Lunges" to "3×12/leg",
                        "Calf Raises" to "4×15",
                        "HIIT Bike" to "8×30s"
                    )
                    exercises.forEach { (name, sets) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface
                            )
                            Text(
                                text = sets,
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { /* Edit */ }) {
                            Text("Edit")
                        }
                        Button(
                            onClick = { /* Start Workout */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Start Workout")
                        }
                    }
                }
            }

            // Weekly Goals Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Weekly Goals",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GoalProgress("Cardio", 2, 3, GoalCardio)
                    GoalProgress("Posture", 2, 3, GoalPosture)
                    GoalProgress("Strength", 2, 3, Primary)
                    GoalProgress("Core", 2, 3, GoalCoreSpine)
                    GoalProgress("Explosive", 3, 3, GoalExplosive)
                }
            }

            // Quick Stats Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("🔥", "12 day", "Streak")
                    StatItem("📊", "45.2t", "Volume")
                    StatItem("😴", "7.1h", "Avg Sleep")
                    StatItem("💪", "2 PRs", "This Week")
                }
            }
        }
    }
}

@Composable
fun CheckInButton(
    label: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) color.copy(alpha = 0.3f) else Surface,
            contentColor = if (selected) color else OnSurfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun GoalProgress(name: String, current: Int, target: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(80.dp)
        )
        LinearProgressIndicator(
            progress = { current.toFloat() / target.toFloat() },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
        Text(
            text = "$current/$target",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant
        )
        if (current >= target) {
            Icon(
                Icons.Rounded.CheckCircle,
                contentDescription = "Complete",
                tint = Success,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun StatItem(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, style = MaterialTheme.typography.titleLarge)
        Text(text = value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
    }
}
