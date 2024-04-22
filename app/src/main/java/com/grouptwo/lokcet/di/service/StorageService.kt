package com.grouptwo.lokcet.di.service

import com.grouptwo.lokcet.data.model.UploadImage
import com.grouptwo.lokcet.utils.DataState
import kotlinx.coroutines.flow.Flow

interface StorageService {
    val images: Flow<List<UploadImage>>
    suspend fun uploadImage(
        imageUpload: ByteArray,
        imageCaption: String,
        visibleUserIds: List<String>? = null,
    ): Flow<DataState<Unit>>

    suspend fun getImages(): List<UploadImage>

    suspend fun deleteImage(imageId: String)

    suspend fun getImagesUploadByUser(userId: String): List<UploadImage>
}