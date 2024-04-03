package com.grouptwo.lokcet.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grouptwo.lokcet.view.splash.SplashScreen
import com.grouptwo.lokcet.view.welcome.WelcomeScreen

@Composable
fun Navigation() {
    // Create a navController
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.SplashScreen.route) {
        composable(Screen.SplashScreen.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.WelcomeScreen.route) {
            WelcomeScreen()
        }
        // Check if the user is logged in (has email)
        // If the user is logged in, navigate to HomeScreen
        // If the user is not logged in, navigate to WelcomeScreen
    }
}


