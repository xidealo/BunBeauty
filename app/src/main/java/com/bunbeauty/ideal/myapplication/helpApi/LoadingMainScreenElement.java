package com.bunbeauty.ideal.myapplication.helpApi;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.DBHelper;
import com.google.firebase.database.DataSnapshot;

public class LoadingMainScreenElement {

    private static final String TAG = "DBInf";

    private static final String TAGS = "tags";


    public static void loadService(DataSnapshot servicesSnapshot, String userId, SQLiteDatabase database) {

        for (DataSnapshot serviceSnapshot : servicesSnapshot.getChildren()) {
            String serviceId = serviceSnapshot.getKey();

            ContentValues contentValues = new ContentValues();

            // Заполняем contentValues информацией о данном сервисе
            contentValues.put(DBHelper.KEY_NAME_SERVICES, String.valueOf(serviceSnapshot.child(Service.NAME).getValue()));
            contentValues.put(DBHelper.KEY_USER_ID, userId);
            contentValues.put(DBHelper.KEY_MIN_COST_SERVICES, String.valueOf(serviceSnapshot.child(Service.COST).getValue()));
            contentValues.put(DBHelper.KEY_IS_PREMIUM_SERVICES, String.valueOf(serviceSnapshot.child(Service.IS_PREMIUM).getValue()));
            contentValues.put(DBHelper.KEY_CREATION_DATE_SERVICES, String.valueOf(serviceSnapshot.child(Service.CREATION_DATE).getValue()));
            contentValues.put(DBHelper.KEY_RATING_SERVICES, String.valueOf(serviceSnapshot.child(Service.AVG_RATING).getValue()));
            contentValues.put(DBHelper.KEY_CATEGORY_SERVICES, String.valueOf(serviceSnapshot.child(Service.CATEGORY).getValue()));
            contentValues.put(DBHelper.KEY_COUNT_OF_RATES_SERVICES, String.valueOf(serviceSnapshot.child(Service.COUNT_OF_RATES).getValue()));

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

                saveTags(serviceSnapshot.child(TAGS), serviceId, database);
            }
        }
    }

    private static void saveTags(DataSnapshot tagsSnapshot, String serviceId, SQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_SERVICE_ID_TAGS, serviceId);
        for(DataSnapshot tag : tagsSnapshot.getChildren()) {
            contentValues.put(DBHelper.KEY_TAG_TAGS, tag.getValue(String.class));
            database.insert(DBHelper.TABLE_TAGS, null, contentValues);
        }
    }
}

