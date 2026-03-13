package com.example.assignment_2_cos30017_jan2026.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.assignment_2_cos30017_jan2026.model.Booking
import com.example.assignment_2_cos30017_jan2026.model.Car
import com.example.assignment_2_cos30017_jan2026.repository.CarRepository

class CarViewModel : ViewModel() {

    private val _availableCars = MutableLiveData<List<Car>>()
    val availableCars: LiveData<List<Car>> = _availableCars

    private val _favourites = MutableLiveData<List<Car>>()
    val favourites: LiveData<List<Car>> = _favourites

    private val _creditBalance = MutableLiveData<Int>()
    val creditBalance: LiveData<Int> = _creditBalance

    private val _bookings = MutableLiveData<List<Booking>>()
    val bookings: LiveData<List<Booking>> = _bookings

    private var currentSearchQuery = ""
    private var currentSortMode: CarRepository.SortMode? = null
    var currentFilterMode: CarRepository.FilterMode = CarRepository.FilterMode.ALL
        private set

    init {
        refreshData()
    }

    fun refreshData() {
        var cars = if (currentSearchQuery.isBlank()) {
            CarRepository.getAllCars()
        } else {
            CarRepository.searchCars(currentSearchQuery)
        }

        cars = CarRepository.filterCars(cars, currentFilterMode)

        currentSortMode?.let { mode ->
            cars = CarRepository.sortCars(cars, mode)
        }

        _availableCars.value = cars
        _favourites.value = CarRepository.getFavourites()
        _creditBalance.value = CarRepository.getCreditBalance()
        _bookings.value = CarRepository.getBookings()
    }

    fun searchCars(query: String) {
        currentSearchQuery = query
        refreshData()
    }

    fun sortCars(mode: CarRepository.SortMode) {
        currentSortMode = mode
        refreshData()
    }

    fun filterCars(mode: CarRepository.FilterMode) {
        currentFilterMode = mode
        refreshData()
    }

    fun toggleFavourite(car: Car) {
        CarRepository.toggleFavourite(car)
        refreshData()
    }

    fun cancelBooking(booking: Booking) {
        CarRepository.cancelBooking(booking)
        refreshData()
    }
}
