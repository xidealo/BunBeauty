package com.example.ideal.myapplication.createService.worker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorkerCreateService implements IWorker {

    private static final String TAG = "DBInf";

    private static final String WORKING_DAYS = "working days";
    private static final String WORKING_TIME = "working time";
    private static final String TIME = "time";

    private static final String DATE = "date";
    private static final String USERS = "users";
    private static final String SERVICES = "services";

    // Добавляем рабочий день в БД
    private String userId;
    private String serviceId;
    private DBHelper dbHelper;

    public WorkerCreateService(String userId, String serviceId, DBHelper dbHelper) {
        this.userId = userId;
        this.serviceId = serviceId;
        this.dbHelper = dbHelper;
    }

    @Override
    public String addWorkingDay(String date) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dateRef = database.getReference(USERS)
                .child(userId)
                .child(SERVICES)
                .child(serviceId)
                .child(WORKING_DAYS);

        Map<String, Object> items = new HashMap<>();
        items.put(DATE, date);

        String dayId = dateRef.push().getKey();
        dateRef = dateRef.child(dayId);
        dateRef.updateChildren(items);

        //putDataDaysInLocalStorage(serviceId, dayId, date);
        return dayId;
    }

    // Добавляем время из буфера workingTime в БД
    @Override
    public void addTime(final String workingDaysId, final ArrayList<String> workingHours) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Получает время
        // Таблицы: рабочие время
        // Условия: уточняем id рабочего дня
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_TIME_WORKING_TIME
                        + " FROM "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{workingDaysId});

        for (String time : workingHours) {
            if (!WorkWithLocalStorageApi.checkTimeForWorker(workingDaysId, time, database)) {

                FirebaseDatabase fdatabase = FirebaseDatabase.getInstance();
                DatabaseReference timeRef = fdatabase.getReference(USERS)
                        .child(userId)
                        .child(SERVICES)
                        .child(serviceId)
                        .child(WORKING_DAYS)
                        .child(workingDaysId)
                        .child(WORKING_TIME);

                Map<String, Object> items = new HashMap<>();
                items.put(TIME, time);

                String timeId = timeRef.push().getKey();
                timeRef = timeRef.child(timeId);
                timeRef.updateChildren(items);

                //putDataTimeInLocalStorage(timeId, time, workingDaysId);
            }
        }
        cursor.close();
    }

    @Override
    public void deleteTime(final String workingDaysId, final ArrayList<String> removedHours) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //получаю время кнопки, на которую нажал
        //получаем все время этого дня
        final DatabaseReference timeRef = database.getReference(USERS)
                .child(userId)
                .child(SERVICES)
                .child(serviceId)
                .child(WORKING_DAYS)
                .child(workingDaysId)
                .child(WORKING_TIME);

        timeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot timesSnapshot) {
                for (String hours : removedHours) {
                    for (DataSnapshot time : timesSnapshot.getChildren()) {
                        if (String.valueOf(time.child(TIME).getValue()).equals(hours)) {
                            String timeId = String.valueOf(time.getKey());
                            timeRef.child(timeId).removeValue();
                        }
                    }
                }
                removedHours.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}

