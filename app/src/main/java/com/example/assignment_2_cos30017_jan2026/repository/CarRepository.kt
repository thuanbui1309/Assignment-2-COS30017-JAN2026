package com.example.assignment_2_cos30017_jan2026.repository

import com.example.assignment_2_cos30017_jan2026.R
import com.example.assignment_2_cos30017_jan2026.model.Booking
import com.example.assignment_2_cos30017_jan2026.model.Car

object CarRepository {

    const val INITIAL_CREDIT = 500
    const val MAX_RENTAL_COST = 400

    private val allCars = mutableListOf(
        Car("McLaren 600LT", "Supercar", 2022, 5, 12000, 50, R.drawable.car1),
        Car("Lamborghini Huracán", "Supercar", 2023, 5, 8000, 55, R.drawable.car2),
        Car("Lamborghini Aventador", "Supercar", 2021, 5, 18000, 45, R.drawable.car3),
        Car("Ferrari F8 Tributo", "Supercar", 2023, 5, 5000, 60, R.drawable.car4),
        Car("McLaren GT", "Grand Tourer", 2022, 4, 22000, 40, R.drawable.car5),
        Car("Lamborghini Huracán EVO", "Supercar", 2024, 5, 3000, 65, R.drawable.car6),
        Car("Tesla Model S", "Electric", 2024, 5, 5000, 30, R.drawable.car7),
        Car("Ferrari LaFerrari", "Hypercar", 2022, 5, 2000, 80, R.drawable.car8),
        Car("BMW 3 Series", "Sedan", 2021, 4, 35000, 20, R.drawable.car9),
        Car("Ferrari FF", "Grand Tourer", 2020, 4, 42000, 35, R.drawable.car10)
    )

    private val bookings = mutableListOf<Booking>()
    private var creditBalance = INITIAL_CREDIT

    fun getAvailableCars(): List<Car> = allCars.filter { it.isAvailable }

    fun getAllCars(): List<Car> = allCars.toList()

    fun getFavourites(): List<Car> = allCars.filter { it.isFavourite }

    fun getBookings(): List<Booking> = bookings.toList()

    fun findBooking(car: Car): Booking? = bookings.find { it.car.name == car.name }

    fun getCreditBalance(): Int = creditBalance

    fun toggleFavourite(car: Car) {
        val target = allCars.find { it.name == car.name } ?: return
        target.isFavourite = !target.isFavourite
    }

    fun rentCar(car: Car, rentalDays: Int): Result<Booking> {
        val totalCost = car.dailyCost * rentalDays

        if (totalCost > MAX_RENTAL_COST) {
            return Result.failure(
                IllegalArgumentException("Rental cost ($totalCost) exceeds maximum ($MAX_RENTAL_COST Credits)")
            )
        }
        if (totalCost > creditBalance) {
            return Result.failure(
                IllegalArgumentException("Insufficient credit. Balance: $creditBalance, Cost: $totalCost")
            )
        }

        val target = allCars.find { it.name == car.name } ?: return Result.failure(
            IllegalStateException("Car not found")
        )
        target.isAvailable = false
        creditBalance -= totalCost

        val booking = Booking(car, rentalDays, totalCost)
        bookings.add(booking)
        return Result.success(booking)
    }

    fun cancelBooking(booking: Booking) {
        val target = allCars.find { it.name == booking.car.name } ?: return
        target.isAvailable = true
        creditBalance += booking.totalCost
        bookings.remove(booking)
    }

    // Resets all state to initial values (used by tests)
    fun reset() {
        allCars.forEach {
            it.isAvailable = true
            it.isFavourite = false
        }
        bookings.clear()
        creditBalance = INITIAL_CREDIT
    }

    enum class SortMode { RATING_DESC, YEAR_DESC, COST_ASC }

    enum class FilterMode { ALL, AVAILABLE, RENTED }

    fun searchCars(query: String): List<Car> {
        if (query.isBlank()) return getAllCars()
        val lowerQuery = query.lowercase()
        return getAllCars().filter {
            it.name.lowercase().contains(lowerQuery) ||
            it.model.lowercase().contains(lowerQuery)
        }
    }

    fun filterCars(cars: List<Car>, mode: FilterMode): List<Car> {
        return when (mode) {
            FilterMode.ALL -> cars
            FilterMode.AVAILABLE -> cars.filter { it.isAvailable }
            FilterMode.RENTED -> cars.filter { !it.isAvailable }
        }
    }

    fun sortCars(cars: List<Car>, mode: SortMode): List<Car> {
        return when (mode) {
            SortMode.RATING_DESC -> cars.sortedByDescending { it.rating }
            SortMode.YEAR_DESC -> cars.sortedByDescending { it.year }
            SortMode.COST_ASC -> cars.sortedBy { it.dailyCost }
        }
    }
}
