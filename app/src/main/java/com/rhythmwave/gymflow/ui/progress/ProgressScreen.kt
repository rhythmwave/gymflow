package com.rhythmwave.gymflow.ui.progress

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
fun ProgressScreen(navController: NavHostController) {
    var selectedTimeRange by remember { mutableStateOf("1M") }
    val timeRanges = listOf("1W", "1M", "3M", "6M", "1Y", "ALL")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress", style = MaterialTheme.typography.titleLarge) }
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
            // Time Range Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                timeRanges.forEach { range ->
                    FilterChip(
                        selected = selectedTimeRange == range,
                        onClick = { selectedTimeRange = range },
                        label = { Text(range, style = MaterialTheme.typography.labelMedium) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Volume Chart Placeholder
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Volume (Total kg lifted)", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    // Chart placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Rounded.BarChart,
                                contentDescription = null,
                                tint = OnSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Volume chart",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant
                            )
                            Text(
                                "Log workouts to see data",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Muscle Heatmap Placeholder
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Muscle Group Heatmap", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    // Simple body representation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MuscleGroup("Shoulders", 0.7f, GoalPosture)
                        MuscleGroup("Chest", 0.5f, GoalBloodFlow)
                        MuscleGroup("Back", 0.8f, GoalPosture)
                        MuscleGroup("Core", 0.6f, GoalCoreSpine)
                        MuscleGroup("Legs", 0.9f, GoalExplosive)
                        MuscleGroup("Arms", 0.4f, GoalDrive)
                    }
                }
            }

            // Personal Records
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
                        Text("Personal Records", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { /* View All */ }) {
                            Text("View All")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    val prs = listOf(
                        Triple("Squat", "80kg × 6", "2 days ago"),
                        Triple("Deadlift", "90kg × 5", "1 week ago"),
                        Triple("Bench", "55kg × 8", "3 days ago"),
                        Triple("Plank", "70s", "5 days ago")
                    )
                    prs.forEach { (exercise, record, time) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Rounded.EmojiEvents,
                                    contentDescription = "PR",
                                    tint = PrColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(exercise, style = MaterialTheme.typography.bodyMedium)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    record,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrColor
                                )
                                Text(
                                    time,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Estimated 1RM
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Estimated 1RM", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    OneRepMaxRow("Squat", 95f, 120f)
                    OneRepMaxRow("Deadlift", 105f, 130f)
                    OneRepMaxRow("Bench", 68f, 100f)
                    OneRepMaxRow("OHP", 45f, 70f)
                }
            }
        }
    }
}

@Composable
fun MuscleGroup(name: String, progress: Float, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = progress)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.FitnessCenter,
                contentDescription = null,
                tint = Color.White.copy(alpha = progress),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(name, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
    }
}

@Composable
fun OneRepMaxRow(exercise: String, current: Float, max: Float) {
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
            modifier = Modifier.width(80.dp)
        )
        LinearProgressIndicator(
            progress = { current / max },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Primary,
            trackColor = Primary.copy(alpha = 0.2f)
        )
        Text(
            text = "${current.toInt()}kg",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
    }
}
