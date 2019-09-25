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
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class UIAddingTests {

    @Rule
    public ActivityTestRule<AuthorizationActivity> mActivityRule = new ActivityTestRule<>(
            AuthorizationActivity.class);

    @Before
    public void setUp() {
        FirebaseAuth.getInstance().signOut();
    }

    @Test
    public void testAddingServiceTests() throws InterruptedException {
        UIAuthorizationActivityTests uiAuthorizationTests = new UIAuthorizationActivityTests();
        uiAuthorizationTests.testEnterPhoneAuthorization();
        uiAuthorizationTests.testEnterCodeVerify();
        //for auth code
        Thread.sleep(10000);
        goToAddingService();
        addService();
    }

    void goToAddingService() {
        //switcher
        onView(withId(R.id.rightSwitcherElementBtn)).perform(click());
        // button
        onView(withId(R.id.addServicesProfileBtn)).perform(click());
    }

    void addService() {
        //name
        String name = "TestName";
        onView(withId(R.id.nameAddServiceInput)).perform(typeText(name));
        // cost
        String cost = "123456";
        onView(withId(R.id.costAddServiceInput)).perform(typeText(cost));
        //address
        String address = "Test address";
        onView(withId(R.id.addressAddServiceInput)).perform(typeText(address));
        //description
        String description = "Test Description";
        onView(withId(R.id.descriptionAddServiceInput)).perform(typeText(description));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.scrollViewAddService)).perform(scrollTo());
        //onView(withId(R.id.addServiceAddServiceBtn)).perform(click());
    }

    @After
    public void tearDown() {
        FirebaseAuth.getInstance().signOut();
    }

}