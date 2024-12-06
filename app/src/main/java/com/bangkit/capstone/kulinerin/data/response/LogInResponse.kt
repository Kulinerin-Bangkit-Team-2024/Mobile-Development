package com.bangkit.capstone.kulinerin.data.response

import com.google.gson.annotations.SerializedName

data class LogInResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("user")
	val user: UserLogin,

	@field:SerializedName("status")
	val status: String,

	@field:SerializedName("token")
	val token: String
)

data class UserLogin(

	@field:SerializedName("user_email")
	val userEmail: String,

	@field:SerializedName("user_id")
	val userId: String,

	@field:SerializedName("user_name")
	val userName: String
)
