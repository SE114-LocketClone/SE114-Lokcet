package com.grouptwo.lokcet.view.register

import com.google.firebase.firestore.GeoPoint

// Define state object for email, password, last name, and first name in the register screen

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val lastName: String = "",
    val firstName: String = "",
    val isButtonEmailEnable: Boolean = false,
    val isButtonPasswordEnable: Boolean = false,
    val isButtonNameEnable: Boolean = false,
    val isButtonPhoneEnable: Boolean = false,
    val isCheckingEmail: Boolean = false,
    val isCheckingPhone: Boolean = false,
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val phoneNumber: String = "",
)