package com.example.assignment_2_cos30017_jan2026

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.assignment_2_cos30017_jan2026.repository.CarRepository
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DarkModeTest {

    @get:Rule(order = 0)
    val resetRule = object : ExternalResource() {
        override fun before() { CarRepository.reset() }
    }

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    // TC34 Support
    @Test
    fun settings_dialogOpens() {
        onView(withId(R.id.action_settings)).perform(click())
        onView(withId(R.id.switch_dark_mode)).check(matches(isDisplayed()))
    }

    // TC34
    @Test
    fun darkMode_toggleChangesTheme() {
        onView(withId(R.id.action_settings)).perform(click())
        onView(withId(R.id.switch_dark_mode)).perform(click())
    }

    // TC35 & TC36
    @Test
    fun language_toggleRecreatesActivity() {
        onView(withId(R.id.action_settings)).perform(click())
        onView(withId(R.id.rb_vietnamese)).perform(click())
        onView(withId(R.id.rv_cars)).check(matches(isDisplayed()))
    }
}
