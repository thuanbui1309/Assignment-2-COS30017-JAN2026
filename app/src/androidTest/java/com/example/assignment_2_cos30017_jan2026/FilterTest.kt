package com.example.assignment_2_cos30017_jan2026

import androidx.test.espresso.Espresso.onView
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
class FilterTest {

    @get:Rule(order = 0)
    val resetRule = object : ExternalResource() {
        override fun before() { CarRepository.reset() }
    }

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // TC11
    @Test
    fun filter_openBottomSheet() {
        onView(withId(R.id.btn_filter)).perform(click())
        onView(withText(R.string.filter_title)).check(matches(isDisplayed()))
        onView(withText(R.string.filter_all)).check(matches(isDisplayed()))
        onView(withText(R.string.filter_available)).check(matches(isDisplayed()))
        onView(withText(R.string.filter_rented)).check(matches(isDisplayed()))
    }

    // Helper to rent first car
    private fun rentFirstCar() {
        onView(withId(R.id.rv_cars)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CarGridAdapter.CarViewHolder>(0, click())
        )
        onView(withId(R.id.btn_rent)).perform(scrollTo(), click())
        onView(withId(R.id.et_name)).perform(scrollTo(), typeText("Test"), closeSoftKeyboard())
        onView(withId(R.id.et_phone)).perform(scrollTo(), typeText("0123456789"), closeSoftKeyboard())
        onView(withId(R.id.et_email)).perform(scrollTo(), typeText("test@test.com"), closeSoftKeyboard())
        Thread.sleep(300)
        onView(withId(R.id.btn_save)).perform(scrollTo(), click())
    }

    // TC12, TC13, TC14
    @Test
    fun filter_applyFilters() {
        rentFirstCar()

        // TC12 - All Cars
        onView(withId(R.id.btn_filter)).perform(click())
        onView(withText(R.string.filter_all)).perform(click())
        onView(withId(R.id.rv_cars)).check(matches(hasDescendant(withText("McLaren 600LT"))))
        onView(withId(R.id.tv_section_title)).check(matches(withText(R.string.filter_all)))

        // TC13 - Available Only
        onView(withId(R.id.btn_filter)).perform(click())
        onView(withText(R.string.filter_available)).perform(click())
        onView(withId(R.id.tv_section_title)).check(matches(withText(R.string.filter_available)))

        // TC14 - Rented Only
        onView(withId(R.id.btn_filter)).perform(click())
        onView(withText(R.string.filter_rented)).perform(click())
        onView(withId(R.id.rv_cars)).check(matches(hasDescendant(withText("McLaren 600LT"))))
        onView(withId(R.id.tv_section_title)).check(matches(withText(R.string.filter_rented)))
    }
}
