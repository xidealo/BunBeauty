package com.bunbeauty.ideal.myapplication.logIn;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.log_in.AuthorizationActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

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
        addService("TestName","123456","Test address","Test Description");
        Thread.sleep(10000);
    }

    @Test
    public void testAddingServiceWithPremiumTests() throws InterruptedException {
        UIAuthorizationActivityTests uiAuthorizationTests = new UIAuthorizationActivityTests();
        uiAuthorizationTests.testEnterPhoneAuthorization("9999999999");
        uiAuthorizationTests.testEnterCodeVerify("123456");
        Thread.sleep(5000);
        addService("TestName","123456","Test address","Test Description");
        Thread.sleep(2000);
        addPremium("bb");
        Thread.sleep(10000);
    }

    private void goToAddingService() {
        //switcher
        //onView(withId(R.id.rightSwitcherElementBtn)).perform(click());
        // button
        onView(withId(R.id.createServiceBtn)).perform(click());
    }

    void addService(String name, String cost, String address, String description) throws InterruptedException {
        goToAddingService();

        //name
        onView(withId(R.id.nameCreationServiceInput)).perform(typeText(name));
        // cost
        onView(withId(R.id.costCreationServiceInput)).perform(typeText(cost));
        //address
        onView(withId(R.id.addressCreationServiceInput)).perform(typeText(address));
        //description
        onView(withId(R.id.descriptionCreationServiceInput)).perform(typeText(description));
        //category
        onView(withId(R.id.categoryCreationServiceLayout)).perform(click());

        onData(anything()).atPosition(1).perform(click());
        Thread.sleep(2000);
        Espresso.closeSoftKeyboard();

        onView(withId(R.id.addServiceCreationServiceBtn))
                .perform(scrollTo(), click());
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