package com.example.grocery

import android.content.Context
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface APIService {

    // Register
    @POST("/users/")
    suspend fun register(@Body requestBody: RequestBody): Response<ResponseBody>

    // Login
    @Multipart
    @POST("/login/")
    suspend fun login(@PartMap map: HashMap<String?, RequestBody?>): retrofit2.Response<ResponseBody>


    // Profile information
    @GET("/users/me/")
    suspend fun profile(@Header("Authorization") auth: String): retrofit2.Response<ResponseBody>


    // Backup
    @POST("/backups/")
    suspend fun backup(@Body requestBody: RequestBody, @Header("Authorization") auth: String): retrofit2.Response<ResponseBody>


    // Delete Backup
    @DELETE(" ")
    suspend fun delete(@Header("Authorization") auth: String): retrofit2.Response<ResponseBody>

}