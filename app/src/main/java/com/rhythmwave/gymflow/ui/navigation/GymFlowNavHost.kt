package com.rhythmwave.gymflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rhythmwave.gymflow.ui.home.HomeScreen
import com.rhythmwave.gymflow.ui.program.ProgramScreen
import com.rhythmwave.gymflow.ui.program.ProgramWizardScreen
import com.rhythmwave.gymflow.ui.progress.ProgressScreen
import com.rhythmwave.gymflow.ui.profile.ProfileScreen
import com.rhythmwave.gymflow.ui.library.ExerciseLibraryScreen

@Composable
fun GymFlowNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Programs.route) {
            ProgramScreen(navController = navController)
        }
        composable(Screen.Workout.route) {
            ExerciseLibraryScreen(navController = navController)
        }
        composable(Screen.Progress.route) {
            ProgressScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        // Modal screens
        composable(Screen.ActiveWorkout.route) {
            // TODO: Phase 3
        }
        composable(Screen.ProgramWizard.route) {
            ProgramWizardScreen(navController = navController)
        }
    }
}
