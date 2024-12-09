package com.bangkit.capstone.kulinerin.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bangkit.capstone.kulinerin.data.api.ApiConfig
import com.bangkit.capstone.kulinerin.data.preference.SessionPreferences
import com.bangkit.capstone.kulinerin.data.preference.sessionDataStore
import com.bangkit.capstone.kulinerin.data.response.ListFoodResponse
import com.bangkit.capstone.kulinerin.ui.adapter.FoodAdapter
import com.bangkit.capstone.kulinerin.databinding.FragmentFoodsBinding
import com.bangkit.capstone.kulinerin.ui.activity.LoginActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodsFragment : Fragment() {

    private var _binding: FragmentFoodsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionPreferences: SessionPreferences
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        foodAdapter = FoodAdapter()
        recyclerView = binding.rvFoods
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = foodAdapter

        sessionPreferences = SessionPreferences.getInstance(requireContext().sessionDataStore)
        val token = runBlocking {
            sessionPreferences.getToken().first()
        }
        val bearerToken = "Bearer $token"
        getListFoods(bearerToken)
    }

    private fun getListFoods(token: String) {
        val apiService = ApiConfig.getApiService()
        val call = apiService.getFood(token)

        call.enqueue(object : Callback<ListFoodResponse> {
            override fun onResponse(call: Call<ListFoodResponse>, response: Response<ListFoodResponse>) {
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
            override fun onFailure(call: Call<ListFoodResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
