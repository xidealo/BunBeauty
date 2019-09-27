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

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;

public class UIRegistrationTests {

    @Rule
    public ActivityTestRule<AuthorizationActivity> mActivityRule = new ActivityTestRule<>(
            AuthorizationActivity.class);

    @Before
    public void setUp() {
        FirebaseAuth.getInstance().signOut();
    }

    @Test
    public void testRegistration() throws InterruptedException {
        UIAuthorizationActivityTests uiAuthorizationTests = new UIAuthorizationActivityTests();
        uiAuthorizationTests.testEnterPhoneAuthorization("9999999999");
        uiAuthorizationTests.testEnterCodeVerify("123456");
        //for auth code
        Thread.sleep(10000);
        testEnterDataRegistration();
    }

    private void testEnterDataRegistration(){
        String name = "TestName";
        String surnameTest = "TestSurname";
        //set data
        onView(withId(R.id.nameRegistrationInput)).perform(typeText(name));
        onView(withId(R.id.surnameRegistrationInput)).perform(typeText(surnameTest));
        //close keyboard
        Espresso.closeSoftKeyboard();
        //select city in spinner
        onView(withId(R.id.citySpinnerRegistrationSpinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        //click on button
        onView(withId(R.id.saveDataRegistrationBtn)).perform(click());
    }


    @After
    public void tearDown() {
        FirebaseAuth.getInstance().signOut();
    }

}