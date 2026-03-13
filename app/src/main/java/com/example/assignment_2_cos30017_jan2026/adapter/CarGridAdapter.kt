package com.example.assignment_2_cos30017_jan2026.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment_2_cos30017_jan2026.R
import com.example.assignment_2_cos30017_jan2026.databinding.ItemCarGridBinding
import com.example.assignment_2_cos30017_jan2026.model.Car

class CarGridAdapter(
    private val onCarClick: (Car) -> Unit,
    private val onFavouriteClick: (Car) -> Unit,
    private val onCarLongPress: (Car) -> Unit
) : ListAdapter<Car, CarGridAdapter.CarViewHolder>(CarDiffCallback()) {

    inner class CarViewHolder(
        private val binding: ItemCarGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(car: Car) {
            binding.ivCar.setImageResource(car.imageResId)
            binding.tvCarName.text = car.name
            binding.rbRating.rating = car.rating.toFloat()
            binding.tvRating.text = car.rating.toString()

            if (!car.isAvailable) {
                binding.tvRentedBadge.visibility = android.view.View.VISIBLE
                binding.ivCar.alpha = 0.5f // Dim image for rented cars
            } else {
                binding.tvRentedBadge.visibility = android.view.View.GONE
                binding.ivCar.alpha = 1.0f
            }

            val context = binding.root.context
            binding.tvCost.text = context.getString(R.string.credits_per_day_format, car.dailyCost)

            val favIcon = if (car.isFavourite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            binding.btnFavourite.setImageResource(favIcon)

            binding.btnFavourite.setOnClickListener { onFavouriteClick(car) }
            binding.root.setOnClickListener { onCarClick(car) }
            binding.root.setOnLongClickListener {
                onCarLongPress(car)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ItemCarGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class CarDiffCallback : DiffUtil.ItemCallback<Car>() {
        override fun areItemsTheSame(oldItem: Car, newItem: Car): Boolean =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: Car, newItem: Car): Boolean =
            oldItem == newItem
    }
}
