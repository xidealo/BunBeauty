package com.android.ideal.myapplication;

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.AddingServiceInteractor;
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
        AddingServiceInteractor addingServiceInteractor = getAddingServiceInstance();
        assertTrue(addingServiceInteractor.getIsNameInputCorrect("ывафыавдфыщалOLASKFkasdldfal"));
    }

    @Test
    public void whenNameLengthLessTwentyAddingService() {
        AddingServiceInteractor addingServiceInteractor = getAddingServiceInstance();
        assertTrue(addingServiceInteractor.getIsNameLengthLessTwenty("qwertyuiopasdfghjkl"));
    }

    private AddingServiceInteractor getAddingServiceInstance() {
        return new AddingServiceInteractor(serviceRepository, tagRepository, photoRepository);
    }
}

