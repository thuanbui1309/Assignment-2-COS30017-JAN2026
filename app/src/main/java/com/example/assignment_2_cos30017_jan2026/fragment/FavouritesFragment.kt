package com.example.assignment_2_cos30017_jan2026.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.assignment_2_cos30017_jan2026.adapter.FavouriteAdapter
import com.example.assignment_2_cos30017_jan2026.databinding.FragmentFavouritesBinding
import com.example.assignment_2_cos30017_jan2026.viewmodel.CarViewModel

class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!

    // Shared ViewModel — fragment–activity communication
    private val carViewModel: CarViewModel by activityViewModels()
    private lateinit var favouriteAdapter: FavouriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeFavourites()
    }

    var onItemClick: ((com.example.assignment_2_cos30017_jan2026.model.Car) -> Unit)? = null

    private fun setupRecyclerView() {
        favouriteAdapter = FavouriteAdapter(
            onItemClick = { car -> onItemClick?.invoke(car) ?: carViewModel.toggleFavourite(car) },
            onRemoveClick = { car -> carViewModel.toggleFavourite(car) }
        )
        binding.rvFavourites.apply {
            layoutManager = GridLayoutManager(context, 4)
            adapter = favouriteAdapter
        }
    }

    private fun observeFavourites() {
        carViewModel.favourites.observe(viewLifecycleOwner) { favourites ->
            val hasFavourites = favourites.isNotEmpty()
            binding.rvFavourites.visibility = if (hasFavourites) View.VISIBLE else View.GONE
            binding.tvNoFavourites.visibility = if (hasFavourites) View.GONE else View.VISIBLE
            favouriteAdapter.updateFavourites(favourites)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
