package com.rollcall.android.network

import com.rollcall.android.model.CheckinRequest
import com.rollcall.android.model.LoginResponse
import com.rollcall.android.model.Session
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

    @POST("/rollcall/session/{id}/checkin")
    suspend fun checkin(@Header("Authorization") bearer: String, @Path("id") id: Long, @Body req: CheckinRequest): Response<String>
}
