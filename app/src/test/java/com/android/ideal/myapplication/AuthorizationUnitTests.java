package com.android.ideal.myapplication;

import android.content.Intent;

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertTrue;

/**
 * Example local unit main_test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthorizationUnitTests {

    @Mock
    UserRepository userRepository;
    @Mock
    Intent intent;

    //verifyPhone
    @Test
    public void whenPhoneEqualTwelveVerifyPhone(){
        AuthorizationInteractor authorizationInteractor = getAuthorizationInstance();
        assertTrue(authorizationInteractor.isPhoneCorrect("+79969224186"));
    }


    private AuthorizationInteractor getAuthorizationInstance() {
        return new AuthorizationInteractor(userRepository, intent);
    }
}

