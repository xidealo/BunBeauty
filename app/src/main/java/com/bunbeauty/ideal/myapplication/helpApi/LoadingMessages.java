package com.bunbeauty.ideal.myapplication.helpApi;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bunbeauty.ideal.myapplication.entity.Service;
import com.bunbeauty.ideal.myapplication.other.DBHelper;
import com.google.firebase.database.DataSnapshot;

import java.util.Date;

public class LoadingMessages {

    private static final String TAG = "DBInf";

    private static final String WORKING_TIME = "working time";
    private static final String ORDERS = "orders";
    private static final String REVIEWS = "reviews";

    private static final String TIME = "time";
    private static final String DATE = "date";
    private static final String USER_ID = "user id";

    private static final String REVIEW = "review";
    private static final String TYPE = "type";
    private static final String RATING = "rating";
    private static final String IS_CANCELED = "is canceled";

    public static void load(DataSnapshot workingDaySnapshot, String serviceId, String workingDayId,
                            String workingTimeId, String orderId, SQLiteDatabase database) {

        LoadingMessages.addWorkingDayInLocalStorage(workingDaySnapshot,
                serviceId,
                database);

        DataSnapshot workingTimeSnapshot = workingDaySnapshot.child(WORKING_TIME).child(workingTimeId);
        LoadingMessages.addTimeInLocalStorage(workingTimeSnapshot,
                workingDayId,
                database);

        DataSnapshot orderSnapshot = workingTimeSnapshot.child(ORDERS).child(orderId);
        LoadingMessages.addOrderInLocalStorage(orderSnapshot,
                workingTimeId,
                database);

        DataSnapshot reviewsSnapshot = orderSnapshot.child(REVIEWS);
        LoadingMessages.addReviewInLocalStorage(reviewsSnapshot,
                orderId,
                database);
    }

    public static void addWorkingDayInLocalStorage(DataSnapshot workingDaySnapshot, String serviceId, SQLiteDatabase database) {

        ContentValues contentValues = new ContentValues();
        String workingDayId = workingDaySnapshot.getKey();
        String date = workingDaySnapshot.child(DATE).getValue(String.class);
        contentValues.put(DBHelper.KEY_DATE_WORKING_DAYS, date);
        contentValues.put(DBHelper.KEY_SERVICE_ID_WORKING_DAYS, serviceId);

        boolean hasSomeData = WorkWithLocalStorageApi.hasSomeData(DBHelper.TABLE_WORKING_DAYS, workingDayId);
        if (hasSomeData) {
            database.update(DBHelper.TABLE_WORKING_DAYS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{workingDayId});
        } else {
            contentValues.put(DBHelper.KEY_ID, workingDayId);
            database.insert(DBHelper.TABLE_WORKING_DAYS, null, contentValues);
        }
    }

    public static void addTimeInLocalStorage(DataSnapshot workingTimeSnapshot, String workingDayId, SQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        String timeId = workingTimeSnapshot.getKey();
        String time = String.valueOf(workingTimeSnapshot.child(TIME).getValue());
        contentValues.put(DBHelper.KEY_TIME_WORKING_TIME, time);
        contentValues.put(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME, workingDayId);

        boolean hasSomeData = WorkWithLocalStorageApi.hasSomeData(DBHelper.TABLE_WORKING_TIME, timeId);

        if (hasSomeData) {
            database.update(DBHelper.TABLE_WORKING_TIME, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{timeId});
        } else {
            contentValues.put(DBHelper.KEY_ID, timeId);
            database.insert(DBHelper.TABLE_WORKING_TIME, null, contentValues);
        }
    }

    public static void addOrderInLocalStorage(DataSnapshot orderSnapshot, String timeId, SQLiteDatabase database) {
        ContentValues contentValues = new ContentValues();
        String orderId = orderSnapshot.getKey();
        String userId = String.valueOf(orderSnapshot.child(USER_ID).getValue());

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

        boolean hasSomeData = WorkWithLocalStorageApi.hasSomeData(DBHelper.TABLE_ORDERS, orderId);

        if (hasSomeData) {
            database.update(DBHelper.TABLE_ORDERS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{orderId});
        } else {
            contentValues.put(DBHelper.KEY_ID, orderId);
            database.insert(DBHelper.TABLE_ORDERS, null, contentValues);
        }
    }

    public static void addReviewInLocalStorage(DataSnapshot reviewsSnapshot, String orderId, SQLiteDatabase database) {
        for (DataSnapshot reviewSnapshot : reviewsSnapshot.getChildren()) {
            ContentValues contentValues = new ContentValues();
            String reviewId = reviewSnapshot.getKey();
            contentValues.put(DBHelper.KEY_REVIEW_REVIEWS, String.valueOf(reviewSnapshot.child(REVIEW).getValue()));
            contentValues.put(DBHelper.KEY_RATING_REVIEWS, String.valueOf(reviewSnapshot.child(RATING).getValue()));
            contentValues.put(DBHelper.KEY_TYPE_REVIEWS, String.valueOf(reviewSnapshot.child(TYPE).getValue()));
            contentValues.put(DBHelper.KEY_ORDER_ID_REVIEWS, orderId);

            boolean hasSomeData = WorkWithLocalStorageApi.hasSomeData(DBHelper.TABLE_REVIEWS, reviewId);

            if (hasSomeData) {
                database.update(DBHelper.TABLE_REVIEWS, contentValues,
                        DBHelper.KEY_ID + " = ?",
                        new String[]{reviewId});
            } else {
                contentValues.put(DBHelper.KEY_ID, reviewId);
                database.insert(DBHelper.TABLE_REVIEWS, null, contentValues);
            }
        }
    }

    public static void addServiceInLocalStorage(DataSnapshot serviceSnapshot, String ownerId, SQLiteDatabase database) {
        String serviceId = serviceSnapshot.getKey();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_NAME_SERVICES, String.valueOf(serviceSnapshot.child(Service.NAME).getValue()));
        contentValues.put(DBHelper.KEY_USER_ID, ownerId);

        boolean hasSomeData = WorkWithLocalStorageApi.hasSomeData(DBHelper.TABLE_CONTACTS_SERVICES, serviceId);

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

    private static String updateMessageTime(String timeId) {
        String updatedTime = "";

        Cursor cursor = WorkWithLocalStorageApi.getServiceCursorByTimeId(timeId);

        if (cursor.moveToFirst()) {
            int indexTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);
            int indexDate = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS);

            String time = cursor.getString(indexTime);
            String date = cursor.getString(indexDate);

            //3600000 * 24 = 24 часа
            String commonDate = date + " " + time;
            long messageDateLong = WorkWithTimeApi.getMillisecondsStringDate(commonDate) + 3600000 * 24;
            long sysdate = WorkWithTimeApi.getSysdateLong();

            if (sysdate > messageDateLong) {
                // вычитаем 3 часа, т.к. метод работает с датой по Гринвичу
                updatedTime = WorkWithTimeApi.getDateInFormatYMDHMS(new Date(messageDateLong - 3600000 * 3));
            }
        }
        cursor.close();
        return updatedTime;
    }
}
