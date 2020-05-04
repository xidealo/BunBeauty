package com.android.ideal.myapplication;

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServiceServiceServiceInteractor;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.PhotoRepository;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.ServiceRepository;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.TagRepository;

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
        CreationServiceServiceServiceInteractor creationServiceInteractor = getAddingServiceInstance();
        assertTrue(creationServiceInteractor.getIsNameInputCorrect("ывафыавдфыщалOLASKFkasdldfal"));
    }

    @Test
    public void whenNameLengthLessTwentyAddingService() {
        CreationServiceServiceServiceInteractor creationServiceInteractor = getAddingServiceInstance();
        assertTrue(creationServiceInteractor.getIsNameLengthLessTwenty("qwertyuiopasdfghjkl"));
    }

    @Test
    public void whenDescriptionLengthLessTwoHundredAddingService() {
        CreationServiceServiceServiceInteractor creationServiceInteractor = getAddingServiceInstance();
        assertTrue(creationServiceInteractor.getIsDescriptionLengthLessTwoHundred("qwertyuiopasdfghjkl"));
    }

    @Test
    public void whenCostCorrectAddingService() {
        CreationServiceServiceServiceInteractor creationServiceInteractor = getAddingServiceInstance();
        assertTrue(creationServiceInteractor.getIsCostInputCorrect("23343564564"));
    }

    @Test
    public void whenCostLengthLessTenAddingService() {
        CreationServiceServiceServiceInteractor creationServiceInteractor = getAddingServiceInstance();
        assertTrue(creationServiceInteractor.getIsCostLengthLessTen("123456789"));
    }

    @Test
    public void whenCategoryCorrectAddingService() {
        CreationServiceServiceServiceInteractor creationServiceInteractor = getAddingServiceInstance();
        assertTrue(creationServiceInteractor.getIsCategoryInputCorrect("yopyoyo"));
    }

    @Test
    public void whenAddressCorrectAddingService() {
        CreationServiceServiceServiceInteractor creationServiceInteractor = getAddingServiceInstance();
        assertTrue(creationServiceInteractor.getIsAddressInputCorrect("ljfngjf ngjafng32234SKJDj"));
    }

    @Test
    public void whenAddressLengthLessThirtyAddingService() {
        CreationServiceServiceServiceInteractor creationServiceInteractor = getAddingServiceInstance();
        assertTrue(creationServiceInteractor.getIsAddressLengthThirty("1234gfcgv567891234567890"));
    }

    private CreationServiceServiceServiceInteractor getAddingServiceInstance() {
        return new CreationServiceServiceServiceInteractor(serviceRepository, tagRepository, photoRepository);
    }
}

