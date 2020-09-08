package com.bunbeauty.ideal.myapplication.logIn;

import androidx.test.rule.ActivityTestRule;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.log_in.AuthorizationActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class UIBottomPanel {

  /*  @Rule
    public ActivityTestRule<AuthorizationActivity> mActivityRule = new ActivityTestRule<>(
            AuthorizationActivity.class);

    @Before
    public void setUp() {
        FirebaseAuth.getInstance().signOut();
    }

    @Test
    public void testBottomPanel() throws InterruptedException {
        UIAuthorizationActivityTests uiAuthorizationTests = new UIAuthorizationActivityTests();
        uiAuthorizationTests.testEnterPhoneAuthorization("9999999999");
        uiAuthorizationTests.testEnterCodeVerify("123456");
        Thread.sleep(7000);
        goToMainScreen();
        Thread.sleep(3000);
        goToProfile();
        Thread.sleep(3000);
        goToChat();
        Thread.sleep(3000);
    }

    public void goToProfile(){
        //onView(withId(R.id.profileBottomPanelText)).perform(click());
    }

    public void goToMainScreen(){
        //onView(withId(R.id.mainScreenBottomPanelText)).perform(click());
    }

    public void goToChat(){
        //onView(withId(R.id.chatBottomPanelText)).perform(click());
    }

    @After
    public void tearDown() {
        FirebaseAuth.getInstance().signOut();
    }*/

}