package com.bangkit.capstone.kulinerin.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.capstone.kulinerin.R
import com.bangkit.capstone.kulinerin.data.api.ApiConfig
import com.bangkit.capstone.kulinerin.data.response.RegisterResponse
import com.bangkit.capstone.kulinerin.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.edInputName.getName()
            val email = binding.edInputEmail.getEmail()
            val password = binding.edInputPassword.getPassword()

            binding.edInputName.setError(null)
            binding.edInputEmail.setError(null)
            binding.edInputPassword.setError(null)

            if (name.isEmpty()) {
                binding.edInputName.setError("Name is empty.")
            }
            if (email.isEmpty()) {
                binding.edInputEmail.setError("Email is empty.")
            }
            if (password.isEmpty()) {
                binding.edInputPassword.setError("Password is empty.")
            }

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (isValidEmail(email) && isValidPassword(password)) {
                    registerUser(name, email, password)
                }
            }
        }

        binding.backIcon.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        )
        return if (!emailPattern.matcher(email).matches()) {
            binding.edInputEmail.setError("Invalid email format")
            false
        } else {
            true
        }
    }

    private fun isValidPassword(password: String): Boolean {
        return if (password.length < 8) {
            binding.edInputPassword.setError("Password must be at least 8 characters")
            false
        } else {
            true
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        val apiService = ApiConfig.getApiService()
        val call = apiService.register(name, email, password)

        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration successful!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java).apply {
                            putExtra("email", email)
                        }
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registration failed. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Terjadi kesalahan: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
//                Log.e("RegisterActivity", "onFailure: ${t.message}", t)
            }
        })
    }
}