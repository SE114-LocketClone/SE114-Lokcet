package com.grouptwo.lokcet.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.google.gson.Gson

@Module
@InstallIn(SingletonComponent::class)

object AppModule {
    @Provides fun provideGson(): Gson = Gson()
}