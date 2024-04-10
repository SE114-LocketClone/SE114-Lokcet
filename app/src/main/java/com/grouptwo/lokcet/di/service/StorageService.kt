package com.grouptwo.lokcet.di.service

import com.grouptwo.lokcet.data.model.UploadImage
import kotlinx.coroutines.flow.Flow

interface StorageService {
    val images: Flow<List<UploadImage>>
    suspend fun uploadImage(image: UploadImage)
    suspend fun getImages(): List<UploadImage>

    suspend fun deleteImage(imageId: String)

    suspend fun getImagesUploadByUser(userId: String): List<UploadImage>
}