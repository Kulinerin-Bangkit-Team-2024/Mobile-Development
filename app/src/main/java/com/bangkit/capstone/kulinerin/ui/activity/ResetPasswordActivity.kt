package com.bangkit.capstone.kulinerin.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("EXTRA_EMAIL")

        startCountDownTimer()

        binding.apply {
            edInputPassword.setHint("New Password")

            btnConfirm.setOnClickListener {
                val password = edInputPassword.getPassword()
                val otp = edInputOtp.getOtp()

                edInputEmail.setError(null)
                edInputPassword.setError(null)
                edInputOtp.setError(null)

                email?.let { edInputEmail.setEmail(it) }
                accountViewModel.setPassword(password)
                accountViewModel.setOtp(otp)

                if (email?.isEmpty() == true) {
                    edInputEmail.setError("Email is empty.")
                } else if (password.isEmpty()) {
                    edInputPassword.setError("Password is empty.")
                } else if (!isValidPassword(password)) {
                    edInputPassword.setError("Password must be at least 8 characters.")
                } else if (otp.isEmpty()) {
                    edInputOtp.setError("OTP is empty.")
                } else {
                    email?.let { email -> resetUserPassword(email, password, otp) }
                }
            }
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val isValid = password.length >= 8
        return isValid
    }

    private fun resetUserPassword(email: String, password: String, otp: String) {
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
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            resetPasswordResponse.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Failed to reset password.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Failed to reset password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun startCountDownTimer() {
        countDownTimer = object : CountDownTimer(5 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000) % 60
                val minutes = millisUntilFinished / 1000 / 60
                binding.tvCountdown.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "Time is up. Redirecting to Login...",
                    Toast.LENGTH_SHORT
                ).show()
                navigateToLogin()
            }
        }.start()
    }


    private fun navigateToLogin() {
        val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}
