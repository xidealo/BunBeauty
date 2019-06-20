package com.example.ideal.myapplication.helpApi;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ideal.myapplication.fragments.objects.Photo;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.database.DataSnapshot;

public class LoadingProfileData {

    private static final String TAG = "DBInf";

    private static final String AVG_RATING = "avg rating";

    private static final String CITY = "city";
    private static final String NAME = "name";
    private static final String PHONE = "phone";

    //PHOTOS
    private static final String PHOTO_LINK = "photo link";

    private static final String SUBSCRIBERS = "subscribers";
    private static final String SUBSCRIPTIONS = "subscriptions";

    private static SQLiteDatabase localDatabase;
    private static Thread photoThread;

    public static void loadUserInfo(final DataSnapshot userSnapshot, SQLiteDatabase _localDatabase) {
        localDatabase = _localDatabase;
        new WorkWithLocalStorageApi(_localDatabase);

        String userId = userSnapshot.getKey();

        photoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                loadPhotos(userSnapshot);
            }
        });
        photoThread.start();


        String userPhone = userSnapshot.child(PHONE).getValue(String.class);
        String userName = userSnapshot.child(NAME).getValue(String.class);
        String userCity = userSnapshot.child(CITY).getValue(String.class);
        float userRating = userSnapshot.child(AVG_RATING).getValue(float.class);

        User user = new User();
        user.setId(userId);
        user.setPhone(userPhone);
        user.setName(userName);
        user.setCity(userCity);
        user.setRating(userRating);

        addUserInfoInLocalStorage(user);

    }

    public static void addSubscriptionsCountInLocalStorage(DataSnapshot userSnapshot, SQLiteDatabase localDatabase) {
        ContentValues contentValues = new ContentValues();
        long subscriptionsCount = userSnapshot.child(SUBSCRIPTIONS).getChildrenCount();
        long subscribersCount = userSnapshot.child(SUBSCRIBERS).getChildrenCount();

        contentValues.put(DBHelper.KEY_SUBSCRIBERS_COUNT_USERS, subscribersCount);
        contentValues.put(DBHelper.KEY_SUBSCRIPTIONS_COUNT_USERS, subscriptionsCount);

        localDatabase.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{userSnapshot.getKey()});
    }

    private static void addUserInfoInLocalStorage(User user) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_NAME_USERS, user.getName());
        contentValues.put(DBHelper.KEY_CITY_USERS, user.getCity());
        contentValues.put(DBHelper.KEY_PHONE_USERS, user.getPhone());
        contentValues.put(DBHelper.KEY_RATING_USERS, user.getRating());

        String userId = user.getId();
        boolean hasSomeData = WorkWithLocalStorageApi.hasSomeData(DBHelper.TABLE_CONTACTS_USERS, userId);

        if (hasSomeData) {
            localDatabase.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{userId});
        } else {
            contentValues.put(DBHelper.KEY_ID, userId);
            localDatabase.insert(DBHelper.TABLE_CONTACTS_USERS, null, contentValues);
        }
    }

    public static void loadUserServices(DataSnapshot servicesSnapshot,String userId,
                                        SQLiteDatabase database, int countOfDownloads) {
        int counter = 0;
        for (DataSnapshot serviceSnapshot : servicesSnapshot.getChildren()) {
            if(counter<countOfDownloads) {
                String serviceId = serviceSnapshot.getKey();
                String serviceName = serviceSnapshot.child(NAME).getValue(String.class);
                float serviceRating = serviceSnapshot.child(AVG_RATING).getValue(Float.class);

                Service service = new Service();
                service.setId(serviceId);
                service.setName(serviceName);
                service.setUserId(userId);
                service.setAverageRating(serviceRating);

                addUserServicesInLocalStorage(service, database);
                counter++;
            }
            else {
                break;
            }
        }
    }

    private static void addUserServicesInLocalStorage(Service service, SQLiteDatabase database) {
        String serviceId = service.getId();

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_NAME_SERVICES, service.getName());
        contentValues.put(DBHelper.KEY_USER_ID, service.getUserId());
        contentValues.put(DBHelper.KEY_RATING_SERVICES, service.getAverageRating());

        boolean hasSomeData = WorkWithLocalStorageApi.hasSomeData(DBHelper.TABLE_CONTACTS_SERVICES, serviceId);

        // Проверка есть ли такой сервис в SQLite
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

    private static void loadPhotos(DataSnapshot userSnapshot) {
        Photo photo = new Photo();
        photo.setPhotoId(userSnapshot.getKey());
        photo.setPhotoLink(String.valueOf(userSnapshot.child(PHOTO_LINK).getValue()));
        addPhotoInLocalStorage(photo);
    }

    private static void addPhotoInLocalStorage(Photo photo) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_ID, photo.getPhotoId());
        contentValues.put(DBHelper.KEY_PHOTO_LINK_PHOTOS, photo.getPhotoLink());
        contentValues.put(DBHelper.KEY_OWNER_ID_PHOTOS, photo.getPhotoOwnerId());

        boolean isUpdate = WorkWithLocalStorageApi.hasSomeData(DBHelper.TABLE_PHOTOS,
                        photo.getPhotoId());

        if (isUpdate) {
            localDatabase.update(DBHelper.TABLE_PHOTOS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{photo.getPhotoId()});
        } else {
            contentValues.put(DBHelper.KEY_ID, photo.getPhotoId());
            localDatabase.insert(DBHelper.TABLE_PHOTOS, null, contentValues);
        }
        photoThread.interrupt();
    }
}
