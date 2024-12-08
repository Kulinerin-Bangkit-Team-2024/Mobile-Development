package com.bangkit.capstone.kulinerin.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.capstone.kulinerin.data.api.ApiConfig
import com.bangkit.capstone.kulinerin.data.response.FoodsItem
import kotlinx.coroutines.launch
import retrofit2.HttpException

class FoodViewModel : ViewModel() {

    private val _food = MutableLiveData<List<FoodsItem>>()
    val food: LiveData<List<FoodsItem>> = _food

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchFoodList() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getFood()
                if (response.isNotEmpty()) {
                    _food.value = response[0].foods
                } else {
                    _errorMessage.value = "No food data found."
                }
            } catch (e: HttpException) {
                _errorMessage.value = "Failed to fetch data: ${e.message}"
            } catch (e: Exception) {
                _errorMessage.value = "An unexpected error occurred: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}