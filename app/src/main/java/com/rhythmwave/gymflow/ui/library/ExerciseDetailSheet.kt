package com.rhythmwave.gymflow.ui.library

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rhythmwave.gymflow.data.local.entity.ExerciseEntity
import com.rhythmwave.gymflow.domain.model.ExperienceLevel
import com.rhythmwave.gymflow.domain.model.GoalTag
import com.rhythmwave.gymflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailSheet(
    exercise: ExerciseEntity,
    alternatives: List<ExerciseEntity> = emptyList(),
    onSwap: ((String) -> Unit)? = null,
    onAddToWorkout: ((String) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val tags = remember { parseTags(exercise.tagsJson) }
    val equipment = remember { parseEquipment(exercise.equipmentJson) }
    val altList = remember { parseEquipment(exercise.alternativesJson) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Column {
                Text(
                    exercise.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "${exercise.muscleGroup.name.lowercase().replaceFirstChar { it.uppercase() }} • " +
                            "${if (exercise.isCompound) "Compound" else "Isolation"} • " +
                            exercise.difficulty.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                }
            }

            // Tags
            if (tags.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tags.forEach { tag ->
                        val color = getTagColor(tag)
                        SuggestionChip(
                            onClick = {},
                            label = { Text(tag.name, style = MaterialTheme.typography.labelMedium) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = color.copy(alpha = 0.15f),
                                labelColor = color
                            )
                        )
                    }
                }
            }

            // Equipment
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Build, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    equipment.joinToString(", ") { it.replace("_", " ").replaceFirstChar { c -> c.uppercase() } },
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }

            HorizontalDivider(color = Divider)

            // Instructions
            Column {
                Text("How To Perform", style = MaterialTheme.typography.titleMedium, color = Primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    exercise.instructions,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }

            HorizontalDivider(color = Divider)

            // Common mistakes (derived from instructions)
            Column {
                Text("Key Points", style = MaterialTheme.typography.titleMedium, color = Secondary)
                Spacer(modifier = Modifier.height(8.dp))
                val tips = extractTips(exercise.instructions)
                tips.forEach { tip ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Rounded.Warning, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(tip, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    }
                }
            }

            // Alternatives
            if (altList.isNotEmpty()) {
                HorizontalDivider(color = Divider)
                Column {
                    Text("Alternatives", style = MaterialTheme.typography.titleMedium, color = Tertiary)
                    Spacer(modifier = Modifier.height(8.dp))
                    alternatives.forEach { alt ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(alt.name, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "${if (alt.isCompound) "Compound" else "Isolation"} • ${alt.muscleGroup.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }
                            if (onSwap != null) {
                                TextButton(onClick = { onSwap(alt.id) }) {
                                    Text("Swap")
                                }
                            }
                        }
                    }
                }
            }

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (onAddToWorkout != null) {
                    Button(
                        onClick = { onAddToWorkout(exercise.id) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add to Workout")
                    }
                }
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = if (onAddToWorkout != null) Modifier.weight(1f) else Modifier.fillMaxWidth()
                ) {
                    Text("Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun parseTags(json: String): List<GoalTag> {
    return try {
        val list: List<String> = com.google.gson.Gson().fromJson(json, object : com.google.gson.reflect.TypeToken<List<String>>() {}.type)
        list.mapNotNull { runCatching { GoalTag.valueOf(it) }.getOrNull() }
    } catch (e: Exception) {
        emptyList()
    }
}

private fun parseEquipment(json: String): List<String> {
    return try {
        com.google.gson.Gson().fromJson(json, object : com.google.gson.reflect.TypeToken<List<String>>() {}.type)
    } catch (e: Exception) {
        emptyList()
    }
}

private fun extractTips(instructions: String): List<String> {
    // Extract lines that start with numbers as tips
    return instructions.lines()
        .filter { it.trim().matches(Regex("^\\d+\\..*")) }
        .map { it.trim().replace(Regex("^\\d+\\.\\s*"), "") }
        .take(6)
}

private fun getTagColor(tag: GoalTag): androidx.compose.ui.graphics.Color {
    return when (tag) {
        GoalTag.POSTURE -> GoalPosture
        GoalTag.CARDIO -> GoalCardio
        GoalTag.EXPLOSIVE -> GoalExplosive
        GoalTag.CORE_SPINE -> GoalCoreSpine
        GoalTag.DRIVE -> GoalDrive
        GoalTag.BLOOD_FLOW -> GoalBloodFlow
        GoalTag.FLEXIBILITY -> GoalFlexibility
    }
}
