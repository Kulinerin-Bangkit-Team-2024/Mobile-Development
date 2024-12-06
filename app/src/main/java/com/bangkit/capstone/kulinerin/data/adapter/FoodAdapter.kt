package com.bangkit.capstone.kulinerin.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstone.kulinerin.data.response.FoodsItem
import com.bangkit.capstone.kulinerin.databinding.ItemFoodBinding
import com.bumptech.glide.Glide

class FoodAdapter(
    private val foodList: List<FoodsItem>

) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(private val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(foods: FoodsItem) {
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

    override fun getItemCount(): Int = foodList.size
}
