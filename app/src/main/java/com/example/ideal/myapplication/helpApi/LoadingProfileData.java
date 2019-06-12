package com.example.ideal.myapplication.helpApi;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ideal.myapplication.fragments.objects.Photo;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.database.DataSnapshot;

public class LoadingProfileData {

    private static final String TAG = "DBInf";

    private static final String DESCRIPTION = "description";
    private static final String COST = "cost";
    private static final String USER_ID = "user id";
    private static final String USERS = "users";
    private static final String IS_PREMIUM = "is premium";
    private static final String CREATION_DATE = "creation date";

    private static final String TIME = "time";
    private static final String WORKING_DAYS = "working days";
    private static final String WORKING_TIME = "working time";
    private static final String DATE = "date";
    private static final String CATEGORY = "category";
    private static final String ADDRESS = "address";

    private static final String ORDERS = "orders";

    private static final String REVIEWS = "reviews";
    private static final String REVIEW = "review";
    private static final String TYPE = "type";
    private static final String RATING = "rating";

    private static final String CITY = "city";
    private static final String NAME = "name";
    private static final String PHONE = "phone";

    //PHOTOS
    private static final String PHOTOS = "photos";
    private static final String PHOTO_LINK = "photo link";

    private static final String IS_CANCELED = "is canceled";

    private static SQLiteDatabase localDatabase;
    private static DataSnapshot userSnapshot;
    private static Thread photoThread = new Thread(new Runnable() {
        @Override
        public void run() {
            loadPhotos(userSnapshot);
        }
    });

    /* public LoadingProfileData(SQLiteDatabase localDatabase) {
        this.localDatabase = localDatabase;
    }*/

    public static void loadUserInfo(DataSnapshot _userSnapshot, SQLiteDatabase _localDatabase) {
        localDatabase = _localDatabase;
        userSnapshot = _userSnapshot;

        photoThread.run();

        String userId = userSnapshot.getKey();
        String userPhone = String.valueOf(userSnapshot.child(PHONE).getValue());
        String userName = String.valueOf(userSnapshot.child(NAME).getValue());
        String userCity = String.valueOf(userSnapshot.child(CITY).getValue());
        String userRating = "0";//String.valueOf(userSnapshot.child(RATING).getValue());
        Log.d(TAG, "loadUserInfo: " + userName);

        User user = new User();
        user.setId(userId);
        user.setPhone(userPhone);
        user.setName(userName);
        user.setCity(userCity);
        user.setRating(userRating);

        // Добавляем все данные о пользователе в SQLite
        addUserInfoInLocalStorage(user);
    }

    private static void addUserInfoInLocalStorage(User user) {
        ContentValues contentValues = new ContentValues();

        // Заполняем contentValues информацией о данном пользователе
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
            Log.d(TAG, "update: ");
        } else {
            contentValues.put(DBHelper.KEY_ID, userId);
            localDatabase.insert(DBHelper.TABLE_CONTACTS_USERS, null, contentValues);
            Log.d(TAG, "insert: ");
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
