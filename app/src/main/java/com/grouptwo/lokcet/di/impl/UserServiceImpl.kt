package com.grouptwo.lokcet.di.impl

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.androidParameters
import com.google.firebase.dynamiclinks.dynamicLink
import com.google.firebase.dynamiclinks.socialMetaTagParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.grouptwo.lokcet.data.model.EmojiReaction
import com.grouptwo.lokcet.data.model.FCMToken
import com.grouptwo.lokcet.data.model.NotificationDataModel
import com.grouptwo.lokcet.data.model.NotificationModel
import com.grouptwo.lokcet.data.model.NotificationNotiModel
import com.grouptwo.lokcet.data.model.UploadImage
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.di.service.ChatService
import com.grouptwo.lokcet.di.service.ContactService
import com.grouptwo.lokcet.di.service.LocationService
import com.grouptwo.lokcet.di.service.UserService
import com.grouptwo.lokcet.utils.Constants
import com.grouptwo.lokcet.utils.DataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class UserServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val accountService: AccountService,
    private val locationService: LocationService,
    private val contactService: ContactService,
    private val chatService: ChatService,
    private val sharedPreferences: SharedPreferences,
    private val firebaseDynamicLinks: FirebaseDynamicLinks,
    private val notificationServiceRepository: NotificationServiceRepository

) : UserService {


    override suspend fun getSuggestFriendList(): Flow<DataState<List<User>>> {
        return callbackFlow {
            try {
                trySend(DataState.Loading)
                // First get the current user's location
                val currentLocation = locationService.getCurrentLocation()
                // Then get the current user's contact list
                val contactList = contactService.getContactList()
                // Initialize an empty list to hold the suggested friends
                val suggestFriendContactList = mutableListOf<User>()
                // Get current user's friend list
                val userDocument =
                    firestore.collection("users").document(accountService.currentUserId)
                val userListener = userDocument.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(DataState.Error(error))
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val user = snapshot.toObject(User::class.java)
                        if (user != null) {
                            val friendList = user.friends
                            val friendWaitList = user.friendWaitList
                            val friendRequests = user.friendRequests
                            // Perform query to get all users except the current user and the user's friends (exclude friendWaitList and friendRequests)
                            val allUsersDeferred = CoroutineScope(Dispatchers.IO).async {
                                firestore.collection("users").whereNotEqualTo(
                                    "id", accountService.currentUserId
                                ) // Exclude the current user
                                    .get().await().toObjects(User::class.java).filter { user ->
                                        user.id !in friendList && user.id !in friendWaitList && user.id !in friendRequests
                                    }
                            }
                            // Perform query to get the suggest friend list based on user has account with phoneNumber in contactList
                            CoroutineScope(Dispatchers.IO).launch {
                                val allUsers = allUsersDeferred.await()
                                val contactListDeferreds = contactList.map { phoneNumber ->
                                    async {
                                        allUsers.filter { it.phoneNumber == phoneNumber }
                                    }
                                }
                                val usersLists = contactListDeferreds.awaitAll()
                                usersLists.forEach { users ->
                                    suggestFriendContactList.addAll(users)
                                }
                                // Filter for users within 10km
                                val nearbyUsers = allUsers.filter { user ->
                                    val userLocation =
                                        GeoPoint(user.location.latitude, user.location.longitude)
                                    val distance =
                                        locationService.calculateDistanceBetweenTwoPoints(
                                            currentLocation, userLocation
                                        )
                                    distance <= Constants.MAX_DISTANCE
                                }
                                // Merge the two lists and remove duplicates
                                val suggestFriendList =
                                    (suggestFriendContactList + nearbyUsers).distinctBy { it.phoneNumber }
// If both lists are empty, query for users with default location (0,0)
                                if (suggestFriendList.isEmpty()) {
                                    val defaultLocationUsers = allUsersDeferred.await()
                                        .filter { it.location.latitude == 0.0 && it.location.longitude == 0.0 }
                                    trySend(DataState.Success(defaultLocationUsers))
                                } else {
                                    trySend(DataState.Success(suggestFriendList))
                                }
                            }
                        } else {
                            trySend(DataState.Error(Exception("Không tìm thấy người dùng")))
                        }
                    }
                }
                awaitClose {
                    // Close channel when the listener is removed
                    channel.close()
                    userListener.remove()
                }
            } catch (e: Exception) {
                trySend(DataState.Error(e))
            }
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
                    if (userFriends.contains(friendId) && friendFriends.contains(userId)) {
                        emit(DataState.Error(Exception("Đã kết bạn với người này")))
                        return@flow
                    } else {
                        val friendWaitList = user.friendWaitList.toMutableList()
                        val friendRequests = friend.friendRequests.toMutableList()
                        val userRequests = user.friendRequests.toMutableList()
                        val userWaitList = friend.friendWaitList.toMutableList()
                        // Check if the friend is in the user's friend wait list and the user is in the friend's friend request list
                        if (friendWaitList.contains(friendId) && friendRequests.contains(userId)) {
                            emit(DataState.Error(Exception("Đã gửi lời mời kết bạn")))
                            return@flow
                        } else if (userRequests.contains(friendId) && userWaitList.contains(userId)) {
                            // Check if the user is in the friend's friend wait list and the friend is in the user's friend request list
                            emit(DataState.Error(Exception("Đã gửi lời mời kết bạn")))
                            return@flow
                        } else {
                            // Add the friend to the user's friend wait list and the user to the friend's friend request list
                            friendWaitList.add(friendId)
                            friendRequests.add(userId)
                            userRef.update("friendWaitList", friendWaitList).await()
                            friendRef.update("friendRequests", friendRequests).await()
                            emit(DataState.Success(Unit))
                        }
                        // Send notification to the friend
                        val friendToken =
                            firestore.collection("fcmTokens").document(friendId).get().await()
                                .toObject(FCMToken::class.java)
                        if (friendToken != null) {
                            val notification = NotificationModel(
                                to = friendToken.token, notification = NotificationNotiModel(
                                    title = "Lời mời kết bạn",
                                    body = "${user.firstName} ${user.lastName} đã gửi lời mời kết bạn",
                                ), data = NotificationDataModel(
                                    message = "${user.firstName} ${user.lastName} đã gửi lời mời kết bạn",
                                    image = user.profilePicture
                                )
                            )
                            CoroutineScope(Dispatchers.IO).launch {
                                val result =
                                    notificationServiceRepository.postNotification(notification)
                                if (result.isSuccessful) {
                                    Log.d("Notification", "Notification sent successfully")
                                } else Log.d("Notification", "Notification failed to send")
                            }
                        }
                    }
                } else {
                    emit(DataState.Error(Exception("Không tìm thấy người dùng")))
                    return@flow // Exit the flow if the user or friend is not found
                }
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }

    override suspend fun acceptFriend(userId: String, friendId: String): Flow<DataState<Unit>> {
        return flow {
            try {
                emit(DataState.Loading)
                val userRef = firestore.collection("users").document(userId)
                val friendRef = firestore.collection("users").document(friendId)
                val user = userRef.get().await().toObject(User::class.java)
                val friend = friendRef.get().await().toObject(User::class.java)
                if (user != null && friend != null) {
                    val userFriendRequests = user.friendRequests.toMutableList()
                    val friendFriendWaitList = friend.friendWaitList.toMutableList()
                    // Check if the user has a friend request from the friend and the friend is in the user's friend wait list
                    if (userFriendRequests.contains(friendId) && friendFriendWaitList.contains(
                            userId
                        )
                    ) {
                        // Remove the friend from the user's friend request list and the user from the friend's friend wait listc
                        userFriendRequests.remove(friendId)
                        friendFriendWaitList.remove(userId)
                        val userFriends = user.friends.toMutableList()
                        val friendFriends = friend.friends.toMutableList()
                        userFriends.add(friendId)
                        friendFriends.add(userId)
                        userRef.update("friendRequests", userFriendRequests).await()
                        userRef.update("friends", userFriends).await()
                        friendRef.update("friendWaitList", friendFriendWaitList).await()
                        friendRef.update("friends", friendFriends).await()
                        chatService.createChatRoom(friendId)
                        // Send notification to the friend
                        val friendToken =
                            firestore.collection("fcmTokens").document(friendId).get().await()
                                .toObject(FCMToken::class.java)
                        if (friendToken != null) {
                            val notification = NotificationModel(
                                to = friendToken.token, notification = NotificationNotiModel(
                                    title = "Kết bạn thành công",
                                    body = "${user.firstName} ${user.lastName} đã chấp nhận lời mời kết bạn",
                                ), data = NotificationDataModel(
                                    message = "${user.firstName} ${user.lastName} đã chấp nhận lời mời kết bạn",
                                    image = user.profilePicture
                                )
                            )
                            CoroutineScope(Dispatchers.IO).launch {
                                val result =
                                    notificationServiceRepository.postNotification(notification)
                                if (result.isSuccessful) {
                                    Log.d("Notification", "Notification sent successfully")
                                } else Log.d("Notification", "Notification failed to send")
                            }

                        }
                        emit(DataState.Success(Unit))
                    } else {
                        emit(DataState.Error(Exception("Không tìm thấy người dùng trong danh sách chờ")))
                        return@flow
                    }
                } else {
                    emit(DataState.Error(Exception("Không tìm thấy người dùng")))
                    return@flow
                }
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }

    override suspend fun rejectFriend(userId: String, friendId: String): Flow<DataState<Unit>> {
        return flow {
            try {
                emit(DataState.Loading)
                val userRef = firestore.collection("users").document(userId)
                val friendRef = firestore.collection("users").document(friendId)
                val user = userRef.get().await().toObject(User::class.java)
                val friend = friendRef.get().await().toObject(User::class.java)
                if (user != null && friend != null) {

                    val userFriendRequests = user.friendRequests.toMutableList()
                    val friendFriendWaitList = friend.friendWaitList.toMutableList()
                    // Check if the user has a friend request from the friend and the friend is in the user's friend wait list
                    if (userFriendRequests.contains(friendId) && friendFriendWaitList.contains(
                            userId
                        )
                    ) {
                        // Remove the friend from the user's friend request list and the user from the friend's friend wait list
                        userFriendRequests.remove(friendId)
                        friendFriendWaitList.remove(userId)
                        userRef.update("friendRequests", userFriendRequests).await()
                        friendRef.update("friendWaitList", friendFriendWaitList).await()
                        emit(DataState.Success(Unit))
                    } else {
                        emit(DataState.Error(Exception("Không tìm thấy người dùng trong danh sách chờ")))
                        return@flow
                    }
                } else {
                    emit(DataState.Error(Exception("Không tìm thấy người dùng")))
                    return@flow
                }

            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }

    override suspend fun removeFriend(userId: String, friendId: String): Flow<DataState<Unit>> {
        return flow {
            try {
                emit(DataState.Loading)
                val userRef = firestore.collection("users").document(userId)
                val friendRef = firestore.collection("users").document(friendId)
                val user = userRef.get().await().toObject(User::class.java)
                val friend = friendRef.get().await().toObject(User::class.java)
                if (user != null && friend != null) {
                    val userFriends = user.friends.toMutableList()
                    val friendFriends = friend.friends.toMutableList()
                    if (userFriends.contains(friendId) && friendFriends.contains(userId)) {
                        userFriends.remove(friendId)
                        friendFriends.remove(userId)
                        userRef.update("friends", userFriends).await()
                        friendRef.update("friends", friendFriends).await()
                        val chatRoomId =
                            if (userId < friendId) "${userId}_$friendId" else "${friendId}_$userId"
                        // Delete the chat room between the user and the friend if it exists
                        chatService.deleteChatRoom(chatRoomId)
                        emit(DataState.Success(Unit))
                    } else {
                        emit(DataState.Error(Exception("Không tìm thấy người dùng trong danh sách bạn bè")))
                        return@flow
                    }
                } else {
                    emit(DataState.Error(Exception("Không tìm thấy người dùng")))
                    return@flow
                }
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }

    override suspend fun getFriendList(): Flow<DataState<List<User>>> {
        return callbackFlow {
            try {
                trySend(DataState.Loading)
                val userDocument =
                    firestore.collection("users").document(accountService.currentUserId)
                val userListener = userDocument.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(DataState.Error(error))
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val user = snapshot.toObject(User::class.java)
                        if (user != null) {
                            val friendList = user.friends
                            CoroutineScope(Dispatchers.IO).launch {
                                val friends = friendList.map { friendId ->
                                    async {
                                        firestore.collection("users").document(friendId).get()
                                            .await().toObject(User::class.java)
                                    }
                                }.awaitAll().filterNotNull()
                                trySend(DataState.Success(friends))
                            }
                        } else {
                            trySend(DataState.Error(Exception("Không tìm thấy người dùng")))
                        }
                    }
                }
                awaitClose {
                    // Close channel when the listener is removed
                    channel.close()
                    userListener.remove()
                }
            } catch (e: Exception) {
                trySend(DataState.Error(e))
            }
        }
    }

    override suspend fun getWaitedFriendList(): Flow<DataState<List<User>>> {
        return callbackFlow {
            try {
                trySend(DataState.Loading)
                val userDocument =
                    firestore.collection("users").document(accountService.currentUserId)
                val userListener = userDocument.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(DataState.Error(error))
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val user = snapshot.toObject(User::class.java)
                        if (user != null) {
                            val friendWaitList = user.friendWaitList
                            CoroutineScope(Dispatchers.IO).launch {
                                val friends = friendWaitList.map { friendId ->
                                    async {
                                        firestore.collection("users").document(friendId).get()
                                            .await().toObject(User::class.java)
                                    }
                                }.awaitAll().filterNotNull()
                                trySend(DataState.Success(friends))
                            }
                        } else {
                            trySend(DataState.Error(Exception("Không tìm thấy người dùng")))
                        }
                    }
                }
                awaitClose {
                    // Close channel when the listener is removed
                    channel.close()
                    userListener.remove()
                }
            } catch (e: Exception) {
                trySend(DataState.Error(e))
            }
        }
    }

    override suspend fun removeWaitedFriend(
        userId: String, friendId: String
    ): Flow<DataState<Unit>> {
        return flow {
            try {
                emit(DataState.Loading)
                val userRef = firestore.collection("users").document(userId)
                val friendRef = firestore.collection("users").document(friendId)
                val user = userRef.get().await().toObject(User::class.java)
                val friend = friendRef.get().await().toObject(User::class.java)
                if (user != null && friend != null) {
                    val userFriendWaitList = user.friendWaitList.toMutableList()
                    val friendFriendRequests = friend.friendRequests.toMutableList()
                    if (userFriendWaitList.contains(friendId) && friendFriendRequests.contains(
                            userId
                        )
                    ) {
                        userFriendWaitList.remove(friendId)
                        friendFriendRequests.remove(userId)
                        userRef.update("friendWaitList", userFriendWaitList).await()
                        friendRef.update("friendRequests", friendFriendRequests).await()
                        emit(DataState.Success(Unit))
                    } else {
                        emit(DataState.Error(Exception("Không tìm thấy người dùng trong danh sách chờ")))
                        return@flow
                    }
                }
            } catch (
                e: Exception
            ) {
                emit(DataState.Error(e))
            }
        }
    }

    override suspend fun getRequestFriendList(): Flow<DataState<List<User>>> {
        return callbackFlow {
            try {
                trySend(DataState.Loading)
                val userDocument =
                    firestore.collection("users").document(accountService.currentUserId)
                val userListener = userDocument.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(DataState.Error(error))
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val user = snapshot.toObject(User::class.java)
                        if (user != null) {
                            val friendRequests = user.friendRequests
                            CoroutineScope(Dispatchers.IO).launch {
                                val friends = friendRequests.map { friendId ->
                                    async {
                                        firestore.collection("users").document(friendId).get()
                                            .await().toObject(User::class.java)
                                    }
                                }.awaitAll().filterNotNull()
                                trySend(DataState.Success(friends))
                            }
                        } else {
                            trySend(DataState.Error(Exception("Không tìm thấy người dùng")))
                        }
                    }
                }
                awaitClose {
                    // Close channel when the listener is removed
                    channel.close()
                    userListener.remove()
                }
            } catch (e: Exception) {
                trySend(DataState.Error(e))
            }
        }
    }

    override suspend fun addEmojiReaction(feedId: String, emoji: String): Flow<DataState<Unit>> {
        return flow {
            try {
                emit(DataState.Loading)
                val currentUser = accountService.getCurrentUser()
                val feedRef = firestore.collection("images").document(feedId)
                val feed = feedRef.get().await().toObject(UploadImage::class.java)

                val reactionRef = firestore.collection("reactions")
                    .whereEqualTo("userId", accountService.currentUserId)
                    .whereEqualTo("imageId", feedId)
                val reaction = reactionRef.get().await().toObjects(EmojiReaction::class.java)
                if (feed != null) {
                    val emojiReaction = EmojiReaction(
                        emojiId = emoji,
                        userId = accountService.currentUserId,
                        imageId = feedId,
                        isViewed = false,
                        userName = "${currentUser.firstName} ${currentUser.lastName}"
                    )
                    if (emoji !in reaction.map { it.emojiId }) {
                        // Add the emoji reaction to the firestore
                        firestore.collection("reactions").add(emojiReaction).await()
                        // Update the emoji id in firestore
                        firestore.collection("reactions")
                            .whereEqualTo("userId", accountService.currentUserId)
                            .whereEqualTo("emojiId", emoji).whereEqualTo("imageId", feedId).get()
                            .await().documents.firstOrNull()?.let {
                                firestore.collection("reactions").document(it.id)
                                    .update("reactionId", it.id).await()
                            }
                        emit(DataState.Success(Unit))
                    } else {
                        emit(DataState.Success(Unit))
                    }
                    // Send notification to the feed owner
                    val feedOwnerToken =
                        firestore.collection("fcmTokens").document(feed.userId).get().await()
                            .toObject(FCMToken::class.java)
                    if (feedOwnerToken != null) {
                        val notification = NotificationModel(
                            to = feedOwnerToken.token, notification = NotificationNotiModel(
                                title = "React mới",
                                body = "${currentUser.firstName} ${currentUser.lastName} đã react $emoji ảnh của bạn",
                            ), data = NotificationDataModel(
                                message = "${currentUser.firstName} ${currentUser.lastName} đã react $emoji ảnh của bạn",
                                image = feed.imageUrl
                            )
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            val result =
                                notificationServiceRepository.postNotification(notification)
                            if (result.isSuccessful) {
                                Log.d("Notification", "Notification sent successfully")
                            } else Log.d("Notification", "Notification failed to send")
                        }
                    }
                } else {
                    emit(DataState.Error(Exception("Không tìm thấy bài viết")))
                    return@flow
                }
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }

    override suspend fun getNumOfNewFeeds(friendIds: List<String>): Flow<DataState<Int>> {
        return callbackFlow {
            try {
                trySend(DataState.Loading)
                // Get the current user's id
                val currentUserId = accountService.currentUserId
                val lastCheckedTime = sharedPreferences.getLong("currentServerTime", 0)
                // Convert the lastCheckedTime to a Date object
                val lastCheckedDate = Date(lastCheckedTime * 1000L)
                // Create a list to hold all the deferred results
                val deferredResults = mutableListOf<Deferred<Int>>()
                // For each friend, start a coroutine to count the number of feeds posted since `lastCheckedDate`
                for (friendId in friendIds) {
                    deferredResults.add(coroutineScope {
                        async {
                            val visibleToAllImagesCount =
                                firestore.collection("images").whereEqualTo("userId", friendId)
                                    .whereEqualTo("visibleToAll", true)
                                    .whereGreaterThan("createdAt", lastCheckedDate).get().await()
                                    .size()

                            val visibleToUserImagesCount =
                                firestore.collection("images").whereEqualTo("userId", friendId)
                                    .whereArrayContains("visibleUserIds", currentUserId)
                                    .whereGreaterThan("createdAt", lastCheckedDate).get().await()
                                    .size()

                            visibleToAllImagesCount + visibleToUserImagesCount
                        }
                    })
                }
                // Wait for all coroutines to finish and sum their results
                val count = deferredResults.awaitAll().sum()
                trySend(DataState.Success(count))
            } catch (e: Exception) {
                trySend(DataState.Error(e))
            }
            awaitClose { channel.close() }
        }
    }

    override suspend fun generateDynamicLink(userId: String): Uri? {
        val dynamicLink = firebaseDynamicLinks.dynamicLink {
            link = Uri.parse("https://lokcet.page.link/addfriend/$userId")
            domainUriPrefix = "https://lokcet.page.link"
            androidParameters("com.grouptwo.lokcet") {
                minimumVersion = 1
            }
            socialMetaTagParameters {
                title = "Kết bạn trên Lokcet \uD83D\uDC9B"
                description =
                    "Kết bạn trên Lokcet! Mời bạn tham gia vào mạng lưới kết nối của Lokcet và kết bạn với tôi. Cùng nhau khám phá những trải nghiệm mới và chia sẻ những khoảnh khắc đáng nhớ. Bấm vào liên kết này để mở ứng dụng Lokcet và kết bạn ngay: "
                imageUrl = Uri.parse("https://i.ibb.co/RPCzz9W/app-logo.png")
            }
        }.uri

        val shortLinkTask =
            FirebaseDynamicLinks.getInstance().createDynamicLink().setLongLink(dynamicLink)
                .buildShortDynamicLink()

        return withContext(Dispatchers.IO) {
            Tasks.await(shortLinkTask).shortLink
        }
    }

    override suspend fun getUserNameFromId(userId: String): Flow<DataState<String>> {
        return flow {
            try {
                emit(DataState.Loading)
                val userDocument = firestore.collection("users").document(userId)
                val user = userDocument.get().await().toObject(User::class.java)
                if (user != null) {
                    emit(DataState.Success(user.firstName + " " + user.lastName))
                } else {
                    emit(DataState.Error(Exception("Không tìm thấy người dùng")))
                    return@flow
                }
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }

}