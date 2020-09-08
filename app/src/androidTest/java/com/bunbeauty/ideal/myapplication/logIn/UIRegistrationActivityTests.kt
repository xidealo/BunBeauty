package com.bunbeauty.ideal.myapplication.logIn

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.android.ideal.myapplication.R
import org.junit.Test

class UIRegistrationActivityTests : MainTest() {

    @Test
    @Throws(InterruptedException::class)
    fun testRegistration() {
        val uiAuthorizationTests = UIAuthorizationActivityTests()
        uiAuthorizationTests.testEnterPhoneAuthorization("9999999999")
        uiAuthorizationTests.testEnterCodeVerify("123456")
        Thread.sleep(PAUSE)
        testEnterDataRegistration("TestName", "TestSurname", "Дубна")
        Thread.sleep(PAUSE)
    }

    private fun testEnterDataRegistration(name: String, surname: String, city: String) {
        //set data
        onView(withId(R.id.activity_registration_et_name))
            .perform(ViewActions.typeText(name))
        Espresso.closeSoftKeyboard()
        Thread.sleep(SHORT_PAUSE)
        onView(withId(R.id.activity_registration_et_surname))
            .perform(ViewActions.typeText(surname))
        Thread.sleep(SHORT_PAUSE)
        //close keyboard
        Espresso.closeSoftKeyboard()
        //select city in spinner
        onView(withId(R.id.activity_registration_sp_city)).perform(click())

        onView(withText(city))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(click())
        //click on button
        onView(withId(R.id.activity_registration_pbtn_register)).perform(click());
    }

}