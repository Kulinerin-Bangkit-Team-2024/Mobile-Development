package com.bangkit.capstone.kulinerin.data.api

import com.bangkit.capstone.kulinerin.data.response.CheckTokenResponse
import com.bangkit.capstone.kulinerin.data.response.DetailFoodResponse
import com.bangkit.capstone.kulinerin.data.response.ForgotPasswordResponse
import com.bangkit.capstone.kulinerin.data.response.ListFoodResponse
import com.bangkit.capstone.kulinerin.data.response.LogInResponse
import com.bangkit.capstone.kulinerin.data.response.LogOutResponse
import com.bangkit.capstone.kulinerin.data.response.RegisterResponse
import com.bangkit.capstone.kulinerin.data.response.ResetPasswordResponse
import com.bangkit.capstone.kulinerin.data.response.ScanFoodResponse
import com.bangkit.capstone.kulinerin.data.response.SearchFoodByNameResponse
import com.bangkit.capstone.kulinerin.data.response.UserProfileResponse
import com.bangkit.capstone.kulinerin.ui.activity.ResetPasswordActivity
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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

    @FormUrlEncoded
    @POST("forgot-password")
    fun forgotPassword(
        @Field("email") email: String
    ): Call<ForgotPasswordResponse>

    @FormUrlEncoded
    @PUT("reset-password")
    fun resetPassword(
        @Field("email") email: String,
        @Field("otp") otp: String,
        @Field("newpass") password: String
    ): Call<ResetPasswordResponse>

    // Foods
    @Multipart
    @POST("foods/predict")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Call<ScanFoodResponse>

    @GET("foods")
    fun getFood(
        @Header("Authorization") token: String
    ): Call<ListFoodResponse>

    @GET("foods/{food_id}")
    fun getFoodDetail(
        @Header("Authorization") token: String,
        @Path("food_id") id: Int
    ): Call<DetailFoodResponse>

    @GET("foods/search/name")
    fun searchFoodByName(
        @Header("Authorization") token: String,
        @Query("name") name: String
    ): Call<SearchFoodByNameResponse>

    // User
    @GET("user/profile")
    fun getUserProfile(
        @Header("Authorization") token: String
    ): Call<UserProfileResponse>
}