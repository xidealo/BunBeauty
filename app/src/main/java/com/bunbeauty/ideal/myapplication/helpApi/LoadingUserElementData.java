package com.bunbeauty.ideal.myapplication.helpApi;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Photo;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.DBHelper;
import com.google.firebase.database.DataSnapshot;

public class LoadingUserElementData {

    /*
    Класс предназначен для того,
    чтобы загружать минимальные данные,
    которые нужны для элементов RV
    */
    private static final String PHOTO_LINK = "photo link";

    private static SQLiteDatabase localDatabase;
    private static Thread photoThread;

    public static void loadUserNameAndPhoto(final DataSnapshot userSnapshot, SQLiteDatabase _localDatabase) {
        localDatabase = _localDatabase;
        new WorkWithLocalStorageApi(_localDatabase);

        String userId = userSnapshot.getKey();

        photoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                loadPhotos(userSnapshot,localDatabase);
            }
        });
        photoThread.start();

        String userName = userSnapshot.child(User.NAME).getValue(String.class);
        User user = new User();
        user.setId(userId);
        user.setName(userName);

        addUserInfoInLocalStorage(user);
    }

    public static void loadUserNameAndPhotoWithCity(final DataSnapshot userSnapshot, SQLiteDatabase _localDatabase) {
        localDatabase = _localDatabase;
        new WorkWithLocalStorageApi(_localDatabase);

        String userId = userSnapshot.getKey();

        photoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                loadPhotos(userSnapshot,localDatabase);
            }
        });
        photoThread.start();

        String userName = userSnapshot.child(User.NAME).getValue(String.class);
        String userCity = userSnapshot.child(User.CITY).getValue(String.class);
        User user = new User();
        user.setId(userId);
        user.setName(userName);
        user.setCity(userCity);

        addUserInfoInLocalStorage(user);
    }

    public static void loadPhotos(DataSnapshot userSnapshot, SQLiteDatabase _localDatabase) {
        localDatabase = _localDatabase;
        new WorkWithLocalStorageApi(_localDatabase);
        Photo photo = new Photo();
        photo.setId(userSnapshot.getKey());
        photo.setLink(String.valueOf(userSnapshot.child(PHOTO_LINK).getValue()));
        addPhotoInLocalStorage(photo);
    }

    private static void addUserInfoInLocalStorage(User user) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_NAME_USERS, user.getName());

        if(user.getCity()!=null){
            contentValues.put(DBHelper.KEY_CITY_USERS, user.getCity());
        }

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

    private static void addPhotoInLocalStorage(Photo photo) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_ID, photo.getId());
        contentValues.put(DBHelper.KEY_PHOTO_LINK_PHOTOS, photo.getLink());
        //contentValues.put(DBHelper.KEY_OWNER_ID_PHOTOS, photo.getOwnerId());

        boolean isUpdate = WorkWithLocalStorageApi.hasSomeData(DBHelper.TABLE_PHOTOS,
                photo.getId());

        if (isUpdate) {
            localDatabase.update(DBHelper.TABLE_PHOTOS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{photo.getId()});
        } else {
            contentValues.put(DBHelper.KEY_ID, photo.getId());
            localDatabase.insert(DBHelper.TABLE_PHOTOS, null, contentValues);
        }
        if(photoThread!=null) {
            photoThread.interrupt();
        }
    }

}
