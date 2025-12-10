package com.kontranik.easycycle.ui

import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kontranik.easycycle.AppViewModelProvider
import com.kontranik.easycycle.R
import com.kontranik.easycycle.ui.DrawerParams.drawerButtons
import com.kontranik.easycycle.ui.settings.SettingsViewModel
import com.kontranik.easycycle.ui.theme.EasyCycleTheme
import com.kontranik.easycycle.ui.appdrawer.AppDrawerContent
import com.kontranik.easycycle.ui.appdrawer.AppDrawerItemInfo
import com.kontranik.easycycle.ui.appnavigation.AppNavigationScaffold
import kotlinx.coroutines.launch

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
            imageVector = Icons.Default.Home,
            title = R.string.title_home
        ),
        AppDrawerItemInfo(
            drawerOption =  Screen.Calendar,
            descriptionId = R.string.title_calendar,
            imageVector = Icons.Filled.CalendarMonth,
            title = R.string.title_calendar
        ),
        AppDrawerItemInfo(
            drawerOption =  Screen.Phases,
            descriptionId = R.string.title_phases,
            imageVector = Icons.AutoMirrored.Filled.ListAlt,
            title = R.string.title_phases
        ),
        AppDrawerItemInfo(
            drawerOption =  Screen.Statistic,
            descriptionId = R.string.title_statistic,
            imageVector = Icons.Default.StackedBarChart,
            title = R.string.title_statistic
        )
    )
}
