package com.bangkit.capstone.kulinerin.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.capstone.kulinerin.data.response.FoodsItem

class SearchViewModel : ViewModel() {
    private val _query = MutableLiveData<String>()
    val query: LiveData<String> get() = _query

    private val _foods = MutableLiveData<List<FoodsItem>>()
    val foods: LiveData<List<FoodsItem>> get() = _foods

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun setQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun setFoods(newFoods: List<FoodsItem>) {
        _foods.value = newFoods
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }
}