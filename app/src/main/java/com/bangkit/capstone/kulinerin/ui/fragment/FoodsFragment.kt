package com.bangkit.capstone.kulinerin.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstone.kulinerin.data.adapter.FoodAdapter
import com.bangkit.capstone.kulinerin.data.viewmodel.FoodViewModel
import com.bangkit.capstone.kulinerin.databinding.FragmentFoodsBinding

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
            adapter.notifyDataSetChanged()
        }

        foodViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.listFoodProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        foodViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        foodViewModel.fetchFoodList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
