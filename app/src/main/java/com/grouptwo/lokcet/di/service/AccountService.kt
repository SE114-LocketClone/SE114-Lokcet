package com.grouptwo.lokcet.di.service

import kotlinx.coroutines.flow.Flow

interface AccountService{
    val currentUserId: String
    val hasUser : Boolean
    val currentUser : Flow<User>
}