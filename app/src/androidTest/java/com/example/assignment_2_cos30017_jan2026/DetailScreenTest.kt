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
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DetailScreenTest {

    @get:Rule(order = 0)
    val resetRule = object : ExternalResource() {
        override fun before() { CarRepository.reset() }
    }

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private fun clickFirstCar() {
        onView(withId(R.id.rv_cars)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CarGridAdapter.CarViewHolder>(0, click())
        )
    }

    // D1
    @Test
    fun carClick_opensDetailActivity() {
        clickFirstCar()
        onView(withId(R.id.tv_spec_km_value)).check(matches(isDisplayed()))
    }

    // D2
    @Test
    fun detailScreen_displaysAllSpecs() {
        clickFirstCar()
        onView(withId(R.id.iv_car)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_car_name)).check(matches(isDisplayed()))
        onView(withId(R.id.rb_rating)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_model_year)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_spec_km_value)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_spec_cost_value)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_spec_year_value)).check(matches(isDisplayed()))
    }

    // D3
    @Test
    fun detailScreen_rentButtonVisible() {
        clickFirstCar()
        onView(withId(R.id.btn_rent)).perform(scrollTo()).check(matches(isDisplayed()))
    }

    // D4
    @Test
    fun detailScreen_backNavigatesToHome() {
        clickFirstCar()
        onView(withId(R.id.tv_spec_km_value)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.et_search)).check(matches(isDisplayed()))
    }

    // Helper: rent the first car through the full flow
    private fun rentFirstCar() {
        clickFirstCar()
        onView(withId(R.id.btn_rent)).perform(scrollTo(), click())
        onView(withId(R.id.et_name)).perform(scrollTo(), typeText("Test"), closeSoftKeyboard())
        onView(withId(R.id.et_phone)).perform(scrollTo(), typeText("0123456789"), closeSoftKeyboard())
        onView(withId(R.id.et_email)).perform(scrollTo(), typeText("test@test.com"), closeSoftKeyboard())
        Thread.sleep(300)
        onView(withId(R.id.btn_save)).perform(scrollTo(), click())
    }

    // TC23
    @Test
    fun detailScreen_availableCarView() {
        clickFirstCar()
        onView(withId(R.id.layout_balance)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_rent)).perform(scrollTo()).check(matches(isDisplayed()))
    }

    // TC24
    @Test
    fun detailScreen_rentedCarView() {
        rentFirstCar()
        clickFirstCar()
        onView(withId(R.id.layout_booking_info)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_cancel_booking)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.btn_rent)).check(matches(not(isDisplayed())))
    }
}
