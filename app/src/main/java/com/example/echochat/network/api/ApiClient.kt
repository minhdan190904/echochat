package com.example.echochat.network.api

import com.example.echochat.repository.SharedPreferencesReManager
import com.example.echochat.util.BASE_DOMAIN
import com.example.echochat.util.TOKEN_KEY
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
            .baseUrl("https://${BASE_DOMAIN}/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val request_chat: Request = Request.Builder().url("wss://${BASE_DOMAIN}/chat").build()
    val request_request: Request = Request.Builder().url("wss://${BASE_DOMAIN}/request").build()

}