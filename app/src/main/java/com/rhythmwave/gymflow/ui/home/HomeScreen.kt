package com.rhythmwave.gymflow.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.rhythmwave.gymflow.ui.theme.*
import com.rhythmwave.gymflow.ui.workout.CheckInDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var selectedCheckIn by remember { mutableStateOf<String?>(null) }
    var showCheckInDialog by remember { mutableStateOf(false) }
    var greeting by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        greeting = when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..20 -> "Good evening"
            else -> "Good night"
        }
    }

    // Refresh data when returning to screen
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        containerColor = Background
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.15f),
                                Background
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Text(
                        text = "$greeting 👋",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (state.isRestDay) "Rest day — recover well" else "Ready to crush it today?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurfaceVariant
                    )
                }
            }

            // Daily Energy Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Primary, PrimaryDark)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            "Today's Energy",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            EnergyButton(
                                emoji = "💪",
                                label = "PUSH",
                                subtitle = "Full intensity",
                                isSelected = selectedCheckIn == "push",
                                onClick = {
                                    selectedCheckIn = "push"
                                    showCheckInDialog = true
                                }
                            )
                            EnergyButton(
                                emoji = "🌤",
                                label = "LIGHT",
                                subtitle = "Easy session",
                                isSelected = selectedCheckIn == "light",
                                onClick = {
                                    selectedCheckIn = "light"
                                    showCheckInDialog = true
                                }
                            )
                            EnergyButton(
                                emoji = "😴",
                                label = "REST",
                                subtitle = "Recover",
                                isSelected = selectedCheckIn == "rest",
                                onClick = {
                                    selectedCheckIn = "rest"
                                    showCheckInDialog = true
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Today's Workout (real data)
            if (state.dayWorkout != null) {
                SectionHeader(title = "Today's Workout", action = "Edit")
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    state.dayWorkout!!.dayName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Timer, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("~${state.dayWorkout!!.estimatedMinutes} min", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Icon(Icons.Rounded.FitnessCenter, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${state.dayWorkout!!.exercises.size} exercises", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Exercise chips (real data)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            state.dayWorkout!!.exercises.take(5).forEach { ex ->
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(ex.name, style = MaterialTheme.typography.labelSmall) },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = Surface
                                    )
                                )
                            }
                            if (state.dayWorkout!!.exercises.size > 5) {
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text("+${state.dayWorkout!!.exercises.size - 5}", style = MaterialTheme.typography.labelSmall) },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = Primary.copy(alpha = 0.2f),
                                        labelColor = Primary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { navController.navigate("active_workout") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Start Workout",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else if (state.isRestDay) {
                // Rest day card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("😴", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Rest Day",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Recovery is part of training. Get 8+ hours of sleep, stay hydrated, and stretch if you feel like it.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Weekly Progress (real data)
            SectionHeader(title = "This Week", action = "See All")
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WeeklyStatCard(
                    icon = "🔥",
                    value = "${state.streak}",
                    label = "Day Streak",
                    color = StreakColor,
                    modifier = Modifier.weight(1f)
                )
                WeeklyStatCard(
                    icon = "📊",
                    value = formatVolume(state.weekVolume),
                    label = "Volume",
                    color = Tertiary,
                    modifier = Modifier.weight(1f)
                )
                WeeklyStatCard(
                    icon = "💪",
                    value = "${state.weekPRs}",
                    label = "New PRs",
                    color = PrColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Goal Rings (real data)
            if (state.weeklyGoals.isNotEmpty()) {
                SectionHeader(title = "Weekly Goals", action = "Details")
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        state.weeklyGoals.forEachIndexed { index, goal ->
                            GoalProgressRow(
                                goal.name,
                                goal.current,
                                goal.target,
                                Color(goal.color)
                            )
                            if (index < state.weeklyGoals.size - 1) {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Actions
            SectionHeader(title = "Quick Actions", action = null)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    icon = Icons.Rounded.BarChart,
                    label = "Progress",
                    onClick = { navController.navigate("progress") },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Rounded.FitnessCenter,
                    label = "Exercises",
                    onClick = { navController.navigate("workout") },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Rounded.ListAlt,
                    label = "Programs",
                    onClick = { navController.navigate("programs") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Check-in dialog
    if (showCheckInDialog) {
        CheckInDialog(
            onDismiss = { showCheckInDialog = false },
            onConfirm = { result ->
                showCheckInDialog = false
                when (result.recommendation) {
                    com.rhythmwave.gymflow.domain.model.DailyRecommendation.PUSH,
                    com.rhythmwave.gymflow.domain.model.DailyRecommendation.LIGHT -> {
                        navController.navigate("active_workout")
                    }
                    else -> {}
                }
            }
        )
    }
}

fun formatVolume(volume: Double): String {
    return when {
        volume >= 1000000 -> "${"%.1f".format(volume / 1000000)}M"
        volume >= 1000 -> "${"%.1f".format(volume / 1000)}t"
        volume > 0 -> "${volume.toInt()}kg"
        else -> "0kg"
    }
}

@Composable
fun SectionHeader(title: String, action: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        if (action != null) {
            Text(
                action,
                style = MaterialTheme.typography.labelLarge,
                color = Primary,
                modifier = Modifier.clickable { }
            )
        }
    }
}

@Composable
fun EnergyButton(
    emoji: String,
    label: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Text(emoji, fontSize = 32.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun WeeklyStatCard(
    icon: String,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
            )
        }
    }
}

@Composable
fun GoalProgressRow(name: String, current: Int, target: Int, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            name,
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
            trackColor = color.copy(alpha = 0.15f)
        )
        Text(
            "$current/$target",
            style = MaterialTheme.typography.labelMedium,
            color = if (current >= target) color else OnSurfaceVariant,
            fontWeight = if (current >= target) FontWeight.Bold else FontWeight.Normal
        )
        if (current >= target) {
            Icon(
                Icons.Rounded.CheckCircle,
                contentDescription = "Complete",
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
