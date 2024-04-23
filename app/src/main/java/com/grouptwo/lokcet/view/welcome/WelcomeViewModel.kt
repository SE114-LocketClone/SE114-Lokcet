package com.grouptwo.lokcet.view.welcome

import com.grouptwo.lokcet.navigation.Screen
import com.grouptwo.lokcet.view.LokcetViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor() : LokcetViewModel() {
    fun onLoginClick(navigate: (String) -> Unit) {
        // Navigate to the login screen
        navigate(Screen.LoginScreen_1.route)
    }

    fun onRegisterClick(navigate: (String) -> Unit) {
        // Navigate to the register screen
        navigate(Screen.RegisterScreen_1.route)
    }
}