package com.example.ideal.myapplication.helpApi;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ImageView;

import com.example.ideal.myapplication.other.DBHelper;
import com.squareup.picasso.Picasso;

public class WorkWithLocalStorageApi {

    private SQLiteDatabase localDatabase;

    private static final String REVIEW_FOR_USER = "review for user";
    private static final String REVIEW_FOR_SERVICE = "review for service";

    public WorkWithLocalStorageApi(SQLiteDatabase database) {
        localDatabase = database;
    }

    public boolean isMyService(String serviceId, String myPhone) {
        // Получает id сервиса
        // Таблицы: services
        // Условия: уточняем id сервиса и id воркера
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_ID
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES
                        + " WHERE "
                        + DBHelper.KEY_ID + " = ? "
                        + " AND "
                        + DBHelper.KEY_USER_ID + " = ?";

        Cursor cursor = localDatabase.rawQuery(sqlQuery, new String[]{serviceId, myPhone});
        boolean isMyService;
        if (cursor.moveToFirst()) {
            isMyService = true;
        } else {
            isMyService = false;
        }
        cursor.close();
        return isMyService;
    }

    private static final String TAG = "DBInf";

    public void setPhotoAvatar(String userId, ImageView avatarImage) {
        //получаем имя, фамилию и город пользователя по его id
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_PHOTO_LINK_PHOTOS
                        + " FROM "
                        + DBHelper.TABLE_PHOTOS
                        + " WHERE "
                        + DBHelper.KEY_OWNER_ID_PHOTOS + " = ?";

        Cursor cursor = localDatabase.rawQuery(sqlQuery,new String[] {userId});

        if(cursor.moveToFirst()){
            int indexPhotoLink = cursor.getColumnIndex(DBHelper.KEY_PHOTO_LINK_PHOTOS);

            String photoLink = cursor.getString(indexPhotoLink);
            //установка аватарки
            Picasso.get()
                    .load(photoLink)
                    .into(avatarImage);
        }
        cursor.close();
    }

    public Cursor getServiceCursorByTimeId(String workingTimeId) {
        //Возвращает serviceId, использую workingTimeId
        //таблицы working time, working days, services
        //связь таблиц, уточнение по workingTimeId
        String sqlQuery =
                "SELECT "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + ", "
                        + DBHelper.KEY_NAME_SERVICES + ", "
                        + DBHelper.KEY_DATE_WORKING_DAYS + ", "
                        + DBHelper.KEY_TIME_WORKING_TIME
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + " = ? "
                        + " AND "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                        + " = "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                        + " = "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID;
        Cursor cursor = localDatabase.rawQuery(sqlQuery, new String[]{workingTimeId});

        return cursor;
    }

    public boolean hasSomeData(String tableName, String id) {

        String sqlQuery = "SELECT * FROM "
                + tableName
                + " WHERE "
                + DBHelper.KEY_ID + " = ?";

        Cursor cursor = localDatabase.rawQuery(sqlQuery, new String[]{id});

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public boolean hasSomeDataForUsers(String tableName, String id) {

        String sqlQuery = "SELECT * FROM "
                + tableName
                + " WHERE "
                + DBHelper.KEY_USER_ID + " = ?";

        Cursor cursor = localDatabase.rawQuery(sqlQuery, new String[]{id});

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public String getDate(String workingTimeId) {
        String fullDate = "";

        // Получает дату в формате yyyy-MM-dd HH:mm
        // Таблицы: рабочие время
        // Условия: уточняем id рабочего дня
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_TIME_WORKING_TIME + ", "
                        + DBHelper.KEY_DATE_WORKING_DAYS
                        + " FROM "
                        + DBHelper.TABLE_WORKING_TIME + ", "
                        + DBHelper.TABLE_WORKING_DAYS
                        + " WHERE "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + " = ? ";

        Cursor cursor = localDatabase.rawQuery(sqlQuery, new String[]{workingTimeId});

        if (cursor.moveToFirst()) {
            int indexDate = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS);
            int indexTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);

            String date = cursor.getString(indexDate);
            String time = cursor.getString(indexTime);

            fullDate = date + " " + time;
        }
        cursor.close();
        return fullDate;
    }

    public boolean isMutualReview(String workingTimeId) {
        // все о сервисе, оценка, количество оценок
        //проверка на удаленный номер
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_NAME_SERVICES
                        + " FROM " + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_WORKING_TIME + ", "
                        + DBHelper.TABLE_REVIEWS
                        + " WHERE "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + " = " + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                        + " AND "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID + " = " + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                        + " AND "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + " = " + DBHelper.KEY_WORKING_TIME_ID_REVIEWS
                        + " AND "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + " = ? "
                        + " AND "
                        + DBHelper.KEY_TYPE_REVIEWS + " = ? "
                        + " AND "
                        + DBHelper.KEY_RATING_REVIEWS + " != 0";

        Cursor cursorServiceReview = localDatabase.rawQuery(sqlQuery, new String[]{workingTimeId, REVIEW_FOR_SERVICE});

        if (cursorServiceReview.moveToFirst()) {
            sqlQuery =
                    "SELECT "
                            + DBHelper.KEY_RATING_REVIEWS
                            + " FROM " + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                            + DBHelper.TABLE_WORKING_DAYS + ", "
                            + DBHelper.TABLE_WORKING_TIME + ", "
                            + DBHelper.TABLE_REVIEWS
                            + " WHERE "
                            + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + " = " + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                            + " AND "
                            + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID + " = " + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                            + " AND "
                            + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + " = " + DBHelper.KEY_WORKING_TIME_ID_REVIEWS
                            + " AND "
                            + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + " = ? "
                            + " AND "
                            + DBHelper.KEY_TYPE_REVIEWS + " = ? "
                            + " AND "
                            + DBHelper.KEY_RATING_REVIEWS + " != 0";

            Cursor cursorUserReview = localDatabase.rawQuery(sqlQuery, new String[]{workingTimeId, REVIEW_FOR_USER});

            if (cursorUserReview.moveToFirst()) {
                cursorUserReview.close();
                return true;
            }
        }
        cursorServiceReview.close();
        return false;
    }

    public boolean isAfterWeek(String workingTimeId) {
        String date  = getDate(workingTimeId);
        WorkWithTimeApi workWithTimeApi = new WorkWithTimeApi();
        long dateMilliseconds = workWithTimeApi.getMillisecondsStringDate(date);
        boolean isAfterWeek = (workWithTimeApi.getSysdateLong() - dateMilliseconds) > 604800000;

        return isAfterWeek;
    }

}
