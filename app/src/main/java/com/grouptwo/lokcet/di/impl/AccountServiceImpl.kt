package com.grouptwo.lokcet.di.impl

import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.grouptwo.lokcet.data.model.FCMToken
import com.grouptwo.lokcet.data.model.Problem
import com.grouptwo.lokcet.data.model.Suggestion
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.utils.Constants
import com.grouptwo.lokcet.utils.DataState
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val sharedPreferences: SharedPreferences,
    private val firebaseMessaging: FirebaseMessaging,
    private val storage: FirebaseStorage,
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
                    val docRef = firestore.collection("users").document(uid)
                    docRef.get().addOnSuccessListener { documentSnapshot ->
                        val user = documentSnapshot.toObject(User::class.java)
                        user?.let { this.trySend(it) }
                    }
                } else {
                    //  If not logged in, send an empty user object (null)
                    this.trySend(User())
                }
            }
            auth.addAuthStateListener(listener)
            awaitClose {
                auth.removeAuthStateListener(listener)
            }
        }

    // Create an account
    override suspend fun createAccount(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        location: GeoPoint,
        phoneNumber: String
    ) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            // Save the user information to the database
            val userObject = User(
                id = user?.uid.orEmpty(),
                email = email,
                firstName = firstName,
                lastName = lastName,
                fullName = "$firstName $lastName",
                location = location,
                phoneNumber = phoneNumber,
                profilePicture = Constants.AVATAR_API_URL + "$firstName $lastName"
            )
            firestore.collection("users").document(user?.uid.orEmpty()).set(userObject).await()
            // Get the token to send to the server
            val token = firebaseMessaging.token.await()
            // Save the token to the shared preferences
            sharedPreferences.edit().putString("deviceToken", token).apply()
            // Save the user information to the shared preferences
            sharedPreferences.edit().putString("userId", user?.uid).apply()
            // Save password to shared preferences
            sharedPreferences.edit().putString("password", password).apply()
            // Save to the server
            val tokenStore = FCMToken(token = token)
            firestore.collection("fcmTokens").document(user?.uid.orEmpty()).set(tokenStore).await()
            // If the account is created, send a verification email
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

    // Get the current user
    override suspend fun getCurrentUser(): User {
        try {
            val docRef =
                firestore.collection("users").document(auth.currentUser?.uid.orEmpty()).get()
                    .await()
            return docRef.toObject(User::class.java) ?: User()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun signIn(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            // Save the user information to the shared preferences
            sharedPreferences.edit().putString("userId", auth.currentUser?.uid).apply()
            sharedPreferences.edit().putString("password", password).apply()
            // Get the token to send to the server
            val token = firebaseMessaging.token.await()
            // Save the token to the shared preferences
            sharedPreferences.edit().putString("deviceToken", token).apply()
            // Save to the server
            val tokenStore = FCMToken(token = token)
            firestore.collection("fcmTokens").document(auth.currentUser?.uid.orEmpty())
                .set(tokenStore).await()
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

    override suspend fun isEmailUsed(email: String): Boolean {
        val docRef = firestore.collection("users").whereEqualTo("email", email).get().await()
        return !docRef.isEmpty
    }

    override suspend fun isPhoneUsed(phone: String): Boolean {
        val docRef = firestore.collection("users").whereEqualTo("phoneNumber", phone).get().await()
        return !docRef.isEmpty
    }

    override suspend fun getCurrentServerTime(): Timestamp? {
        return try {
            // First update the server time
            val serverTimeRef = firestore.collection("serverTime").document("current")
            serverTimeRef.set(mapOf("time" to FieldValue.serverTimestamp()), SetOptions.merge())
                .await()
            // Get the server time
            val snapshot = serverTimeRef.get().await()
            snapshot.getTimestamp("time")
        } catch (e: Exception) {
            val mostRecentDocumentSnapshot =
                firestore.collection("images").orderBy("createAt", Query.Direction.DESCENDING)
                    .limit(1).get().await().documents.firstOrNull()
            if (mostRecentDocumentSnapshot == null) {
                // If both attempts fail, throw an exception
                throw e
            } else mostRecentDocumentSnapshot.getTimestamp("createAt")
        }
    }

    override suspend fun updateProfile(
        firstName: String, lastName: String, phoneNumber: String
    ): Flow<DataState<User>> {
        return flow {
            try {
                emit(DataState.Loading)
                val user = getCurrentUser()
                val updatedUser = user.copy(
                    firstName = firstName,
                    lastName = lastName,
                    fullName = "$firstName $lastName",
                    phoneNumber = phoneNumber
                )
                firestore.collection("users").document(user.id).set(updatedUser).await()
                emit(DataState.Success(updatedUser))
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }

    override suspend fun reportProblem(
        problem: String, email: String, userId: String
    ): Flow<DataState<Unit>> {
        return flow {
            try {
                emit(DataState.Loading)
                // Create document reference
                val problemRef = firestore.collection("problems").document()
                // Create problem object
                val problemObject = Problem(
                    problem = problem, email = email, userId = userId, problemId = problemRef.id
                )
                problemRef.set(problemObject).await()
                emit(DataState.Success(Unit))
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }

    override suspend fun makeSuggestions(
        suggestion: String, email: String, userId: String
    ): Flow<DataState<Unit>> {
        return flow {
            try {
                emit(DataState.Loading)
                // Create document reference
                val suggestionRef = firestore.collection("suggestions").document()
                // Create suggestion object
                val suggestionObject = Suggestion(
                    suggestion = suggestion,
                    email = email,
                    userId = userId,
                    suggestionId = suggestionRef.id
                )
                suggestionRef.set(suggestionObject).await()
                emit(DataState.Success(Unit))
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }

    override suspend fun deleteAccount(userId: String): Flow<DataState<Unit>> {
        return flow {
            try {
                emit(DataState.Loading)
                coroutineScope {
                    // Update user status to deleted
                    val updateUserStatus = async {
                        firestore.collection("users").document(userId).update("deleted", true)
                            .await()
                    }

                    // Update user name to deleted
                    val updateUserFullName = async {
                        firestore.collection("users").document(userId)
                            .update("firstName", "Deleted").await()
                        firestore.collection("users").document(userId).update("lastName", "Account")
                            .await()
                    }
                    // Delete the user from the FCM tokens
                    val deleteUserFromFCM = async {
                        firestore.collection("fcmTokens").document(userId).delete().await()
                    }
                    // Delete the user from the shared preferences
                    val deleteUserFromPrefs = async {
                        sharedPreferences.edit().remove("userId").apply()
                        sharedPreferences.edit().remove("deviceToken").apply()
                    }
                    // Delete the user from the problems
                    val deleteUserFromProblems = async {
                        firestore.collection("problems").whereEqualTo("userId", userId).get()
                            .await().documents.forEach { document ->
                                firestore.collection("problems").document(document.id).delete()
                                    .await()
                            }
                    }
                    // Delete the user from the suggestions
                    val deleteUserFromSuggestions = async {
                        firestore.collection("suggestions").whereEqualTo("userId", userId).get()
                            .await().documents.forEach { document ->
                                firestore.collection("suggestions").document(document.id).delete()
                                    .await()
                            }
                    }

                    // Delete the user from the authentication
                    val deleteUserFromAuth = async {
                        // Re-authenticate the user
                        val user = auth.currentUser
                        user?.email?.let {
                            auth.signInWithEmailAndPassword(
                                it, sharedPreferences.getString("password", "")!!
                            ).await()
                        }
                        // Delete the user
                        auth.currentUser?.delete()?.await()
                    }

                    // Wait for all operations to finish
                    updateUserStatus.await()
                    updateUserFullName.await()
                    deleteUserFromFCM.await()
                    deleteUserFromPrefs.await()
                    deleteUserFromProblems.await()
                    deleteUserFromSuggestions.await()
                    deleteUserFromAuth.await()
                }
                emit(DataState.Success(Unit))
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }

    override suspend fun uploadProfileImage(
        userId: String, image: ByteArray
    ): Flow<DataState<String>> {
        return flow {
            try {
                emit(DataState.Loading)
                val timestamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val imageName = "IMG_${userId}_$timestamp"
                val storageRef = storage.reference.child("profileImages/$imageName")
                val uploadTask = storageRef.putBytes(image)
                uploadTask.await()
                val downloadUrl = storageRef.downloadUrl.await().toString()
                firestore.collection("users").document(userId).update("profilePicture", downloadUrl)
                    .await()
                emit(DataState.Success(downloadUrl))
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }

    override suspend fun deleteProfileImage(
        userId: String, firstName: String, lastName: String
    ): Flow<DataState<String>> {
        return flow {
            try {
                emit(DataState.Loading)
                // Remove the profile picture from the storage using profilePicture URL
                val user = firestore.collection("users").document(userId).get().await()
                    .toObject(User::class.java)
                val imageUrl = Constants.AVATAR_API_URL + "$firstName $lastName"
                //If user hasn't change avatar still default avatar url then don't delete
                if (user?.profilePicture != null && user.profilePicture != imageUrl) {
                    storage.getReferenceFromUrl(user.profilePicture).delete().await()
                }
                // Update the profile picture URL to the default avatar URL
                firestore.collection("users").document(userId).update("profilePicture", imageUrl)
                    .await()
                emit(DataState.Success(imageUrl))
            } catch (e: Exception) {
                Log.e("deleteProfileImage", e.toString())
                emit(DataState.Error(e))

            }
        }
    }
}