package com.grouptwo.lokcet.di.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.grouptwo.lokcet.data.model.ChatRoom
import com.grouptwo.lokcet.data.model.FCMToken
import com.grouptwo.lokcet.data.model.LatestMessage
import com.grouptwo.lokcet.data.model.Message
import com.grouptwo.lokcet.data.model.NotificationDataModel
import com.grouptwo.lokcet.data.model.NotificationModel
import com.grouptwo.lokcet.data.model.NotificationNotiModel
import com.grouptwo.lokcet.data.model.UploadImage
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.di.service.ChatService
import com.grouptwo.lokcet.di.service.NotificationService
import com.grouptwo.lokcet.di.service.StorageService
import com.grouptwo.lokcet.utils.DataState
import com.grouptwo.lokcet.utils.getFriendId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatServiceImpl @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService,
    private val firestore: FirebaseFirestore,
    private val notificationServiceRepository: NotificationServiceRepository,
) : ChatService {
    override suspend fun createChatRoom(user2Id: String) {
        try {
            // When user accept friend request, create chat room between user and friend
            val user1Id = accountService.currentUserId
            // Create chat room id that is combination of user1Id and user2Id and easy to retrieve chat room
            val chatRoomId = if (user1Id < user2Id) "${user1Id}_$user2Id" else "${user2Id}_$user1Id"
            val chatRoom = ChatRoom(
                chatRoomId = chatRoomId, user1Id = user1Id, user2Id = user2Id
            )
            // Create chat room
            firestore.collection("chatrooms").document(chatRoomId).set(chatRoom).await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun sendMessage(
        chatRoomId: String,
        messageContent: String,
    ): Flow<DataState<Unit>> {
        return flow {
            try {
//                emit(DataState.Loading)
//                val user1Id = accountService.currentUserId
//                val message = Message(
//                    senderId = user1Id,
//                    receiverId = chatRoomId.getFriendId(user1Id),
//                    messageContent = messageContent,
//                    seenAt = false
//                )
//                val chatRoomRef = firestore.collection("chatrooms").document(chatRoomId)
//                val messagesRef = chatRoomRef.collection("messages")
//                val messageDocRef = messagesRef.add(message).await()
//                messageDocRef.update("messageId", messageDocRef.id).await()
//                // Get the updated message
//                val updatedMessage = messageDocRef.get().await().toObject(Message::class.java)!!
//                // Save latest message of chat room in latest messages collection to show in chat list (OPTIMIZATION)
//                val latestMessage = LatestMessage(
//                    chatRoomId = chatRoomId, message = updatedMessage
//                )
//                firestore.collection("latest_messages").document(chatRoomId).set(latestMessage)
//                    .await()
//                Log.e("ChatServiceImpl", "sendMessage: $updatedMessage")
//                emit(DataState.Success(Unit))
                emit(DataState.Loading)
                val user1Id = accountService.currentUserId
                val userName = accountService.getCurrentUser().firstName
                val chatRoomRef = firestore.collection("chatrooms").document(chatRoomId)
                val messagesRef = chatRoomRef.collection("messages")
                val newDocRef = messagesRef.document()
                val message = Message(
                    messageId = newDocRef.id,
                    senderId = user1Id,
                    receiverId = chatRoomId.getFriendId(user1Id),
                    messageContent = messageContent,
                    seenAt = false
                )
                newDocRef.set(message).await()
                // Get the message
                val newMessage = newDocRef.get().await().toObject(Message::class.java)!!
                // Save latest message of chat room in latest messages collection to show in chat list (OPTIMIZATION)
                val latestMessage = LatestMessage(
                    chatRoomId = chatRoomId, message = newMessage
                )
                firestore.collection("latest_messages").document(chatRoomId).set(latestMessage)
                    .await()
                // Send notification to receiver
                val receiverToken =
                    firestore.collection("fcmTokens").document(newMessage.receiverId).get().await()
                        .toObject(FCMToken::class.java)
                if (receiverToken != null) {
                    val notification = NotificationModel(
                        to = receiverToken.token, notification = NotificationNotiModel(
                            title = "${userName} đã gửi tin nhắn cho bạn",
                            body = newMessage.messageContent,

                            ), data = NotificationDataModel(
                            message = newMessage.messageContent, image = ""
                        )
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        val result = notificationServiceRepository.postNotification(notification)
                    }
                }
                emit(DataState.Success(Unit))


            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }

    override suspend fun sendReplyMessage(
        chatRoomId: String, messageContent: String, feed: UploadImage
    ): Flow<DataState<Unit>> {
        return flow {
            try {
//                emit(DataState.Loading)
//                val user1Id = accountService.currentUserId
//                val message = Message(
//                    senderId = user1Id,
//                    receiverId = chatRoomId.getFriendId(user1Id),
//                    messageContent = messageContent,
//                    isReplyToFeed = true,
//                    feed = feed,
//                    seenAt = false
//                )
//                val chatRoomRef = firestore.collection("chatrooms").document(chatRoomId)
//                val messagesRef = chatRoomRef.collection("messages")
//                val messageDocRef = messagesRef.add(message).await()
//                messageDocRef.update("messageId", messageDocRef.id).await()
//                // Get the updated message
//                val updatedMessage = messageDocRef.get().await().toObject(Message::class.java)!!
//                // Save latest message of chat room in latest messages collection to show in chat list (OPTIMIZATION)
//                val latestMessage = LatestMessage(
//                    chatRoomId = chatRoomId, message = updatedMessage
//                )
//                firestore.collection("latest_messages").document(chatRoomId).set(latestMessage)
//                    .await()
//                emit(DataState.Success(Unit))
                emit(DataState.Loading)
                val user1Id = accountService.currentUserId
                val userName = accountService.getCurrentUser().firstName
                val chatRoomRef = firestore.collection("chatrooms").document(chatRoomId)
                val messagesRef = chatRoomRef.collection("messages")
                val newDocRef = messagesRef.document()
                val message = Message(
                    messageId = newDocRef.id,
                    senderId = user1Id,
                    receiverId = chatRoomId.getFriendId(user1Id),
                    messageContent = messageContent,
                    isReplyToFeed = true,
                    feed = feed,
                    seenAt = false
                )
                newDocRef.set(message).await()
                // Get the message
                val newMessage = newDocRef.get().await().toObject(Message::class.java)!!
                // Save latest message of chat room in latest messages collection to show in chat list (OPTIMIZATION)
                val latestMessage = LatestMessage(
                    chatRoomId = chatRoomId, message = newMessage
                )
                firestore.collection("latest_messages").document(chatRoomId).set(latestMessage)
                    .await()
                // Send notification to receiver
                val receiverToken =
                    firestore.collection("fcmTokens").document(newMessage.receiverId).get().await()
                        .toObject(FCMToken::class.java)
                if (receiverToken != null) {
                    val notification = NotificationModel(
                        to = receiverToken.token, notification = NotificationNotiModel(
                            title = "$userName đã trả lời ảnh của bạn",
                            body = newMessage.messageContent,
                        ), data = NotificationDataModel(
                            message = newMessage.messageContent, image = feed.imageUrl
                        )
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        val result = notificationServiceRepository.postNotification(notification)
                    }

                }
                emit(DataState.Success(Unit))

            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }

    override suspend fun getChatRoomList(
        friendList: List<User>
    ): Flow<DataState<List<ChatRoom>>> {
        return callbackFlow {
            try {
                trySend(DataState.Loading)
                // Get chat room list of current user from friend list real time updates
                val chatRoomList = mutableListOf<ChatRoom>()
                val listener = mutableListOf<ListenerRegistration>()
                val jobs = friendList.map { friend ->
                    // Launch a separate coroutine for each listener
                    CoroutineScope(Dispatchers.IO).async {
                        val chatRoomId =
                            if (accountService.currentUserId < friend.id) "${accountService.currentUserId}_${friend.id}" else "${friend.id}_${accountService.currentUserId}"
                        val chatRoomListener =
                            firestore.collection("chatrooms").document(chatRoomId)
                                .addSnapshotListener { value, error ->
                                    if (error != null) {
                                        trySend(DataState.Error(error))
                                        return@addSnapshotListener
                                    }
                                    val chatRoom = value?.toObject(ChatRoom::class.java)
                                    if (chatRoom != null) {
                                        chatRoomList.add(chatRoom)
                                    }
                                    trySend(DataState.Success(chatRoomList))
                                }
                        listener.add(chatRoomListener)
                    }
                }

                // Wait for all coroutines to finish
                jobs.awaitAll()

                // Call awaitClose at the end of callbackFlow block
                awaitClose {
                    channel.close()
                    // Close all listeners
                    listener.forEach { it.remove() }
                }

            } catch (e: Exception) {
                trySend(DataState.Error(e))
            }
        }
    }

    //    override suspend fun markLastMessageAsSeen(chatRoomId: String) {
//        try {
//            // Get the last message of the chat room and mark it as seen
//            val lastMessage =
//                firestore.collection("chatrooms").document(chatRoomId).collection("messages")
//                    .orderBy("createdAt", Query.Direction.DESCENDING).limit(1).get()
//                    .await().documents.firstOrNull()?.toObject(Message::class.java)
//            // Query the latest message of the chat room
//            if (lastMessage != null  && lastMessage.receiverId == accountService.currentUserId && !lastMessage.seenAt) {
//                // Update the 'seenAt' field of the last message with the current server timestamp
//                firestore.collection("chatrooms").document(chatRoomId).collection("messages")
//                    .document(lastMessage.messageId).update("seenAt", true).await()
//                val latestMessage =
//                    firestore.collection("latest_messages").document(chatRoomId).get().await()
//                        .toObject(LatestMessage::class.java)
//                // Check if the latest message matches the last message and has not been seen
//                if (latestMessage != null && latestMessage.message.receiverId == accountService.currentUserId && !latestMessage.message.seenAt) {
//                    // Update the 'seenAt' field of the latest message with the current server timestamp
//                    firestore.collection("latest_messages").document(chatRoomId)
//                        .update("message.seenAt", true).await()
//                }
//            }
//
//        } catch (e: Exception) {
//            // Handle the exception
//            Log.d("ChatServiceImpl", "markLastMessageAsSeen: $e")
//            throw e
//        }
//    }
    override suspend fun markLastMessageAsSeen(chatRoomId: String) {
        try {
            var attempts = 0
            while (attempts < 3) {
                // Get the last message of the chat room and mark it as seen
                val lastMessage =
                    firestore.collection("chatrooms").document(chatRoomId).collection("messages")
                        .orderBy("createdAt", Query.Direction.DESCENDING).limit(1).get()
                        .await().documents.firstOrNull()?.toObject(Message::class.java)
                if (lastMessage != null && lastMessage.messageId.isNotEmpty() && lastMessage.receiverId == accountService.currentUserId && !lastMessage.seenAt) {
                    // Update the 'seenAt' field of the last message with the current server timestamp
                    firestore.collection("chatrooms").document(chatRoomId).collection("messages")
                        .document(lastMessage.messageId).update("seenAt", true).await()
                    val latestMessage =
                        firestore.collection("latest_messages").document(chatRoomId).get().await()
                            .toObject(LatestMessage::class.java)
                    // Check if the latest message matches the last message and has not been seen
                    if (latestMessage != null && latestMessage.message.receiverId == accountService.currentUserId && !latestMessage.message.seenAt) {
                        // Update the 'seenAt' field of the latest message with the current server timestamp
                        firestore.collection("latest_messages").document(chatRoomId)
                            .update("message.seenAt", true).await()
                    }
                    break
                } else {
                    attempts++
                    delay(1000)  // wait for 1 second before retrying
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getMessageList(chatRoomId: String): Flow<DataState<List<Message>>> {
        return callbackFlow {
            try {
                trySend(DataState.Loading)
                // Get message list of chat room real time updates
                val messageList = mutableListOf<Message>()
                val messagesListener =
                    firestore.collection("chatrooms").document(chatRoomId).collection("messages")
                        .orderBy("createdAt").addSnapshotListener(
                            MetadataChanges.INCLUDE
                        ) { value, error ->

                            if (error != null) {
                                trySend(DataState.Error(error))
                                return@addSnapshotListener
                            }
                            val messages = value?.toObjects(Message::class.java)
                            if (messages != null) {
                                messageList.clear()
                                messageList.addAll(messages)
                                trySend(DataState.Success(messageList))
                            }
                        }
                awaitClose {
                    channel.close()
                    // Close listener
                    messagesListener.remove()
                }
            } catch (e: Exception) {
                trySend(DataState.Error(e))
            }
        }
    }

    override suspend fun getLastestMessage(chatRoomList: List<ChatRoom>): Flow<DataState<Map<String, LatestMessage>>> {
        return callbackFlow {
            try {
                trySend(DataState.Loading)
                // Get last message of chat room to show in chat list screen real time updates
                val latestMessageMap = mutableMapOf<String, LatestMessage>()
                val listeners = mutableListOf<ListenerRegistration>()
                val jobs = chatRoomList.map { chatRoom ->
                    // Launch a separate coroutine for each listener
                    CoroutineScope(Dispatchers.IO).launch {
                        val latestMessageListener =
                            firestore.collection("latest_messages").document(chatRoom.chatRoomId)
                                .addSnapshotListener { value, error ->
                                    if (error != null) {
                                        trySend(DataState.Error(error))
                                        return@addSnapshotListener
                                    }
                                    val latestMessage = value?.toObject(LatestMessage::class.java)
                                    if (latestMessage != null) {
                                        latestMessageMap[chatRoom.chatRoomId] = latestMessage
                                    }
                                    trySend(DataState.Success(latestMessageMap))
                                }
                        listeners.add(latestMessageListener)

                    }
                }
                // Wait for all coroutines to finish
                jobs.forEach { it.join() }

                // Call awaitClose at the end of callbackFlow block
                awaitClose {
                    channel.close()
                    listeners.forEach { it.remove() }
                }
            } catch (e: Exception) {
                trySend(DataState.Error(e))
            }
        }
    }

    override suspend fun deleteChatRoom(chatRoomId: String) {
        try {
            // Delete chat room and all messages in chat room when user unfriend with friend or delete account
            firestore.collection("chatrooms").document(chatRoomId).delete().await()
            firestore.collection("latest_messages").document(chatRoomId).delete().await()
            // Delete all messages in chat room
            val deleteMessages = CoroutineScope(Dispatchers.IO).async {
                val messagesRef =
                    firestore.collection("chatrooms").document(chatRoomId).collection("messages")
                val messages = messagesRef.get().await().documents
                val deletionJobs = messages.map { message ->
                    async {
                        message.reference.delete().await()
                    }
                }
                deletionJobs.awaitAll()
            }
            deleteMessages.await() // Wait for all messages to be deleted
        } catch (e: Exception) {
            throw e
        }
    }

}

