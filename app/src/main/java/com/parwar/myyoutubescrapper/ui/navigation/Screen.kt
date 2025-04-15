package com.parwar.myyoutubescrapper.ui.navigation
 
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Results : Screen("results")
    object Settings : Screen("settings")
} 