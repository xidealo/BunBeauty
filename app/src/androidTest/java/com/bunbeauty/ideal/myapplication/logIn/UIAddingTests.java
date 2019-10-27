package com.bunbeauty.ideal.myapplication.logIn;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn.AuthorizationActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

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
        uiAuthorizationTests.testEnterPhoneAuthorization("9999999999");
        uiAuthorizationTests.testEnterCodeVerify("123456");
        Thread.sleep(5000);
        goToAddingService();
        addService();
        Thread.sleep(10000);
    }

    @Test
    public void testAddingServiceWithPremiumTests() throws InterruptedException {
        UIAuthorizationActivityTests uiAuthorizationTests = new UIAuthorizationActivityTests();
        uiAuthorizationTests.testEnterPhoneAuthorization("9999999999");
        uiAuthorizationTests.testEnterCodeVerify("123456");
        Thread.sleep(5000);
        goToAddingService();
        addService();
        Thread.sleep(2000);
        addPremium("bb");
        Thread.sleep(10000);
    }

    private void goToAddingService() {
        //switcher
        onView(withId(R.id.rightSwitcherElementBtn)).perform(click());
        // button
        onView(withId(R.id.addServicesProfileBtn)).perform(click());
    }

    void addService() throws InterruptedException {
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
        Thread.sleep(2000);

        Espresso.closeSoftKeyboard();
        onView(withId(R.id.addServiceAddServiceBtn)).perform(click());
    }

    void addPremium(String premiumCode) throws InterruptedException {
        onView(withId(R.id.codePremiumElement)).perform(typeText(premiumCode));
        Thread.sleep(1000);

        onView(withId(R.id.setPremiumPremiumElementBtn)).perform(click());
    }

    @After
    public void tearDown() {
        FirebaseAuth.getInstance().signOut();
    }

}