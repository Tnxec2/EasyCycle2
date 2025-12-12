package com.kontranik.easycycle.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kontranik.easycycle.AppViewModelProvider
import com.kontranik.easycycle.R
import com.kontranik.easycycle.model.navigation_calendar
import com.kontranik.easycycle.model.navigation_info
import com.kontranik.easycycle.model.navigation_phases
import com.kontranik.easycycle.model.navigation_statistic
import com.kontranik.easycycle.ui.DrawerParams.drawerButtons
import com.kontranik.easycycle.ui.appdrawer.AppDrawerItemInfo
import com.kontranik.easycycle.ui.appnavigation.AppNavigationScaffold
import com.kontranik.easycycle.ui.settings.SettingsViewModel
import com.kontranik.easycycle.ui.theme.EasyCycleTheme

data class DarkTheme(val isDark: Boolean = false)

val LocalTheme = compositionLocalOf { DarkTheme() }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainCompose(
    navController: NavHostController = rememberNavController(),
) {
    val scope = rememberCoroutineScope()

    val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)

    LaunchedEffect(Unit) {
        // settingsViewModel.settingsState
    }

    CompositionLocalProvider() {
        EasyCycleTheme(darkTheme = LocalTheme.current.isDark)
        {
            Surface {
                AppNavigationScaffold(
                    menuItems = drawerButtons,
                    defaultPick = Screen.Home,
                    showLabel = false,
                    onClick = { onUserPickedOption ->
                        when (onUserPickedOption) {
                            Screen.Home,
                            Screen.Calendar,
                            Screen.Phases,
                            Screen.PhaseEdit,
                            Screen.Statistic,
                            Screen.Settings,
                                -> {
                                Log.d(
                                    "PICK",
                                    "MainCompose: ${onUserPickedOption.route}"
                                )
                                navController.navigate(onUserPickedOption.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(NavRoutes.MainRoute.name) {
                                        saveState = true
                                    }
                                }
                            }
                        }
                    }
                ) {
                    NavHost(
                        navController,
                        startDestination = NavRoutes.MainRoute.name
                    ) {
                        mainGraph(
                            navController,
                            settingsViewModel
                        )
                    }
                }
            }
        }
    }
}

enum class NavRoutes {
    MainRoute,
}

object DrawerParams {
    val drawerButtons = arrayListOf(
        AppDrawerItemInfo(
            drawerOption = Screen.Home,
            descriptionId = R.string.title_home,
            imageVector = Icons.Default.Info,
            title = R.string.title_home,
            navigationId = navigation_info
        ),
        AppDrawerItemInfo(
            drawerOption =  Screen.Calendar,
            descriptionId = R.string.title_calendar,
            imageVector = Icons.Filled.CalendarMonth,
            title = R.string.title_calendar,
            navigationId = navigation_calendar
        ),
        AppDrawerItemInfo(
            drawerOption =  Screen.Statistic,
            descriptionId = R.string.title_statistic,
            imageVector = Icons.Default.StackedBarChart,
            title = R.string.title_statistic,
            navigationId = navigation_statistic
        ),
        AppDrawerItemInfo(
            drawerOption =  Screen.Phases,
            descriptionId = R.string.title_phases,
            imageVector = Icons.AutoMirrored.Filled.ListAlt,
            title = R.string.title_phases,
            navigationId = navigation_phases
        ),
    )
}
