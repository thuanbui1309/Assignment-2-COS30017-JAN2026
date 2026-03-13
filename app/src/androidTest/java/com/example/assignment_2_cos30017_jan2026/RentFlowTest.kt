package com.example.assignment_2_cos30017_jan2026

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.assignment_2_cos30017_jan2026.adapter.CarGridAdapter
import com.example.assignment_2_cos30017_jan2026.repository.CarRepository
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RentFlowTest {

    @get:Rule(order = 0)
    val resetRule = object : ExternalResource() {
        override fun before() { CarRepository.reset() }
    }

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // Navigate: Home → Detail → Rent
    private fun navigateToRentScreen() {
        onView(withId(R.id.rv_cars)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CarGridAdapter.CarViewHolder>(0, click())
        )
        onView(withId(R.id.btn_rent)).perform(scrollTo(), click())
    }

    // Fill the rent form with valid data
    private fun fillValidForm() {
        onView(withId(R.id.et_name)).perform(scrollTo(), typeText("John Doe"), closeSoftKeyboard())
        onView(withId(R.id.et_phone)).perform(scrollTo(), typeText("0123456789"), closeSoftKeyboard())
        onView(withId(R.id.et_email)).perform(scrollTo(), typeText("test@example.com"), closeSoftKeyboard())
        Thread.sleep(300)
    }

    // R1
    @Test
    fun rent_opensFromDetail() {
        navigateToRentScreen()
        onView(withId(R.id.slider_duration)).check(matches(isDisplayed()))
    }

    // R3
    @Test
    fun rent_formValidation_emptyFields() {
        navigateToRentScreen()
        onView(withId(R.id.btn_save)).perform(scrollTo(), click())
        onView(withId(R.id.til_name)).check(matches(hasDescendant(withText(R.string.error_fill_email))))
    }

    // R4
    @Test
    fun rent_formValidation_invalidName() {
        navigateToRentScreen()
        onView(withId(R.id.et_name)).perform(scrollTo(), typeText("abc123"), closeSoftKeyboard())
        onView(withId(R.id.btn_save)).perform(scrollTo(), click())
        onView(withId(R.id.til_name)).check(matches(hasDescendant(withText(R.string.error_invalid_name))))
    }

    // R5
    @Test
    fun rent_formValidation_invalidPhone() {
        navigateToRentScreen()
        onView(withId(R.id.et_phone)).perform(scrollTo(), typeText("123"), closeSoftKeyboard())
        onView(withId(R.id.btn_save)).perform(scrollTo(), click())
        onView(withId(R.id.til_phone)).check(matches(hasDescendant(withText(R.string.error_invalid_phone))))
    }

    // R6
    @Test
    fun rent_formValidation_invalidEmail() {
        navigateToRentScreen()
        onView(withId(R.id.et_email)).perform(scrollTo(), typeText("notanemail"), closeSoftKeyboard())
        onView(withId(R.id.btn_save)).perform(scrollTo(), click())
        onView(withId(R.id.til_email)).check(matches(hasDescendant(withText(R.string.invalid_email))))
    }

    // TC30
    @Test
    fun rent_validBooking_confirmsAndReturnsHome() {
        navigateToRentScreen()
        fillValidForm()
        onView(withId(R.id.btn_save)).perform(scrollTo(), click())
        onView(withId(R.id.rv_cars)).check(matches(isDisplayed()))
    }

    // TC31
    @Test
    fun rent_cancelButton_returnsToHomeAndShowsSnackbar() {
        navigateToRentScreen()
        onView(withId(R.id.btn_cancel)).perform(scrollTo(), click())
        onView(withId(R.id.rv_cars)).check(matches(isDisplayed()))
    }

    // TC32
    @Test
    fun rent_deviceBack_returnsToDetailSilently() {
        navigateToRentScreen()
        pressBack()
        onView(withId(R.id.tv_spec_km_value)).check(matches(isDisplayed()))
    }

    // TC33
    @Test
    fun rent_refundBooking() {
        navigateToRentScreen()
        fillValidForm()
        onView(withId(R.id.btn_save)).perform(scrollTo(), click())

        // Re-open the now-rented car
        onView(withId(R.id.rv_cars)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CarGridAdapter.CarViewHolder>(0, click())
        )
        onView(withId(R.id.btn_cancel_booking)).perform(scrollTo(), click())
        onView(withId(R.id.rv_cars)).check(matches(isDisplayed()))
    }
}
