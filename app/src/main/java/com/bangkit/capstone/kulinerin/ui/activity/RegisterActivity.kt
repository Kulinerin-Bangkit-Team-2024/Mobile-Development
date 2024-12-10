package com.bangkit.capstone.kulinerin.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.kulinerin.data.api.ApiConfig
import com.bangkit.capstone.kulinerin.data.response.RegisterResponse
import com.bangkit.capstone.kulinerin.databinding.ActivityRegisterBinding
import com.bangkit.capstone.kulinerin.ui.model.AccountViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val accountViewModel: AccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        accountViewModel.name.observe(this) { name ->
            binding.edInputName.setName(name)
        }
        accountViewModel.email.observe(this) { email ->
            binding.edInputEmail.setEmail(email)
        }
        accountViewModel.password.observe(this) { password ->
            binding.edInputPassword.setPassword(password)
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.edInputName.getName()
            val email = binding.edInputEmail.getEmail()
            val password = binding.edInputPassword.getPassword()

            binding.edInputName.setError(null)
            binding.edInputEmail.setError(null)
            binding.edInputPassword.setError(null)
            binding.edInputPassword.setHint("Password")

            accountViewModel.setName(name)
            accountViewModel.setEmail(email)
            accountViewModel.setPassword(password)

            if (name.isEmpty()) {
                binding.edInputName.setError("Name is empty.")
            } else if (email.isEmpty()) {
                binding.edInputEmail.setError("Email is empty.")
            } else if (!isValidEmail(email)) {
                binding.edInputEmail.setError("Invalid email format.")
            } else if (password.isEmpty()) {
                binding.edInputPassword.setError("Password is empty.")
            } else if (!isValidPassword(password)) {
                binding.edInputPassword.setError("Password must be at least 8 characters.")
            } else {
                registerUser(name, email, password)
            }
        }

        binding.backIcon.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        )
        return emailPattern.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8
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
                    if (registerResponse != null && registerResponse.status == "success") {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration successful!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent =
                            Intent(this@RegisterActivity, LoginActivity::class.java).apply {
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