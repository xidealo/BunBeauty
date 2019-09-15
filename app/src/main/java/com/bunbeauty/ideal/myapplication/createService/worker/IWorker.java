package com.bunbeauty.ideal.myapplication.createService.worker;

import java.util.ArrayList;

public interface IWorker {
    String addWorkingDay(String date);
    void addTime(final String workingDaysId, final ArrayList<String> workingHours);
    void deleteTime(final String workingDaysId, final ArrayList<String> removedHours);
}
