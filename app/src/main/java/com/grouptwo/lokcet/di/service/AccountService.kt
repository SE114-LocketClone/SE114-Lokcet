package com.grouptwo.lokcet.di.service

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.utils.DataState
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

    suspend fun getCurrentUser(): User
    suspend fun sendEmailVerify(
        email: String
    )

    suspend fun signIn(email: String, password: String)
    suspend fun signOut()
    suspend fun sendPasswordResetEmail(email: String)
    suspend fun isEmailUsed(email: String): Boolean
    suspend fun isPhoneUsed(phone: String): Boolean
    suspend fun getCurrentServerTime(): Timestamp? // return null if failed

    suspend fun updateProfile(
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): Flow<DataState<User>>

    suspend fun reportProblem(
        problem: String, email: String, userId: String
    ): Flow<DataState<Unit>>

    suspend fun makeSuggestions(
        suggestion: String, email: String, userId: String
    ): Flow<DataState<Unit>>

    suspend fun deleteAccount(
        userId: String
    ): Flow<DataState<Unit>>

    suspend fun uploadProfileImage(
        userId: String,
        image: ByteArray,
    ): Flow<DataState<String>>

    suspend fun deleteProfileImage(
        userId: String, firstName: String, lastName: String
    ): Flow<DataState<String>>
}