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

    void testEnterPhoneAuthorization(String phone){
        //set phone
        onView(withId(R.id.phoneAuthInput)).perform(typeText(phone));
        //close keyboard
        Espresso.closeSoftKeyboard();
        //click on button
        onView(withId(R.id.verifyAuthBtn)).perform(click());
    }
    void testEnterCodeVerify(String code){
        //set code
        onView(withId(R.id.codeVerifyInput)).perform(typeText(code));
        //close keyboard
        Espresso.closeSoftKeyboard();
        //click on button
        onView(withId(R.id.verifyVerifyBtn)).perform(click());
    }

    @Test
    public void testVerification() throws InterruptedException {
        testEnterPhoneAuthorization("9999999999");
        testEnterCodeVerify("123456");
        Thread.sleep(2000);
    }

    @Test
    public void testVerificationWrongCode() throws InterruptedException {
        testEnterPhoneAuthorization("9999999999");
        testEnterCodeVerify("123455");
        Thread.sleep(2000);
    }

    @After
    public void tearDown() {
        FirebaseAuth.getInstance().signOut();
    }

}