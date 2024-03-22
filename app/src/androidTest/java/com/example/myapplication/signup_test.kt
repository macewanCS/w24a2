package com.example.myapplication
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class SignupFragmentTest {

    @Test
    fun testSignupFragmentUI() {

        onView(withId(R.id.register_btn)).check(matches(isDisplayed()))
        onView(withId(R.id.FirstName)).check(matches(isDisplayed()))
        onView(withId(R.id.LastName)).check(matches(isDisplayed()))
        onView(withId(R.id.EmailAddress)).check(matches(isDisplayed()))
        onView(withId(R.id.Password)).check(matches(isDisplayed()))
        onView(withId(R.id.tutorCheckBtn)).check(matches(isDisplayed()))
        onView(withId(R.id.existingAccountLogin)).check(matches(isDisplayed()))
    }

    @Test
    fun testRegisterButtonClicked() {
        onView(withId(R.id.FirstName)).perform(typeText("John"), closeSoftKeyboard())
        onView(withId(R.id.LastName)).perform(typeText("Doe"), closeSoftKeyboard())
        onView(withId(R.id.EmailAddress)).perform(typeText("john.doe@example.com"), closeSoftKeyboard())
        onView(withId(R.id.Password)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.tutorCheckBtn)).perform(click())
        onView(withId(R.id.register_btn)).perform(click())

    }
}
