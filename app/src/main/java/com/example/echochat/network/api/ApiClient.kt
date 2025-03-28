package com.example.echochat.network.api

import com.example.echochat.util.SharedPreferencesReManager
import com.example.echochat.util.BASE_DOMAIN
import com.example.echochat.util.MY_USER_ID
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


    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://${BASE_DOMAIN}/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }
    val chatApi: ChatApi by lazy { retrofit.create(ChatApi::class.java) }
    val fileApi: FileApi by lazy { retrofit.create(FileApi::class.java) }
    val friendRequestApi: FriendRequestApi by lazy { retrofit.create(FriendRequestApi::class.java) }
    val notificationApi: NotificationApi by lazy { retrofit.create(NotificationApi::class.java) }


    val request_chat: Request = Request.Builder().url("wss://${BASE_DOMAIN}/chat").build()
    val request_request: Request = Request.Builder().url("wss://${BASE_DOMAIN}/request").build()
    val request_status: Request = Request.Builder().url("wss://${BASE_DOMAIN}/status/${MY_USER_ID}").build()
}