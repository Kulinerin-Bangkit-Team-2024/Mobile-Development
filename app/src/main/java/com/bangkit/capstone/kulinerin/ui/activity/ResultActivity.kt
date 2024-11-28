package com.bangkit.capstone.kulinerin.ui.activity

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.kulinerin.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUriString = intent.getStringExtra("image_uri")
        val foodName = intent.getStringExtra("food_name") ?: "Unknown"
        imageUri = Uri.parse(imageUriString)

        binding.ivResult.setImageURI(imageUri)
        binding.tvFoodName.text = foodName
    }
}
