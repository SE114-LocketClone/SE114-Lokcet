package com.grouptwo.lokcet.di.impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.di.service.ContactService
import com.grouptwo.lokcet.di.service.LocationService
import com.grouptwo.lokcet.di.service.UserService
import com.grouptwo.lokcet.utils.Constants
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
            val currentLocation = locationService.getCurrentLocation()
            // Then get the current user's contact list
            val contactList = contactService.getContactList()
            // Initialize an empty list to hold the suggested friends
            val suggestFriendContactList = mutableListOf<User>()
            // Perform query to get all users except the current user
            val allUsersDeferred = CoroutineScope(Dispatchers.IO).async {
                firestore.collection("users")
                    .whereNotEqualTo("id", accountService.currentUserId) // Exclude the current user
                    .get()
                    .await()
                    .toObjects(User::class.java)
            }
            // Perform query to get the suggest friend list based on user has account with phoneNumber in contactList
            val contactListDeferreds = contactList.map { phoneNumber ->
                CoroutineScope(Dispatchers.IO).async {
                    allUsersDeferred.await().filter { it.phoneNumber == phoneNumber }
                }
            }
            val usersLists = contactListDeferreds.awaitAll()
            usersLists.forEach { users ->
                suggestFriendContactList.addAll(users)
            }
            // Filter for users within 10km
            val nearbyUsers = allUsersDeferred.await().filter { user ->
                val userLocation = GeoPoint(user.location.latitude, user.location.longitude)
                val distance =
                    locationService.calculateDistanceBetweenTwoPoints(currentLocation, userLocation)
                distance <= Constants.MAX_DISTANCE
            }

            // Merge the two lists and remove duplicates
            val suggestFriendList =
                (suggestFriendContactList + nearbyUsers).distinctBy { it.phoneNumber }
// If both lists are empty, query for users with default location (0,0)
            if (suggestFriendList.isEmpty()) {
                val defaultLocationUsers = allUsersDeferred.await()
                    .filter { it.location.latitude == 0.0 && it.location.longitude == 0.0 }
                emit(DataState.Success(defaultLocationUsers))
            } else {
                emit(DataState.Success(suggestFriendList))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }

    override suspend fun addFriend(userId: String, friendId: String): Flow<DataState<Unit>> {
        return flow {
            emit(DataState.Loading)
            try {
                val userRef = firestore.collection("users").document(userId)
                val friendRef = firestore.collection("users").document(friendId)
                val user = userRef.get().await().toObject(User::class.java)
                val friend = friendRef.get().await().toObject(User::class.java)
                if (user != null && friend != null) {
                    val userFriends = user.friends.toMutableList()
                    val friendFriends = friend.friends.toMutableList()
                    if (userFriends.contains(friendId) || friendFriends.contains(userId)) {
                        emit(DataState.Error(Exception("Đã kết bạn với người này")))
                    } else {
                        val friendWaitList = user.friendWaitList.toMutableList()
                        val friendRequests = friend.friendRequests.toMutableList()
                        friendWaitList.add(friendId)
                        friendRequests.add(userId)
                        userRef.update("friendWaitList", friendWaitList).await()
                        friendRef.update("friendRequests", friendRequests).await()
                        emit(DataState.Success(Unit))
                    }
                } else {
                    emit(DataState.Error(Exception("Không tìm thấy người dùng")))
                }
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }

}