package com.example.echochat.repository

import android.util.Log
import com.example.echochat.model.ResResponse
import com.example.echochat.model.ResUpLoadFileDTO
import com.example.echochat.model.User
import com.example.echochat.network.NetworkResource
import com.example.echochat.network.api.ApiService
import com.example.echochat.util.USER_SESSION
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class FileRepository(private val apiService: ApiService) {
    var myUser: User? = null

    init {
        myUser = SharedPreferencesReManager.getData(USER_SESSION, User::class.java)
    }

    suspend fun uploadFile(file: MultipartBody.Part, folder: String): NetworkResource<ResUpLoadFileDTO> {
        return try {
            val response = apiService.uploadFile(file, folder)
            NetworkResource.Success(response.data)
        } catch (ex: HttpException) {
            val errorBody = ex.response()?.errorBody()?.string() ?: "Không có nội dung lỗi từ server"
            Log.e("UploadError", "HTTP ${ex.code()} - ${ex.message()}\nLỗi chi tiết từ server: $errorBody\nStackTrace: ${ex.stackTraceToString()}")
            NetworkResource.Error("Lỗi HTTP ${ex.code()}: $errorBody")
        } catch (ex: IOException) {
            Log.e("UploadError", "Network Error: ${ex.message}\nStackTrace: ${ex.stackTraceToString()}")
            NetworkResource.NetworkException("Lỗi kết nối mạng. Hãy kiểm tra internet của bạn.")
        } catch (ex: Exception) {
            Log.e("UploadError", "Unknown Error: ${ex.message}\nStackTrace: ${ex.stackTraceToString()}")
            NetworkResource.Error("Lỗi không xác định: ${ex.message}")
        }
    }


}