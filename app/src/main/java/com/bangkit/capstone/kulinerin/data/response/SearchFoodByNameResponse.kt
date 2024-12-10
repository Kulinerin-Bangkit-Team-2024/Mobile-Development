package com.bangkit.capstone.kulinerin.data.response

import com.google.gson.annotations.SerializedName

data class SearchFoodByNameResponse(

	@field:SerializedName("foods")
	val foods: List<FoodsSearchItem>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class FoodsSearchItem(

	@field:SerializedName("food_name")
	val foodName: String,

	@field:SerializedName("food_id")
	val foodId: Int,

	@field:SerializedName("food_image")
	val foodImage: String,

	@field:SerializedName("place_of_origin")
	val placeOfOrigin: String
)
