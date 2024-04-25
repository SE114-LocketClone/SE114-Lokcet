package com.grouptwo.lokcet.di.module

import android.content.ContentResolver
import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File

@Module
@InstallIn(SingletonComponent::class)

object AppModule {
    @Provides
    fun provideGson(): Gson = Gson()

    @Provides
    fun provideCacheDir(@ApplicationContext context: Context): File {
        return context.cacheDir
    }

    @Provides
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }
}