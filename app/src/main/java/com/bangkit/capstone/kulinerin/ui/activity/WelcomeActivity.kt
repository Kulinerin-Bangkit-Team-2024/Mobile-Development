package com.bangkit.capstone.kulinerin.ui.activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.kulinerin.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        val fadeOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
        window.decorView.startAnimation(fadeInAnimation)

        val translateY = resources.displayMetrics.density * -120
        val animator = ObjectAnimator.ofFloat(binding.welcomeLogo, "translationY", 0f, translateY)
        animator.duration = 1500
        animator.start()

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            val options = android.app.ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            startActivity(intent, options.toBundle())
            finish()
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            val options = android.app.ActivityOptions.makeCustomAnimation(
                this,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            startActivity(intent, options.toBundle())
            finish()
        }
    }
}