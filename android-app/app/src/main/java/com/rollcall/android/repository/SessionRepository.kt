package com.rollcall.android.repository

import com.rollcall.android.model.AttendanceRecord
import com.rollcall.android.model.CheckinRequest
import com.rollcall.android.model.Session
import com.rollcall.android.network.NetworkModule
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import android.content.Context
import android.net.Uri
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

    suspend fun uploadFile(uri: Uri): Response<String> {
        // read bytes from uri
        val resolver = context.contentResolver
        val input: InputStream? = resolver.openInputStream(uri)
        val bytes = input?.readBytes() ?: ByteArray(0)
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), bytes)
        val part = MultipartBody.Part.createFormData("file", "photo.jpg", requestBody)
        return api.upload(part)
    }
}
