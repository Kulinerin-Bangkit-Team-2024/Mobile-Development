package com.bangkit.capstone.kulinerin.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bangkit.capstone.kulinerin.R
import com.bangkit.capstone.kulinerin.data.api.ApiConfig
import com.bangkit.capstone.kulinerin.data.preference.SessionPreferences
import com.bangkit.capstone.kulinerin.data.preference.sessionDataStore
import com.bangkit.capstone.kulinerin.data.response.LogOutResponse
import com.bangkit.capstone.kulinerin.data.response.UserProfileResponse
import com.bangkit.capstone.kulinerin.databinding.FragmentProfileBinding
import com.bangkit.capstone.kulinerin.ui.activity.SettingActivity
import com.bangkit.capstone.kulinerin.ui.activity.WelcomeActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionPreferences: SessionPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionPreferences = SessionPreferences.getInstance(requireContext().sessionDataStore)

        binding.apply {
            btnSetting.setOnClickListener {
                val intent = Intent(requireContext(), SettingActivity::class.java)
                startActivity(intent)
            }
            btnLogout.setOnClickListener {
                showLogoutConfirmationDialog()
            }
        }

        loadUserData()
    }

    private fun loadUserData() {
        val token = runBlocking {
            sessionPreferences.getToken().first()
        }
        val bearerToken = "Bearer $token"

        val apiService = ApiConfig.getApiService()
        val call = apiService.getUserProfile(bearerToken)

        call.enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(
                call: Call<UserProfileResponse>,
                response: Response<UserProfileResponse>
            ) {
                if (response.isSuccessful) {
                    val userProfile = response.body()
                    if (userProfile != null) {
                        binding.pvUsername.setName(userProfile.user.name)
                        binding.pvEmail.setEmail(userProfile.user.email)
                        Glide.with(requireActivity())
                            .load(userProfile.user.profilePicture)
                            .placeholder(R.drawable.profile)
                            .error(R.drawable.profile)
                            .into(binding.profileImage)
                    } else {
                        Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes") { _, _ ->
            logoutUser()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun logoutUser() {
        val token = runBlocking {
            sessionPreferences.getToken().first()
        }
        val bearerToken = "Bearer $token"

        val apiService = ApiConfig.getApiService()
        val call = apiService.logout(bearerToken)

        call.enqueue(object : Callback<LogOutResponse> {
            override fun onResponse(
                call: Call<LogOutResponse>,
                response: Response<LogOutResponse>
            ) {
                if (response.isSuccessful) {
                    val logOutResponse = response.body()
                    if (logOutResponse != null && logOutResponse.status == "success") {
                        Toast.makeText(
                            requireContext(),
                            "Logout successful!",
                            Toast.LENGTH_SHORT
                        ).show()

                        runBlocking {
                            sessionPreferences.clearToken()
                        }

                        val intent = Intent(requireContext(), WelcomeActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Logout failed. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            override fun onFailure(call: Call<LogOutResponse>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}