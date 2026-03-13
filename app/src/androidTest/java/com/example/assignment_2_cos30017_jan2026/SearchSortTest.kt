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
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchSortTest {

    @get:Rule(order = 0)
    val resetRule = object : ExternalResource() {
        override fun before() { CarRepository.reset() }
    }

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // TC06
    @Test
    fun search_validQuery_filtersResults() {
        onView(withId(R.id.et_search))
            .perform(typeText("McLaren"), closeSoftKeyboard())
        onView(withId(R.id.rv_cars))
            .check(matches(isDisplayed()))
        onView(withId(R.id.rv_cars))
            .check(matches(hasDescendant(withText("McLaren 600LT"))))
    }

    // TC08
    @Test
    fun search_noMatch_showsEmptyState() {
        onView(withId(R.id.et_search))
            .perform(typeText("ZZZZZ"), closeSoftKeyboard())
        onView(withId(R.id.tv_no_cars))
            .check(matches(isDisplayed()))
        onView(withId(R.id.rv_cars))
            .check(matches(not(isDisplayed())))
    }

    // TC07
    @Test
    fun search_clearQuery_restoresFullList() {
        onView(withId(R.id.et_search))
            .perform(typeText("ZZZZZ"), closeSoftKeyboard())
        onView(withId(R.id.tv_no_cars)).check(matches(isDisplayed()))

        onView(withId(R.id.et_search))
            .perform(clearText(), closeSoftKeyboard())
        onView(withId(R.id.rv_cars))
            .check(matches(isDisplayed()))
    }

    // TC09
    @Test
    fun sort_byRating_reordersGrid() {
        onView(withId(R.id.btn_sort)).perform(click())
        onView(withId(R.id.btn_sort_rating)).perform(click())
        onView(withId(R.id.rv_cars)).check(matches(isDisplayed()))
    }

    // TC10
    @Test
    fun sort_byCost_reordersGrid() {
        onView(withId(R.id.btn_sort)).perform(click())
        onView(withId(R.id.btn_sort_cost)).perform(click())
        onView(withId(R.id.rv_cars))
            .check(matches(hasDescendant(withText("BMW 3 Series"))))
    }
}
