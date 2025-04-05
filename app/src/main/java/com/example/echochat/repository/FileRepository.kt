package com.example.echochat.repository

import com.example.echochat.model.User
import com.example.echochat.model.dto.ResUpLoadFileDTO
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.FileApi
import com.example.echochat.util.SharedPreferencesReManager
import com.example.echochat.util.USER_SESSION
import com.example.echochat.util.handleNetworkCall
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepository @Inject constructor(
    private val fileApi: FileApi
){
    suspend fun uploadFile(file: MultipartBody.Part, folder: String): NetworkResource<ResUpLoadFileDTO> {
        return handleNetworkCall(
            call = { fileApi.uploadFile(file, folder).data },
            customErrorMessages = mapOf(
                400 to "Error uploading file"
            )
        )
    }


}