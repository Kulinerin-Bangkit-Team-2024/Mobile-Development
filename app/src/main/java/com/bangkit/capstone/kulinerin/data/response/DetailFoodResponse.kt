package com.bangkit.capstone.kulinerin.data.response

import com.google.gson.annotations.SerializedName

data class DetailFoodResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("food")
	val food: Food,

	@field:SerializedName("status")
	val status: String
)

data class Food(

	@field:SerializedName("food_name")
	val foodName: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("food_id")
	val foodId: Int,

	@field:SerializedName("food_image")
	val foodImage: String,

	@field:SerializedName("place_of_origin")
	val placeOfOrigin: String
)
