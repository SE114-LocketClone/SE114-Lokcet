package com.grouptwo.lokcet.navigation

sealed class Screen(val route: String) {
    // Splash Screen
    object SplashScreen : Screen("splash_screen")

    // Intro Screen
    object WelcomeScreen : Screen("welcome_screen")


    // Login Screen
    object LoginScreen_1 : Screen("login_screen_1")
    object LoginScreen_2 : Screen("login_screen_2")

    // Register Screen
    object RegisterScreen_1 : Screen("register_screen_1")
    object RegisterScreen_2 : Screen("register_screen_2")
    object RegisterScreen_3 : Screen("register_screen_3")
    object RegisterScreen_4 : Screen("register_screen_4")

    // Add Friend Screen
    object AddFriendScreen : Screen("add_friend_screen")

    // Home Screen
    object HomeScreen_1 : Screen("home_screen_1")
    object HomeScreen_2 : Screen("home_screen_2")

    // Feed Screen
    object FeedScreen : Screen("feed_screen")

    // Friend Screen
    object FriendScreen : Screen("friend_screen")

    // Chat Screen
    object ChatScreen_1 : Screen("chat_screen_1")
    object ChatScreen_2 : Screen("chat_screen_2")

    // Setting Screen
    object SettingScreen : Screen("setting_screen")
    object SettingScreen_1 : Screen("setting_screen_1")
    object SettingScreen_2 : Screen("setting_screen_2")
    object SettingScreen_3 : Screen("setting_screen_3")
    object SettingScreen_4 : Screen("setting_screen_4")


}