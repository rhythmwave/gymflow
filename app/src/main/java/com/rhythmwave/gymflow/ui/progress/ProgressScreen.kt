package com.rhythmwave.gymflow.ui.progress

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rhythmwave.gymflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    navController: androidx.navigation.NavHostController,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val timeRanges = listOf("1W", "1M", "3M", "6M", "1Y", "ALL")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Progress", style = MaterialTheme.typography.titleLarge) })
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
            // Time range selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                timeRanges.forEach { range ->
                    FilterChip(
                        selected = state.selectedTimeRange == range,
                        onClick = { viewModel.setTimeRange(range) },
                        label = { Text(range, style = MaterialTheme.typography.labelMedium) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    label = "Sessions",
                    value = "${state.totalSessions}",
                    icon = Icons.Rounded.FitnessCenter,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Volume",
                    value = formatVolume(state.totalVolume),
                    icon = Icons.Rounded.MonitorWeight,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Streak",
                    value = "${state.streakDays}d",
                    icon = Icons.Rounded.LocalFireDepartment,
                    modifier = Modifier.weight(1f)
                )
            }

            // Volume Chart
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Volume (Total kg lifted)", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (state.volumeData.isNotEmpty() && state.volumeData.any { it.value > 0 }) {
                        VolumeChart(
                            data = state.volumeData,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    } else {
                        EmptyChartPlaceholder("Log workouts to see volume data")
                    }
                }
            }

            // Muscle Group Heatmap
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Muscle Group Heatmap", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (state.muscleGroupVolumes.isNotEmpty()) {
                        MuscleHeatmap(
                            volumes = state.muscleGroupVolumes,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        EmptyChartPlaceholder("Train to see muscle group data")
                    }
                }
            }

            // Personal Records
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Personal Records", style = MaterialTheme.typography.titleMedium)
                        if (state.personalRecords.size > 5) {
                            TextButton(onClick = { /* View all */ }) {
                                Text("View All")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (state.personalRecords.isNotEmpty()) {
                        state.personalRecords.take(5).forEach { pr ->
                            PRRow(pr)
                        }
                    } else {
                        Text(
                            "No PRs yet — complete workouts to track records",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            }

            // Estimated 1RM
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Estimated 1RM", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Epley formula: weight × (1 + reps/30)",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (state.oneRepMaxes.isNotEmpty()) {
                        val maxEst = state.oneRepMaxes.maxOfOrNull { it.estimated1RM } ?: 1.0
                        state.oneRepMaxes.forEach { entry ->
                            OneRepMaxRow(
                                exercise = entry.exerciseName,
                                current = entry.estimated1RM.toFloat(),
                                max = maxEst.toFloat(),
                                displayValue = "${entry.estimated1RM.toInt()}kg"
                            )
                        }
                    } else {
                        EmptyChartPlaceholder("Log heavy sets to estimate 1RM")
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
        }
    }
}

@Composable
fun VolumeChart(
    data: List<VolumeDataPoint>,
    modifier: Modifier = Modifier
) {
    val maxValue = data.maxOfOrNull { it.value }?.coerceAtLeast(1.0) ?: 1.0
    val primaryColor = Primary
    val surfaceColor = Surface

    Canvas(modifier = modifier) {
        val barWidth = size.width / data.size * 0.6f
        val spacing = size.width / data.size

        data.forEachIndexed { index, point ->
            val barHeight = (point.value / maxValue * (size.height - 40f)).toFloat()
            val x = index * spacing + (spacing - barWidth) / 2
            val y = size.height - 40f - barHeight

            // Bar
            drawRoundRect(
                color = primaryColor.copy(alpha = 0.8f),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Label
            drawContext.canvas.nativeCanvas.drawText(
                point.label,
                x + barWidth / 2,
                size.height - 8f,
                android.graphics.Paint().apply {
                    color = 0xFF9E9E9E.toInt()
                    textSize = 28f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}

@Composable
fun MuscleHeatmap(
    volumes: List<MuscleGroupVolume>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        volumes.forEach { mv ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = mv.muscleGroup.replace("_", " ").lowercase()
                        .replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(90.dp)
                )
                LinearProgressIndicator(
                    progress = { mv.progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = getMuscleGroupColor(mv.muscleGroup),
                    trackColor = getMuscleGroupColor(mv.muscleGroup).copy(alpha = 0.15f)
                )
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(60.dp)) {
                    Text("${mv.sets} sets", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    Text(formatVolume(mv.volume), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PRRow(pr: PersonalRecordEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.EmojiEvents, contentDescription = "PR", tint = PrColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(pr.exerciseName, style = MaterialTheme.typography.bodyMedium)
                Text("${pr.daysAgo}d ago", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "${pr.weight}kg × ${pr.reps}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = PrColor
            )
            Text(
                "Est 1RM: ${pr.estimated1RM.toInt()}kg",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
            )
        }
    }
}

@Composable
fun OneRepMaxRow(exercise: String, current: Float, max: Float, displayValue: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = exercise,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(80.dp),
            maxLines = 1
        )
        LinearProgressIndicator(
            progress = { (current / max).coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Primary,
            trackColor = Primary.copy(alpha = 0.2f)
        )
        Text(
            text = displayValue,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Primary,
            modifier = Modifier.width(55.dp)
        )
    }
}

@Composable
fun EmptyChartPlaceholder(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Surface),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Rounded.BarChart, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
        }
    }
}

fun getMuscleGroupColor(group: String): Color {
    return when (group.uppercase()) {
        "CHEST" -> GoalBloodFlow
        "BACK" -> GoalPosture
        "SHOULDERS" -> GoalPosture
        "LEGS" -> GoalExplosive
        "ARMS" -> GoalDrive
        "CORE" -> GoalCoreSpine
        "CARDIO" -> GoalCardio
        else -> Primary
    }
}

fun formatVolume(volume: Double): String {
    return when {
        volume >= 1000000 -> "${"%.1f".format(volume / 1000000)}M"
        volume >= 1000 -> "${"%.1f".format(volume / 1000)}t"
        else -> "${volume.toInt()}kg"
    }
}
