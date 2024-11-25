package com.bangkit.capstone.kulinerin.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bangkit.capstone.kulinerin.R
import com.bangkit.capstone.kulinerin.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

        binding.fabScan.setOnClickListener {
            goToScanActivity()
        }
    }

    private fun goToScanActivity() {
        val intent = Intent(this, ScanActivity::class.java)
        startActivity(intent)
    }
}