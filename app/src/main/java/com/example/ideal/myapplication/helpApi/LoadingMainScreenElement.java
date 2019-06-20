package com.example.ideal.myapplication.helpApi;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.ideal.myapplication.fragments.objects.Photo;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class LoadingMainScreenElement {

    private static final String TAG = "DBInf";

    private static final String COST = "cost";
    private static final String IS_PREMIUM = "is premium";
    private static final String CREATION_DATE = "creation date";
    private static final String CATEGORY = "category";

    private static final String NAME = "name";


    public static void loadService(DataSnapshot servicesSnapshot, String userId, SQLiteDatabase database) {

        for (DataSnapshot serviceSnapshot : servicesSnapshot.getChildren()) {
            String serviceId = serviceSnapshot.getKey();

            ContentValues contentValues = new ContentValues();
            // Заполняем contentValues информацией о данном сервисе

            contentValues.put(DBHelper.KEY_NAME_SERVICES, String.valueOf(serviceSnapshot.child(NAME).getValue()));
            contentValues.put(DBHelper.KEY_USER_ID, userId);
            contentValues.put(DBHelper.KEY_MIN_COST_SERVICES, String.valueOf(serviceSnapshot.child(COST).getValue()));
            contentValues.put(DBHelper.KEY_IS_PREMIUM_SERVICES, String.valueOf(serviceSnapshot.child(IS_PREMIUM).getValue()));
            contentValues.put(DBHelper.KEY_CREATION_DATE_SERVICES, String.valueOf(serviceSnapshot.child(CREATION_DATE).getValue()));
            contentValues.put(DBHelper.KEY_CATEGORY_SERVICES, String.valueOf(serviceSnapshot.child(CATEGORY).getValue()));

            boolean hasSomeData = WorkWithLocalStorageApi
                    .hasSomeData(DBHelper.TABLE_CONTACTS_SERVICES, serviceId);

            if (hasSomeData) {
                database.update(
                        DBHelper.TABLE_CONTACTS_SERVICES,
                        contentValues,
                        DBHelper.KEY_ID + " = ?",
                        new String[]{serviceId});
            } else {
                contentValues.put(DBHelper.KEY_ID, serviceId);
                database.insert(DBHelper.TABLE_CONTACTS_SERVICES, null, contentValues);
            }
        }
    }
}

