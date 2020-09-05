package com.bunbeauty.ideal.myapplication.logIn

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.android.ideal.myapplication.R
import org.junit.Test

class UICreationServiceTests : MainTest() {

    @Test
    @Throws(InterruptedException::class)
    fun testCreationServiceTests() {
        val uiRegistrationActivityTests = UIRegistrationActivityTests()
        uiRegistrationActivityTests.testRegistration()
        Thread.sleep(PAUSE)
        goToAddingService()
        Thread.sleep(PAUSE)
        addService("Test Name", "123456", "Test Address", "Test Description", "ногти")
        Thread.sleep(PAUSE)
    }

    @Test
    @Throws(InterruptedException::class)
    fun testAddingServiceWithPremiumTests() {
        val uiAuthorizationActivityTests = UIAuthorizationActivityTests()
        uiAuthorizationActivityTests.testVerification()
        Thread.sleep(PAUSE)
        goToAddingService()
        Thread.sleep(PAUSE)
        addService("Test Name", "123456", "Test address", "Test Description", "ногти")
        Thread.sleep(PAUSE)
        addPremium("bb")
        Thread.sleep(LONG_PAUSE)
    }

    private fun goToAddingService() {
        onView(ViewMatchers.withText("Услуги"))
            .perform(click())
        onView(withId(R.id.fragment_services_btn_add_service)).perform(click())
    }

    @Throws(InterruptedException::class)
    private fun addService(
        name: String?,
        cost: String?,
        address: String?,
        description: String?,
        category: String
    ) {
        //name
        onView(withId(R.id.activity_creation_service_et_name))
            .perform(ViewActions.typeText(name))
        // cost
        onView(withId(R.id.activity_creation_service_et_cost))
            .perform(ViewActions.typeText(cost))
        //address
        onView(withId(R.id.activity_creation_service_et_address))
            .perform(ViewActions.typeText(address))
        //description
        onView(withId(R.id.activity_creation_service_et_description))
            .perform(ViewActions.typeText(description))
        Espresso.closeSoftKeyboard()
        Thread.sleep(SHORT_PAUSE)
        //category
        onView(withId(R.id.activity_creation_service_fg_category))
            .perform(scrollTo(), click())
        Thread.sleep(SHORT_PAUSE)
        onView(ViewMatchers.withText(category))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(click())
        onView(withId(R.id.activity_creation_service_btn_add_service))
            .perform(scrollTo(), click())
    }

    private fun addPremium(premiumCode: String?) {
        onView(withId(R.id.fragment_premium_et_code))
            .perform(ViewActions.typeText(premiumCode))
        Espresso.closeSoftKeyboard()
        Thread.sleep(PAUSE)
        onView(withId(R.id.fragment_premium_btn_set))
            .perform(scrollTo(), click())
    }

}