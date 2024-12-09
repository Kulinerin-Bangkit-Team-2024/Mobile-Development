package com.bangkit.capstone.kulinerin.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstone.kulinerin.data.response.FoodsItem
import com.bangkit.capstone.kulinerin.databinding.ItemFoodBinding
import com.bangkit.capstone.kulinerin.ui.activity.DetailFoodActivity
import com.bumptech.glide.Glide

class FoodAdapter(private val onClick: (FoodsItem) -> Unit) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    private val foodList = ArrayList<FoodsItem>()

    inner class FoodViewHolder(private val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(food: FoodsItem) {
            binding.apply {
                tvFoodName.text = food.foodName
                tvFoodOrigin.text = food.placeOfOrigin
                Glide.with(itemView.context)
                    .load(food.foodImage)
                    .into(imgFoodCover)

                root.setOnClickListener{
                    onClick(food)
                }
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

    fun setData(newFoodList: List<FoodsItem>) {
        foodList.clear()
        foodList.addAll(newFoodList)
        notifyDataSetChanged()
    }
}