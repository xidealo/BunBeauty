package com.bunbeauty.ideal.myapplication.logIn

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.android.ideal.myapplication.R
import org.junit.Test

class UIEditProfileTest : MainTest() {

    @Test
    @Throws(InterruptedException::class)
    fun testCreationServiceTests() {
        val uiRegistrationActivityTests = UIRegistrationActivityTests()
        uiRegistrationActivityTests.testRegistration()
        Thread.sleep(PAUSE)
        goToEditProfile()
        Thread.sleep(PAUSE)
        editProfile("EditName", "EditSurname", "Кимры")
        Thread.sleep(PAUSE)
    }

    private fun goToEditProfile() {
        onView(withId(R.id.navigation_action))
            .perform(click())
    }

    @Throws(InterruptedException::class)
    private fun editProfile(name: String, surname: String, city: String) {
        onView(withId(R.id.activity_edit_profile_et_name))
            .perform(clearText())
            .perform(typeText(name))
        Thread.sleep(SHORT_PAUSE)
        Espresso.closeSoftKeyboard()

        onView(withId(R.id.activity_edit_profile_et_surname))
            .perform(clearText())
            .perform(typeText(surname))
        Thread.sleep(SHORT_PAUSE)
        Espresso.closeSoftKeyboard()

        onView(withId(R.id.activity_edit_profile_sp_city))
            .perform(scrollTo(), click())

        onView(ViewMatchers.withText(city))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(click())
        Thread.sleep(SHORT_PAUSE)

        onView(withId(R.id.activity_edit_profile_btn_save))
            .perform(scrollTo(), click())
    }

}