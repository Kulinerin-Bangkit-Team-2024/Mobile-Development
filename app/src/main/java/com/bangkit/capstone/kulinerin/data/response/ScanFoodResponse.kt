package com.bangkit.capstone.kulinerin.data.response

import com.google.gson.annotations.SerializedName

data class ScanFoodResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("status")
	val status: String
)

data class QueryResultItem(

	@field:SerializedName("food_name")
	val foodName: String,

	@field:SerializedName("place_of_origin")
	val placeOfOrigin: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("food_id")
	val foodId: Int
)

data class Data(

	@field:SerializedName("predictedFoodName")
	val predictedFoodName: String,

	@field:SerializedName("queryResult")
	val queryResult: List<QueryResultItem>
)
