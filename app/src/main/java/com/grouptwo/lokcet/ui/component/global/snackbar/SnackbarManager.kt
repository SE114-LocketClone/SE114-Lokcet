package com.grouptwo.lokcet.ui.component.global.snackbar

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Create object SnackbarManager to manage the Snackbar state and display it

object SnackbarManager {
    // Define a MutableStateFlow to store the SnackbarMessage state and initialize it with null
    private val messages: MutableStateFlow<SnackbarMessage?> = MutableStateFlow(null)

    // Get state flow of SnackbarMessage to prevent other class from changing the state (only observe)
    val snackbarMessages: StateFlow<SnackbarMessage?> get() = messages.asStateFlow()

    // Create a function to show message in Snackbar from resources
    fun showMessage(@StringRes message: Int) {
        messages.value = SnackbarMessage.ResourceSnackbar(message)
    }

    // Create a function to show message in Snackbar from string
    fun showMessage(message: SnackbarMessage) {
        messages.value = message
    }

    // Create a function to clear the message in Snackbar
    fun clearSnackbarState() {
        messages.value = null
    }
}