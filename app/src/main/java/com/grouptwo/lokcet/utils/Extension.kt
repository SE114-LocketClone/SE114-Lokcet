package com.grouptwo.lokcet.utils

import android.util.Patterns

// Define validate function for app
fun String.isValidEmail(): Boolean {
    // Check if the email is not blank and matches the email pattern
    return this.isNotBlank() &&  Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

private const val MIN_PASSWORD_LENGTH = 6
// Password must contain at least one uppercase letter,
// one lowercase letter, one number, and no whitespace.
private val PASSWORD_PATTERN = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=\\S+$).{6,}$")

fun String.isValidPassword(): Boolean {
    // Check if the password is not blank and matches the password pattern
    return this.isNotBlank() && this.length >= MIN_PASSWORD_LENGTH && PASSWORD_PATTERN.matches(this)
}

fun String.isValidName(): Boolean {
    // Check if the name is not blank
    return this.isNotBlank()
}

fun String.isMatchingPassword(password: String): Boolean {
    // Check if the password matches the confirm password
    return this == password
}