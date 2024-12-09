package com.bangkit.capstone.kulinerin.data.response

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("user")
	val user: User,

	@field:SerializedName("status")
	val status: String
)

data class User(

	@field:SerializedName("user_id")
	val userId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("profile_picture")
	val profilePicture: Any,

	@field:SerializedName("email")
	val email: String
)
