package com.example.echochat.repository

import com.example.echochat.model.User
import com.example.echochat.model.dto.ResUpLoadFileDTO
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.FileApi
import com.example.echochat.util.SharedPreferencesReManager
import com.example.echochat.util.USER_SESSION
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
        return try {
            val response = fileApi.uploadFile(file, folder)
            NetworkResource.Success(response.data)
        } catch (ex: HttpException) {
            val errorBody = ex.response()?.errorBody()?.string() ?: "Không có nội dung lỗi từ server"
            NetworkResource.Error("Lỗi HTTP ${ex.code()}: $errorBody")
        } catch (ex: IOException) {
            NetworkResource.NetworkException("Lỗi kết nối mạng. Hãy kiểm tra internet của bạn.")
        } catch (ex: Exception) {
            NetworkResource.Error("Lỗi không xác định: ${ex.message}")
        }
    }


}