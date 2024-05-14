package com.grouptwo.lokcet.di.module

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import androidx.paging.PagingConfig
import com.google.firebase.Firebase
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.dynamicLinks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import com.google.gson.Gson
import com.grouptwo.lokcet.di.impl.NotificationServiceRepository
import com.grouptwo.lokcet.di.paging.FeedRepository
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.di.service.NotificationService
import com.grouptwo.lokcet.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
            pageSize = Constants.PAGE_SIZE.toInt(), enablePlaceholders = false
        )
    }

    @Provides
    fun provideFeedRepository(
        pagingConfig: PagingConfig, firestore: FirebaseFirestore, accountService: AccountService
    ): FeedRepository {
        return FeedRepository(pagingConfig, firestore, accountService)
    }


    @Provides
    fun provideSharedPref(app: Application): SharedPreferences {
        return app.getSharedPreferences("local_shared_pref", Context.MODE_PRIVATE)
    }

    @Provides
    fun provideFirebaseDynamicLink(): FirebaseDynamicLinks {
        return Firebase.dynamicLinks
    }


    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    fun provideRetrofit(
        gsonConverterFactory: GsonConverterFactory
    ): NotificationService {
        return Retrofit.Builder().baseUrl(NotificationService.BASE_URL)
            .addConverterFactory(gsonConverterFactory).build()
            .create(NotificationService::class.java)
    }

    @Provides
    fun provideNotificationRepository(
        notificationService: NotificationService
    ): NotificationServiceRepository {
        return NotificationServiceRepository(notificationService)
    }

    @Provides
    fun provideFirebaseMessaging(): FirebaseMessaging {
        return Firebase.messaging
    }

}