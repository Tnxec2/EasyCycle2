package com.kontranik.easycycle.ui


sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Calendar : Screen("calendar")
    data object Statistic : Screen("statistic")
    data object Phases : Screen("phases")
    data object Settings : Screen("settings")

    data object PhaseEdit : Screen("phase_edit/{phaseId}") {
        fun createRoute(phaseId: String) = "phase_edit/$phaseId"
    }
}