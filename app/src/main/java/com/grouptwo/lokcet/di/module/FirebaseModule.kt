package com.grouptwo.lokcet.di.module

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    // Firebase Auth
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    // Firebase Firestore
    @Provides fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}