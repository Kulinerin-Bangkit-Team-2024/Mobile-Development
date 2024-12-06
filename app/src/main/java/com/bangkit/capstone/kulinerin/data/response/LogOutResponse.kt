package com.bangkit.capstone.kulinerin.data.response

import com.google.gson.annotations.SerializedName

data class LogOutResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)
