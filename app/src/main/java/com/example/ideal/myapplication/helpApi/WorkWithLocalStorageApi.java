package com.example.ideal.myapplication.helpApi;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ImageView;

import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class WorkWithLocalStorageApi {

    static private SQLiteDatabase localDatabase;

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

    public void setPhotoAvatar(String userId, ImageView avatarImage, int width, int height) {
        //получаем имя, фамилию и город пользователя по его id

        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_PHOTO_LINK_PHOTOS
                        + " FROM "
                        + DBHelper.TABLE_PHOTOS
                        + " WHERE "
                        + DBHelper.KEY_ID + " = ?";

        Cursor cursor = localDatabase.rawQuery(sqlQuery, new String[]{userId});

        if (cursor.moveToFirst()) {
            int indexPhotoLink = cursor.getColumnIndex(DBHelper.KEY_PHOTO_LINK_PHOTOS);

            String photoLink = cursor.getString(indexPhotoLink);
            //установка аватарки
            Picasso.get()
                    .load(photoLink)
                    .resize(width, height)
                    .centerCrop()
                    .transform(new CircularTransformation())
                    .into(avatarImage);
        }
        cursor.close();
    }

    static public Cursor getServiceCursorByTimeId(String workingTimeId) {
        //Возвращает serviceId, использую workingTimeId
        //таблицы working time, working days, services
        //связь таблиц, уточнение по workingTimeId
        String sqlQuery =
                "SELECT "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + ", "
                        + DBHelper.KEY_NAME_SERVICES + ", "
                        + DBHelper.KEY_USER_ID + ", "
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

    boolean hasSomeData(String tableName, String id) {

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

    static public boolean hasAvailableTime(String serviceId, String userId) {
        // Получаем всё время данного сервиса, которое доступно данному юзеру
        String busyTimeQuery = "SELECT "
                + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                + " FROM "
                + DBHelper.TABLE_WORKING_DAYS + ", "
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_ORDERS
                + " WHERE "
                + DBHelper.KEY_SERVICE_ID_WORKING_DAYS + " = ?"
                + " AND "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_WORKING_TIME_ID_ORDERS + " = "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_IS_CANCELED_ORDERS + " = 'false'";

        String myTimeQuery = "SELECT "
                + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                + " FROM "
                + DBHelper.TABLE_WORKING_DAYS + ", "
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_ORDERS
                + " WHERE "
                + DBHelper.KEY_SERVICE_ID_WORKING_DAYS + " = ?"
                + " AND "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_WORKING_TIME_ID_ORDERS + " = "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_USER_ID + " = ?"
                + " AND "
                + DBHelper.KEY_IS_CANCELED_ORDERS + " = 'false'";

        String sqlQuery = "SELECT "
                + DBHelper.KEY_TIME_WORKING_TIME
                + " FROM "
                + DBHelper.TABLE_WORKING_DAYS + ", "
                + DBHelper.TABLE_WORKING_TIME
                + " WHERE "
                + DBHelper.KEY_SERVICE_ID_WORKING_DAYS + " = ?"
                + " AND "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                + " AND ((("
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " NOT IN (" + busyTimeQuery + ")"
                + " AND ("
                // 3 часа - разница с Гринвичем
                // 2 часа - минимум времени до сеанса, чтобы за писаться
                + "(STRFTIME('%s', 'now')+(3+2)*60*60) - STRFTIME('%s',"
                + DBHelper.KEY_DATE_WORKING_DAYS
                + "||' '||" + DBHelper.KEY_TIME_WORKING_TIME
                + ") <= 0)"
                + ") OR (("
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + " IN (" + myTimeQuery + ")"
                + ") AND ("
                + "(STRFTIME('%s', 'now')+3*60*60) - (STRFTIME('%s',"
                + DBHelper.KEY_DATE_WORKING_DAYS
                + "||' '||" + DBHelper.KEY_TIME_WORKING_TIME
                + ")) <= 0))))";

        Cursor cursor = localDatabase.rawQuery(sqlQuery, new String[]{serviceId, serviceId, serviceId, userId});
        boolean result = cursor.moveToFirst();
        cursor.close();

        return result;
    }

    public Cursor getUser(String userId) {
        String sqlQuery = "SELECT * FROM "
                + DBHelper.TABLE_CONTACTS_USERS
                + " WHERE "
                + DBHelper.KEY_ID + " = ?";

        return localDatabase.rawQuery(sqlQuery, new String[]{userId});
    }

    public Cursor getService(String serviceId) {
        String sqlQuery = "SELECT * FROM "
                + DBHelper.TABLE_CONTACTS_SERVICES
                + " WHERE "
                + DBHelper.KEY_ID + " = ?";

        return localDatabase.rawQuery(sqlQuery, new String[]{serviceId});
    }

    private String getDate(String workingTimeId) {
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

    static public boolean hasSomeWork(String dayId) {
        // Получает id рабочего дня
        // Таблицы: рабочие время
        // Условия: уточняем id рабочего дня
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_ID
                        + " FROM "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = ? ";

        Cursor cursor = localDatabase.rawQuery(sqlQuery, new String[]{dayId});

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public boolean isMutualReview(String orderId) {

        String sqlQuery = "SELECT "
                + DBHelper.KEY_RATING_REVIEWS
                + " FROM "
                + DBHelper.TABLE_REVIEWS + ", "
                + DBHelper.TABLE_ORDERS
                + " WHERE "
                + DBHelper.KEY_ORDER_ID_REVIEWS
                + " = "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID + " = ?"
                + " AND "
                + DBHelper.KEY_RATING_REVIEWS + " != 0";

        Cursor cursor = localDatabase.rawQuery(sqlQuery, new String[]{orderId});
        int counter = 0;
        if (cursor.moveToFirst()) {
            do {
                counter++;
            } while (cursor.moveToNext());
            cursor.close();
            return counter == 2;
        }
        return false;
    }

    public boolean isAfterThreeDays(String workingTimeId) {
        String date = getDate(workingTimeId);
        WorkWithTimeApi workWithTimeApi = new WorkWithTimeApi();
        long dateMilliseconds = workWithTimeApi.getMillisecondsStringDate(date);
        boolean isAfterWeek = (workWithTimeApi.getSysdateLong() - dateMilliseconds) > 259200000;

        return isAfterWeek;
    }

    //Возвращает id дня по id данного сервиса и дате
    static public String checkCurrentDay(String day, String serviceId) {
        // Получает id рабочего дня
        // Таблицы: рабочии дни
        // Условия: уточняем id сервиса и дату
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_ID
                        + " FROM "
                        + DBHelper.TABLE_WORKING_DAYS
                        + " WHERE "
                        + DBHelper.KEY_SERVICE_ID_WORKING_DAYS + " = ? AND "
                        + DBHelper.KEY_DATE_WORKING_DAYS + " = ? ";

        Cursor cursor = localDatabase.rawQuery(sqlQuery, new String[]{serviceId, day});
        if (cursor.moveToFirst()) {
            int indexId = cursor.getColumnIndex(DBHelper.KEY_ID);
            return String.valueOf(cursor.getString(indexId));
        }
        cursor.close();
        return "0";
    }

    // Проверяет есть ли какие-либо записи на данное время
    static public boolean checkTimeForWorker(String workingDaysId, String time) {
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_TIME_WORKING_TIME
                        + " FROM "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = ?"
                        + " AND "
                        + DBHelper.KEY_TIME_WORKING_TIME + " = ?";

        Cursor cursor = localDatabase.rawQuery(sqlQuery, new String[]{workingDaysId, time});

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }

        cursor.close();
        return false;
    }

    static public String getWorkingTimeId(String time, String workingDaysId) {
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_ID
                        + " FROM "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.KEY_TIME_WORKING_TIME + " = ? AND "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = ?";

        Cursor cursor = localDatabase.rawQuery(sqlQuery, new String[]{time, workingDaysId});
        String timeId = "";
        if (cursor.moveToFirst()) {
            int indexTimeId = cursor.getColumnIndex(DBHelper.KEY_ID);
            timeId = cursor.getString(indexTimeId);
        }

        cursor.close();
        return timeId;
    }

    static public String getUserPhone() {
        return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    }

    static public String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
