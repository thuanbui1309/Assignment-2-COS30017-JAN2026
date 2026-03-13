package com.example.assignment_2_cos30017_jan2026.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment_2_cos30017_jan2026.databinding.ItemFavouriteBinding
import com.example.assignment_2_cos30017_jan2026.model.Car

class FavouriteAdapter(
    private var favourites: List<Car> = emptyList(),
    private val onItemClick: (Car) -> Unit,
    private val onRemoveClick: (Car) -> Unit
) : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemFavouriteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavouriteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val car = favourites[position]
        holder.binding.ivFavCar.setImageResource(car.imageResId)
        holder.binding.tvFavName.text = car.name
        holder.itemView.setOnClickListener { onItemClick(car) }
        holder.binding.btnRemoveFav.setOnClickListener { onRemoveClick(car) }
    }

    override fun getItemCount(): Int = favourites.size

    fun updateFavourites(newList: List<Car>) {
        favourites = newList
        notifyDataSetChanged()
    }
}
