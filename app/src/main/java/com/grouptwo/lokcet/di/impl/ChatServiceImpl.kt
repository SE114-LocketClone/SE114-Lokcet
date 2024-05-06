package com.grouptwo.lokcet.di.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.grouptwo.lokcet.data.model.ChatRoom
import com.grouptwo.lokcet.data.model.LatestMessage
import com.grouptwo.lokcet.data.model.Message
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.di.service.ChatService
import com.grouptwo.lokcet.di.service.StorageService
import com.grouptwo.lokcet.utils.DataState
import com.grouptwo.lokcet.utils.getFriendId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
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
                emit(DataState.Loading)
                val user1Id = accountService.currentUserId
                val message = Message(
                    senderId = user1Id,
                    receiverId = chatRoomId.getFriendId(user1Id),
                    messageContent = messageContent
                )
                val chatRoomRef = firestore.collection("chatrooms").document(chatRoomId)
                val messagesRef = chatRoomRef.collection("messages")
                val messageDocRef = messagesRef.add(message).await()
                messageDocRef.update("messageId", messageDocRef.id).await()
                // Get the updated message
                val updatedMessage = messageDocRef.get().await().toObject(Message::class.java)!!
                // Save latest message of chat room in latest messages collection to show in chat list (OPTIMIZATION)
                val latestMessage = LatestMessage(
                    chatRoomId = chatRoomId, message = updatedMessage
                )
                firestore.collection("latest_messages").document(chatRoomId).set(latestMessage)
                    .await()
                emit(DataState.Success(Unit))
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }

    override suspend fun sendReplyMessage(
        chatRoomId: String, messageContent: String, feedId: String
    ): Flow<DataState<Unit>> {
        return flow {
            try {
                emit(DataState.Loading)
                val user1Id = accountService.currentUserId
                val message = Message(
                    senderId = user1Id,
                    receiverId = chatRoomId.getFriendId(user1Id),
                    messageContent = messageContent,
                    isReplyToFeed = true,
                    feedId = feedId
                )
                val chatRoomRef = firestore.collection("chatrooms").document(chatRoomId)
                val messagesRef = chatRoomRef.collection("messages")
                val messageDocRef = messagesRef.add(message).await()
                messageDocRef.update("messageId", messageDocRef.id).await()
                // Get the updated message
                val updatedMessage = messageDocRef.get().await().toObject(Message::class.java)!!
                // Save latest message of chat room in latest messages collection to show in chat list (OPTIMIZATION)
                val latestMessage = LatestMessage(
                    chatRoomId = chatRoomId, message = updatedMessage
                )
                firestore.collection("latest_messages").document(chatRoomId).set(latestMessage)
                    .await()
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
                val jobs = friendList.map { friend ->
                    // Launch a separate coroutine for each listener
                    CoroutineScope(Dispatchers.IO).launch {
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
                                    } else {
                                        trySend(DataState.Error(Exception("Không tìm thấy phòng chat")))
                                    }
                                    trySend(DataState.Success(chatRoomList))
                                }
                        awaitClose {
                            // Close listener
                            chatRoomListener.remove()
                        }
                    }
                }

                // Wait for all coroutines to finish
                jobs.forEach { it.join() }

            } catch (e: Exception) {
                trySend(DataState.Error(e))
            }
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
                        .orderBy("createdAt")
                        .addSnapshotListener { value, error ->
                            if (error != null) {
                                trySend(DataState.Error(error))
                                return@addSnapshotListener
                            }
                            val messages = value?.toObjects(Message::class.java)
                            if (messages != null) {
                                messageList.clear()
                                messageList.addAll(messages)
                                trySend(DataState.Success(messageList))
                            } else {
                                trySend(DataState.Error(Exception("Không tìm thấy tin nhắn")))
                            }
                        }
                awaitClose {
                    // Close listener
                    messagesListener.remove()
                }
            } catch (e: Exception) {
                trySend(DataState.Error(e))
            }
        }
    }

    override suspend fun getLastestMessage(chatRoomId: String): Flow<DataState<LatestMessage>> {
        return callbackFlow {
            try {
                trySend(DataState.Loading)
                // Get last message of chat room to show in chat list screen real time updates
                val latestMessageListener =
                    firestore.collection("latest_messages").document(chatRoomId)
                        .addSnapshotListener { value, error ->
                            if (error != null) {
                                trySend(DataState.Error(error))
                                return@addSnapshotListener
                            }
                            val latestMessage = value?.toObject(LatestMessage::class.java)
                            if (latestMessage != null) {
                                trySend(DataState.Success(latestMessage))
                            } else {
                                trySend(DataState.Error(Exception("Không tìm thấy tin nhắn mới nhất")))
                            }
                        }
                awaitClose {
                    // Close listener
                    latestMessageListener.remove()
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
                val messagesRef = firestore.collection("chatrooms").document(chatRoomId)
                    .collection("messages")
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

