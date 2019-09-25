package com.bunbeauty.ideal.myapplication.logIn;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.presentation.logIn.AuthorizationActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class UIAuthorizationActivityTests {

    @Rule
    public ActivityTestRule<AuthorizationActivity> mActivityRule = new ActivityTestRule<>(
            AuthorizationActivity.class);

    @Before
    public void setUp() {
        FirebaseAuth.getInstance().signOut();
    }

    void testEnterPhoneAuthorization(){
        String phone = "9999999999";
        //set phone
        onView(withId(R.id.phoneAuthInput)).perform(typeText(phone));
        //close keyboard
        Espresso.closeSoftKeyboard();
        //click on button
        onView(withId(R.id.verifyAuthBtn)).perform(click());
    }
    void testEnterCodeVerify(){
        String code = "123456";
        //set code
        onView(withId(R.id.codeVerifyInput)).perform(typeText(code));
        //close keyboard
        Espresso.closeSoftKeyboard();
        //click on button
        onView(withId(R.id.verifyVerifyBtn)).perform(click());
    }

    @Test
    public void testRegistration(){
        testEnterPhoneAuthorization();
        testEnterCodeVerify();
    }

    @After
    public void tearDown() {
        FirebaseAuth.getInstance().signOut();
    }

}