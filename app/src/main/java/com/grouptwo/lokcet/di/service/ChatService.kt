package com.grouptwo.lokcet.di.service

import com.grouptwo.lokcet.data.model.ChatRoom
import com.grouptwo.lokcet.data.model.LatestMessage
import com.grouptwo.lokcet.data.model.Message
import com.grouptwo.lokcet.data.model.UploadImage
import com.grouptwo.lokcet.data.model.User
import com.grouptwo.lokcet.utils.DataState
import kotlinx.coroutines.flow.Flow

interface ChatService {
    // create chat room
    suspend fun createChatRoom(user2Id: String) // When user accept friend request, create chat room between user and friend

    // send message
    suspend fun sendMessage(
        chatRoomId: String,
        messageContent: String,
    ): Flow<DataState<Unit>> // Send message to chat room

    // send reply message to feed
    suspend fun sendReplyMessage(
        chatRoomId: String,
        messageContent: String,
        feed: UploadImage,
    ): Flow<DataState<Unit>> // Send reply message to feed

    // get chat room list
    suspend fun getChatRoomList(
        friendList: List<User>,
    ): Flow<DataState<List<ChatRoom>>> // Get chat room list of user

    // get message list of chat room
    suspend fun getMessageList(chatRoomId: String): Flow<DataState<List<Message>>> // Get message list of chat room

    // get last message of chat room to show in Chat List screen
    suspend fun getLastestMessage(chatRoomList: List<ChatRoom>): Flow<DataState<Map<String,LatestMessage>> >// Get last message of chat room to show in Chat List screen

    // delete chat room
    suspend fun deleteChatRoom(chatRoomId: String) // Delete chat room and all messages in chat room when user unfriend with friend or delete account

}