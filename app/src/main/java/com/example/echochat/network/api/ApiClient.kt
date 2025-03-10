package com.example.echochat.network.api

import com.example.echochat.repository.SharedPreferencesReManager
import com.example.echochat.util.BASE_URL
import com.example.echochat.util.BASE_URL_LOCAL_HOST
import com.example.echochat.util.TOKEN_KEY
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val authInterceptor = Interceptor { chain ->
        val token = SharedPreferencesReManager.getData(TOKEN_KEY, String::class.java)
        val request = chain.request().newBuilder()
            .apply {
                token?.let {
                    addHeader("Authorization", "Bearer $it")
                }
            }
            .build()
        chain.proceed(request)
    }

    val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .pingInterval(15, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val request: Request = Request.Builder().url("ws://4c92-2001-ee0-1ad2-54a0-9c5e-5d91-1c8-9cf1.ngrok-free.app/chat").build()
}