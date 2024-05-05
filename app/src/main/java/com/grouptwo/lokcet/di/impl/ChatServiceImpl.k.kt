package com.grouptwo.lokcet.di.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.grouptwo.lokcet.data.model.ChatRoom
import com.grouptwo.lokcet.data.model.Message
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.di.service.ChatService
import com.grouptwo.lokcet.di.service.StorageService
import com.grouptwo.lokcet.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatServiceImpl @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService,
    private val firestore: FirebaseFirestore
) : ChatService {
    override suspend fun createChatRoom(user2Id: String) { // When user accept friend request, create chat room between user and friend
        val user1Id = accountService.currentUserId
        // Create chat room id that is combination of user1Id and user2Id and easy to retrieve chat room
        val chatRoomId =
            if (user1Id < user2Id) "${user1Id}_$user2Id" else "${user2Id}_$user1Id"
        val chatRoom = ChatRoom(
            chatRoomId = chatRoomId,
            user1Id = user1Id,
            user2Id = user2Id
        )
        // Create chat room
        firestore.collection("chatrooms").document(chatRoomId).set(chatRoom).await()
    }

    override suspend fun sendMessage(
        chatRoomId: String,
        messageContent: String,
    ): Flow<DataState<Unit>> {
        return flow {
            try {
                val user1Id = accountService.currentUserId
                val message = Message(
                    senderId = user1Id,
                    receiverId = chatRoomId.replace(user1Id, "").replace("_", ""),
                    messageContent = messageContent
                )
                val chatRoomRef = firestore.collection("chatrooms").document(chatRoomId)
                val messagesRef = chatRoomRef.collection("messages")
                val messageDocRef = messagesRef.add(message).await()
                messageDocRef.update("messageId", messageDocRef.id).await()
                emit(DataState.Success(Unit))
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }

    override suspend fun sendReplyMessage(
        chatRoomId: String,
        messageContent: String,
        feedId: String
    ): Flow<DataState<Unit>> {
        return flow {
            try {
                val user1Id = accountService.currentUserId
                val message = Message(
                    senderId = user1Id,
                    receiverId = chatRoomId.replace(user1Id, "").replace("_", ""),
                    messageContent = messageContent,
                    isReplyToFeed = true,
                    feedId = feedId
                )
                val chatRoomRef = firestore.collection("chatrooms").document(chatRoomId)
                val messagesRef = chatRoomRef.collection("messages")
                val messageDocRef = messagesRef.add(message).await()
                messageDocRef.update("messageId", messageDocRef.id).await()
                emit(DataState.Success(Unit))
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }

    override suspend fun getChatRoomList(): Flow<DataState<List<ChatRoom>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMessageList(chatRoomId: String): Flow<DataState<List<Message>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getLastMessage(chatRoomId: String): Flow<DataState<Message>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteChatRoom(chatRoomId: String): Flow<DataState<Unit>> {
        TODO("Not yet implemented")
    }
}

