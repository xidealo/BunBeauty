package com.android.ideal.myapplication;

import android.content.Intent;

import com.bunbeauty.ideal.myapplication.clean_architecture.domain.log_in.RegistrationUserInteractor;
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.UserRepository;

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
        RegistrationUserInteractor registrationUserInteractor = getRegistrationInstance();
        assertFalse(registrationUserInteractor.getIsCityInputCorrect("Выбрать город"));
    }

    @Test
    public void whenNameCorrectRegistration() {
        RegistrationUserInteractor registrationUserInteractor = getRegistrationInstance();
        assertTrue(registrationUserInteractor.getIsNameInputCorrect("ЫВАЫФЫФфываывфлфвалпуцкльвап"));
    }

    @Test
    public void whenNameLengthLessTwentyRegistration() {
        RegistrationUserInteractor registrationUserInteractor = getRegistrationInstance();
        assertTrue(registrationUserInteractor.getIsNameLengthLessTwenty("qwertyuiopasdfghjklz"));
    }

    @Test
    public void whenSurnameCorrectRegistration() {
        RegistrationUserInteractor registrationUserInteractor = getRegistrationInstance();
        assertTrue(registrationUserInteractor.getIsSurnameInputCorrect("dasfjdfgdlsлывальвыалплд"));
    }

    @Test
    public void whenSurnameLengthLessTwentyRegistration() {
        RegistrationUserInteractor registrationUserInteractor = getRegistrationInstance();
        assertTrue(registrationUserInteractor.getIsSurnameLengthLessTwenty("qwertyuiopasdfghjklz"));
    }

    private RegistrationUserInteractor getRegistrationInstance() {
        return new RegistrationUserInteractor(userRepository, intent);
    }
}

