package com.bangkit.capstone.kulinerin.ui.activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.capstone.kulinerin.R
import com.bangkit.capstone.kulinerin.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        val translateY = resources.displayMetrics.density * -120
        val animator = ObjectAnimator.ofFloat(binding.welcomeLogo, "translationY", 0f, translateY)
        animator.duration = 1500
        animator.start()

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}