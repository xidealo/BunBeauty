package com.example.ideal.myapplication.createService.worker;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class WorkerCreateService implements IWorker {

    private static final String WORKING_DAYS = "working days";
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

        putDataInLocalStorage(serviceId, dayId,date);
        return dayId;
    }

    private void putDataInLocalStorage(String serviceId, String dayId,String date) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ID, dayId);
        contentValues.put(DBHelper.KEY_DATE_WORKING_DAYS, date);
        contentValues.put(DBHelper.KEY_SERVICE_ID_WORKING_DAYS, serviceId);

        database.insert(DBHelper.TABLE_WORKING_DAYS, null, contentValues);
    }

}
