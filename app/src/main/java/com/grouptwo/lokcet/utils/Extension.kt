package com.grouptwo.lokcet.utils

import android.util.Patterns

// Define validate function for app
fun String.isValidEmail(): Boolean {
    // Check if the email is not blank and matches the email pattern
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

private const val MIN_PASSWORD_LENGTH = 6

// Password must contain at least one uppercase letter,
// one lowercase letter, one number, and no whitespace.
private val PASSWORD_PATTERN =
    Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+=|<>?{}\\\\[\\\\]~-])(?=\\S+$)")

fun String.isValidPassword(): Boolean {
    // Check if the password is not blank and matches the password pattern
    return this.isNotBlank() && this.length >= MIN_PASSWORD_LENGTH
}

fun String.isValidName(): Boolean {
    // Check if the name is not blank
    return this.isNotBlank()
}

private val MAXIMUM_PHONE_NUMBER_LENGTH = 9

fun String.isValidPhoneNumber(): Boolean {
    // Check if the phone number is not blank and matches the phone number pattern
    return this.isNotBlank() && this.length == MAXIMUM_PHONE_NUMBER_LENGTH
}

fun String.isMatchingPassword(password: String): Boolean {
    // Check if the password matches the confirm password
    return this == password
}