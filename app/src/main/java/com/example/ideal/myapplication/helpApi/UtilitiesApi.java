package com.example.ideal.myapplication.helpApi;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ideal.myapplication.other.DBHelper;

public class UtilitiesApi {
    public boolean isMyService(String serviceId, String myPhone, SQLiteDatabase localDatabase) {
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
