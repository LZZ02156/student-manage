package com.rollcall.network;

import com.rollcall.util.BaseResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RollCallService {
    @Multipart
    @POST("/rollCall/uploadRecord")
    Call<BaseResponse<String>> uploadRollCallRecord(
            @Part("studentId") RequestBody studentId,
            @Part("studentName") RequestBody studentName,
            @Part("className") RequestBody className,
            @Part("courseName") RequestBody courseName,
            @Part("location") RequestBody location,
            @Part MultipartBody.Part faceImage
    );
}
