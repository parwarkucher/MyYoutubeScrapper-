package com.parwar.myyoutubescrapper.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.parwar.myyoutubescrapper.ui.screens.home.HomeScreen
import com.parwar.myyoutubescrapper.ui.screens.results.ResultsScreen
import com.parwar.myyoutubescrapper.ui.screens.settings.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        
        composable(Screen.Results.route) {
            val searchParamsFromHome = navController.previousBackStackEntry
                ?.savedStateHandle?.get<String>("searchQuery")
            val selectedModelIdFromHome = navController.previousBackStackEntry
                ?.savedStateHandle?.get<String>("selectedModelId")
            val maxResultsFromHome = navController.previousBackStackEntry
                ?.savedStateHandle?.get<Int>("maxResults")
            val timeFilterFromHome = navController.previousBackStackEntry
                ?.savedStateHandle?.get<String>("timeFilter")
                
            ResultsScreen(
                navController = navController,
                searchQueryFromHome = searchParamsFromHome,
                selectedModelIdFromHome = selectedModelIdFromHome,
                maxResultsFromHome = maxResultsFromHome,
                timeFilterFromHome = timeFilterFromHome
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
} 