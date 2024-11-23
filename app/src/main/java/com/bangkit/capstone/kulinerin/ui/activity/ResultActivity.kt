package com.bangkit.capstone.kulinerin.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.kulinerin.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}