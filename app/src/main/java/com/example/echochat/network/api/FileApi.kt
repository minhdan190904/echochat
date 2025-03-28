package com.example.echochat.network.api

import com.example.echochat.model.ResResponse
import com.example.echochat.model.dto.ResUpLoadFileDTO
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface FileApi {
    @Multipart
    @POST("/files/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Query("folder") folder: String,
    ): ResResponse<ResUpLoadFileDTO>
}