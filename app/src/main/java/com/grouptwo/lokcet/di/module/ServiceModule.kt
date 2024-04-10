package com.grouptwo.lokcet.di.module

import com.grouptwo.lokcet.di.impl.AccountServiceImpl
import com.grouptwo.lokcet.di.service.AccountService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun provideAccountService(impl: AccountServiceImpl): AccountService
}