package com.grouptwo.lokcet.view.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isCheckingEmail: Boolean = false,
    val isCheckingUser: Boolean = false,
    val isButtonEmailEnable: Boolean = false,
    val isButtonPasswordEnable: Boolean = false,
    val isNetworkAvailable: Boolean = false,
    val isSendingForgetPassword: Boolean = false,
    val hasSentForgetPassword: Boolean = false,
)