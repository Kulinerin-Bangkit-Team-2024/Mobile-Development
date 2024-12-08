package com.bangkit.capstone.kulinerin.data.response

import com.google.gson.annotations.SerializedName

data class CheckTokenResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
