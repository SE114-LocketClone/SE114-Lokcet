package com.grouptwo.lokcet.view.home

import com.grouptwo.lokcet.navigation.Screen
import com.grouptwo.lokcet.view.LokcetViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
) : LokcetViewModel() {
    // ViewModel for the HomeScreen
    fun onSwipeUp(navigate: (String) -> Unit) {
        // Navigate to the FeedScreen
        navigate(Screen.FeedScreen.route)
    }
}