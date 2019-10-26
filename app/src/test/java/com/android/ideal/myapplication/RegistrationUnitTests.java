package com.android.ideal.myapplication;

import android.content.Context;
import android.content.Intent;

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationInteractor;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.UserFirebaseApi;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dbInstance.LocalDatabase;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * Example local unit main_test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RegistrationUnitTests {
    //Registration
    private UserRepository getUserRepositoryMock() {
        return new UserRepository(LocalDatabase.Companion.getDatabase((Context) new Object()).getUserDao(), new UserFirebaseApi());
    }

    @Test
    public void whenCityNotEqualChooseCity() {
        RegistrationInteractor registrationInteractor = new RegistrationInteractor(getUserRepositoryMock(), new Intent());
        assertFalse(registrationInteractor.getIsCityInputCorrect("Выбрать город"));
    }

    /*@Test
    public void whenNameCorrectRegistration(){
        RegistrationInteractor registrationInteractor = new RegistrationInteractor();
        assertTrue(registrationInteractor.getIsNameInputCorrect("ЫВАЫФЫФфываывфлфвалпуцкльвап"));
    }

    @Test
    public void whenNameLengthLessTwentyRegistration(){
        RegistrationInteractor registrationInteractor = new RegistrationInteractor();
        assertTrue(registrationInteractor.getIsNameLengthLessTwenty("qwertyuiopasdfghjklz"));
    }


    @Test
    public void whenSurnameCorrectRegistration(){
        RegistrationInteractor registrationInteractor = new RegistrationInteractor();
        assertTrue(registrationInteractor.getIsSurnameInputCorrect("dasfjdfgdlsлывальвыалплд"));
    }

    @Test
    public void whenSurnameLengthLessTwentyRegistration(){
        RegistrationInteractor registrationInteractor = new RegistrationInteractor();
        assertTrue(registrationInteractor.getIsSurnameLengthLessTwenty("qwertyuiopasdfghjklz"));
    }*/
}

