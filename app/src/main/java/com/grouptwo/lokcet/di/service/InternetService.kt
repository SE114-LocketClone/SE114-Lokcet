package com.grouptwo.lokcet.di.service

import com.grouptwo.lokcet.utils.ConnectionState
import kotlinx.coroutines.flow.Flow

interface InternetService {
    val networkStatus: Flow<ConnectionState>
}