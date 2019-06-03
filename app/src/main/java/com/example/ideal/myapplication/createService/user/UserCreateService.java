package com.example.ideal.myapplication.createService.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserCreateService implements IUser {

    private static final String WORKING_TIME = "working time";
    private static final String WORKING_DAYS = "working days";
    private static final String SERVICES = "services";
    private static final String USERS = "users";
    private static final String USER_ID = "user id";
    private static final String ORDERS = "orders";
    private static final String REVIEWS = "reviews";
    private static final String IS_CANCELED = "is canceled";
    private static final String WORKER_ID = "worker id";
    private static final String TIME = "time";
    private static final String SERVICE_ID = "service id";

    private static final String RATING = "rating";
    private static final String REVIEW = "review";
    private static final String TYPE = "type";
    private static final String REVIEW_FOR_SERVICE = "review for service";
    private static final String REVIEW_FOR_USER = "review for user";

    private String userId;
    private String serviceId;
    private String workingDaysId;
    private DBHelper dbHelper;

    public UserCreateService(String userId, String serviceId, DBHelper dbHelper, String workingDaysId) {
        this.userId = userId;
        this.serviceId = serviceId;
        this.workingDaysId = workingDaysId;
        this.dbHelper = dbHelper;
    }

    // заносим данные о записи в БД
    @Override
    public void makeOrder(String workingTimeId) {
        String serviceOwnerId = getServiceOwnerId(workingTimeId);
        String orderId = makeOrderForService(workingTimeId, serviceOwnerId);
        makeOrderForUser(orderId, serviceOwnerId);
    }

    // Создаёт запись в разделе Сервис
    private String makeOrderForService(String workingTimeId, String serviceOwnerId) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Добавляем информацию о записи в LocalStorage и Firebase
        DatabaseReference myRef = database
                .getReference(USERS)
                .child(serviceOwnerId)
                .child(SERVICES)
                .child(serviceId)
                .child(WORKING_DAYS)
                .child(workingDaysId)
                .child(WORKING_TIME)
                .child(workingTimeId)
                .child(ORDERS);
        String orderId = myRef.push().getKey();

        String messageTime = WorkWithTimeApi.getDateInFormatYMDHMS(new Date());

        Map<String, Object> items = new HashMap<>();
        items.put(IS_CANCELED, false);
        items.put(USER_ID, userId);
        items.put(TIME, messageTime);

        myRef = myRef.child(orderId);
        myRef.updateChildren(items);

        addOrderInLocalStorage(orderId, workingTimeId, messageTime);

        // создаём отзыв дял сервиса
        makeReview(myRef, REVIEW_FOR_SERVICE);

        return orderId;
    }

    // Создаёт запись в разделе UserCreateService
    private void makeOrderForUser(String orderId, String workerId) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database
                .getReference(USERS)
                .child(userId)
                .child(ORDERS)
                .child(orderId);

        Map<String, Object> items = new HashMap<>();
        items.put(WORKER_ID, workerId);
        items.put(SERVICE_ID, serviceId);

        myRef.updateChildren(items);

        // создаём отзыв для пользователя
        makeReview(myRef, REVIEW_FOR_USER);
    }

    // создаёт пустой отзыв по указанной ссылке на запись и типу
    private void makeReview(DatabaseReference myRef, String type) {
        String orderId = myRef.getKey();

        myRef = myRef.child(REVIEWS);
        String reviewId = myRef.push().getKey();
        myRef = myRef.child(reviewId);

        Map<String, Object> items = new HashMap<>();
        items.put(RATING, 0);
        items.put(REVIEW, "");
        items.put(TYPE, type);
        myRef.updateChildren(items);

        addReviewInLocalStorage(orderId, reviewId, type);
    }

    private void addReviewInLocalStorage(final String orderId, final String reviewId, final String type) {
        SQLiteDatabase localDatabase = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ID, reviewId);
        contentValues.put(DBHelper.KEY_REVIEW_REVIEWS, "");
        contentValues.put(DBHelper.KEY_RATING_REVIEWS, "0");
        contentValues.put(DBHelper.KEY_TYPE_REVIEWS, type);
        contentValues.put(DBHelper.KEY_ORDER_ID_REVIEWS, orderId);

        localDatabase.insert(DBHelper.TABLE_REVIEWS, null, contentValues);
    }

    private String getServiceOwnerId(String workingTimeId) {

        Cursor cursor = WorkWithLocalStorageApi.getServiceCursorByTimeId(workingTimeId);

        String ownerId = "";
        if (cursor.moveToFirst()) {
            int indexOwnerId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);
            ownerId = cursor.getString(indexOwnerId);
        }

        cursor.close();
        return ownerId;
    }

    private void addOrderInLocalStorage(final String orderId,final String timeId,final String messageTime) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_ID, orderId);
        contentValues.put(DBHelper.KEY_USER_ID, userId);
        contentValues.put(DBHelper.KEY_IS_CANCELED_ORDERS, "false");
        contentValues.put(DBHelper.KEY_WORKING_TIME_ID_ORDERS, timeId);
        contentValues.put(DBHelper.KEY_MESSAGE_TIME_ORDERS, messageTime);

        database.insert(DBHelper.TABLE_ORDERS, null, contentValues);
    }
}

