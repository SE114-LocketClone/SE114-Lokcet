package com.grouptwo.lokcet.view.register

import android.provider.ContactsContract.CommonDataKinds.Email

// Define state object for email, password, last name, and first name in the register screen

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val lastName: String = "",
    val firstName: String = "",
    val isButtonEmailEnable: Boolean = false,
    val isButtonPasswordEnable: Boolean = false,
    val isButtonNameEnable: Boolean = false,
    val isCheckingEmail: Boolean = false,
)