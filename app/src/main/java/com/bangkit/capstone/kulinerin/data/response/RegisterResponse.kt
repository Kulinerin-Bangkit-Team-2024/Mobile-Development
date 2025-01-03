package com.bangkit.capstone.kulinerin.data.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("user")
	val user: UserRegister,

	@field:SerializedName("status")
	val status: String
)

data class UserRegister(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("email")
	val email: String
)
