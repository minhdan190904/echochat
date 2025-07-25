package com.example.echochat.di

import com.example.echochat.network.api.AuthApi
import com.example.echochat.network.api.ChatApi
import com.example.echochat.network.api.FileApi
import com.example.echochat.network.api.FriendRequestApi
import com.example.echochat.network.api.NotificationApi
import com.example.echochat.network.api.UserApi
import com.example.echochat.util.BASE_DOMAIN
import com.example.echochat.util.CHAT_REQUEST
import com.example.echochat.util.REQUEST_REQUEST
import com.example.echochat.util.STATUS_REQUEST
import com.example.echochat.util.myUser
import com.example.echochat.util.tokenApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideAuthInter(): Interceptor {
        return Interceptor { chain ->
            val token = tokenApi
            val request = chain.request().newBuilder()
                .apply {
                    token?.let {
                        addHeader("Authorization", "Bearer $it")
                    }
                }
                .build()
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideHttpClient(authInterceptor: Interceptor): OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .pingInterval(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://$BASE_DOMAIN/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Provides
    fun provideChatApi(retrofit: Retrofit): ChatApi = retrofit.create(ChatApi::class.java)

    @Provides
    fun provideFileApi(retrofit: Retrofit): FileApi = retrofit.create(FileApi::class.java)

    @Provides
    fun provideFriendRequestApi(retrofit: Retrofit): FriendRequestApi = retrofit.create(FriendRequestApi::class.java)

    @Provides
    fun provideNotificationApi(retrofit: Retrofit): NotificationApi = retrofit.create(NotificationApi::class.java)

    @Provides
    @Named(CHAT_REQUEST)
    fun provideChatRequest(): Request {
        return Request.Builder().url("wss://${BASE_DOMAIN}/chat/${myUser?.id}").build()
    }

    @Provides
    @Singleton
    @Named(REQUEST_REQUEST)
    fun provideRequestRequest(): Request {
        return Request.Builder().url("wss://${BASE_DOMAIN}/request").build()
    }

    @Provides
    @Named(STATUS_REQUEST)
    fun provideStatusRequest(): Request {
        return Request.Builder().url("wss://${BASE_DOMAIN}/status/${myUser?.id}").build()
    }

}