package com.bangkit.capstone.kulinerin.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstone.kulinerin.R
import com.bangkit.capstone.kulinerin.data.api.ApiConfig
import com.bangkit.capstone.kulinerin.data.preference.SessionPreferences
import com.bangkit.capstone.kulinerin.data.preference.sessionDataStore
import com.bangkit.capstone.kulinerin.data.response.ListFoodResponse
import com.bangkit.capstone.kulinerin.data.response.UserProfileResponse
import com.bangkit.capstone.kulinerin.ui.activity.DetailFoodActivity
import com.bangkit.capstone.kulinerin.ui.adapter.FoodAdapter
import com.bangkit.capstone.kulinerin.databinding.FragmentHomeBinding
import com.bangkit.capstone.kulinerin.ui.adapter.BannerAdapter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionPreferences: SessionPreferences
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var bannerAdapter: BannerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.let { binding ->

            // Inisialisasi adapter untuk banner
            val banners = listOf(
                R.drawable.banner1,
                R.drawable.banner2
            )
            bannerAdapter = BannerAdapter(banners)

            // Set adapter ke RecyclerView
            binding.recyclerViewBanner.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerViewBanner.adapter = bannerAdapter

            foodAdapter = FoodAdapter { selectedFood ->
                val intent = Intent(requireContext(), DetailFoodActivity::class.java).apply {
                    putExtra("FOOD_ID", selectedFood.foodId)
                }
                startActivity(intent)
            }
            recyclerView = binding.rvFoodsHome
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = foodAdapter

            sessionPreferences = SessionPreferences.getInstance(requireContext().sessionDataStore)
            val token = runBlocking {
                sessionPreferences.getToken().first()
            }
            val bearerToken = "Bearer $token"
            getListFoods(bearerToken)
        } ?: run {
            Toast.makeText(requireContext(), "Error: binding is not initialized", Toast.LENGTH_SHORT).show()
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
                if (_binding != null) {
                    if (response.isSuccessful) {
                        val userProfile = response.body()
                        if (userProfile != null) {
                            binding.greeting.text = "Hello, ${userProfile.user.name}!"
                        } else {
                            Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                if (_binding != null) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun getListFoods(token: String) {
        binding.listFoodProgressBar.visibility = View.VISIBLE
        val apiService = ApiConfig.getApiService()
        val call = apiService.getFood(token)

        call.enqueue(object : Callback<ListFoodResponse> {
            override fun onResponse(call: Call<ListFoodResponse>, response: Response<ListFoodResponse>) {
                if (_binding != null) {
                    binding.listFoodProgressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            val foodList = responseBody.foods ?: emptyList()
                            if (foodList.isNotEmpty()) {
                                foodAdapter.setData(foodList)
                            } else {
                                Toast.makeText(requireContext(), "No foods found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        val errorMessage = "Error: ${response.code()} ${response.message()}"
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ListFoodResponse>, t: Throwable) {
                if (_binding != null) {
                    binding.listFoodProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
