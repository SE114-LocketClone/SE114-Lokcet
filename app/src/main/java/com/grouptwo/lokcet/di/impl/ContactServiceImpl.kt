package com.grouptwo.lokcet.di.impl

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.ContactsContract
import com.grouptwo.lokcet.di.service.ContactService
import javax.inject.Inject

class ContactServiceImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : ContactService {
    @SuppressLint("Range")
    override suspend fun getContactList(): List<String> {
        val contactList = mutableListOf<String>()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        while (cursor?.moveToNext() == true) {
            // Get the contact phone number and remove the leading 0
            val phoneNumber =
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    .trimStart('0').replace("-", "").replace(" ", "")
            contactList.add(phoneNumber)
        }
        cursor?.close()
        return contactList
    }
}