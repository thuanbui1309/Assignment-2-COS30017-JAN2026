package com.example.assignment_2_cos30017_jan2026

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.assignment_2_cos30017_jan2026.adapter.CarGridAdapter
import com.example.assignment_2_cos30017_jan2026.databinding.ActivityMainBinding
import com.example.assignment_2_cos30017_jan2026.fragment.FavouritesFragment
import com.example.assignment_2_cos30017_jan2026.model.Car
import com.example.assignment_2_cos30017_jan2026.repository.CarRepository
import com.example.assignment_2_cos30017_jan2026.ui.DetailActivity
import com.example.assignment_2_cos30017_jan2026.util.DialogHelper
import com.example.assignment_2_cos30017_jan2026.util.LocaleHelper
import com.example.assignment_2_cos30017_jan2026.viewmodel.CarViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CarViewModel by viewModels()
    private lateinit var carGridAdapter: CarGridAdapter
    private var currentLanguage: String = ""

    private val detailLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.refreshData()
        if (result.resultCode == RESULT_OK) {
            Snackbar.make(binding.root, R.string.booking_confirmed, Snackbar.LENGTH_SHORT).show()
        } else if (result.resultCode == RESULT_FIRST_USER + 1) {
            Snackbar.make(binding.root, R.string.booking_cancelled, Snackbar.LENGTH_SHORT).show()
        } else if (result.resultCode == RESULT_FIRST_USER) {
            val refunded = result.data?.getIntExtra("refunded_amount", 0) ?: 0
            val msg = getString(R.string.booking_cancelled_refund, refunded)
            Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSearchBar()
        setupSortButton()
        setupFilterButton()
        setupCarGrid()
        setupFavouritesFragment()
        observeViewModel()

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
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_settings) {
                DialogHelper.showSettingsBottomSheet(this)
                true
            } else {
                false
            }
        }
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchCars(s?.toString() ?: "")
            }
        })
    }

    private fun setupSortButton() {
        binding.btnSort.setOnClickListener {
            showSortBottomSheet()
        }
    }

    private fun showSortBottomSheet() {
        val bottomSheet = BottomSheetDialog(this)
        bottomSheet.setContentView(R.layout.dialog_sort)

        bottomSheet.findViewById<TextView>(R.id.btn_sort_rating)?.setOnClickListener {
            viewModel.sortCars(CarRepository.SortMode.RATING_DESC)
            bottomSheet.dismiss()
        }
        bottomSheet.findViewById<TextView>(R.id.btn_sort_year)?.setOnClickListener {
            viewModel.sortCars(CarRepository.SortMode.YEAR_DESC)
            bottomSheet.dismiss()
        }
        bottomSheet.findViewById<TextView>(R.id.btn_sort_cost)?.setOnClickListener {
            viewModel.sortCars(CarRepository.SortMode.COST_ASC)
            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }

    private fun setupFilterButton() {
        binding.btnFilter.setOnClickListener {
            showFilterBottomSheet()
        }
    }

    private fun showFilterBottomSheet() {
        val bottomSheet = BottomSheetDialog(this)
        bottomSheet.setContentView(R.layout.dialog_filter)

        bottomSheet.findViewById<TextView>(R.id.btn_filter_all)?.setOnClickListener {
            viewModel.filterCars(CarRepository.FilterMode.ALL)
            bottomSheet.dismiss()
        }
        bottomSheet.findViewById<TextView>(R.id.btn_filter_available)?.setOnClickListener {
            viewModel.filterCars(CarRepository.FilterMode.AVAILABLE)
            bottomSheet.dismiss()
        }
        bottomSheet.findViewById<TextView>(R.id.btn_filter_rented)?.setOnClickListener {
            viewModel.filterCars(CarRepository.FilterMode.RENTED)
            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }

    private fun setupCarGrid() {
        carGridAdapter = CarGridAdapter(
            onCarClick = { car -> launchDetailActivity(car) },
            onFavouriteClick = { car -> viewModel.toggleFavourite(car) },
            onCarLongPress = { car ->
                viewModel.toggleFavourite(car)
                val msgRes = if (!car.isFavourite) R.string.added_to_favourites
                    else R.string.removed_from_favourites
                Snackbar.make(binding.root, getString(msgRes, car.name), Snackbar.LENGTH_SHORT).show()
            }
        )

        val spanCount = calculateSpanCount()
        binding.rvCars.apply {
            layoutManager = GridLayoutManager(this@MainActivity, spanCount)
            adapter = carGridAdapter
        }
    }

    private fun calculateSpanCount(): Int {
        val screenWidthDp = resources.configuration.screenWidthDp
        return (screenWidthDp / 180).coerceIn(2, 4)
    }

    private fun launchDetailActivity(car: Car) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_CAR, car)
        }
        detailLauncher.launch(intent)
    }

    private fun setupFavouritesFragment() {
        var fragment = supportFragmentManager.findFragmentById(R.id.favourites_section) as? FavouritesFragment
        if (fragment == null) {
            fragment = FavouritesFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.favourites_section, fragment)
                .commit()
        }
        
        // Pass the detail launcher click event to the fragment
        fragment.onItemClick = { car -> launchDetailActivity(car) }
    }

    private fun observeViewModel() {
        viewModel.availableCars.observe(this) { cars ->
            // Fix DiffUtil bug by submitting a mapped list of copies
            carGridAdapter.submitList(cars.map { it.copy() })
            val hasCars = cars.isNotEmpty()
            binding.rvCars.visibility = if (hasCars) View.VISIBLE else View.GONE
            binding.tvNoCars.visibility = if (hasCars) View.GONE else View.VISIBLE

            // Update section title dynamically
            val titleResId = when (viewModel.currentFilterMode) {
                CarRepository.FilterMode.ALL -> R.string.filter_all
                CarRepository.FilterMode.AVAILABLE -> R.string.filter_available
                CarRepository.FilterMode.RENTED -> R.string.filter_rented
            }
            binding.tvSectionTitle.text = getString(titleResId)
        }

        viewModel.creditBalance.observe(this) { balance ->
            binding.tvCreditBalance.text = getString(R.string.credits_format, balance)
        }
    }
}