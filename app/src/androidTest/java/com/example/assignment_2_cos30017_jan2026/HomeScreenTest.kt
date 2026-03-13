package com.example.assignment_2_cos30017_jan2026

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.assignment_2_cos30017_jan2026.adapter.CarGridAdapter
import com.example.assignment_2_cos30017_jan2026.repository.CarRepository
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule(order = 0)
    val resetRule = object : ExternalResource() {
        override fun before() { CarRepository.reset() }
    }

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // H1
    @Test
    fun homeScreen_toolbarDisplayed() {
        onView(withId(R.id.toolbar))
            .check(matches(isDisplayed()))
        onView(withId(R.id.toolbar))
            .check(matches(hasDescendant(withText(R.string.app_name))))
    }

    // H2
    @Test
    fun homeScreen_creditBalanceDisplayed() {
        onView(withId(R.id.tv_credit_balance))
            .check(matches(isDisplayed()))
    }

    // H3
    @Test
    fun homeScreen_carGridDisplayed() {
        onView(withId(R.id.rv_cars))
            .check(matches(isDisplayed()))
        onView(withId(R.id.rv_cars))
            .check(matches(hasMinimumChildCount(1)))
    }

    // H4
    @Test
    fun homeScreen_searchBarAndSortButtonVisible() {
        onView(withId(R.id.et_search))
            .check(matches(isDisplayed()))
        onView(withId(R.id.btn_sort))
            .check(matches(isDisplayed()))
    }

    // TC04 — No visible "Rented" badge when all cars are available
    @Test
    fun homeScreen_rentedBadgeHiddenByDefault() {
        onView(allOf(withId(R.id.tv_rented_badge), isDisplayed()))
            .check(doesNotExist())
    }

    // TC05
    @Test
    fun homeScreen_clickNavigatesToDetail() {
        onView(withId(R.id.rv_cars)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CarGridAdapter.CarViewHolder>(0, click())
        )
        onView(withId(R.id.tv_spec_km_value)).check(matches(isDisplayed()))
    }
}
