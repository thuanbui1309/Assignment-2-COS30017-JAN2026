package com.example.assignment_2_cos30017_jan2026.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.assignment_2_cos30017_jan2026.R
import com.example.assignment_2_cos30017_jan2026.databinding.ActivityDetailBinding
import com.example.assignment_2_cos30017_jan2026.model.Car
import com.example.assignment_2_cos30017_jan2026.repository.CarRepository
import com.example.assignment_2_cos30017_jan2026.util.LocaleHelper
import com.example.assignment_2_cos30017_jan2026.util.ThemeHelper
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CAR = "extra_car"
    }

    private lateinit var binding: ActivityDetailBinding
    private lateinit var car: Car
    private var currentLanguage: String = ""

    private val rentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            setResult(RESULT_OK)
            finish()
        } else if (result.resultCode == RESULT_FIRST_USER) {
            setResult(RESULT_FIRST_USER + 1) // Rent cancelled by user
            finish()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        car = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_CAR, Car::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_CAR)
        } ?: run {
            finish()
            return
        }

        setupToolbar()
        displayCarInfo()
        setupFavouriteButton()
        setupRentAction()

        currentLanguage = LocaleHelper.getLanguage(this)
    }

    override fun onResume() {
        super.onResume()
        val savedLanguage = LocaleHelper.getLanguage(this)
        if (currentLanguage.isNotEmpty() && currentLanguage != savedLanguage) {
            currentLanguage = savedLanguage
            recreate()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.toolbar.inflateMenu(R.menu.menu_toolbar_detail)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_share -> {
                    shareCarDetails()
                    true
                }
                R.id.action_settings -> {
                    com.example.assignment_2_cos30017_jan2026.util.DialogHelper.showSettingsBottomSheet(this)
                    true
                }
                else -> false
            }
        }
    }

    private fun displayCarInfo() {
        binding.ivCar.setImageResource(car.imageResId)
        binding.tvCarName.text = car.name
        binding.rbRating.rating = car.rating.toFloat()
        binding.tvModelYear.text = getString(R.string.model_year_format, car.model, car.year)

        // Specchips
        binding.tvSpecKmValue.text = getString(R.string.km_format, car.kilometres)
        binding.tvSpecCostValue.text = getString(R.string.credits_per_day_format, car.dailyCost)
        binding.tvSpecYearValue.text = car.year.toString()

        // Credit balance
        val balance = CarRepository.getCreditBalance()
        binding.tvBalance.text = getString(R.string.credits_format, balance)

        // Rented state vs Available state
        if (!car.isAvailable) {
            binding.btnRent.visibility = android.view.View.GONE
            binding.btnCancelBooking?.visibility = android.view.View.VISIBLE
            binding.layoutBalance?.visibility = android.view.View.GONE
            binding.layoutBookingInfo?.visibility = android.view.View.VISIBLE
            
            val booking = CarRepository.findBooking(car)
            if (booking != null) {
                binding.tvBookingDetails?.text = getString(R.string.booking_days_format, booking.rentalDays, booking.totalCost)
            }
        } else {
            binding.btnRent.visibility = android.view.View.VISIBLE
            binding.btnCancelBooking?.visibility = android.view.View.GONE
            binding.layoutBalance?.visibility = android.view.View.VISIBLE
            binding.layoutBookingInfo?.visibility = android.view.View.GONE
        }
    }

    private fun setupFavouriteButton() {
        updateFavouriteIcon()
        binding.btnFavourite.setOnClickListener {
            CarRepository.toggleFavourite(car)
            car.isFavourite = !car.isFavourite
            updateFavouriteIcon()
            val msgRes = if (car.isFavourite) R.string.added_to_favourites
                else R.string.removed_from_favourites
            Snackbar.make(binding.root, getString(msgRes, car.name), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun updateFavouriteIcon() {
        val icon = if (car.isFavourite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        binding.btnFavourite.setImageResource(icon)
    }

    private fun setupRentAction() {
        binding.btnRent.setOnClickListener {
            val intent = Intent(this, RentActivity::class.java).apply {
                putExtra(RentActivity.EXTRA_CAR, car)
            }
            rentLauncher.launch(intent)
        }
        
        binding.btnCancelBooking?.setOnClickListener {
            val booking = CarRepository.findBooking(car)
            if (booking != null) {
                val refunded = booking.totalCost
                CarRepository.cancelBooking(booking)
                
                val resultIntent = Intent().apply {
                    putExtra("refunded_amount", refunded)
                }
                setResult(RESULT_FIRST_USER, resultIntent) // Custom result code for cancel
                finish()
            }
        }
    }

    private fun shareCarDetails() {
        // 1. Prepare the text payload
        val shareText = getString(
            R.string.share_car_text,
            car.name,
            car.year,
            car.rating.toFloat(),
            car.dailyCost
        )

        // 2. Extract bitmap from the currently displayed ImageView (or resource)
        val drawable = ContextCompat.getDrawable(this, car.imageResId)
        val bitmap = (drawable as? BitmapDrawable)?.bitmap ?: return

        // 3. Save bitmap to cache directory exposed by FileProvider
        try {
            val cachePath = File(cacheDir, "images")
            cachePath.mkdirs() // Create directory if it doesn't exist
            val imageFile = File(cachePath, "shared_car.png")
            val stream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            // 4. Generate content URI using FileProvider
            val imageUri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                imageFile
            )

            // 5. Build and launch ACTION_SEND Intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, shareText)
                // Grant read permission to whichever app the user chooses
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, getString(R.string.cd_share)))
            
        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(binding.root, "Error preparing share content", Snackbar.LENGTH_SHORT).show()
        }
    }
}
