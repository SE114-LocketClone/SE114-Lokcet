package com.grouptwo.lokcet.view.register

import androidx.compose.runtime.mutableStateOf
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarManager
import com.grouptwo.lokcet.utils.isValidEmail
import com.grouptwo.lokcet.view.LokcetViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val accountService: AccountService
) : LokcetViewModel() {
    // Initialize the state of the register screen
    var uiState = mutableStateOf(RegisterUiState())
        private set

    private val email get() = uiState.value.email
    private val password get() = uiState.value.password
    private val lastName get() = uiState.value.lastName
    private val firstName get() = uiState.value.firstName

    fun onEmailChange(email: String) {
        uiState.value = uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        uiState.value = uiState.value.copy(password = password)
    }

    fun onLastNameChange(lastName: String) {
        uiState.value = uiState.value.copy(lastName = lastName)
    }

    fun onFirstNameChange(firstName: String) {
        uiState.value = uiState.value.copy(firstName = firstName)
    }

    fun onBackClick(popUp: () -> Unit) {
        popUp()
    }

    fun onMailClick() {
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(R.string.email_invalid)
            return
        }
    }
}