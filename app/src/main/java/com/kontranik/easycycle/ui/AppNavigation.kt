package com.kontranik.easycycle.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.kontranik.easycycle.model.Settings
import com.kontranik.easycycle.ui.DrawerParams.drawerButtons
import com.kontranik.easycycle.ui.calendar.CalendarScreen
import com.kontranik.easycycle.ui.home.HomeScreen
import com.kontranik.easycycle.ui.phases.PhasesScreen
import com.kontranik.easycycle.ui.settings.SettingsScreen
import com.kontranik.easycycle.ui.settings.SettingsViewModel
import com.kontranik.easycycle.ui.statistic.StatisticScreen
import kotlinx.coroutines.launch

// Navigation Setup
@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainGraph(
    start: String,
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
) {



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
                }
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
            route = Screen.Phases.route,
        ) {
            PhasesScreen(
            )
        }

        composable(
            route = Screen.Statistic.route,
        ) {
            StatisticScreen(

            )
        }

        composable(
            route = Screen.Settings.route,
        ) {
            val coroutineScope = rememberCoroutineScope()
            SettingsScreen(
                navigateBack = {
                    coroutineScope.launch {
                        navController.popBackStack()
                    }
                },
                settingsViewModel = settingsViewModel
            )
        }
    }
}
