package com.android.ideal.myapplication;

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.AddingServiceInteractor;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.AuthorizationInteractor;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn.RegistrationInteractor;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.UserDao;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit main_test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public  void Check_DBData(){ assertEquals(4, 2 + 2); }

    @Test
    public  void getMillisecondsStringDate_Test(){
        assertEquals(WorkWithTimeApi.getMillisecondsStringDate("2019-05-23 16:09"),1558627740000L);
    }

    @Test
    public void getMillisecondsWithSeconds_Test(){
        assertEquals(WorkWithTimeApi.getMillisecondsStringDateWithSeconds("2019-05-23 16:09:04"),1558627744000L);
    }

    //правильное наиминование тестов
    @Test
    public void whenCutStringAAAAAReturnOneAWithThreeDots(){
        assertEquals(WorkWithStringsApi.cutString("AAAAA",1),"A...");
    }

    //logIn
    //verifyPhone
    @Test
    public void whenPhoneEqualTwelveVerifyPhone(){
        AuthorizationInteractor authorizationInteractor = new AuthorizationInteractor();
        assertTrue(authorizationInteractor.isPhoneCorrect("+79969224186"));
    }

    //Registration
    @Test
    public void whenCityNotEqualChooseCity(){
        RegistrationInteractor registrationInteractor = new RegistrationInteractor();
        assertFalse(registrationInteractor.getIsCityInputCorrect("Выбрать город"));
    }

    @Test
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
    }

    //addService
    @Test
    public void whenNameCorrectAddingService(){
        AddingServiceInteractor addingServiceInteractor= new AddingServiceInteractor();
        assertTrue(addingServiceInteractor.getIsNameInputCorrect("ывафыавдфыщалOLASKFkasdldfal"));
    }

    @Test
    public void whenNameLengthLessTwentyAddingService(){
        RegistrationInteractor registrationInteractor = new RegistrationInteractor();
        assertTrue(registrationInteractor.getIsNameLengthLessTwenty("qwertyuiopasdfghjklz"));
    }

}

