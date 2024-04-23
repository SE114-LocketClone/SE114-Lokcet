package com.grouptwo.lokcet.view.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.grouptwo.lokcet.R
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.di.service.InternetService
import com.grouptwo.lokcet.navigation.Screen
import com.grouptwo.lokcet.ui.component.global.snackbar.SnackbarManager
import com.grouptwo.lokcet.utils.ConnectionState
import com.grouptwo.lokcet.utils.isValidEmail
import com.grouptwo.lokcet.utils.isValidPassword
import com.grouptwo.lokcet.view.LokcetViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val accountService: AccountService,
    private val savedStateHandle: SavedStateHandle,
    private val internetService: InternetService
) : LokcetViewModel() {
    private val networkStatus: StateFlow<ConnectionState> = internetService.networkStatus.stateIn(
        scope = viewModelScope,
        initialValue = ConnectionState.Unknown,
        started = WhileSubscribed(5000)
    )

    init {
        viewModelScope.launch {
            networkStatus.collect { connectionState ->
                uiState.value = uiState.value.copy(
                    isNetworkAvailable = connectionState == ConnectionState.Available
                )
            }
        }
    }

    var uiState = mutableStateOf(
        LoginUiState(
            email = savedStateHandle["email"] ?: "",
            password = savedStateHandle["password"] ?: "",
        )
    )
        private set

    private val email get() = uiState.value.email
    private val password get() = uiState.value.password

    fun onEmailChange(email: String) {
        uiState.value = uiState.value.copy(
            email = email,
            isButtonEmailEnable = email.isNotBlank() && email.isValidEmail()
        )
        savedStateHandle["email"] = email
    }

    fun onPasswordChange(password: String) {
        uiState.value = uiState.value.copy(
            password = password,
            isButtonPasswordEnable = password.isNotBlank() && password.isValidPassword()
        )
        savedStateHandle["password"] = password
    }

    fun onEmailClick(
        navigate: (String) -> Unit
    ) {
        if (!uiState.value.isNetworkAvailable) {
            SnackbarManager.showMessage(R.string.no_internet)
            return
        }
        launchCatching {
            try {
                uiState.value = uiState.value.copy(
                    isCheckingEmail = true
                )
                // Check if email exist in the database
                if (accountService.isEmailUsed(email)) {
                    navigate(Screen.LoginScreen_2.route)
                } else {
                    SnackbarManager.showMessage(R.string.non_exist_email)
                }
            } finally {
                uiState.value = uiState.value.copy(
                    isCheckingEmail = false
                )
            }
        }
    }

    fun onBackClick(popUp: () -> Unit) {
        popUp()
    }

    fun onForgetPasswordClick() {
        if (!uiState.value.isNetworkAvailable) {
            SnackbarManager.showMessage(R.string.no_internet)
            return
        }
        launchCatching {
            try {
                uiState.value = uiState.value.copy(
                    isSendingForgetPassword = true
                )
                accountService.sendPasswordResetEmail(email)
                // Update the UI to show that the email has been sent
                uiState.value = uiState.value.copy(
                    hasSentForgetPassword = true
                )
            } finally {
                uiState.value = uiState.value.copy(
                    isSendingForgetPassword = false
                )
            }
        }
    }

    fun onLoginClick(
        clearAndNavigate: (String) -> Unit
    ) {
        if (!uiState.value.isNetworkAvailable) {
            SnackbarManager.showMessage(R.string.no_internet)
            return
        }
        launchCatching {
            try {
                uiState.value = uiState.value.copy(
                    isCheckingUser = true
                )
                accountService.signIn(email, password)
                // Navigate to the home screen after successful login
                clearAndNavigate(Screen.HomeScreen_1.route)
            } finally {
                uiState.value = uiState.value.copy(
                    isCheckingUser = false
                )
            }
        }
    }
}