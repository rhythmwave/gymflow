package com.rhythmwave.gymflow.ui.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rhythmwave.gymflow.domain.model.DailyRecommendation
import com.rhythmwave.gymflow.domain.model.SorenessLevel
import com.rhythmwave.gymflow.ui.theme.*

data class CheckInResult(
    val sleepQuality: Int,
    val energyLevel: Int,
    val soreness: SorenessLevel,
    val sorenessLocation: String?,
    val availableMinutes: Int,
    val recommendation: DailyRecommendation
)

@Composable
fun CheckInDialog(
    onDismiss: () -> Unit,
    onConfirm: (CheckInResult) -> Unit
) {
    var sleepQuality by remember { mutableIntStateOf(3) }
    var energyLevel by remember { mutableIntStateOf(3) }
    var soreness by remember { mutableStateOf(SorenessLevel.NONE) }
    var availableMinutes by remember { mutableIntStateOf(60) }

    val recommendation = remember(sleepQuality, energyLevel, soreness) {
        val score = sleepQuality + energyLevel
        when {
            soreness == SorenessLevel.SEVERE -> DailyRecommendation.REST
            sleepQuality <= 2 -> DailyRecommendation.REST
            score <= 4 -> DailyRecommendation.LIGHT
            score <= 6 && soreness == SorenessLevel.MODERATE -> DailyRecommendation.LIGHT
            else -> DailyRecommendation.PUSH
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    "☀️ Good morning!",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    "How are you today?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurfaceVariant
                )

                // Sleep quality
                Column {
                    Text("Sleep quality", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        (1..5).forEach { level ->
                            val emoji = when (level) {
                                1 -> "😫"
                                2 -> "😐"
                                3 -> "🙂"
                                4 -> "😊"
                                5 -> "🤩"
                                else -> ""
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { sleepQuality = level }
                            ) {
                                Text(emoji, style = MaterialTheme.typography.headlineLarge)
                                if (level == sleepQuality) {
                                    Text("●", color = Primary, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                // Energy level
                Column {
                    Text("Energy level", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        (1..5).forEach { level ->
                            val emoji = when (level) {
                                1 -> "😫"
                                2 -> "😐"
                                3 -> "🙂"
                                4 -> "😊"
                                5 -> "🤩"
                                else -> ""
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { energyLevel = level }
                            ) {
                                Text(emoji, style = MaterialTheme.typography.headlineLarge)
                                if (level == energyLevel) {
                                    Text("●", color = Primary, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                // Soreness
                Column {
                    Text("Soreness", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SorenessLevel.entries.forEach { level ->
                            val label = when (level) {
                                SorenessLevel.NONE -> "None"
                                SorenessLevel.MILD -> "Mild"
                                SorenessLevel.MODERATE -> "Moderate"
                                SorenessLevel.SEVERE -> "Severe"
                            }
                            FilterChip(
                                selected = soreness == level,
                                onClick = { soreness = level },
                                label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Available time
                Column {
                    Text("Available time", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(30, 45, 60, 90).forEach { mins ->
                            FilterChip(
                                selected = availableMinutes == mins,
                                onClick = { availableMinutes = mins },
                                label = { Text("${mins}min") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Recommendation
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (recommendation) {
                            DailyRecommendation.PUSH -> Primary.copy(alpha = 0.15f)
                            DailyRecommendation.LIGHT -> Secondary.copy(alpha = 0.15f)
                            DailyRecommendation.REST -> OnSurfaceVariant.copy(alpha = 0.1f)
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            when (recommendation) {
                                DailyRecommendation.PUSH -> "💪"
                                DailyRecommendation.LIGHT -> "🌤"
                                DailyRecommendation.REST -> "😴"
                            },
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                when (recommendation) {
                                    DailyRecommendation.PUSH -> "Let's go! Full intensity"
                                    DailyRecommendation.LIGHT -> "Take it easy today"
                                    DailyRecommendation.REST -> "Rest day recommended"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                when (recommendation) {
                                    DailyRecommendation.PUSH -> "You're well-rested and ready"
                                    DailyRecommendation.LIGHT -> "Reduced weight and volume"
                                    DailyRecommendation.REST -> "Recovery is part of training"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            onConfirm(
                                CheckInResult(
                                    sleepQuality = sleepQuality,
                                    energyLevel = energyLevel,
                                    soreness = soreness,
                                    sorenessLocation = null,
                                    availableMinutes = availableMinutes,
                                    recommendation = recommendation
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (recommendation) {
                                DailyRecommendation.PUSH -> Primary
                                DailyRecommendation.LIGHT -> Secondary
                                DailyRecommendation.REST -> OnSurfaceVariant
                            }
                        )
                    ) {
                        Text(
                            when (recommendation) {
                                DailyRecommendation.PUSH -> "Let's Go!"
                                DailyRecommendation.LIGHT -> "Light Day"
                                DailyRecommendation.REST -> "Rest Day"
                            }
                        )
                    }
                }
            }
        }
    }
}
