package com.grouptwo.lokcet.data.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ChatRoom(
    val chatRoomId: String = "",
    val user1Id: String = "",
    val user2Id: String = "",
    val messages: List<Message> = emptyList(),
    @ServerTimestamp val createdAt: Date = Date()
)

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val messageContent: String = "",
    @PropertyName("replyToFeed") var isReplyToFeed: Boolean = false,
    val feed: UploadImage = UploadImage(), // This is the document id of the feed if the message is a reply to a feed(Upload Image id)
    val seenAt: Boolean = false, // When the message is seen by the receiver user it is true (false if not seen)
    @ServerTimestamp val createdAt: Date = Date()
)

data class LatestMessage(
    val chatRoomId: String = "",
    val message: Message = Message(),
)