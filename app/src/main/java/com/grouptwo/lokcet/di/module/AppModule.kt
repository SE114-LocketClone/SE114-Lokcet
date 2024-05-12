package com.grouptwo.lokcet.di.module

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import androidx.paging.PagingConfig
import com.google.firebase.Firebase
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.dynamicLinks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.grouptwo.lokcet.di.paging.FeedRepository
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.utils.Constants
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

    @Provides
    fun providePageConfig(): PagingConfig {
        return PagingConfig(
            pageSize = Constants.PAGE_SIZE.toInt(),
            enablePlaceholders = false
        )
    }

    @Provides
    fun provideFeedRepository(
        pagingConfig: PagingConfig, firestore: FirebaseFirestore, accountService: AccountService
    ): FeedRepository {
        return FeedRepository(pagingConfig, firestore, accountService)
    }


    @Provides
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("local_shared_pref", Context.MODE_PRIVATE)
    }

    @Provides
    fun provideFirebaseDynamicLink(): FirebaseDynamicLinks {
        return Firebase.dynamicLinks
    }

}