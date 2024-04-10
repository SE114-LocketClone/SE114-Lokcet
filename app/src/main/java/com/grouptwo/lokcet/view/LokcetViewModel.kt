package com.grouptwo.lokcet.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarManager
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarMessage.Companion.toSnackbarMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class LokcetViewModel() : ViewModel() {
    // Define function to to perform coroutine operation and catch throwable exception to show in snackbar
    fun launchCatching(
        snackbar: Boolean = true, block: suspend CoroutineScope.() -> Unit
    ) {
        // Launch viewModelScope coroutine
        viewModelScope.launch(
            // Use CoroutineExceptionHandler to catch throwable exception and show in snackbar
            CoroutineExceptionHandler { _, throwable ->
                if (snackbar) {
                    // Show snackbar with throwable message
                    SnackbarManager.showMessage(throwable.toSnackbarMessage())
                }
            },
            // Execute block of code that want to run in coroutine
            block = block
        )
    }
}