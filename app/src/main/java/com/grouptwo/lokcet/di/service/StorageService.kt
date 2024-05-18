package com.grouptwo.lokcet.di.service

import android.graphics.Bitmap
import com.grouptwo.lokcet.data.model.UploadImage
import com.grouptwo.lokcet.utils.DataState
import kotlinx.coroutines.flow.Flow

interface StorageService {
    val images: Flow<List<UploadImage>>
    suspend fun uploadImage(
        imageUpload: ByteArray,
        imageCaption: String,
        visibleUserIds: List<String> = emptyList(),
    ): Flow<DataState<Unit>>

    suspend fun deleteImage(imageId: String): Flow<DataState<Unit>>

    suspend fun getImagesUploadByUser(userId: String): List<UploadImage>

    suspend fun downloadImage(imageUrl: String): Bitmap
}