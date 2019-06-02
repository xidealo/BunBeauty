package com.example.ideal.myapplication.createService;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.ideal.myapplication.createService.user.UserCreateService;
import com.example.ideal.myapplication.createService.worker.WorkerCreateService;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MyCalendar extends AppCompatActivity implements View.OnClickListener {

    private static final String SERVICE_ID = "service id";
    private static final String WORKING_DAYS_ID = "working day id";
    private static final String STATUS_USER_BY_SERVICE = "status UserCreateService";
    private static final String WORKER = "worker";
    private static final int WEEKS_COUNT = 4;
    private static final int DAYS_COUNT = 7;

    private String statusUser;
    private String date;
    private String serviceId;
    private String userId;
    private Button[][] dayBtns;
    private Button nextBtn;
    private RelativeLayout mainLayout;
    private DBHelper dbHelper;
    private UserCreateService userCreateService;
    private WorkerCreateService workerCreateService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_calendar);

        mainLayout = findViewById(R.id.mainMyCalendarLayout);
        nextBtn = findViewById(R.id.continueMyCalendarBtn);
        dayBtns = new Button[WEEKS_COUNT][DAYS_COUNT];
        userId = getUserId();
        dbHelper = new DBHelper(this);

        // получаем статус, чтобы определить, кто зашел, worker or UserCreateService
        statusUser = getIntent().getStringExtra(STATUS_USER_BY_SERVICE);
        serviceId = getIntent().getStringExtra(SERVICE_ID);
        if (statusUser.equals(WORKER)) {
            workerCreateService = new WorkerCreateService(userId, serviceId, dbHelper);
        }
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
                        // Проверка на существование такого дня
                        if (!WorkWithLocalStorageApi.checkCurrentDay(date, serviceId).equals("0")) {
                            goToMyTime(WorkWithLocalStorageApi.checkCurrentDay(date, serviceId), statusUser);
                        } else {
                            //add
                            String dayId = workerCreateService.addWorkingDay(date);
                            goToMyTime(dayId, statusUser);
                        }
                    } else {
                        Toast.makeText(this, "Выбирите дату, на которую хотите настроить расписание", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (isDaySelected()) {
                        goToMyTime(WorkWithLocalStorageApi.checkCurrentDay(date, serviceId), statusUser);
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
                    date = WorkWithStringsApi.convertDateToYMD(btn.getText().toString(), btn.getTag(R.string.yearId).toString());
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

                dayId = WorkWithLocalStorageApi
                        .checkCurrentDay(WorkWithStringsApi.convertDateToYMD(dayAndMonth, year), serviceId);
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
                stringMonth = WorkWithStringsApi.monthToString(month);
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
                        date = WorkWithStringsApi
                                .convertDateToYMD(dayBtns[i][j]
                                                .getText()
                                                .toString(),
                                        dayBtns[i][j]
                                                .getTag(R.string.yearId)
                                                .toString());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // проверяет имеется ли у данного пользователя запись на услугу
    public void checkOrder() {
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
                            .checkCurrentDay(WorkWithStringsApi.convertDateToYMD(dayAndMonth, year), serviceId);
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
        if (statusUser.equals(WORKER)) {
            selectWorkingDayWithTime();
        } else {
            checkOrder();
        }
    }

    private void goToMyTime(String dayId, String statusUser) {
        Intent intent = new Intent(this, MyTime.class);
        intent.putExtra(WORKING_DAYS_ID, dayId);
        intent.putExtra(STATUS_USER_BY_SERVICE, statusUser);
        intent.putExtra(SERVICE_ID, serviceId);
        startActivity(intent);
    }
}
