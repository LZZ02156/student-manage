diff --git a/android-app/app/src/main/java/com/rollcall/android/network/ApiService.kt b/android-app/app/src/main/java/com/rollcall/android/network/ApiService.kt
index 0000000..0000000 100644
--- a/android-app/app/src/main/java/com/rollcall/android/network/ApiService.kt
+++ b/android-app/app/src/main/java/com/rollcall/android/network/ApiService.kt
@@
 interface ApiService {
     @FormUrlEncoded
     @POST("/auth/login")
     suspend fun login(@Field("username") username: String, @Field("password") password: String): Response<LoginResponse>
 
-    @GET("/rollcall/session")
-    suspend fun listSessions(@Header("Authorization") bearer: String): Response<List<Session>>
+    @GET("/rollcall/session")
+    suspend fun listSessions(@Header("Authorization") bearer: String): Response<List<Session>>
@@
 }
