package com.grouptwo.lokcet.di.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.di.service.ContactService
import com.grouptwo.lokcet.di.service.LocationService
import com.grouptwo.lokcet.di.service.UserService
import com.grouptwo.lokcet.utils.DataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val accountService: AccountService,
    private val locationService: LocationService,
    private val contactService: ContactService
) : UserService {
    override suspend fun getSuggestFriendList(): Flow<DataState<List<User>>> = flow {
        emit(DataState.Loading)
        try {
            // First get the current user's location
//            val location = locationService.getCurrentLocation()
            // Then get the current user's contact list
            val contactList = contactService.getContactList()
            println("Contact list: $contactList")
            // Initialize an empty list to hold the suggested friends
            val suggestFriendContactList = mutableListOf<User>()
            // Perform query to get the suggest friend list based on user has account with phoneNumber in contactList
            val deferreds = contactList.map { phoneNumber ->
                CoroutineScope(Dispatchers.IO).async {
                    val result = firestore.collection("users")
                        .whereEqualTo("phoneNumber", phoneNumber)
                        .get()
                        .await()
                    println("Query result for phoneNumber $phoneNumber: $result")
                    result.toObjects(User::class.java)
                }
            }
            val usersLists = deferreds.awaitAll()
            usersLists.forEach { users ->
                suggestFriendContactList.addAll(users)
            }
            emit(DataState.Success(suggestFriendContactList))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }
}