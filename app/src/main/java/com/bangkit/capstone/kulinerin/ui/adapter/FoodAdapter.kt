package com.bangkit.capstone.kulinerin.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstone.kulinerin.data.response.FoodsItem
import com.bangkit.capstone.kulinerin.databinding.ItemFoodBinding
import com.bumptech.glide.Glide

class FoodAdapter(private var foodList: List<FoodsItem>) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(private val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(foods: FoodsItem) {
            Log.d("FoodAdapter", "Binding food item: ${foods.foodName}")
            binding.apply {
                tvFoodName.text = foods.foodName
                tvFoodOrigin.text = foods.placeOfOrigin
                Glide.with(itemView.context)
                    .load(foods.foodImage)
                    .into(imgFoodCover)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foodList[position])
    }

    override fun getItemCount(): Int {
        Log.d("FoodAdapter", "getItemCount called. Size: ${foodList.size}")
        return foodList.size
    }

    fun updateData(newFoodList: List<FoodsItem>) {
        foodList = newFoodList
        notifyDataSetChanged()

    }
}
