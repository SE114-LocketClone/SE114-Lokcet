package com.grouptwo.lokcet.di.service

interface ContactService {
    suspend fun getContactList(): List<String>
}