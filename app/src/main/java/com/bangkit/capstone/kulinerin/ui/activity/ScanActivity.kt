package com.bangkit.capstone.kulinerin.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.kulinerin.databinding.ActivityScanBinding

class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}