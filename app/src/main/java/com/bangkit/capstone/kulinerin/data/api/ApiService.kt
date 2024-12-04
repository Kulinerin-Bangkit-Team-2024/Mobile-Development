package com.bangkit.capstone.kulinerin.data.api

import com.bangkit.capstone.kulinerin.data.response.DetailFoodResponse
import com.bangkit.capstone.kulinerin.data.response.FoodsItem
import com.bangkit.capstone.kulinerin.data.response.ListFoodResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @Multipart
    @POST("predict")
    fun uploadImage(
        @Part file: MultipartBody.Part
    ): Call<ResponseBody> //ImageUploadResponse

    @GET("foods")
    suspend fun getFood(): List<ListFoodResponse>

    @GET("foods/{food_id}")
    suspend fun getFoodDetail(@Path("food_id") id: Int): DetailFoodResponse
}