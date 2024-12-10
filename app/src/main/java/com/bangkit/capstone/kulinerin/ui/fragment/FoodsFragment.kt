package com.bangkit.capstone.kulinerin.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bangkit.capstone.kulinerin.data.api.ApiConfig
import com.bangkit.capstone.kulinerin.data.preference.SessionPreferences
import com.bangkit.capstone.kulinerin.data.preference.sessionDataStore
import com.bangkit.capstone.kulinerin.data.response.FoodsItem
import com.bangkit.capstone.kulinerin.data.response.ListFoodResponse
import com.bangkit.capstone.kulinerin.data.response.SearchFoodByNameResponse
import com.bangkit.capstone.kulinerin.databinding.FragmentFoodsBinding
import com.bangkit.capstone.kulinerin.ui.activity.DetailFoodActivity
import com.bangkit.capstone.kulinerin.ui.adapter.FoodAdapter
import com.bangkit.capstone.kulinerin.ui.model.SearchViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodsFragment : Fragment() {

    private var _binding: FragmentFoodsBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel by viewModels<SearchViewModel>()
    private lateinit var sessionPreferences: SessionPreferences
    private lateinit var foodAdapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        foodAdapter = FoodAdapter { selectedFood ->
            val intent = Intent(requireContext(), DetailFoodActivity::class.java).apply {
                putExtra("FOOD_ID", selectedFood.foodId)
            }
            startActivity(intent)
        }

        binding.rvFoods.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = foodAdapter
        }

        sessionPreferences = SessionPreferences.getInstance(requireContext().sessionDataStore)
        val token = runBlocking {
            sessionPreferences.getToken().first()
        }
        val bearerToken = "Bearer $token"

        setupSearchView(bearerToken)
        observeViewModel()
        if (searchViewModel.query.value.isNullOrEmpty()) {
            getListFoods(bearerToken)
        } else {
            searchFoodByName(bearerToken, searchViewModel.query.value!!)
        }
    }

    private fun setupSearchView(token: String) {
        with(binding) {
            svFoods.setupWithSearchBar(sbFoods)

            svFoods.editText.setOnEditorActionListener { _, _, _ ->
                val query = svFoods.text.toString()
                if (query.isNotEmpty()) {
                    searchViewModel.setQuery(query)
                    searchFoodByName(token, query)
                } else {
                    getListFoods(token)
                }
                svFoods.hide()
                sbFoods.setText(svFoods.text)
                false
            }

            sbFoods.setOnMenuItemClickListener {
                svFoods.show() // Show search view
                true
            }
        }
    }

    private fun observeViewModel() {
        searchViewModel.foods.observe(viewLifecycleOwner) { foods ->
            foodAdapter.setData(foods)
        }

        searchViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.listFoodProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        searchViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                searchViewModel.setErrorMessage(null) // Clear error message
            }
        }
    }

    private fun searchFoodByName(token: String, query: String) {
        searchViewModel.setLoading(true)
        val apiService = ApiConfig.getApiService()
        apiService.searchFoodByName(token, query).enqueue(object : Callback<SearchFoodByNameResponse> {
            override fun onResponse(call: Call<SearchFoodByNameResponse>, response: Response<SearchFoodByNameResponse>) {
                searchViewModel.setLoading(false)
                if (response.isSuccessful) {
                    val foodList = response.body()?.foods ?: emptyList()
                    searchViewModel.setFoods(foodList.map {
                        FoodsItem(
                            foodName = it.foodName,
                            foodId = it.foodId,
                            foodImage = it.foodImage,
                            placeOfOrigin = it.placeOfOrigin
                        )
                    })
                } else {
                    searchViewModel.setErrorMessage("Failed to fetch data")
                }
            }

            override fun onFailure(call: Call<SearchFoodByNameResponse>, t: Throwable) {
                searchViewModel.setLoading(false)
                searchViewModel.setErrorMessage("Error: ${t.message}")
            }
        })
    }

    private fun getListFoods(token: String) {
        searchViewModel.setLoading(true)
        ApiConfig.getApiService().getFood(token).enqueue(object : Callback<ListFoodResponse> {
            override fun onResponse(call: Call<ListFoodResponse>, response: Response<ListFoodResponse>) {
                searchViewModel.setLoading(false)
                if (response.isSuccessful) {
                    val foodList = response.body()?.foods ?: emptyList()
                    searchViewModel.setFoods(foodList)
                } else {
                    searchViewModel.setErrorMessage("Failed to fetch data")
                }
            }

            override fun onFailure(call: Call<ListFoodResponse>, t: Throwable) {
                searchViewModel.setLoading(false)
                searchViewModel.setErrorMessage("Error: ${t.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}