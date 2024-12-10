package com.bangkit.capstone.kulinerin.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.kulinerin.data.api.ApiConfig
import com.bangkit.capstone.kulinerin.data.response.ResetPasswordResponse
import com.bangkit.capstone.kulinerin.databinding.ActivityResetPasswordBinding
import com.bangkit.capstone.kulinerin.ui.model.AccountViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private val accountViewModel: AccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("EXTRA_EMAIL")
        Log.d(TAG, "Received email: $email") // Log the email value

        binding.apply {
            edInputPassword.setHint("New Password")

            btnConfirm.setOnClickListener {
                val password = edInputPassword.getPassword()
                val otp = edInputOtp.getOtp() // This will now correctly return the OTP string

                Log.d(TAG, "Password entered: $password")  // Log the password
                Log.d(TAG, "OTP entered: $otp")  // Log the OTP

                edInputEmail.setError(null)
                edInputPassword.setError(null)
                edInputOtp.setError(null)

                email?.let { edInputEmail.setEmail(it) }
                accountViewModel.setPassword(password)
                accountViewModel.setOtp(otp)

                if (email?.isEmpty() == true) {
                    Log.d(TAG, "btnConfirm: Email is empty")
                    edInputEmail.setError("Email is empty.")
                } else if (password.isEmpty()) {
                    Log.d(TAG, "btnConfirm: Password is empty")
                    edInputPassword.setError("Password is empty.")
                } else if (!isValidPassword(password)) {
                    Log.d(TAG, "btnConfirm: Invalid password (less than 8 characters)")
                    edInputPassword.setError("Password must be at least 8 characters.")
                } else if (otp.isEmpty()) {
                    Log.d(TAG, "btnConfirm: OTP is empty")
                    edInputOtp.setError("OTP is empty.")
                } else {
                    Log.d(TAG, "All inputs are valid, proceeding with reset")
                    email?.let { email -> resetUserPassword(email, password, otp) }
                }
            }
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val isValid = password.length >= 8
        Log.d(TAG, "isValidPassword: Password length -> ${password.length} | Valid -> $isValid")
        return isValid
    }

    private fun resetUserPassword(email: String, password: String, otp: String) {
        Log.d(TAG, "Resetting password for email: $email, OTP: $otp, password: $password")
        val apiService = ApiConfig.getApiService()
        val call = apiService.resetPassword(email, otp, password)

        call.enqueue(object : Callback<ResetPasswordResponse> {
            override fun onResponse(
                call: Call<ResetPasswordResponse>,
                response: Response<ResetPasswordResponse>
            ) {
                if (response.isSuccessful) {
                    val resetPasswordResponse = response.body()
                    if (resetPasswordResponse != null && resetPasswordResponse.status == "success") {
                        Log.d(TAG, "Password reset successful: ${resetPasswordResponse.message}")
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            resetPasswordResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d(TAG, "Failed to reset password: ${resetPasswordResponse?.message}")
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Failed to reset password.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.d(TAG, "Response error: ${response.code()}")
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Failed to reset password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                Log.d(TAG, "Error: ${t.message}") // Log error message if failure occurs
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    companion object {
        private const val TAG = "ResetPasswordActivity"
    }
}
