package com.bangkit.capstone.kulinerin.data.api

import com.google.gson.annotations.SerializedName

data class ImageUploadResponse (
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String
)