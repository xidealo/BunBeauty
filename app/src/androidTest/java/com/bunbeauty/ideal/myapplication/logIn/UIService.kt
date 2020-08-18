package com.bunbeauty.ideal.myapplication.logIn

import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.service.ServiceActivity
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
class UIService {

    companion object {
        @get:Rule
        val mActivityRule = ActivityTestRule(ServiceActivity::class.java)
    }

    @Before
    fun setUp() {
        FirebaseAuth.getInstance().signOut()
    }

    @Test (expected = InterruptedException::class)
    fun transitionToService() {
        val uiAuthorizationTests = UIAuthorizationActivityTests()
        uiAuthorizationTests.testEnterPhoneAuthorization("9999999999")
        uiAuthorizationTests.testEnterCodeVerify("123456")
        Thread.sleep(7000)
        val uiAddingTests = UIAddingTests()
        uiAddingTests.addService("TestName", "123456", "Test address", "Test Description")
        val uiBottomPanel = UIBottomPanel()
        uiBottomPanel.goToProfile()
    }
}