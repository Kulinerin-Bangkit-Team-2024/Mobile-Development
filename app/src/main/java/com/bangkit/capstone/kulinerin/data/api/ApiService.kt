package com.bangkit.capstone.kulinerin.data.api

import com.bangkit.capstone.kulinerin.data.response.CheckTokenResponse
import com.bangkit.capstone.kulinerin.data.response.DetailFoodResponse
import com.bangkit.capstone.kulinerin.data.response.ListFoodResponse
import com.bangkit.capstone.kulinerin.data.response.LogInResponse
import com.bangkit.capstone.kulinerin.data.response.LogOutResponse
import com.bangkit.capstone.kulinerin.data.response.RegisterResponse
import com.bangkit.capstone.kulinerin.data.response.ScanFoodResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    // Auth
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("pass") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("pass") password: String
    ): Call<LogInResponse>

    @POST("logout")
    fun logout(
        @Header("Authorization") token: String
    ): Call<LogOutResponse>

    @GET("check-token")
    fun checkToken(
        @Header("Authorization") token: String
    ): Call<CheckTokenResponse>

    // Foods
    @Multipart
    @POST("foods/predict")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Call<ScanFoodResponse>

    @GET("foods")
    suspend fun getFood(): List<ListFoodResponse>

    @GET("foods/{food_id}")
    suspend fun getFoodDetail(@Path("food_id") id: Int): DetailFoodResponse
}