package com.rollcall.android.network

import com.rollcall.android.model.AttendanceRecord
import com.rollcall.android.model.CheckinRequest
import com.rollcall.android.model.LoginResponse
import com.rollcall.android.model.Session
import com.rollcall.android.model.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("/auth/login")
    suspend fun login(@Field("username") username: String, @Field("password") password: String): Response<LoginResponse>

    @GET("/rollcall/session")
    suspend fun listSessions(@Header("Authorization") bearer: String): Response<List<Session>>

    @GET("/rollcall/session/{id}")
    suspend fun getSession(@Header("Authorization") bearer: String, @Path("id") id: Long): Response<Session>

    @GET("/rollcall/session/{id}/attendance")
    suspend fun getAttendance(@Header("Authorization") bearer: String, @Path("id") id: Long): Response<List<AttendanceRecord>>

    @POST("/rollcall/session/{id}/checkin")
    suspend fun checkin(@Header("Authorization") bearer: String, @Path("id") id: Long, @Body req: CheckinRequest): Response<String>

    @Multipart
    @POST("/uploads")
    suspend fun upload(@Part file: MultipartBody.Part): Response<UploadResponse>
}
