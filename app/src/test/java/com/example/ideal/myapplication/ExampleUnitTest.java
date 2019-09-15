package com.example.ideal.myapplication;

import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.example.ideal.myapplication.helpApi.WorkWithTimeApi;

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

}

