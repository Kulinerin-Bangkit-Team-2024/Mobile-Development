package com.bangkit.capstone.kulinerin.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.capstone.kulinerin.R
import com.bangkit.capstone.kulinerin.data.api.ApiConfig
import com.bangkit.capstone.kulinerin.data.preference.SessionPreferences
import com.bangkit.capstone.kulinerin.data.preference.sessionDataStore
import com.bangkit.capstone.kulinerin.data.response.DetailFoodResponse
import com.bangkit.capstone.kulinerin.data.response.Food
import com.bangkit.capstone.kulinerin.databinding.ActivityDetailFoodBinding
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFoodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val foodId = intent.getIntExtra("FOOD_ID", -1)
        if (foodId != -1) {
            fetchFoodDetails(foodId)
        } else {
            finish()
        }
    }

    private fun fetchFoodDetails(foodId: Int) {
        binding.progressBar.visibility = View.VISIBLE
        val token = "Bearer ${getTokenFromSession()}"
        val apiService = ApiConfig.getApiService()
//        val call = apiService.getFoodDetail(token, foodId)

        apiService.getFoodDetail(token, foodId).enqueue(object : Callback<DetailFoodResponse> {
            override fun onResponse(call: Call<DetailFoodResponse>, response: Response<DetailFoodResponse>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val foodDetail = response.body()?.food
                    if (foodDetail != null) {
                        displayFoodDetail(foodDetail)
                    }
                } else {
                    Toast.makeText(this@DetailFoodActivity, "Failed to fetch details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DetailFoodResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@DetailFoodActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayFoodDetail(food: Food) {
        binding.apply {
            tvFoodName.text = food.foodName
            tvFoodOrigin.text = food.placeOfOrigin
            tvFoodDescription.text = food.description
            Glide.with(this@DetailFoodActivity)
                .load(food.foodImage)
                .into(imgFoodCover)
        }
    }

    private fun getTokenFromSession(): String {
        return runBlocking {
            val sessionPreferences = SessionPreferences.getInstance(applicationContext.sessionDataStore)
            sessionPreferences.getToken().first()
        }
    }

}