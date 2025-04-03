package com.example.echochat.di

import android.app.Application
import com.example.echochat.network.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideNetworkMonitor(application: Application): NetworkMonitor {
        return NetworkMonitor(application)
    }

}