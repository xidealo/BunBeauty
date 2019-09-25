package com.bunbeauty.ideal.myapplication.helpApi;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Photo;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.DBHelper;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class LoadingProfileData {

    private static final String TAG = "DBInf";

    private static final String WORKER_ID = "worker id";


    //PHOTOS
    private static final String PHOTO_LINK = "photo link";

    private static final String SUBSCRIBERS = "subscribers";
    private static final String SUBSCRIPTIONS = "subscriptions";

    private static final String WORKING_DAYS = "working days";
    private static final String WORKING_TIME = "working time";
    private static final String ORDERS = "orders";
    private static final String TIME = "time";
    private static final String DATE = "date";


    private static SQLiteDatabase localDatabase;
    private static Thread photoThread;
    private static Thread subscriptionThread;

    public static void loadUserInfo(final DataSnapshot userSnapshot, SQLiteDatabase _localDatabase) {
        localDatabase = _localDatabase;
        new WorkWithLocalStorageApi(_localDatabase);

        final String userId = userSnapshot.getKey();

        photoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                loadPhotos(userSnapshot);
            }
        });
        photoThread.start();
        subscriptionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                addUserSubscriptionInLocalStorage(userSnapshot.child(SUBSCRIPTIONS), userId);
            }
        });
        subscriptionThread.start();

        String userPhone = userSnapshot.child(User.PHONE).getValue(String.class);
        String userName = userSnapshot.child(User.NAME).getValue(String.class);
        String userCity = userSnapshot.child(User.CITY).getValue(String.class);
        long userCountOfRates = userSnapshot.child(User.COUNT_OF_RATES).getValue(long.class);
        float userRating = userSnapshot.child(User.AVG_RATING).getValue(float.class);

        User user = new User();
        user.setId(userId);
        user.setPhone(userPhone);
        user.setName(userName);
        user.setCity(userCity);
        user.setRating(userRating);
        user.setCountOfRates(userCountOfRates);

        addUserInfoInLocalStorage(user);
    }

    public static void addSubscriptionsCountInLocalStorage(DataSnapshot userSnapshot, SQLiteDatabase localDatabase) {
        ContentValues contentValues = new ContentValues();
        long subscriptionsCount = userSnapshot.child(SUBSCRIPTIONS).getChildrenCount();

        contentValues.put(DBHelper.KEY_SUBSCRIPTIONS_COUNT_USERS, subscriptionsCount);

        localDatabase.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{userSnapshot.getKey()});
    }

    public static void addSubscribersCountInLocalStorage(DataSnapshot userSnapshot, SQLiteDatabase localDatabase) {
        ContentValues contentValues = new ContentValues();
        long subscribersCount = userSnapshot.child(SUBSCRIBERS).getChildrenCount();

        contentValues.put(DBHelper.KEY_SUBSCRIBERS_COUNT_USERS, subscribersCount);

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
        contentValues.put(DBHelper.KEY_COUNT_OF_RATES_USERS, user.getCountOfRates());

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

    public static void loadUserServices(DataSnapshot servicesSnapshot, String userId,
                                        SQLiteDatabase database) {
        for (final DataSnapshot serviceSnap : servicesSnapshot.getChildren()) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    removeOverdueTime(serviceSnap);
                }
            }.start();

            Log.d(TAG, "loadUserServices: ");

            String serviceId = serviceSnap.getKey();
            String serviceName = serviceSnap.child(Service.NAME).getValue(String.class);
            float serviceRating = serviceSnap.child(Service.AVG_RATING).getValue(Float.class);

            Service service = new Service();
            service.setId(serviceId);
            service.setName(serviceName);
            service.setUserId(userId);
            service.setAverageRating(serviceRating);
            ArrayList<String> tagsArray = new ArrayList<>();
            for (DataSnapshot tag : serviceSnap.child(Service.TAGS).getChildren()) {
                tagsArray.add(tag.getValue(String.class));
            }
            service.setTags(tagsArray);

            addUserServicesInLocalStorage(service, database);
        }
    }

    private static void removeOverdueTime(DataSnapshot serviceSnapshot) {
        for (DataSnapshot workingDaySnapshot : serviceSnapshot.child(WORKING_DAYS).getChildren()) {
            String date = workingDaySnapshot.child(DATE).getValue(String.class);
            for (DataSnapshot workingTimeSnapshot : workingDaySnapshot.child(WORKING_TIME).getChildren()) {
                String time = workingTimeSnapshot.child(TIME).getValue(String.class);
                if ((workingTimeSnapshot.child(ORDERS).getValue() == null) &&
                        isTimeOverdue(time , date)) {
                    workingTimeSnapshot.getRef().removeValue();
                }
            }
            if (workingDaySnapshot.child(WORKING_TIME).getValue() == null) {
                workingDaySnapshot.getRef().removeValue();
            }
        }
    }

    private static boolean isTimeOverdue(String time, String date) {
        long orderDate = WorkWithTimeApi.getMillisecondsStringDate(date + " " + time);
        long sysdate = WorkWithTimeApi.getSysdateLong();

        if (orderDate < sysdate) {
            return true;
        }

        return false;
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

        contentValues.clear();
        contentValues.put(DBHelper.KEY_SERVICE_ID_TAGS, serviceId);
        for (String tag : service.getTags()) {
            contentValues.put(DBHelper.KEY_TAG_TAGS, tag);
            database.insert(DBHelper.TABLE_TAGS, null, contentValues);
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

    private static void addUserSubscriptionInLocalStorage(DataSnapshot subsSnapshot, String userId) {

        for (DataSnapshot subSnapshot : subsSnapshot.getChildren()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.KEY_ID, subSnapshot.getKey());
            contentValues.put(DBHelper.KEY_USER_ID, userId);
            contentValues.put(DBHelper.KEY_WORKER_ID, subSnapshot.child(WORKER_ID).getValue(String.class));

            boolean hasSomeData = WorkWithLocalStorageApi.hasSomeData(DBHelper.TABLE_SUBSCRIBERS, subSnapshot.getKey());
            if (hasSomeData) {
                localDatabase.update(DBHelper.TABLE_SUBSCRIBERS, contentValues,
                        DBHelper.KEY_ID + " = ?",
                        new String[]{subSnapshot.getKey()});
            } else {
                contentValues.put(DBHelper.KEY_ID, subSnapshot.getKey());
                localDatabase.insert(DBHelper.TABLE_SUBSCRIBERS, null, contentValues);
            }
        }

        subscriptionThread.interrupt();
    }
}
