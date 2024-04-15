package com.grouptwo.lokcet.view.register

// Define state object for email, password, last name, and first name in the register screen
data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val lastName: String = "",
    val firstName: String = "",
)