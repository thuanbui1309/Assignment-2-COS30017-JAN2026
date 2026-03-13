package com.example.assignment_2_cos30017_jan2026.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment_2_cos30017_jan2026.R
import com.example.assignment_2_cos30017_jan2026.databinding.ActivityRentBinding
import com.example.assignment_2_cos30017_jan2026.model.Car
import com.example.assignment_2_cos30017_jan2026.util.LocaleHelper
import com.example.assignment_2_cos30017_jan2026.util.ThemeHelper
import com.example.assignment_2_cos30017_jan2026.viewmodel.RentViewModel
import com.google.android.material.snackbar.Snackbar
import android.content.Intent
import android.net.Uri

class RentActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CAR = "extra_car"
    }

    private lateinit var binding: ActivityRentBinding
    private val viewModel: RentViewModel by viewModels()

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val car = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_CAR, Car::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_CAR)
        }

        if (car == null) {
            finish()
            return
        }

        viewModel.setCar(this, car)
        setupToolbar()
        displayCarSummary(car)
        setupSlider()
        setupButtons()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_settings) {
                com.example.assignment_2_cos30017_jan2026.util.DialogHelper.showSettingsBottomSheet(this)
                true
            } else {
                false
            }
        }
    }

    private fun displayCarSummary(car: Car) {
        binding.ivRentCar.setImageResource(car.imageResId)
        binding.tvRentCarName.text = car.name
        binding.tvRentCarCost.text = getString(R.string.credits_per_day_format, car.dailyCost)
    }

    private fun setupSlider() {
        binding.sliderDuration.addOnChangeListener { _, value, _ ->
            val days = value.toInt()
            viewModel.setRentalDays(this, days)
            binding.tvDurationValue.text = if (days == 1) {
                getString(R.string.day_format, days)
            } else {
                getString(R.string.days_format, days)
            }
        }
        // Set initial value
        binding.tvDurationValue.text = getString(R.string.day_format, 1)
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            if (!validateForm()) return@setOnClickListener

            if (viewModel.confirmBooking()) {
                Snackbar.make(binding.root, R.string.booking_confirmed, Snackbar.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        }

        binding.btnCancel.setOnClickListener {
            cancelAndReturn()
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true
        
        // Validate Name (no numbers or special characters)
        val name = binding.etName.text?.toString()?.trim() ?: ""
        val nameRegex = "^[\\p{L} .'-]+$".toRegex()
        if (name.isEmpty()) {
            binding.tilName.error = getString(R.string.error_fill_email) 
            isValid = false
        } else if (!nameRegex.matches(name)) {
            binding.tilName.error = getString(R.string.error_invalid_name)
            isValid = false
        } else {
            binding.tilName.error = null
        }

        // Validate Phone (10 digits starting with 0)
        val phone = binding.etPhone.text?.toString()?.trim() ?: ""
        val phoneRegex = "^0\\d{9}$".toRegex()
        if (phone.isEmpty()) {
            binding.tilPhone.error = getString(R.string.error_fill_email)
            isValid = false
        } else if (!phoneRegex.matches(phone)) {
            binding.tilPhone.error = getString(R.string.error_invalid_phone)
            isValid = false
        } else {
            binding.tilPhone.error = null
        }

        // Validate Email
        val email = binding.etEmail.text?.toString()?.trim() ?: ""
        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.error_fill_email)
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.invalid_email)
            isValid = false
        } else {
            binding.tilEmail.error = null
        }
        
        return isValid
    }

    private fun setupBackHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                cancelAndReturn()
            }
        })
    }

    private fun cancelAndReturn() {
        setResult(RESULT_FIRST_USER) // Explicit cancel by user
        finish()
    }

    private fun observeViewModel() {
        val car = viewModel.selectedCar.value ?: return

        viewModel.totalCost.observe(this) { cost ->
            binding.tvTotalCost.text = getString(R.string.credits_format, cost)
            binding.tvDailyCost.text = getString(R.string.credits_format, car.dailyCost)
            binding.tvSummaryDuration.text = if ((viewModel.rentalDays.value ?: 1) == 1) {
                getString(R.string.day_format, viewModel.rentalDays.value ?: 1)
            } else {
                getString(R.string.days_format, viewModel.rentalDays.value ?: 1)
            }
            // Update after booking balance whenever cost changes
            val balance = viewModel.creditBalance.value ?: 0
            val afterBooking = balance - cost
            binding.tvAfterBooking.text = getString(R.string.credits_format, afterBooking)
        }

        viewModel.creditBalance.observe(this) { balance ->
            binding.tvBalance.text = getString(R.string.credits_format, balance)
            
            // Also update after booking balance if initial balance changes
            val cost = viewModel.totalCost.value ?: 0
            val afterBooking = balance - cost
            binding.tvAfterBooking.text = getString(R.string.credits_format, afterBooking)
        }

        viewModel.validationError.observe(this) { error ->
            if (error != null) {
                binding.tvValidationError.text = error
                binding.tvValidationError.visibility = View.VISIBLE
                binding.btnSave.isEnabled = false
                binding.btnSave.alpha = 0.5f
            } else {
                binding.tvValidationError.visibility = View.GONE
                binding.btnSave.isEnabled = true
                binding.btnSave.alpha = 1.0f
            }
        }
    }
}
