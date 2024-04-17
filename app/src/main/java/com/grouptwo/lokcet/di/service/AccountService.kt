package com.grouptwo.lokcet.di.service

import com.google.firebase.firestore.GeoPoint
import com.grouptwo.lokcet.data.model.User
import kotlinx.coroutines.flow.Flow

interface AccountService {
    val currentUserId: String
    val hasUser: Boolean
    val currentUser: Flow<User>
    suspend fun createAccount(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        location: GeoPoint,
        phoneNumber: String
    )

    suspend fun sendEmailVerify(
        email: String
    )

    suspend fun signIn(email: String, password: String)
    suspend fun signOut()
    suspend fun sendPasswordResetEmail(email: String)

    suspend fun isEmailUsed(email: String): Boolean
    suspend fun isPhoneUsed(phone: String): Boolean
}