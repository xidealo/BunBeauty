package com.example.ideal.myapplication.createService;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MyCalendar extends AppCompatActivity implements View.OnClickListener {

    private static final String WORKING_DAYS = "working days";
    private static final String SERVICE_ID = "service id";
    private static final String WORKING_DAYS_ID = "working day id";
    private static final String STATUS_USER_BY_SERVICE = "status User";
    private static final String DATE = "date";
    private static final String USER = "user";
    private static final String WORKER = "worker";
    private static final String USERS = "users";
    private static final String SERVICES = "services";
    private static final int WEEKS_COUNT = 4;
    private static final int DAYS_COUNT = 7;

    private String statusUser;
    private String date;
    private String serviceId;
    private Button[][] dayBtns;
    private Button nextBtn;
    private RelativeLayout mainLayout;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_calendar);

        mainLayout = findViewById(R.id.mainMyCalendarLayout);
        nextBtn = findViewById(R.id.continueMyCalendarBtn);
        dayBtns = new Button[WEEKS_COUNT][DAYS_COUNT];

        dbHelper = new DBHelper(this);

        // получаем статус, чтобы определить, кто зашел, worker or User
        statusUser = getIntent().getStringExtra(STATUS_USER_BY_SERVICE);
        serviceId = getIntent().getStringExtra(SERVICE_ID);

        // создаём календарь
        createCalendar();

        nextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueMyCalendarBtn:
                if (statusUser.equals(WORKER)) {
                    if (isDaySelected()) {
                        addWorkingDay();
                    } else {
                        Toast.makeText(this, "Выбирите дату, на которую хотите настроить расписание", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (isDaySelected()) {
                        goToMyTime(checkCurrentDay(date), statusUser);
                    } else {
                        Toast.makeText(this, "Выбирите дату, на которую хотите записаться", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default:
                Button btn = (Button) v;
                // Проверка былв ли кнопка выбрана
                if (!Boolean.valueOf(btn.getTag(R.string.selectedId).toString())) {
                    // Не была выбрана - фокусируемся на ней
                    btn.setBackgroundResource(R.drawable.selected_day_button);
                    for (int i = 0; i < WEEKS_COUNT; i++) {
                        for (int j = 0; j < DAYS_COUNT; j++) {
                            if (Boolean.valueOf(dayBtns[i][j].getTag(R.string.selectedId).toString())) {
                                dayBtns[i][j].setTag(R.string.selectedId, false);
                                dayBtns[i][j].setBackgroundResource(R.drawable.day_button);
                                break;
                            }
                        }
                    }
                    date = convertDate(btn.getText().toString(), btn.getTag(R.string.yearId).toString());
                    btn.setTag(R.string.selectedId, true);
                } else {
                    // Была выбрана - снимаем выделение
                    btn.setTag(R.string.selectedId, false);
                    btn.setBackgroundResource(R.drawable.day_button);
                }
                break;
        }
    }

    private static final String TAG = "DBInf";

    //Выделяет рабочие дни
    private void selectWorkingDayWithTime() {
        String dayAndMonth, year;
        String dayId;
        int dayWithTimesColor = ContextCompat.getColor(this, R.color.dayWithTimes);
        int dayWithoutTimesColor = ContextCompat.getColor(this, R.color.black);
        for (int i = 0; i < WEEKS_COUNT; i++) {
            for (int j = 0; j < DAYS_COUNT; j++) {
                dayAndMonth = dayBtns[i][j].getText().toString();
                year = dayBtns[i][j].getTag(R.string.yearId).toString();

                dayId = checkCurrentDay(convertDate(dayAndMonth, year));
                if (!dayId.equals("0")) {
                    if (hasSomeWork(dayId)) {
                        dayBtns[i][j].setTextColor(dayWithTimesColor);
                    } else {
                        dayBtns[i][j].setTextColor(dayWithoutTimesColor);
                    }
                }
            }
        }
    }

    private boolean hasSomeWork(String dayId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

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

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{dayId});

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    // проверяет имеется ли у данного пользователя запись на услугу
    private void checkOrder() {
        //Если пользователь записан на какой-то день выделить только его
        date = getOrderDate(); // дата YYYY-mm-d
        if (!date.equals("")) {
            String[] arrDate = date.split("-");
            String orderDate = arrDate[2] + " " + monthToString(arrDate[1]);
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

                    dayId = checkCurrentDay(convertDate(dayAndMonth, year));
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

    //Возвращает дату записи
    private String getOrderDate() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String userId = getUserId();
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

    private void createCalendar() {
        Calendar calendar = Calendar.getInstance();

        Display display = getWindowManager().getDefaultDisplay();

        int margin = 6;
        int btnWidth = (display.getWidth() - (DAYS_COUNT + 1) * margin) / DAYS_COUNT;
        int btnHeight = btnWidth;
        //(display.getHeight()/2 - (WEEKS_COUNT+1)*6) / WEEKS_COUNT;

        String[] weekDays = {"ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС"};
        for (int i = 0; i < DAYS_COUNT; i++) {
            TextView weekDayText = new TextView(this);

            weekDayText.setX(i * (btnWidth + margin) + margin);
            weekDayText.setY(margin);
            weekDayText.setText(weekDays[i]);
            weekDayText.setGravity(Gravity.CENTER);
            weekDayText.setTypeface(ResourcesCompat.getFont(this, R.font.roboto_bold));
            weekDayText.setLayoutParams(new ViewGroup.LayoutParams(btnWidth, btnHeight));

            mainLayout.addView(weekDayText);
        }

        int dayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % DAYS_COUNT;
        int dayOfMonth, year;
        String stringMonth, month;

        //Создание календаря
        calendar.add(Calendar.DATE, -dayOfWeek);
        for (int i = 0; i < WEEKS_COUNT; i++) {
            for (int j = 0; j < DAYS_COUNT; j++) {
                dayBtns[i][j] = new Button(this);

                //положение, бэкграунд, размеры
                dayBtns[i][j].setX(j * (btnWidth + margin) + margin);
                dayBtns[i][j].setY((i + 1) * (btnHeight + margin) + margin);
                dayBtns[i][j].setBackgroundResource(R.drawable.day_button);
                dayBtns[i][j].setLayoutParams(new ViewGroup.LayoutParams(btnWidth, btnHeight));

                //тэги
                dayBtns[i][j].setTag(R.string.selectedId, false);
                year = calendar.get(Calendar.YEAR);
                dayBtns[i][j].setTag(R.string.yearId, year);

                //надпись
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
                if (month.length() == 1) {
                    month = "0" + month;
                }
                stringMonth = monthToString(month);
                dayBtns[i][j].setTextSize(11);
                dayBtns[i][j].setText(dayOfMonth + " " + stringMonth);

                //отрисовываем прошедше дни
                if ((j < dayOfWeek) && (i == 0)) {
                    dayBtns[i][j].setEnabled(false);
                    dayBtns[i][j].setBackgroundResource(R.drawable.disabled_button);
                } else {
                    dayBtns[i][j].setOnClickListener(this);
                }

                if (dayBtns[i][j].getParent() != null) {
                    ((ViewGroup) dayBtns[i][j].getParent()).removeView(dayBtns[i][j]);
                }
                mainLayout.addView(dayBtns[i][j]);
                calendar.add(Calendar.DATE, 1);
            }
        }
    }

    // Возвращает выбран ли како-либо день
    private boolean isDaySelected() {
        for (int i = 0; i < WEEKS_COUNT; i++) {
            for (int j = 0; j < DAYS_COUNT; j++) {
                if (dayBtns[i][j].isEnabled()) {
                    if (Boolean.valueOf(dayBtns[i][j].getTag(R.string.selectedId).toString())) {
                        date = convertDate(dayBtns[i][j].getText().toString(), dayBtns[i][j].getTag(R.string.yearId).toString());
                        return true;
                    }
                }
            }
        }
        return false;
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

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{dayId, dayId, getUserId()});

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }


    // Преобразует дату в формат БД
    private String convertDate(String dayAndMonth, String year) {
        String[] arrDate = dayAndMonth.split(" ");
        String day = arrDate[0];
        if (day.length() == 1) {
            day = "0" + day;
        }
        String month = monthToNumber(arrDate[1]);

        String convertedDate = year + "-" + month + "-" + day;

        return convertedDate;
    }

    // Добавляе рабочий день в БД
    private void addWorkingDay() {
        String id = checkCurrentDay(date);

        // Проверка на существование такого дня
        if (!id.equals("0")) {
            goToMyTime(id, statusUser);
        } else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference dateRef = database.getReference(USERS)
                    .child(getUserId())
                    .child(SERVICES)
                    .child(serviceId)
                    .child(WORKING_DAYS);

            Map<String, Object> items = new HashMap<>();
            items.put(DATE, date);

            String dayId = dateRef.push().getKey();
            dateRef = dateRef.child(dayId);
            dateRef.updateChildren(items);

            putDataInLocalStorage(serviceId, String.valueOf(dayId));

        }
    }

    private void putDataInLocalStorage(String serviceId, String dayId) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ID, dayId);
        contentValues.put(DBHelper.KEY_DATE_WORKING_DAYS, date);
        contentValues.put(DBHelper.KEY_SERVICE_ID_WORKING_DAYS, serviceId);

        database.insert(DBHelper.TABLE_WORKING_DAYS, null, contentValues);

        goToMyTime(dayId, statusUser);
    }

    //Возвращает id дня по id данного сервиса и дате
    private String checkCurrentDay(String day) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
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

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId, day});
        if (cursor.moveToFirst()) {
            int indexId = cursor.getColumnIndex(DBHelper.KEY_ID);
            return String.valueOf(cursor.getString(indexId));
        }
        cursor.close();
        return "0";
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FragmentManager manager = getSupportFragmentManager();
        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerMyCalendarLayout);
        panelBuilder.buildHeader(manager, "Расписание", R.id.headerMyCalendarLayout);

        // проверяем имеется ли у данного пользователя запись на данную услугу
        if (statusUser.equals(USER)) {
            checkOrder();
        } else {
            selectWorkingDayWithTime();
        }
    }

    private void goToMyTime(String dayId, String statusUser) {

        Intent intent = new Intent(this, MyTime.class);
        intent.putExtra(WORKING_DAYS_ID, dayId);
        intent.putExtra(STATUS_USER_BY_SERVICE, statusUser);
        intent.putExtra(SERVICE_ID, serviceId);

        startActivity(intent);
    }

    private String monthToString(String month) {
        switch (month) {
            case "01":
                return "янв";
            case "02":
                return "фев";
            case "03":
                return "мар";
            case "04":
                return "апр";
            case "05":
                return "май";
            case "06":
                return "июнь";
            case "07":
                return "июль";
            case "08":
                return "авг";
            case "09":
                return "сен";
            case "10":
                return "окт";
            case "11":
                return "ноя";
            case "12":
                return "дек";
        }

        return "";
    }

    private String monthToNumber(String month) {
        switch (month) {
            case "янв":
                return "01";
            case "фев":
                return "02";
            case "мар":
                return "03";
            case "апр":
                return "04";
            case "май":
                return "05";
            case "июнь":
                return "06";
            case "июль":
                return "07";
            case "авг":
                return "08";
            case "сен":
                return "09";
            case "окт":
                return "10";
            case "ноя":
                return "11";
            case "дек":
                return "12";
        }
        return "";
    }
}
