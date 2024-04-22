package com.grouptwo.lokcet.view.add_widget

import com.grouptwo.lokcet.navigation.Screen
import com.grouptwo.lokcet.view.LokcetViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddWidgetViewModel @Inject constructor() : LokcetViewModel() {

    fun onConfirmClick(
        clearAndNavigate: (String) -> Unit
    ) {
        clearAndNavigate(Screen.HomeScreen_1.route)
    }
}