package com.bangkit.capstone.kulinerin.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bangkit.capstone.kulinerin.data.api.ApiConfig
import com.bangkit.capstone.kulinerin.data.preference.SessionPreferences
import com.bangkit.capstone.kulinerin.data.preference.SettingPreferences
import com.bangkit.capstone.kulinerin.data.preference.sessionDataStore
import com.bangkit.capstone.kulinerin.data.preference.settingDataStore
import com.bangkit.capstone.kulinerin.data.response.CheckTokenResponse
import com.bangkit.capstone.kulinerin.databinding.ActivitySplashScreenBinding
import com.bangkit.capstone.kulinerin.ui.model.SettingViewModel
import com.bangkit.capstone.kulinerin.ui.model.SettingViewModelFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var settingViewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        lifecycleScope.launch {
            val sessionPreferences = SessionPreferences.getInstance(dataStore = applicationContext.sessionDataStore)
            val tokenFlow = sessionPreferences.getToken()

            tokenFlow.collect { token ->
                handleNavigation(token)
            }
        }

        val pref = SettingPreferences.getInstance(application.settingDataStore)
        settingViewModel = ViewModelProvider(this, SettingViewModelFactory(pref))[SettingViewModel::class.java]

        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun handleNavigation(token: String?) {
        val splashTime: Long = 2000
        window.decorView.postDelayed({
            if (token.isNullOrEmpty()) {
                navigateToWelcome()
            } else {
                checkTokenBanlist(token)
            }
        }, splashTime)
    }

    private fun checkTokenBanlist(token: String?) {
        val apiService = ApiConfig.getApiService()
        val call = apiService.checkToken("Bearer $token")

        call.enqueue(object : Callback<CheckTokenResponse> {
            override fun onResponse(
                call: Call<CheckTokenResponse>,
                response: Response<CheckTokenResponse>
            ) {
                lifecycleScope.launch {
                    val sessionPreferences = SessionPreferences.getInstance(dataStore = applicationContext.sessionDataStore)
                    if (response.isSuccessful || response.code() == 404) {
                        val checkTokenResponse = response.body()
                        if (checkTokenResponse?.status != "success") {
                            sessionPreferences.saveToken(token.orEmpty())
                            navigateToMain()
                        } else {
                            sessionPreferences.saveToken(null)
                            navigateToWelcome()
                        }
                    } else {
                        sessionPreferences.saveToken(null)
                        navigateToWelcome()
                    }
                }
            }

            override fun onFailure(call: Call<CheckTokenResponse>, t: Throwable) {
                lifecycleScope.launch {
                    val sessionPreferences = SessionPreferences.getInstance(dataStore = applicationContext.sessionDataStore)
                    sessionPreferences.saveToken(null)
                    navigateToWelcome()
                }
            }
        })
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun navigateToWelcome() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }
}