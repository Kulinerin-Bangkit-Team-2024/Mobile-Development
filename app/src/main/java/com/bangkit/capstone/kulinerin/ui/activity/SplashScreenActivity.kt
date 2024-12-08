package com.bangkit.capstone.kulinerin.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bangkit.capstone.kulinerin.data.api.ApiConfig
import com.bangkit.capstone.kulinerin.data.preference.SessionPreferences
import com.bangkit.capstone.kulinerin.data.preference.sessionDataStore
import com.bangkit.capstone.kulinerin.data.response.CheckTokenResponse
import com.bangkit.capstone.kulinerin.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        lifecycleScope.launch {
            val sessionPreferences = SessionPreferences.getInstance(dataStore = applicationContext.sessionDataStore)
            val tokenFlow = sessionPreferences.getToken()

            tokenFlow.collect { token ->
                Log.d("SplashScreenActivity", "Collected token: $token")
                handleNavigation(token)
            }
        }
    }

    private fun handleNavigation(token: String?) {
        Log.d("SplashScreenActivity", "Handling navigation with token: $token")
        val splashTime: Long = 2000
        window.decorView.postDelayed({
            if (token.isNullOrEmpty()) {
                Log.d("SplashScreenActivity", "Token is null or empty, navigating to Welcome")
                navigateToWelcome()
            } else {
                Log.d("SplashScreenActivity", "Token is not empty $token")
                checkTokenBanlist(token)
            }
        }, splashTime)
    }

    private fun checkTokenBanlist(token: String?) {
        Log.d("SplashScreenActivity", "Checking token banlist with token: $token")
        val apiService = ApiConfig.getApiService()
        val call = apiService.checkToken("Bearer $token")

        call.enqueue(object : Callback<CheckTokenResponse> {
            override fun onResponse(
                call: Call<CheckTokenResponse>,
                response: Response<CheckTokenResponse>
            ) {
                Log.d("SplashScreenActivity", "Response code: ${response.code()}")
                lifecycleScope.launch {
                    val sessionPreferences = SessionPreferences.getInstance(dataStore = applicationContext.sessionDataStore)
                    if (response.isSuccessful || response.code() == 404) {
                        val checkTokenResponse = response.body()
                        Log.d("SplashScreenActivity", "Response body: ${checkTokenResponse?.status}")
                        if (checkTokenResponse?.status != "success") {
                            Log.d("SplashScreenActivity", "Token is valid, navigating to Main")
                            sessionPreferences.saveToken(token.orEmpty())
                            navigateToMain()
                        } else {
                            Log.d("SplashScreenActivity", "Token is banned, navigating to Welcome")
                            sessionPreferences.saveToken(null)
                            navigateToWelcome()
                        }
                    } else {
                        Log.d("SplashScreenActivity", "Response failed, removing token and navigating to Welcome")
                        sessionPreferences.saveToken(null)
                        navigateToWelcome()
                    }
                }
            }

            override fun onFailure(call: Call<CheckTokenResponse>, t: Throwable) {
                Log.e("SplashScreenActivity", "Failure: ${t.message}", t)
                lifecycleScope.launch {
                    val sessionPreferences = SessionPreferences.getInstance(dataStore = applicationContext.sessionDataStore)
                    Log.d("SplashScreenActivity", "Removing token and navigating to Welcome due to failure")
                    sessionPreferences.saveToken(null)
                    navigateToWelcome()
                }
            }
        })
    }

    private fun navigateToMain() {
        Log.d("SplashScreenActivity", "Navigating to MainActivity")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun navigateToWelcome() {
        Log.d("SplashScreenActivity", "Navigating to WelcomeActivity")
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }
}