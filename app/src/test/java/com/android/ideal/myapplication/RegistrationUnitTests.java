package com.android.ideal.myapplication;

import android.content.Intent;

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationInteractor;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.UserRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Example local unit main_test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(MockitoJUnitRunner.class)
public class RegistrationUnitTests {

    @Mock
    UserRepository userRepository;

    @Mock
    Intent intent;

    @Test
    public void whenCityNotEqualChooseCity() {
        RegistrationInteractor registrationInteractor = getRegistrationInstance();
        assertFalse(registrationInteractor.getIsCityInputCorrect("Выбрать город"));
    }

    @Test
    public void whenNameCorrectRegistration() {
        RegistrationInteractor registrationInteractor = getRegistrationInstance();
        assertTrue(registrationInteractor.getIsNameInputCorrect("ЫВАЫФЫФфываывфлфвалпуцкльвап"));
    }

    @Test
    public void whenNameLengthLessTwentyRegistration() {
        RegistrationInteractor registrationInteractor = getRegistrationInstance();
        assertTrue(registrationInteractor.getIsNameLengthLessTwenty("qwertyuiopasdfghjklz"));
    }

    @Test
    public void whenSurnameCorrectRegistration() {
        RegistrationInteractor registrationInteractor = getRegistrationInstance();
        assertTrue(registrationInteractor.getIsSurnameInputCorrect("dasfjdfgdlsлывальвыалплд"));
    }

    @Test
    public void whenSurnameLengthLessTwentyRegistration() {
        RegistrationInteractor registrationInteractor = getRegistrationInstance();
        assertTrue(registrationInteractor.getIsSurnameLengthLessTwenty("qwertyuiopasdfghjklz"));
    }

    private RegistrationInteractor getRegistrationInstance() {
        return new RegistrationInteractor(userRepository, intent);
    }
}

