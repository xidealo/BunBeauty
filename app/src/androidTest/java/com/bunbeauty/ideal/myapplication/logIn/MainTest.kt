package com.bunbeauty.ideal.myapplication.logIn

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.log_in.AuthorizationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
abstract class MainTest {

    @get:Rule
    val mActivityRule: ActivityScenarioRule<AuthorizationActivity> =
        ActivityScenarioRule(AuthorizationActivity::class.java)

    @Before
    fun setup() {
        FirebaseAuth.getInstance().signOut()

        val userReference = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child("nwy4LggmHsRMqioQOupcoeiNT0H2")

        userReference.child(User.NAME).setValue("")
        userReference.child(User.SURNAME).setValue("")
    }

    @After
    fun tearDown() {
        FirebaseAuth.getInstance().signOut()
    }

    companion object{
        const val PAUSE = 2000L
        const val LONG_PAUSE = 10000L
        const val SHORT_PAUSE = 500L
    }
}