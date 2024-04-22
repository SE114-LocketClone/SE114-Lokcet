package com.grouptwo.lokcet.utils

sealed class ConnectionState {
    object Unknown : ConnectionState()
    object Available : ConnectionState()
    object Unavailable : ConnectionState()
}