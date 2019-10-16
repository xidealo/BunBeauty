package com.bunbeauty.ideal.myapplication.helpApi;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Photo;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.DBHelper;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service;
import com.google.firebase.database.DataSnapshot;

public class LoadingGuestServiceData {

    private static final String TAG = "DBInf";

    private static final String USER_ID = "user id";
    private static final String TIME = "time";
    private static final String DATE = "date";
    private static final String IS_CANCELED = "is canceled";
    private static final String IS_BLOCKED = "is blocked";

    //PHOTOS
    private static final String PHOTO_LINK = "photo link";

    public static void addServiceInfoInLocalStorage(final DataSnapshot serviceSnapshot, final SQLiteDatabase localDatabase) {
        final String serviceId = serviceSnapshot.getKey();

        ContentValues contentValues = new ContentValues();
        // Заполняем contentValues информацией о данном сервисе

        contentValues.put(DBHelper.KEY_NAME_SERVICES, String.valueOf(serviceSnapshot.child(Service.NAME).getValue()));
        contentValues.put(DBHelper.KEY_DESCRIPTION_SERVICES, String.valueOf(serviceSnapshot.child(Service.DESCRIPTION).getValue()));
        contentValues.put(DBHelper.KEY_MIN_COST_SERVICES, String.valueOf(serviceSnapshot.child(Service.COST).getValue()));
        contentValues.put(DBHelper.KEY_RATING_SERVICES, String.valueOf(serviceSnapshot.child(Service.AVG_RATING).getValue()));
         //contentValues.put(DBHelper.KEY_IS_PREMIUM_SERVICES, String.valueOf(serviceSnapshot.child(Service.IS_PREMIUM).getValue()));
        contentValues.put(DBHelper.KEY_CATEGORY_SERVICES, String.valueOf(serviceSnapshot.child(Service.CATEGORY).getValue()));
        contentValues.put(DBHelper.KEY_ADDRESS_SERVICES, String.valueOf(serviceSnapshot.child(Service.ADDRESS).getValue()));
        contentValues.put(DBHelper.KEY_COUNT_OF_RATES_SERVICES, String.valueOf(serviceSnapshot.child(Service.COUNT_OF_RATES).getValue()));
        Log.d(TAG, "COUNT_OF_RATES: " + serviceSnapshot.child(Service.COUNT_OF_RATES).getValue());
        boolean hasSomeData = WorkWithLocalStorageApi
                .hasSomeData(DBHelper.TABLE_CONTACTS_SERVICES, serviceId);

        // Проверка есть ли такой сервис в SQLite
        if (hasSomeData) {
            // Данный сервис уже есть
            // Обновляем информацию о нём
            localDatabase.update(
                    DBHelper.TABLE_CONTACTS_SERVICES,
                    contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{serviceId});
            Log.d(TAG, "Update");
        } else {
            // Данного сервиса нет
            // Добавляем serviceId в contentValues
            contentValues.put(DBHelper.KEY_ID, serviceId);
            // Добавляем данный сервис в SQLite
            localDatabase.insert(DBHelper.TABLE_CONTACTS_SERVICES, null, contentValues);
        }
    }

    public static void addWorkingDaysInLocalStorage(DataSnapshot workingDaySnapshot, String serviceId, SQLiteDatabase localDatabase) {
            ContentValues contentValues = new ContentValues();
            String dayId = workingDaySnapshot.getKey();
            String date = String.valueOf(workingDaySnapshot.child(DATE).getValue());
            contentValues.put(DBHelper.KEY_DATE_WORKING_DAYS, date);
            contentValues.put(DBHelper.KEY_SERVICE_ID_WORKING_DAYS, serviceId);

            boolean hasSomeData = WorkWithLocalStorageApi
                    .hasSomeData(DBHelper.TABLE_WORKING_DAYS, dayId);
            if (hasSomeData) {
                localDatabase.update(DBHelper.TABLE_WORKING_DAYS, contentValues,
                        DBHelper.KEY_ID + " = ?",
                        new String[]{dayId});
            } else {
                contentValues.put(DBHelper.KEY_ID, dayId);
                localDatabase.insert(DBHelper.TABLE_WORKING_DAYS, null, contentValues);
            }
    }

