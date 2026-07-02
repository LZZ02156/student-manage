package com.rollcall.android.repository

import android.content.Context
import android.net.Uri
import com.rollcall.android.model.AttendanceRecord
import com.rollcall.android.model.CheckinRequest
import com.rollcall.android.model.Session
import com.rollcall.android.model.UploadResponse
import com.rollcall.android.network.NetworkModule
import com.rollcall.android.network.ProgressRequestBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import retrofit2.Response
import java.io.InputStream

class SessionRepository(private val context: Context) {
    private val api = NetworkModule.apiService

    suspend fun listSessions(bearer: String): Response<List<Session>> {
        return api.listSessions(bearer)
    }

    suspend fun getSession(bearer: String, id: Long): Response<Session> {
        return api.getSession(bearer, id)
    }

    suspend fun getAttendance(bearer: String, id: Long): Response<List<AttendanceRecord>> {
        return api.getAttendance(bearer, id)
    }

    suspend fun checkin(bearer: String, id: Long, req: CheckinRequest): Response<String> {
        return api.checkin(bearer, id, req)
    }

    suspend fun uploadFile(uri: Uri, onProgress: (Long, Long) -> Unit = { _, _ -> }): Response<UploadResponse> {
        val resolver = context.contentResolver
        val input: InputStream? = resolver.openInputStream(uri)
        val bytes = input?.readBytes() ?: ByteArray(0)
        val contentType = resolver.getType(uri) ?: "image/*"
        val requestBody = ProgressRequestBody(bytes, contentType, onProgress)
        val part = MultipartBody.Part.createFormData("file", "photo.jpg", requestBody)
        return api.upload(part)
    }
}
