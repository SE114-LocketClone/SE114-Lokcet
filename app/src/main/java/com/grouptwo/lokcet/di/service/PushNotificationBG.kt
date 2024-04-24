package com.grouptwo.lokcet.di.service

import com.google.firebase.firestore.GeoPoint
import com.grouptwo.lokcet.data.model.User
import kotlinx.coroutines.flow.Flow

interface PushNotificationBG {
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

// package com.grouptwo.lokcet.di.module

// import com.google.firebase.Firebase
// import com.google.firebase.auth.FirebaseAuth
// import com.google.firebase.auth.auth
// import com.google.firebase.firestore.FirebaseFirestore
// import com.google.firebase.firestore.firestore
// import com.google.firebase.storage.FirebaseStorage
// import com.google.firebase.storage.storage
// import dagger.Module
// import dagger.Provides
// import dagger.hilt.InstallIn
// import dagger.hilt.components.SingletonComponent

// @Module
// @InstallIn(SingletonComponent::class)
// object FirebaseModule {
//     Firebase Auth
//     @Provides
//     fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

//     Firebase Firestore
//     @Provides
//     fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

//     Firebase Storage
//     @Provides
//     fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage
// }