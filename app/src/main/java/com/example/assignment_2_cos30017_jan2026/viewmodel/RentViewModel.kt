package com.example.assignment_2_cos30017_jan2026.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.assignment_2_cos30017_jan2026.model.Booking
import com.example.assignment_2_cos30017_jan2026.model.Car
import com.example.assignment_2_cos30017_jan2026.repository.CarRepository
import com.example.assignment_2_cos30017_jan2026.R

class RentViewModel : ViewModel() {

    private val _selectedCar = MutableLiveData<Car>()
    val selectedCar: LiveData<Car> = _selectedCar

    private val _rentalDays = MutableLiveData(1)
    val rentalDays: LiveData<Int> = _rentalDays

    private val _totalCost = MutableLiveData(0)
    val totalCost: LiveData<Int> = _totalCost

    private val _creditBalance = MutableLiveData<Int>()
    val creditBalance: LiveData<Int> = _creditBalance

    private val _validationError = MutableLiveData<String?>()
    val validationError: LiveData<String?> = _validationError

    private val _bookingResult = MutableLiveData<Result<Booking>?>()
    val bookingResult: LiveData<Result<Booking>?> = _bookingResult

    fun setCar(context: android.content.Context, car: Car) {
        _selectedCar.value = car
        _creditBalance.value = CarRepository.getCreditBalance()
        updateTotalCost(context)
    }

    fun setRentalDays(context: android.content.Context, days: Int) {
        _rentalDays.value = days
        updateTotalCost(context)
    }

    fun updateTotalCost(context: android.content.Context) {
        val car = _selectedCar.value ?: return
        val days = _rentalDays.value ?: 1
        val cost = car.dailyCost * days
        _totalCost.value = cost
        validate(context, cost)
    }

    fun validate(context: android.content.Context, cost: Int) {
        val balance = CarRepository.getCreditBalance()
        _validationError.value = when {
            cost > CarRepository.MAX_RENTAL_COST ->
                context.getString(R.string.error_max_limit_exceeded, cost, CarRepository.MAX_RENTAL_COST)
            cost > balance ->
                context.getString(R.string.error_insufficient_credit, balance)
            else -> null
        }
    }

    fun confirmBooking(): Boolean {
        val car = _selectedCar.value ?: return false
        val days = _rentalDays.value ?: return false
        val result = CarRepository.rentCar(car, days)
        _bookingResult.value = result
        return result.isSuccess
    }
}
