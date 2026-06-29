package com.rhythmwave.gymflow.ui.profile

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rhythmwave.gymflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", style = MaterialTheme.typography.titleLarge) }
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
            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.FitnessCenter,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Rhythmwave",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        "Training since Jan 2026",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatBadge("75kg", "Weight")
                        StatBadge("175cm", "Height")
                        StatBadge("Intermediate", "Level")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = { /* Edit Profile */ }) {
                        Text("Edit Profile")
                    }
                }
            }

            // Goals Card
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
                        Text("My Goals", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { /* Edit Goals */ }) {
                            Text("Edit")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    val goals = listOf(
                        Triple("🧘", "Fix posture", 5),
                        Triple("🫀", "Better blood flow", 4),
                        Triple("❤️", "Better heart rate", 3),
                        Triple("⚡", "Burst + endurance", 4),
                        Triple("🎯", "Abdomen shape", 5),
                        Triple("🦴", "Core/spine strength", 5),
                        Triple("💕", "Sex drive", 3)
                    )
                    goals.forEach { (icon, name, priority) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(icon, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(name, style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(
                                "★".repeat(priority) + "☆".repeat(5 - priority),
                                style = MaterialTheme.typography.bodySmall,
                                color = PrColor
                            )
                        }
                    }
                }
            }

            // Preferences Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Preferences", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    PreferenceRow("Training days", "4/week")
                    PreferenceRow("Session duration", "60 min")
                    PreferenceRow("Units", "kg")
                    PreferenceRow("Rest days", "Wed, Sat, Sun")
                }
            }

            // Equipment Card
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
                        Text("Equipment", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { /* Edit Equipment */ }) {
                            Text("Edit")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val equipment = listOf("Barbell", "Dumbbell", "Cable", "Pull-up", "Bench", "Kettlebell")
                        equipment.forEach { chip ->
                            SuggestionChip(
                                onClick = { },
                                label = { Text(chip, style = MaterialTheme.typography.labelMedium) }
                            )
                        }
                    }
                }
            }

            // Data Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* Export */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.Upload, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export CSV")
                }
                OutlinedButton(
                    onClick = { /* Reset */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Error)
                ) {
                    Icon(Icons.Rounded.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset Data")
                }
            }
        }
    }
}

@Composable
fun StatBadge(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant
        )
    }
}

@Composable
fun PreferenceRow(label: String, value: String) {
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
