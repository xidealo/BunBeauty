package com.bunbeauty.ideal.myapplication.logIn;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import com.android.ideal.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;

public class UITests {

    @Rule
    public ActivityTestRule<Authorization> mActivityRule = new ActivityTestRule<>(
            Authorization.class);

    @Before
    public void setUp() throws Exception {
        FirebaseAuth.getInstance().signOut();
    }

   @Test
    public void testEnterPhoneAuthorization(){
        String phone = "9999999999";
        //set phone
        Espresso.onView(withId(R.id.phoneAuthInput)).perform(typeText(phone));
        //close keyboard
        Espresso.closeSoftKeyboard();
        //click on button
        Espresso.onView(withId(R.id.verifyAuthBtn)).perform(click());
    }

    @Test
    public void testEnterCodeVerify(){
        String code = "123456";
        //set code
        Espresso.onView(withId(R.id.codeVerifyInput)).perform(typeText(code));
        //close keyboard
        Espresso.closeSoftKeyboard();
        //click on button
        Espresso.onView(withId(R.id.verifyVerifyBtn)).perform(click());
    }

    @Test
    public void testEnterDataRegistration(){
        String name = "TestName";
        String surnameTest = "TestSurname";
        //set data
        Espresso.onView(withId(R.id.nameRegistrationInput)).perform(typeText(name));
        Espresso.onView(withId(R.id.surnameRegistrationInput)).perform(typeText(surnameTest));
        //select city in spinner
        Espresso.onData(allOf(is(instanceOf(String.class)), is("дубна")))
                .perform(click());
        Espresso.onView(withId(R.id.citySpinnerRegistrationSpinner))
                .check(matches(withSpinnerText(containsString("дубна"))));

       //close keyboard
        Espresso.closeSoftKeyboard();
        //click on button
        Espresso.onView(withId(R.id.saveDataRegistrationBtn)).perform(click());
    }

    @Test
    public void testVerifyPhone(){
        testEnterPhoneAuthorization();
        testEnterCodeVerify();
    }

    @Test
    public void testRegistration() throws InterruptedException {
        testEnterPhoneAuthorization();
        testEnterCodeVerify();
        Thread.sleep(5000);
        testEnterDataRegistration();
    }


    @After
    public void tearDown() throws Exception {
        FirebaseAuth.getInstance().signOut();
    }

}