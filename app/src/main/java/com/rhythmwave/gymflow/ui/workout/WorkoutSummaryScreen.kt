package com.rhythmwave.gymflow.ui.workout

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rhythmwave.gymflow.ui.theme.*

@Composable
fun WorkoutSummaryScreen(
    state: WorkoutUiState,
    onRatingSelected: (Int) -> Unit,
    onFinish: () -> Unit
) {
    var selectedRating by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Celebration
        Icon(
            Icons.Rounded.EmojiEvents,
            contentDescription = null,
            tint = PrColor,
            modifier = Modifier.size(64.dp)
        )
        Text(
            "Workout Complete!",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = Primary
        )

        // Stats card
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(state.dayName, style = MaterialTheme.typography.titleMedium, color = Primary)
                Spacer(modifier = Modifier.height(4.dp))
                StatRow(Icons.Rounded.Timer, "Duration", formatTime(state.elapsedSeconds))
                StatRow(Icons.Rounded.FitnessCenter, "Volume", "${"%.0f".format(state.totalVolume)} kg")
                StatRow(Icons.Rounded.CheckCircle, "Sets", "${state.completedSets}/${state.totalSets}")
                StatRow(Icons.Rounded.LocalFireDepartment, "Est. Calories", "${(state.totalVolume * 0.05).toInt()} kcal")
            }
        }

        // PR detection (placeholder)
        Card(colors = CardDefaults.cardColors(containerColor = PrColor.copy(alpha = 0.1f))) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.EmojiEvents, contentDescription = null, tint = PrColor)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Personal Records", style = MaterialTheme.typography.titleMedium, color = PrColor)
                    Text(
                        "Check progress tab for details",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
            }
        }

        // Goal progress
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Goal Progress", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                // Placeholder — real data from workout tags
                GoalProgressRow("Strength", 0.8f, Primary)
                GoalProgressRow("Core", 0.6f, GoalCoreSpine)
                GoalProgressRow("Cardio", 0.4f, GoalCardio)
            }
        }

        // Rating
        Card(colors = CardDefaults.cardColors(containerColor = SurfaceVariant)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("How was this workout?", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        1 to "😰\nHard",
                        2 to "😐\nOkay",
                        3 to "😊\nJust Right",
                        4 to "💪\nGood",
                        5 to "🤩\nEasy"
                    ).forEach { (rating, label) ->
                        val selected = rating == selectedRating
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selectedRating = rating
                                onRatingSelected(rating)
                            },
                            label = {
                                Text(label, style = MaterialTheme.typography.bodySmall)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        }

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onFinish,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Icon(Icons.Rounded.Home, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Home")
            }
            OutlinedButton(
                onClick = { /* Navigate to progress */ },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Rounded.TrendingUp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Progress")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StatRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
        }
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GoalProgressRow(name: String, progress: Float, color: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(70.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
        Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
    }
}
