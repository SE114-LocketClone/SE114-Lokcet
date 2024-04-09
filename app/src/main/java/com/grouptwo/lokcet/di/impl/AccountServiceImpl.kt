package com.grouptwo.lokcet.di.impl

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.di.service.AccountService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth, private val firestore: FirebaseFirestore
) : AccountService {

    // Check if the user is logged in
    override val hasUser: Boolean
        get() = auth.currentUser != null

    // Get the current user ID
    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val currentUser: Flow<User>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                val firebaseUser = auth.currentUser
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    val docRef = FirebaseFirestore.getInstance().collection("users").document(uid)
                    docRef.get().addOnSuccessListener { documentSnapshot ->
                        val user = documentSnapshot.toObject(User::class.java)
                        user?.let { this.trySend(it) }
                    }
                } else {
                    this.trySend(User())
                }
            }
            auth.addAuthStateListener(listener)
            awaitClose {
                auth.removeAuthStateListener(listener)
            }
        }
    // Create an account
    override suspend fun createAccount(email: String, password: String) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            user?.sendEmailVerification()?.await()
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthUserCollisionException -> {
                    throw Exception("Email đã được sử dụng")
                }

                is FirebaseAuthInvalidCredentialsException -> {
                    throw Exception("Email không hợp lệ")
                }

                is FirebaseNetworkException -> {
                    throw Exception("Không có kết nối mạng vui lòng kiểm tra lại")
                }

                else -> throw e
            }
        }
    }
    // If the account is created, send a verification email


    // Send a verification email
    override suspend fun sendEmailVerify(email: String) {
        // Make sure the user is logged in before sending the email
        if (auth.currentUser == null) {
            throw Exception("Người dùng chưa đăng nhập")
        } else {
            // Send the email verification
            auth.currentUser?.sendEmailVerification()?.await()
        }
    }

    override suspend fun signIn(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidUserException -> {
                    throw Exception("Email không tồn tại hoặc đã bị xóa")
                }

                is FirebaseAuthInvalidCredentialsException -> {
                    throw Exception("Mật khẩu không đúng")
                }

                is FirebaseNetworkException -> {
                    throw Exception("Không có kết nối mạng vui lòng kiểm tra lại")
                }

                else -> throw e
            }
        }
    }

    override suspend fun signOut() {
        auth.signOut()
        if (auth.currentUser != null) {
            throw Exception("Đăng xuất không thành công")
        }
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }
}