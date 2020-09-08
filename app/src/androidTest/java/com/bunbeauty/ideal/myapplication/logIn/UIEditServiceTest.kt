package com.bunbeauty.ideal.myapplication.logIn

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.ProfileServiceAdapter
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Test


class UIEditServiceTest : MainTest() {
    private val expectedName = "Edit Name"
    private val expectedCost = "654321"
    private val expectedAddress = "Edit Address"
    private val expectedDescription = "Edit Description"

    @Test
    @Throws(InterruptedException::class)
    fun testEditServiceTests() {
        val uiCreationServiceTests = UICreationServiceTests()
        uiCreationServiceTests.testCreationServiceTests()
        Thread.sleep(PAUSE)
        goToEditService()
        Thread.sleep(PAUSE)
        editService(expectedName, expectedCost, expectedAddress, expectedDescription, "брови")
        Thread.sleep(PAUSE)

        /* onView(withId(R.id.activity_service_tv_cost))
             .check(matches(hasValueEqualTo(expectedName)))*/
        /*     (onView(withId(R.id.menu_top_panel_tv_title)) as TextView).text.toString(),
             (onView(withId(R.id.activity_service_tv_cost)) as TextView).text.toString(),
             (onView(withId(R.id.activity_service_tv_address)) as TextView).text.toString(),
             (onView(withId(R.id.activity_service_tv_description)) as TextView).text.toString()*/
    }

    @Test
    @Throws(InterruptedException::class)
    fun testDeleteServiceTests() {
        val uiCreationServiceTests = UICreationServiceTests()
        uiCreationServiceTests.testCreationServiceTests()
        Thread.sleep(PAUSE)
        goToEditService()
        Thread.sleep(PAUSE)
        deleteService()
        Thread.sleep(PAUSE)
        onView(withText("Услуги"))
            .perform(click())
        Thread.sleep(PAUSE)
    }

    private fun hasValueEqualTo(content: String) = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) {
            description?.appendText("The TextView/EditText has value")
        }

        override fun matchesSafely(view: View?): Boolean {
            if (view !is TextView && view !is EditText) {
                return false
            }
            val text: String = if (view is TextView) {
                view.text.toString()
            } else {
                (view as EditText).text.toString()
            }
            return text.equals(content, ignoreCase = true)
        }
    }


    private fun goToEditService() {
        onView(withId(R.id.navigation_profile)).perform(click())
        Thread.sleep(SHORT_PAUSE)
        onView(withText("Услуги"))
            .perform(click())
        Thread.sleep(PAUSE)

        onView(withId(R.id.fragment_services_rv_list))
            .perform(
                actionOnItemAtPosition<ProfileServiceAdapter.ProfileServiceViewHolder>(
                    0,
                    click()
                )
            )

        Thread.sleep(PAUSE)
        onView(withId(R.id.navigation_action))
            .perform(click())
    }

    @Throws(InterruptedException::class)
    private fun editService(
        name: String,
        cost: String,
        address: String,
        description: String,
        category: String
    ) {
        //name
        onView(withId(R.id.activity_edit_service_et_name))
            .perform(clearText())
            .perform(typeText(name))
        // cost
        onView(withId(R.id.activity_edit_service_et_cost))
            .perform(clearText())
            .perform(typeText(cost))
        //address
        onView(withId(R.id.activity_edit_service_et_address))
            .perform(clearText())
            .perform(typeText(address))
        //description
        onView(withId(R.id.activity_edit_service_et_description))
            .perform(clearText())
            .perform(typeText(description))
        Espresso.closeSoftKeyboard()
        Thread.sleep(SHORT_PAUSE)
        //category
        onView(withId(R.id.fragment_category_sp_category))
            .perform(scrollTo(), click())
        Thread.sleep(SHORT_PAUSE)
        onView(withText(category))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(click())
        Thread.sleep(SHORT_PAUSE)
        onView(withId(R.id.activity_edit_service_btn_save))
            .perform(scrollTo(), click())
    }

    private fun deleteService() {
        onView(withId(R.id.activity_edit_service_btn_delete))
            .perform(scrollTo(), click())

        onView(withText("Удалить")).perform(click());
    }

    private fun checkEditData(
        realName: String,
        realCost: String,
        realAddress: String,
        realDescription: String
    ) {
        if (realName != expectedName) {
            throw Exception("Не совпадает имя")
        }
        if (realCost != expectedCost) {
            throw Exception("Не совпадает цена")
        }
        if (realAddress != expectedAddress) {
            throw Exception("Не совпадает адрес")
        }
        if (realDescription != expectedDescription) {
            throw Exception("Не совпадает описание")
        }
    }
}