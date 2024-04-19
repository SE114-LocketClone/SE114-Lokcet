package com.grouptwo.lokcet.view.splash

import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.navigation.Screen
import com.grouptwo.lokcet.view.LokcetViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val accountService: AccountService
) : LokcetViewModel() {
    fun onAppStart(openAndPopUp: (String, String) -> Unit) {
        if (accountService.hasUser) {
            // Navigate to the home screen
            openAndPopUp(Screen.AddFriendScreen.route, Screen.SplashScreen.route)
        } else {
            // Navigate to the welcome screen
            openAndPopUp(Screen.WelcomeScreen.route, Screen.SplashScreen.route)
        }
    }
}