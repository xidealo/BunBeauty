package com.example.ideal.myapplication.helpApi;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.ideal.myapplication.other.DBHelper;
import com.squareup.picasso.Picasso;

public class WorkWithLocalStorageApi {

    private SQLiteDatabase localDatabase;

    public WorkWithLocalStorageApi(SQLiteDatabase database){
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
        if(cursor.moveToFirst()){
            isMyService = true;
        }
        else {
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
                        + DBHelper.TABLE_CONTACTS_SERVICES +"."+ DBHelper.KEY_ID + ", "
                        + DBHelper.KEY_NAME_SERVICES + ", "
                        + DBHelper.KEY_DATE_WORKING_DAYS + ", "
                        + DBHelper.KEY_TIME_WORKING_TIME
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.TABLE_WORKING_TIME + "."+DBHelper.KEY_ID + " = ? "
                        + " AND "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                        + " = "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                        + " = "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID ;
        Cursor cursor = localDatabase.rawQuery(sqlQuery, new String[]{workingTimeId});

        return cursor;
    }

    public boolean hasSomeData(String tableName, String id){

        String sqlQuery = "SELECT * FROM "
                + tableName
                + " WHERE "
                + DBHelper.KEY_ID + " = ?";

        Cursor cursor = localDatabase.rawQuery(sqlQuery, new String[]{id});

        if(cursor.moveToFirst()){
            cursor.close();
            return true;
        }
        else {
            cursor.close();
            return false;
        }
    }

    public boolean hasSomeDataForUsers(String tableName, String id){

        String sqlQuery = "SELECT * FROM "
                + tableName
                + " WHERE "
                + DBHelper.KEY_USER_ID + " = ?";

        Cursor cursor = localDatabase.rawQuery(sqlQuery, new String[]{id});

        if(cursor.moveToFirst()){
            cursor.close();
            return true;
        }
        else {
            cursor.close();
            return false;
        }
    }
}
