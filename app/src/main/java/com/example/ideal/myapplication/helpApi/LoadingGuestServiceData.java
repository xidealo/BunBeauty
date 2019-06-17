package com.example.ideal.myapplication.helpApi;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.ideal.myapplication.fragments.objects.Photo;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.database.DataSnapshot;

public class LoadingGuestServiceData {

    private static final String TAG = "DBInf";

    private static final String DESCRIPTION = "description";
    private static final String COST = "cost";
    private static final String IS_PREMIUM = "is premium";

    private static final String TIME = "time";
    private static final String WORKING_DAYS = "working days";
    private static final String WORKING_TIME = "working time";
    private static final String DATE = "date";
    private static final String CATEGORY = "category";
    private static final String AVG_RATING = "avg rating";
    private static final String ADDRESS = "address";
    private static final String NAME = "name";

    //PHOTOS
    private static final String PHOTOS = "photos";
    private static final String PHOTO_LINK = "photo link";

    private static Thread photoThread;

    public static void addServiceInfoInLocalStorage(final DataSnapshot serviceSnapshot, final SQLiteDatabase localDatabase) {
        final String serviceId = serviceSnapshot.getKey();
        //потоки с другими add
        photoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                loadPhotosByServiceId(serviceSnapshot.child(PHOTOS), serviceId, localDatabase);
            }
        });
        photoThread.start();

        ContentValues contentValues = new ContentValues();
        // Заполняем contentValues информацией о данном сервисе

        contentValues.put(DBHelper.KEY_NAME_SERVICES, String.valueOf(serviceSnapshot.child(NAME).getValue()));
        contentValues.put(DBHelper.KEY_DESCRIPTION_SERVICES, String.valueOf(serviceSnapshot.child(DESCRIPTION).getValue()));
        contentValues.put(DBHelper.KEY_MIN_COST_SERVICES, String.valueOf(serviceSnapshot.child(COST).getValue()));
        contentValues.put(DBHelper.KEY_IS_PREMIUM_SERVICES, String.valueOf(serviceSnapshot.child(IS_PREMIUM).getValue()));
        contentValues.put(DBHelper.KEY_CATEGORY_SERVICES, String.valueOf(serviceSnapshot.child(CATEGORY).getValue()));
        contentValues.put(DBHelper.KEY_ADDRESS_SERVICES, String.valueOf(serviceSnapshot.child(ADDRESS).getValue()));

        boolean hasSomeData = WorkWithLocalStorageApi
                .hasSomeData(DBHelper.TABLE_CONTACTS_SERVICES, serviceSnapshot.getKey());

        // Проверка есть ли такой сервис в SQLite
        if (hasSomeData) {
            // Данный сервис уже есть
            // Обновляем информацию о нём
            localDatabase.update(
                    DBHelper.TABLE_CONTACTS_SERVICES,
                    contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{serviceId});
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
            //addTimeInLocalStorage(workingDaySnapshot.child(WORKING_TIME), dayId, localDatabase);
    }

    public static void addTimeInLocalStorage(DataSnapshot timeSnapshot, String workingDayId, SQLiteDatabase localDatabase) {
            ContentValues contentValues = new ContentValues();
            String timeId = timeSnapshot.getKey();
            contentValues.put(DBHelper.KEY_TIME_WORKING_TIME, String.valueOf(timeSnapshot.child(TIME).getValue()));
            contentValues.put(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME, workingDayId);

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

    private static void loadPhotosByServiceId(DataSnapshot photosSnapshot, String serviceId, SQLiteDatabase localDatabase) {
        for (DataSnapshot fPhoto : photosSnapshot.getChildren()) {
            Photo photo = new Photo();

            photo.setPhotoId(fPhoto.getKey());
            photo.setPhotoLink(String.valueOf(fPhoto.child(PHOTO_LINK).getValue()));
            photo.setPhotoOwnerId(serviceId);

            addPhotoInLocalStorage(photo, localDatabase);
        }
        photoThread.interrupt();
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
}

