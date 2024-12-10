package com.bangkit.capstone.kulinerin.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bangkit.capstone.kulinerin.data.api.ApiConfig
import com.bangkit.capstone.kulinerin.data.preference.SessionPreferences
import com.bangkit.capstone.kulinerin.data.preference.sessionDataStore
import com.bangkit.capstone.kulinerin.data.response.LogInResponse
import com.bangkit.capstone.kulinerin.databinding.ActivityLoginBinding
import com.bangkit.capstone.kulinerin.ui.model.AccountViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val accountViewModel: AccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Initializing LoginActivity")
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        accountViewModel.email.observe(this) { email ->
            Log.d(TAG, "onCreate: Observed email change -> $email")
            binding.edInputEmail.setEmail(email)
        }
        accountViewModel.password.observe(this) { password ->
            Log.d(TAG, "onCreate: Observed password change -> $password")
            binding.edInputPassword.setPassword(password)
        }

        binding.apply {
            btnLogin.setOnClickListener {
                val email = binding.edInputEmail.getEmail()
                val password = binding.edInputPassword.getPassword()

                Log.d(TAG, "btnLogin: Email entered -> $email")
                Log.d(TAG, "btnLogin: Password entered -> $password")

                binding.edInputEmail.setError(null)
                binding.edInputPassword.setError(null)

                accountViewModel.setEmail(email)
                accountViewModel.setPassword(password)

                if (email.isEmpty()) {
                    Log.d(TAG, "btnLogin: Email is empty")
                    binding.edInputEmail.setError("Email is empty.")
                } else if (!isValidEmail(email)) {
                    Log.d(TAG, "btnLogin: Invalid email format")
                    binding.edInputEmail.setError("Invalid email format.")
                } else if (password.isEmpty()) {
                    Log.d(TAG, "btnLogin: Password is empty")
                    binding.edInputPassword.setError("Password is empty.")
                } else if (!isValidPassword(password)) {
                    Log.d(TAG, "btnLogin: Password is too short")
                    binding.edInputPassword.setError("Password must be at least 8 characters.")
                } else {
                    Log.d(TAG, "btnLogin: Attempting login")
                    loginUser(email, password)
                }
            }

            backIcon.setOnClickListener {
                Log.d(TAG, "backIcon: Back button clicked")
                val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        )
        val isValid = emailPattern.matcher(email).matches()
        Log.d(TAG, "isValidEmail: Email -> $email | Valid -> $isValid")
        return isValid
    }

    private fun isValidPassword(password: String): Boolean {
        val isValid = password.length >= 8
        Log.d(TAG, "isValidPassword: Password length -> ${password.length} | Valid -> $isValid")
        return isValid
    }

    private fun loginUser(email: String, password: String) {
        val apiService = ApiConfig.getApiService()
        Log.d(TAG, "loginUser: Sending login request for email -> $email")

        val call = apiService.login(email, password)

        call.enqueue(object : Callback<LogInResponse> {
            override fun onResponse(
                call: Call<LogInResponse>,
                response: Response<LogInResponse>
            ) {
                Log.d(TAG, "onResponse: Response received")
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    Log.d(TAG, "onResponse: Response body -> $loginResponse")
                    if (loginResponse != null && loginResponse.status == "success") {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login successful!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val token = loginResponse.token
                        Log.d(TAG, "onResponse: Login successful. Token -> $token")

                        val sessionPreferences = SessionPreferences.getInstance(dataStore = applicationContext.sessionDataStore)
                        lifecycleScope.launch {
                            Log.d(TAG, "onResponse: Saving token")
                            sessionPreferences.saveToken(token)
                        }

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d(TAG, "onResponse: Login failed. Status -> ${loginResponse?.status}")
                        Toast.makeText(
                            this@LoginActivity,
                            "Login failed. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.d(TAG, "onResponse: Response not successful -> ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<LogInResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: Error during login request -> ${t.message}")
                Toast.makeText(
                    this@LoginActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}