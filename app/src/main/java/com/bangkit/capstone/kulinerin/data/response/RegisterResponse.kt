package com.bangkit.capstone.kulinerin.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("user")
	val user: User,

	@field:SerializedName("status")
	val status: String
)

data class User(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("email")
	val email: String
)
