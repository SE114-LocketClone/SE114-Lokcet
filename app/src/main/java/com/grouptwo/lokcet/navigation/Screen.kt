package com.grouptwo.lokcet.navigation

sealed class Screen(val route: String){
    // Splash Screen
    object SplashScreen: Screen("splash_screen")
    // Intro Screen
    object WelcomeScreen: Screen("welcome_screen")
    // Add Widget Tutorial Screen
    object AddWidgetTutorialScreen: Screen("add_widget_tutorial_screen")
    // Login Screen
    object LoginScreen: Screen("login_screen")
    // Register Screen
    object RegisterScreen: Screen("register_screen")
    // Forgot Password Screen
    object ForgotPasswordScreen: Screen("forgot_password_screen")
    // Reset Password Screen
    object ResetPasswordScreen: Screen("reset_password_screen")
    // Verify Email Screen (Mail)
    object VerifyEmailScreen: Screen("verify_email_screen")
    // Verify Phone Screen (OTP)
    object VerifyPhoneScreen: Screen("verify_phone_screen")
    // Home Screen
    object HomeScreen: Screen("home_screen")
    // Profile Screen
    object ProfileScreen: Screen("profile_screen")
    // Settings Screen
    object SettingsScreen: Screen("settings_screen")
    // Friends List Screen
    object FriendsListScreen: Screen("friends_list_screen")
}