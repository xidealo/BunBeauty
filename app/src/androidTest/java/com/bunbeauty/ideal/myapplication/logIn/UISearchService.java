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

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

public class UISearchService {

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
     /*   Thread.sleep(5000);
        uiAuthorizationTests.addService("TestName","123456","Test address","Test Description");
        Thread.sleep(10000);*/
    }

    @Test
    public void testSearchServiceByServiceName() throws InterruptedException {
        UIAuthorizationActivityTests uiAuthorizationTests = new UIAuthorizationActivityTests();
        uiAuthorizationTests.testEnterPhoneAuthorization("9999999999");
        uiAuthorizationTests.testEnterCodeVerify("123456");
        Thread.sleep(7000);
        UIAddingTests uiAddingTests = new UIAddingTests();
        uiAddingTests.addService("TestName", "123456", "Test address", "Test Description");
        UIBottomPanel uiBottomPanel = new UIBottomPanel();
        uiBottomPanel.goToMainScreen();
        Thread.sleep(3000);
        searchByServiceName("TestName");
        Thread.sleep(10000);
    }

    @Test
    public void testSearchServiceByUserName() throws InterruptedException {
        UIAuthorizationActivityTests uiAuthorizationTests = new UIAuthorizationActivityTests();
        uiAuthorizationTests.testEnterPhoneAuthorization("9999999999");
        uiAuthorizationTests.testEnterCodeVerify("123456");
        Thread.sleep(7000);
        UIAddingTests uiAddingTests = new UIAddingTests();
        uiAddingTests.addService("TestName", "123456", "Test address", "Test Description");
        UIBottomPanel uiBottomPanel = new UIBottomPanel();
        uiBottomPanel.goToMainScreen();
        Thread.sleep(3000);
        searchByUserName("TestName testsurname");
        Thread.sleep(10000);
    }

    @Test
    public void test() throws InterruptedException {
        UIAuthorizationActivityTests uiAuthorizationTests = new UIAuthorizationActivityTests();
        uiAuthorizationTests.testEnterPhoneAuthorization("9999999999");
        uiAuthorizationTests.testEnterCodeVerify("123456");
        Thread.sleep(7000);
        UIAddingTests uiAddingTests = new UIAddingTests();
        uiAddingTests.addService("TestName", "123456", "Test address", "Test Description");
        UIBottomPanel uiBottomPanel = new UIBottomPanel();
        uiBottomPanel.goToMainScreen();
        Thread.sleep(3000);
        searchByUserName("TestName testsurname");
        Thread.sleep(10000);
    }

    public void searchByServiceName(String serviceName) throws InterruptedException {
        //choose search
        onView(withId(R.id.multiTopPanelText)).perform(click());
        Thread.sleep(3000);
        //enter data
        onView(withId(R.id.searchLineSearchServiceInput)).perform(typeText(serviceName));
        Thread.sleep(3000);
        //search
        onView(withId(R.id.findServiceSearchServiceText)).perform(click());
        Espresso.closeSoftKeyboard();
    }

    public void searchByUserName(String userName) throws InterruptedException {
        //choose search
        onView(withId(R.id.multiTopPanelText)).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.searchBySearchServiceSpinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        Thread.sleep(3000);
        //enter data
        onView(withId(R.id.searchLineSearchServiceInput)).perform(typeText(userName));
        Thread.sleep(3000);
        //search
        onView(withId(R.id.findServiceSearchServiceText)).perform(click());
        Espresso.closeSoftKeyboard();
    }

    @After
    public void tearDown() {
        FirebaseAuth.getInstance().signOut();
    }

}