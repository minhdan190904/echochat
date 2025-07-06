package com.example.echochat.di

import com.example.echochat.network.AndroidDownloader
import com.example.echochat.network.Downloader
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class  DownloaderModule {

    @Binds
    @Singleton
    abstract fun bindDownloader(
        androidDownloader: AndroidDownloader
    ): Downloader
}