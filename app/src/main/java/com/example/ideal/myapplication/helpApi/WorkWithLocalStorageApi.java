package com.example.ideal.myapplication.helpApi;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ideal.myapplication.other.DBHelper;

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
