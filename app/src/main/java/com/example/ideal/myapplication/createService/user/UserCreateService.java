package com.example.ideal.myapplication.createService.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.example.ideal.myapplication.other.DBHelper;

public class UserCreateService implements IUser {

    private static final int WEEKS_COUNT = 4;
    private static final int DAYS_COUNT = 7;
    private DBHelper dbHelper;
    private String userId;
    private String serviceId;

    public UserCreateService(DBHelper _dbHelper, String _userId, String _serviceId) {
        dbHelper = _dbHelper;
        userId = _userId;
        serviceId = _serviceId;
    }

    @Override
    // проверяет имеется ли у данного пользователя запись на услугу
    public void checkOrder(Button[][] dayBtns) {
        //Если пользователь записан на какой-то день выделить только его
        String date = getOrderDate(); // дата YYYY-mm-d
        if (!date.equals("")) {
            String[] arrDate = date.split("-");
            String orderDate = arrDate[2] + " " + WorkWithStringsApi.monthToString(arrDate[1]);
            if (orderDate.charAt(0) == '0') {
                orderDate = orderDate.substring(1);
            }

            for (int i = 0; i < WEEKS_COUNT; i++) {
                for (int j = 0; j < DAYS_COUNT; j++) {
                    if (orderDate.equals(dayBtns[i][j].getText().toString()) && arrDate[0].equals(dayBtns[i][j].getTag(R.string.yearId).toString())) {
                        dayBtns[i][j].setBackgroundResource(R.drawable.selected_day_button);
                        dayBtns[i][j].setTag(R.string.selectedId, true);
                        dayBtns[i][j].setClickable(false);
                    } else {
                        dayBtns[i][j].setTag(R.string.selectedId, false);
                        dayBtns[i][j].setEnabled(false);
                        dayBtns[i][j].setBackgroundResource(R.drawable.disabled_button);
                    }
                }
            }
        } else {
            // Если записи на данный сервис нет, отключаем всё нерабочие дни
            String dayAndMonth, year;
            String dayId;

            for (int i = 0; i < WEEKS_COUNT; i++) {
                for (int j = 0; j < DAYS_COUNT; j++) {
                    dayAndMonth = dayBtns[i][j].getText().toString();
                    year = dayBtns[i][j].getTag(R.string.yearId).toString();

                    dayId = WorkWithLocalStorageApi
                            .checkCurrentDay(WorkWithStringsApi.convertDateToYMD(dayAndMonth, year),serviceId);
                    if (dayId.equals("0")) {
                        dayBtns[i][j].setEnabled(false);
                        dayBtns[i][j].setBackgroundResource(R.drawable.disabled_button);
                    } else {
                        if (!hasSomeTime(dayId)) {
                            dayBtns[i][j].setEnabled(false);
                            dayBtns[i][j].setBackgroundResource(R.drawable.disabled_button);
                        }
                    }
                }
            }
        }
    }

    // Возвращает есть ли в рабочем дне рабочие часы
    private boolean hasSomeTime(String dayId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // Проверяет есть ли доступное время на данный день по его id
        String takedTimeQuery = "SELECT "
                + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                + " FROM "
                + DBHelper.TABLE_WORKING_DAYS + ", "
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_ORDERS
                + " WHERE "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = ?"
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
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_ORDERS
                + " WHERE "
                + DBHelper.KEY_WORKING_TIME_ID_ORDERS + " = "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_USER_ID + " = ?"
                + " AND "
                + DBHelper.KEY_IS_CANCELED_ORDERS + " = 'false'";

        String sqlQuery = "SELECT "
                + DBHelper.KEY_TIME_WORKING_TIME
                + " FROM "
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_WORKING_DAYS
                + " WHERE "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = ?"
                + " AND ((("
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " NOT IN (" + takedTimeQuery + ")"
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

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{dayId, dayId, userId});

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    //Возвращает дату записи
    private String getOrderDate() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // Получает дату записи
        // Таблицы: рабочии дни, рабочие время
        // Условия: связываем таблицы по id рабочего дня; уточняем id сервиса и id пользователя
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_DATE_WORKING_DAYS
                        + " FROM "
                        + DBHelper.TABLE_ORDERS + ", "
                        + DBHelper.TABLE_WORKING_TIME + ", "
                        + DBHelper.TABLE_WORKING_DAYS
                        + " WHERE "
                        + DBHelper.KEY_SERVICE_ID_WORKING_DAYS + " = ?"
                        + " AND "
                        + DBHelper.KEY_USER_ID + " = ? "
                        + " AND "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                        + " = " + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                        + " AND "
                        + DBHelper.KEY_IS_CANCELED_ORDERS + " = 'false'"
                        + " AND "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                        + " = " + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                        + " AND ("
                        + "(STRFTIME('%s', 'now')+3*60*60) - (STRFTIME('%s',"
                        + DBHelper.KEY_DATE_WORKING_DAYS
                        + "||' '||" + DBHelper.KEY_TIME_WORKING_TIME
                        + ")) <= 0)";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId, userId});
        if (cursor.moveToFirst()) {
            int indexDate = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS);
            String orderDate = cursor.getString(indexDate);
            cursor.close();
            return orderDate;
        } else {
            cursor.close();
            return "";
        }
    }
}

