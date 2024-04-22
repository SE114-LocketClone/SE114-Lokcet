package com.grouptwo.lokcet.navigation

sealed class Screen(val route: String) {
    // Splash Screen
    object SplashScreen : Screen("splash_screen")

    // Intro Screen
    object WelcomeScreen : Screen("welcome_screen")

    // Add Widget Tutorial Screen
    object AddWidgetTutorialScreen : Screen("add_widget_tutorial_screen")

    // Login Screen
    object LoginScreen : Screen("login_screen")

    // Register Screen
    object RegisterScreen_1 : Screen("register_screen_1")
    object RegisterScreen_2 : Screen("register_screen_2")
    object RegisterScreen_3 : Screen("register_screen_3")
    object RegisterScreen_4 : Screen("register_screen_4")

    // Add Friend Screen
    object AddFriendScreen : Screen("add_friend_screen")

    // Forgot Password Screen
    object ForgotPasswordScreen : Screen("forgot_password_screen")

    // Reset Password Screen
    object ResetPasswordScreen : Screen("reset_password_screen")

    // Verify Email Screen (Mail)
    object VerifyEmailScreen : Screen("verify_email_screen")

    // Verify Phone Screen (OTP)
    object VerifyPhoneScreen : Screen("verify_phone_screen")

    // Home Screen
    object HomeScreen_1 : Screen("home_screen_1")
    object HomeScreen_2 : Screen("home_screen_2")
    // Feed Screen
    object FeedScreen : Screen("feed_screen")

    // Profile Screen
    object ProfileScreen : Screen("profile_screen")

    // Settings Screen
    object SettingsScreen : Screen("settings_screen")

    // Friends List Screen
    object FriendsListScreen : Screen("friends_list_screen")
}