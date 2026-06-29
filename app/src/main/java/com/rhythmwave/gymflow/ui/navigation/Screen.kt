package com.rhythmwave.gymflow.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : Screen("home", "Home", Icons.Rounded.Home)
    object Programs : Screen("programs", "Programs", Icons.Rounded.FitnessCenter)
    object Workout : Screen("workout", "Workout", Icons.Rounded.SportsGymnastics)
    object Progress : Screen("progress", "Progress", Icons.Rounded.TrendingUp)
    object Profile : Screen("profile", "Profile", Icons.Rounded.Person)

    // Modal routes
    object ActiveWorkout : Screen("active_workout", "Active Workout", Icons.Rounded.SportsGymnastics)
    object WorkoutSummary : Screen("workout_summary/{sessionId}", "Summary", Icons.Rounded.CheckCircle)
    object ProgramWizard : Screen("program_wizard", "New Program", Icons.Rounded.AddCircle)
    object ExerciseDetail : Screen("exercise/{exerciseId}", "Exercise", Icons.Rounded.Info)

    companion object {
        val bottomNavItems = listOf(Home, Programs, Workout, Progress, Profile)
    }
}
