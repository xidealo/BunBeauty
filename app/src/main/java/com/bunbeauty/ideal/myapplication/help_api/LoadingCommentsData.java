package com.bunbeauty.ideal.myapplication.help_api;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithLocalStorageApi;
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.DBHelper;
import com.google.firebase.database.DataSnapshot;

public class LoadingCommentsData {

    private static final String USER_ID = "user id";
    private static final String TIME = "time";
    private static final String SORT_TIME = "sort time";
    private static final String DATE = "date";
    private static final String REVIEW = "creation_comment";
    private static final String TYPE = "type";
    private static final String RATING = "rating";

    public static void addWorkingDaysInLocalStorage(DataSnapshot workingDaySnapshot, String serviceId,
                                                    SQLiteDatabase localDatabase) {
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

    public static void addTimeInLocalStorage(DataSnapshot timeSnapshot, String workingDayId,
                                             SQLiteDatabase localDatabase) {
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

    static public void addOrderInLocalStorage(final DataSnapshot orderSnapshot, String timeId,
                                              SQLiteDatabase localDatabase) {
        ContentValues contentValues = new ContentValues();
        String orderId = orderSnapshot.getKey();

        contentValues.put(DBHelper.KEY_ID, orderId);
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

    static public void addReviewInLocalStorage(DataSnapshot reviewSnapshot, String orderId,
                                               SQLiteDatabase localDatabase)  {

        ContentValues contentValues = new ContentValues();
        String reviewId = reviewSnapshot.getKey();
        contentValues.put(DBHelper.KEY_REVIEW_REVIEWS, String.valueOf(reviewSnapshot.child(REVIEW).getValue()));
        contentValues.put(DBHelper.KEY_RATING_REVIEWS, String.valueOf(reviewSnapshot.child(RATING).getValue()));
        contentValues.put(DBHelper.KEY_TYPE_REVIEWS, String.valueOf(reviewSnapshot.child(TYPE).getValue()));
        contentValues.put(DBHelper.KEY_ORDER_ID_REVIEWS, orderId);
        contentValues.put(DBHelper.KEY_TIME_REVIEWS,  String.valueOf(reviewSnapshot.child(SORT_TIME).getValue()));

        boolean hasSomeData = WorkWithLocalStorageApi
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
