package com.grouptwo.lokcet.di.module

import com.grouptwo.lokcet.di.impl.AccountServiceImpl
import com.grouptwo.lokcet.di.impl.ChatServiceImpl
import com.grouptwo.lokcet.di.impl.ContactServiceImpl
import com.grouptwo.lokcet.di.impl.InternetServiceImpl
import com.grouptwo.lokcet.di.impl.LocationServiceImpl
import com.grouptwo.lokcet.di.impl.StorageServiceImpl
import com.grouptwo.lokcet.di.impl.UserServiceImpl
import com.grouptwo.lokcet.di.service.AccountService
import com.grouptwo.lokcet.di.service.ChatService
import com.grouptwo.lokcet.di.service.ContactService
import com.grouptwo.lokcet.di.service.InternetService
import com.grouptwo.lokcet.di.service.LocationService
import com.grouptwo.lokcet.di.service.StorageService
import com.grouptwo.lokcet.di.service.UserService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds
    abstract fun provideLocationService(impl: LocationServiceImpl): LocationService

    @Binds
    abstract fun provideContactService(impl: ContactServiceImpl): ContactService

    @Binds
    abstract fun provideUserService(impl: UserServiceImpl): UserService

    @Binds
    abstract fun provideStorageService(impl: StorageServiceImpl): StorageService

    @Binds
    abstract fun provideInternetService(impl: InternetServiceImpl): InternetService

    @Binds
    abstract fun provideChatService(impl: ChatServiceImpl): ChatService
}