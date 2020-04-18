package com.android.ideal.myapplication;

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServiceInteractor;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.PhotoRepository;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.TagRepository;

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
public class AddingServiceUnitTests {

    @Mock
    ServiceRepository serviceRepository;
    @Mock
    TagRepository tagRepository;
    @Mock
    PhotoRepository photoRepository;

    //addService
    @Test
    public void whenNameCorrectAddingService() {
        CreationServiceInteractor creationServiceInteractor = getAddingServiceInstance();
        assertTrue(creationServiceInteractor.getIsNameInputCorrect("ывафыавдфыщалOLASKFkasdldfal"));
    }

    @Test
    public void whenNameLengthLessTwentyAddingService() {
        CreationServiceInteractor creationServiceInteractor = getAddingServiceInstance();
        assertTrue(creationServiceInteractor.getIsNameLengthLessTwenty("qwertyuiopasdfghjkl"));
    }

    @Test
    public void whenDescriptionLengthLessTwoHundredAddingService() {
        CreationServiceInteractor creationServiceInteractor = getAddingServiceInstance();
        assertTrue(creationServiceInteractor.getIsDescriptionLengthLessTwoHundred("qwertyuiopasdfghjkl"));
    }

    @Test
    public void whenCostCorrectAddingService() {
        CreationServiceInteractor creationServiceInteractor = getAddingServiceInstance();
        assertTrue(creationServiceInteractor.getIsCostInputCorrect("23343564564"));
    }

    @Test
    public void whenCostLengthLessTenAddingService() {
        CreationServiceInteractor creationServiceInteractor = getAddingServiceInstance();
        assertTrue(creationServiceInteractor.getIsCostLengthLessTen("123456789"));
    }

    @Test
    public void whenCategoryCorrectAddingService() {
        CreationServiceInteractor creationServiceInteractor = getAddingServiceInstance();
        assertTrue(creationServiceInteractor.getIsCategoryInputCorrect("yopyoyo"));
    }

    @Test
    public void whenAddressCorrectAddingService() {
        CreationServiceInteractor creationServiceInteractor = getAddingServiceInstance();
        assertTrue(creationServiceInteractor.getIsAddressInputCorrect("ljfngjf ngjafng32234SKJDj"));
    }

    @Test
    public void whenAddressLengthLessThirtyAddingService() {
        CreationServiceInteractor creationServiceInteractor = getAddingServiceInstance();
        assertTrue(creationServiceInteractor.getIsAddressLengthThirty("1234gfcgv567891234567890"));
    }

    private CreationServiceInteractor getAddingServiceInstance() {
        return new CreationServiceInteractor(serviceRepository, tagRepository, photoRepository);
    }
}

