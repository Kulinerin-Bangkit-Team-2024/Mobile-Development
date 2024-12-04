package com.bangkit.capstone.kulinerin.data.api



data class ImageUploadResponse (
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String
)