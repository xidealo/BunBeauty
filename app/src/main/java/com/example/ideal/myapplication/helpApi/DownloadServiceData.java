package com.example.ideal.myapplication.helpApi;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.foundElements.foundServiceElement;
import com.example.ideal.myapplication.fragments.foundElements.foundServiceProfileElement;
import com.example.ideal.myapplication.fragments.objects.Message;
import com.example.ideal.myapplication.fragments.objects.Photo;
import com.example.ideal.myapplication.fragments.objects.RatingReview;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class DownloadServiceData {

    private static final String TAG = "DBInf";

    private static final String DESCRIPTION = "description";
    private static final String COST = "cost";
    private static final String USER_ID = "user id";
    private static final String USERS = "users";

    private static final String TIME = "time";
    private static final String WORKING_DAYS = "working days";
    private static final String WORKING_TIME = "working time";
    private static final String DATE = "date";

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

    private WorkWithLocalStorageApi LSApi;
    private SQLiteDatabase localDatabase;

    private String status;

    public DownloadServiceData(SQLiteDatabase _database, String _status) {
        localDatabase = _database;
        status = _status;
        LSApi = new WorkWithLocalStorageApi(localDatabase);
    }

    public void loadUserInfo(DataSnapshot userSnapshot) {

        loadPhotosByPhoneNumber(userSnapshot);

        String userId = userSnapshot.getKey();
        String userPhone = String.valueOf(userSnapshot.child(PHONE).getValue());
        String userName = String.valueOf(userSnapshot.child(NAME).getValue());
        String userCity = String.valueOf(userSnapshot.child(CITY).getValue());

        User user = new User();
        user.setId(userId);
        user.setPhone(userPhone);
        user.setName(userName);
        user.setCity(userCity);

        // Добавляем все данные о пользователе в SQLite
        addUserInfoInLocalStorage(user);
    }

    // Обновляет информацию о текущем пользователе в SQLite
    private void addUserInfoInLocalStorage(User user) {
        ContentValues contentValues = new ContentValues();

        // Заполняем contentValues информацией о данном пользователе
        contentValues.put(DBHelper.KEY_NAME_USERS, user.getName());
        contentValues.put(DBHelper.KEY_CITY_USERS, user.getCity());
        contentValues.put(DBHelper.KEY_PHONE_USERS, user.getPhone());

        String userId = user.getId();
        boolean hasSomeData = LSApi
                .hasSomeData(DBHelper.TABLE_CONTACTS_USERS, userId);

        if (hasSomeData) {
            localDatabase.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{userId});
        } else {
            contentValues.put(DBHelper.KEY_ID, userId);
            localDatabase.insert(DBHelper.TABLE_CONTACTS_USERS, null, contentValues);
        }

    }

    public void loadSchedule(DataSnapshot servicesSnapshot, String userId) {

        for (DataSnapshot serviceSnapshot : servicesSnapshot.getChildren()) {
            String serviceId = serviceSnapshot.getKey();

            addServiceInLocalStorage(serviceSnapshot, userId);

            //загрузка фотографий для сервисов
            loadPhotosByServiceId(serviceSnapshot.child(PHOTOS), serviceId);
        }
    }

    private void addWorkingDaysInLocalStorage(DataSnapshot workingDaysSnapshot, String serviceId) {
        for(DataSnapshot workingDaySnapshot: workingDaysSnapshot.getChildren()) {

            ContentValues contentValues = new ContentValues();
            String dayId = workingDaySnapshot.getKey();
            String date = String.valueOf(workingDaySnapshot.child(DATE).getValue());
            contentValues.put(DBHelper.KEY_DATE_WORKING_DAYS, date);
            contentValues.put(DBHelper.KEY_SERVICE_ID_WORKING_DAYS, serviceId);

            boolean hasSomeData = LSApi
                    .hasSomeData(DBHelper.TABLE_WORKING_DAYS, dayId);
            if (hasSomeData) {
                localDatabase.update(DBHelper.TABLE_WORKING_DAYS, contentValues,
                        DBHelper.KEY_ID + " = ?",
                        new String[]{dayId});
            } else {
                contentValues.put(DBHelper.KEY_ID, dayId);
                localDatabase.insert(DBHelper.TABLE_WORKING_DAYS, null, contentValues);
            }
            addTimeInLocalStorage(workingDaySnapshot.child(WORKING_TIME), dayId);
        }
    }

    private void addTimeInLocalStorage(DataSnapshot timesSnapshot, String workingDayId) {
        for (DataSnapshot timeSnapshot : timesSnapshot.getChildren()) {
            ContentValues contentValues = new ContentValues();
            String timeId = timeSnapshot.getKey();
            contentValues.put(DBHelper.KEY_TIME_WORKING_TIME, String.valueOf(timeSnapshot.child(TIME).getValue()));
            contentValues.put(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME, workingDayId);
            
            boolean hasSomeData = LSApi
                    .hasSomeData(DBHelper.TABLE_WORKING_TIME, timeId);

            if (hasSomeData) {
                localDatabase.update(DBHelper.TABLE_WORKING_TIME, contentValues,
                        DBHelper.KEY_ID + " = ?",
                        new String[]{timeId});
            } else {
                contentValues.put(DBHelper.KEY_ID, timeId);
                localDatabase.insert(DBHelper.TABLE_WORKING_TIME, null, contentValues);
            }

            addOrdersInLocalStorage(timeSnapshot.child(ORDERS), timeId);
            //loadRating();
        }
    }

    private void addOrdersInLocalStorage(DataSnapshot ordersSnapshot, String timeId) {
        for (DataSnapshot orderSnapshot : ordersSnapshot.getChildren()) {
            ContentValues contentValues = new ContentValues();
            String orderId = orderSnapshot.getKey();
            String userId = String.valueOf(orderSnapshot.child(USER_ID).getValue());

            loadReviewForUser(userId, orderId);

            contentValues.put(DBHelper.KEY_ID, orderId);
            contentValues.put(DBHelper.KEY_USER_ID, userId);
            contentValues.put(DBHelper.KEY_IS_CANCELED_ORDERS, String.valueOf(orderSnapshot.child(IS_CANCELED).getValue()));
            contentValues.put(DBHelper.KEY_WORKING_TIME_ID_ORDERS, timeId);

            String updatedTime = updateMessageTime(timeId);
            if (!updatedTime.equals("")) {
                contentValues.put(DBHelper.KEY_MESSAGE_TIME_ORDERS, updatedTime);
            } else {
                contentValues.put(DBHelper.KEY_MESSAGE_TIME_ORDERS, String.valueOf(orderSnapshot.child(TIME).getValue()));
            }

            boolean hasSomeData = LSApi
                    .hasSomeData(DBHelper.TABLE_ORDERS, orderId);

            if (hasSomeData) {
                localDatabase.update(DBHelper.TABLE_ORDERS, contentValues,
                        DBHelper.KEY_ID + " = ?",
                        new String[]{orderId});
            } else {
                contentValues.put(DBHelper.KEY_ID, orderId);
                localDatabase.insert(DBHelper.TABLE_ORDERS, null, contentValues);
            }
            addReviewInLocalStorage(orderSnapshot.child(REVIEWS),orderId);
        }
    }


    private void loadReviewForUser(String userId, final String orderId) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(USERS)
                .child(userId)
                .child(ORDERS)
                .child(orderId)
                .child(REVIEWS);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot reviewsSnapshot) {
                addReviewInLocalStorage(reviewsSnapshot, orderId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private String updateMessageTime(String timeId) {
        String updatedTime = "";

        Cursor cursor = LSApi.getServiceCursorByTimeId(timeId);

        if (cursor.moveToFirst()) {
            int indexTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);
            int indexDate = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS);

            String time = cursor.getString(indexTime);
            String date = cursor.getString(indexDate);

            WorkWithTimeApi workWithTimeApi = new WorkWithTimeApi();
            //3600000 * 24 = 24 часа
            String commonDate = date + " " + time;
            Long messageDateLong = workWithTimeApi.getMillisecondsStringDate(commonDate) + 3600000*24;
            Long sysdate = workWithTimeApi.getSysdateLong();

            if (sysdate > messageDateLong) {
                // вычитаем 3 часа, т.к. метод работает с датой по Гринвичу
                updatedTime = workWithTimeApi.getDateInFormatYMDHMS(new Date(messageDateLong - 3600000*3));
            }
        }

        cursor.close();
        return  updatedTime;
    }

    public void addReviewInLocalStorage(DataSnapshot reviewsSnapshot, String orderId){

        for (DataSnapshot reviewSnapshot : reviewsSnapshot.getChildren()) {
            ContentValues contentValues = new ContentValues();
            String reviewId = reviewSnapshot.getKey();
            contentValues.put(DBHelper.KEY_REVIEW_REVIEWS, String.valueOf(reviewSnapshot.child(REVIEW).getValue()));
            contentValues.put(DBHelper.KEY_RATING_REVIEWS, String.valueOf(reviewSnapshot.child(RATING).getValue()));
            contentValues.put(DBHelper.KEY_TYPE_REVIEWS, String.valueOf(reviewSnapshot.child(TYPE).getValue()));
            contentValues.put(DBHelper.KEY_ORDER_ID_REVIEWS, orderId);

            boolean hasSomeData = LSApi
                    .hasSomeData(DBHelper.TABLE_REVIEWS, reviewId);

            if (hasSomeData) {
                localDatabase.update(DBHelper.TABLE_REVIEWS, contentValues,
                        DBHelper.KEY_ID + " = ?",
                        new String[]{reviewId});
            } else {
                contentValues.put(DBHelper.KEY_ID, reviewId);
                localDatabase.insert(DBHelper.TABLE_REVIEWS, null, contentValues);
            }
        }
    }

    public void addServiceInLocalStorage(DataSnapshot serviceSnapshot, String userId) {

        String serviceId = serviceSnapshot.getKey();

        ContentValues contentValues = new ContentValues();
        // Заполняем contentValues информацией о данном сервисе

        contentValues.put(DBHelper.KEY_NAME_SERVICES, String.valueOf(serviceSnapshot.child(NAME).getValue()));
        contentValues.put(DBHelper.KEY_USER_ID, userId);
        contentValues.put(DBHelper.KEY_DESCRIPTION_SERVICES, String.valueOf(serviceSnapshot.child(DESCRIPTION).getValue()));
        contentValues.put(DBHelper.KEY_MIN_COST_SERVICES, String.valueOf(serviceSnapshot.child(COST).getValue()));

        boolean hasSomeData =  LSApi
                .hasSomeData(DBHelper.TABLE_CONTACTS_SERVICES, serviceId);

        // Проверка есть ли такой сервис в SQLite
        if(hasSomeData) {
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

        // Добавление рабочего времени данного сервиса
        addWorkingDaysInLocalStorage(serviceSnapshot.child(WORKING_DAYS), serviceId);
    }


    private void loadPhotosByServiceId(DataSnapshot photosSnapshot, String serviceId) {

        for (DataSnapshot fPhoto : photosSnapshot.getChildren()) {
            Photo photo = new Photo();

            photo.setPhotoId(fPhoto.getKey());
            photo.setPhotoLink(String.valueOf(fPhoto.child(PHOTO_LINK).getValue()));
            photo.setPhotoOwnerId(serviceId);

            addPhotoInLocalStorage(photo);
        }
    }

    private void loadPhotosByPhoneNumber(DataSnapshot userSnapshot) {

        Photo photo = new Photo();
        photo.setPhotoId(userSnapshot.getKey());
        photo.setPhotoLink(String.valueOf(userSnapshot.child(PHOTO_LINK).getValue()));

        addPhotoInLocalStorage(photo);
    }

    private void addPhotoInLocalStorage(Photo photo) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_ID, photo.getPhotoId());
        contentValues.put(DBHelper.KEY_PHOTO_LINK_PHOTOS, photo.getPhotoLink());
        contentValues.put(DBHelper.KEY_OWNER_ID_PHOTOS,photo.getPhotoOwnerId());

        boolean isUpdate = LSApi
                .hasSomeData(DBHelper.TABLE_PHOTOS,
                        photo.getPhotoId());

        if(isUpdate){
            localDatabase.update(DBHelper.TABLE_PHOTOS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{photo.getPhotoId()});
        }
        else {
            contentValues.put(DBHelper.KEY_ID, photo.getPhotoId());
            localDatabase.insert(DBHelper.TABLE_PHOTOS, null, contentValues);
        }
    }

    //время полученное по timeId больше 3 дней
    private boolean isAfterWeek(String workingTimeId) {

        String date  = LSApi.getDate(workingTimeId);
        WorkWithTimeApi workWithTimeApi = new WorkWithTimeApi();
        long dateMilliseconds = workWithTimeApi.getMillisecondsStringDate(date);
        boolean isAfterWeek = (workWithTimeApi.getSysdateLong() - dateMilliseconds) > 604800000;

        return isAfterWeek;
    }

    //ревью оставили 2 человека?
    private boolean isMutualReview(DataSnapshot reviewsSnapshot) {
        if(reviewsSnapshot.getChildrenCount()==0){
            return false;
        }
        for (DataSnapshot reviewSnapshot : reviewsSnapshot.getChildren()) {
            String rating = String.valueOf(reviewSnapshot.child(RATING).getValue());
            //если хоть 1 оценка 0, то возвращаем false
            if (rating.equals("0")) {
                return false;
            }
        }
        return true;
    }
}

