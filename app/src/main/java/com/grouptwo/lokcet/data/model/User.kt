package com.grouptwo.lokcet.data.model

import com.google.type.DateTime

data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val gender: String = "",
    val phoneNumber: String = "",
    val profilePicture: String = "",
    val friends: List<String> = emptyList(),
    val friendRequests: List<String> = emptyList(),
    val isOnline: Boolean = false,
    val isBannded: Boolean = false,
    val isDeleted: Boolean = false,

)