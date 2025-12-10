package com.kontranik.easycycle.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.kontranik.easycycle.ui.calendar.CalendarScreen
import com.kontranik.easycycle.ui.home.HomeScreen
import com.kontranik.easycycle.ui.settings.SettingsScreen
import com.kontranik.easycycle.ui.settings.SettingsViewModel
import kotlinx.coroutines.launch

// Navigation Setup
@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
) {

    val start = Screen.Home.route

    navigation(
        startDestination = start,
        route = NavRoutes.MainRoute.name
    ) {

        composable(
            route = Screen.Home.route,
        ) {
            HomeScreen(
                navigateSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                settingsViewModel = settingsViewModel
            )
        }


        composable(
            route = Screen.Calendar.route,
        ) {
            CalendarScreen(
                navigateBack = {
                    navController.popBackStack()
                },
            )
        }


        composable(
            route = Screen.Settings.route,
        ) {
            val coroutineScope = rememberCoroutineScope()
            SettingsScreen(
                navigateBack = {
                    coroutineScope.launch {
                    }
                },
                settingsViewModel = settingsViewModel
            )
        }
    }
}
