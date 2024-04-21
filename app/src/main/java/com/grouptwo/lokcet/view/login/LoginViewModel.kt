package com.grouptwo.lokcet.view.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.room.util.copy
import com.grouptwo.lokcet.view.LokcetViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) : LokcetViewModel(){
    var uiState_login = mutableStateOf(
        LoginUiState(
            email = savedStateHandle["email"] ?: ""
        )
    )
    private set
    private val email get() = uiState_login.value.email
    private val isButtonEmailEnable get() = uiState_login.value.isButtonEmailEnable

    fun onEmailChange(email: String) {
        uiState_login.value = uiState_login.value.copy(email = email, isButtonEmailEnable = email.isNotBlank())
        savedStateHandle["email"] = email
    }
    fun onBackClick(popUp: () -> Unit) {
        popUp()
    }
}