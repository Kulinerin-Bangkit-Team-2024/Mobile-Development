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
import com.bangkit.capstone.kulinerin.data.preference.SessionPreferences
import com.bangkit.capstone.kulinerin.ui.adapter.FoodAdapter
import com.bangkit.capstone.kulinerin.ui.model.FoodViewModel
import com.bangkit.capstone.kulinerin.databinding.FragmentFoodsBinding
import com.bangkit.capstone.kulinerin.ui.activity.LoginActivity

class FoodsFragment : Fragment() {

    private var _binding: FragmentFoodsBinding? = null
    private val binding get() = _binding!!

    private val foodViewModel: FoodViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FoodAdapter(emptyList())
        binding.rvFoods.adapter = adapter
        binding.rvFoods.layoutManager = LinearLayoutManager(requireContext())

        foodViewModel.food.observe(viewLifecycleOwner) { foods ->
            if (foods != null) {
                Log.d("FoodsFragment", "Food list updated with ${foods.size} items.")
                adapter.updateData(foods)
            } else {
                Log.d("FoodsFragment", "Food list is null.")
            }
        }

        foodViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.listFoodProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        foodViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Log.e("FoodsFragment", "Error message received: $message")
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        foodViewModel.fetchFoodList("your_token_here")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
