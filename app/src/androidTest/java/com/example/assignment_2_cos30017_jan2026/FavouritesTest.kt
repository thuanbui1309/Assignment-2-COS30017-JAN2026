package com.example.assignment_2_cos30017_jan2026

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
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
class FavouritesTest {

    @get:Rule(order = 0)
    val resetRule = object : ExternalResource() {
        override fun before() { CarRepository.reset() }
    }

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // TC17
    @Test
    fun favourite_toggleOnDetail_showsSnackbar() {
        onView(withId(R.id.rv_cars)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CarGridAdapter.CarViewHolder>(0, click())
        )
        onView(withId(R.id.btn_favourite)).perform(click())
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(isDisplayed()))
    }

    // TC15
    @Test
    fun favourite_appearsInFavouritesSection() {
        onView(withId(R.id.rv_cars)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CarGridAdapter.CarViewHolder>(2, longClick())
        )
        onView(withId(R.id.rv_favourites))
            .check(matches(isDisplayed()))
            .check(matches(hasMinimumChildCount(1)))
    }

    // TC16 & TC19
    @Test
    fun favourite_untoggle_removesFromSection() {
        onView(withId(R.id.rv_cars)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CarGridAdapter.CarViewHolder>(2, longClick())
        )
        onView(withId(R.id.rv_favourites)).check(matches(isDisplayed()))

        onView(withId(R.id.rv_cars)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CarGridAdapter.CarViewHolder>(2, longClick())
        )
    }

    // TC18
    @Test
    fun favourite_longPressOnGrid_togglesFavourite() {
        onView(withId(R.id.rv_cars)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CarGridAdapter.CarViewHolder>(0, longClick())
        )
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(isDisplayed()))
    }
}