    public static void addTimeInLocalStorage(DataSnapshot timeSnapshot, String workingDayId, SQLiteDatabase localDatabase) {
            ContentValues contentValues = new ContentValues();
            String timeId = timeSnapshot.getKey();
            contentValues.put(DBHelper.KEY_TIME_WORKING_TIME, String.valueOf(timeSnapshot.child(TIME).getValue()));
            contentValues.put(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME, workingDayId);
            contentValues.put(DBHelper.KEY_IS_BLOCKED_TIME, String.valueOf(timeSnapshot.child(IS_BLOCKED).getValue()));
            boolean hasSomeData = WorkWithLocalStorageApi
                    .hasSomeData(DBHelper.TABLE_WORKING_TIME, timeId);

            if (hasSomeData) {
                localDatabase.update(DBHelper.TABLE_WORKING_TIME, contentValues,
                        DBHelper.KEY_ID + " = ?",
                        new String[]{timeId});
            } else {
                contentValues.put(DBHelper.KEY_ID, timeId);
                localDatabase.insert(DBHelper.TABLE_WORKING_TIME, null, contentValues);
            }
    }

    public static void loadPhotosByServiceId(DataSnapshot photosSnapshot, String serviceId, SQLiteDatabase localDatabase) {
        for (DataSnapshot fPhoto : photosSnapshot.getChildren()) {
            Photo photo = new Photo();

            photo.setPhotoId(fPhoto.getKey());
            photo.setPhotoLink(String.valueOf(fPhoto.child(PHOTO_LINK).getValue()));
            photo.setPhotoOwnerId(serviceId);

            addPhotoInLocalStorage(photo, localDatabase);
        }
    }

    private static void addPhotoInLocalStorage(Photo photo, SQLiteDatabase localDatabase) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_ID, photo.getPhotoId());
        contentValues.put(DBHelper.KEY_PHOTO_LINK_PHOTOS, photo.getPhotoLink());
        contentValues.put(DBHelper.KEY_OWNER_ID_PHOTOS, photo.getPhotoOwnerId());

        boolean isUpdate = WorkWithLocalStorageApi
                .hasSomeData(DBHelper.TABLE_PHOTOS,
                        photo.getPhotoId());

        if (isUpdate) {
            localDatabase.update(DBHelper.TABLE_PHOTOS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{photo.getPhotoId()});
        } else {
            contentValues.put(DBHelper.KEY_ID, photo.getPhotoId());
            localDatabase.insert(DBHelper.TABLE_PHOTOS, null, contentValues);
        }
    }

    static public void deleteTimeFromLocalStorage(final String timeId,SQLiteDatabase localDatabase) {
        localDatabase.delete(
                DBHelper.TABLE_WORKING_TIME,
                DBHelper.KEY_ID + " = ? ",
                new String[]{timeId});
    }
    static public void addOrderInLocalStorage(final DataSnapshot orderSnapshot, String timeId, SQLiteDatabase localDatabase){
            ContentValues contentValues = new ContentValues();
            String orderId = orderSnapshot.getKey();

            contentValues.put(DBHelper.KEY_ID, orderId);
            contentValues.put(DBHelper.KEY_IS_CANCELED_ORDERS, String.valueOf(orderSnapshot.child(IS_CANCELED).getValue()));
            contentValues.put(DBHelper.KEY_WORKING_TIME_ID_ORDERS, timeId);
            contentValues.put(DBHelper.KEY_USER_ID,  String.valueOf(orderSnapshot.child(USER_ID).getValue()));

            boolean hasSomeData = WorkWithLocalStorageApi
                    .hasSomeData(DBHelper.TABLE_ORDERS, orderId);

            if (hasSomeData) {
                localDatabase.update(DBHelper.TABLE_ORDERS, contentValues,
                        DBHelper.KEY_ID + " = ?",
                        new String[]{orderId});
            } else {
                contentValues.put(DBHelper.KEY_ID, orderId);
                localDatabase.insert(DBHelper.TABLE_ORDERS, null, contentValues);
            }

    }
}

