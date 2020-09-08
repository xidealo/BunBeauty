package com.bunbeauty.ideal.myapplication.logIn

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.android.ideal.myapplication.R
import org.junit.Test

class UIAuthorizationActivityTests : MainTest() {

    @Test
    @Throws(InterruptedException::class)
    fun testEnterPhone() {
        testEnterPhoneAuthorization("9999999999")
        Thread.sleep(PAUSE)
    }

    @Test
    @Throws(InterruptedException::class)
    fun testVerification() {
        testEnterPhoneAuthorization("9999999999")
        testEnterCodeVerify("123456")
        Thread.sleep(PAUSE)
    }

    @Test
    @Throws(InterruptedException::class)
    fun testVerificationWrongCode() {
        testEnterPhoneAuthorization("9999999999")
        testEnterCodeVerify("123455")
        Thread.sleep(PAUSE)
    }

    fun testEnterCodeVerify(code: String?) {
        //set code
        onView(withId(R.id.activity_verify_phone_number_et_code)).perform(typeText(code));
        //close keyboard
        Espresso.closeSoftKeyboard()
        Thread.sleep(PAUSE)
        //click on button
        onView(withId(R.id.activity_verify_phone_number_pbtn_log_in)).perform(click());
    }

    fun testEnterPhoneAuthorization(phone: String?) {
        //set phone
        onView(withId(R.id.activity_authorization_et_phone)).perform(typeText(phone))
        //close keyboard
        Espresso.closeSoftKeyboard()
        //click on button
        onView(withId(R.id.activity_authorization_pbtn_log_in)).perform(click())
    }

}