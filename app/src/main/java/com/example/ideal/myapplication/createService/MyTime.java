package com.example.ideal.myapplication.createService;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyTime extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DBInf";
    private static final String WORKING_DAYS_ID = "working day id";
    private static final String WORKING_TIME = "working time";
    private static final String WORKING_DAYS = "working days";
    private static final String SERVICES = "services";

    private static final String USERS = "users";
    private static final String USER_ID = "user id";

    private static final String SERVICE_ID = "service id";
    private static final String STATUS_USER_BY_SERVICE = "status User";
    private static final String TIME = "time";

    private static final String WORKER = "worker";

    private static final int ROWS_COUNT = 6;
    private static final int COLUMNS_COUNT = 4;
    private static final String ORDERS = "orders";
    private static final String REVIEWS = "reviews";
    private static final String IS_CANCELED = "is canceled";
    private static final String WORKER_ID = "worker id";

    private static final String RATING = "rating";
    private static final String REVIEW = "review";
    private static final String TYPE = "type";
    private static final String REVIEW_FOR_SERVICE = "review for service";
    private static final String REVIEW_FOR_USER = "review for user";

    private String statusUser;
    private String userId;
    private String workingDaysId;
    private String serviceId;
    private int width;
    private int height;
    private WorkWithTimeApi workWithTimeApi;
    private WorkWithLocalStorageApi LSApi;

    private Button[][] timeBtns;

    //временный буфер добавленного рабочего времени
    private ArrayList<String> workingHours;
    //временный буфер удалённого рабочего времени
    private ArrayList<String> removedHours;

    private DBHelper dbHelper;
    private RelativeLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_time);

        statusUser = getIntent().getStringExtra(STATUS_USER_BY_SERVICE);

        userId = getUserId();
        workingDaysId = getIntent().getStringExtra(WORKING_DAYS_ID);
        serviceId = getIntent().getStringExtra(SERVICE_ID);

        mainLayout = findViewById(R.id.mainMyTimeLayout);

        timeBtns = new Button[ROWS_COUNT][COLUMNS_COUNT];
        Button saveBtn = findViewById(R.id.saveMyTimeBtn);

        SwitchCompat amOrPmMyTimeSwitch = findViewById(R.id.amOrPmMyTimeSwitch);

        FragmentManager manager = getSupportFragmentManager();
        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerMyTimeLayout);
        panelBuilder.buildHeader(manager, "Расписание", R.id.headerMyTimeLayout);

        //инициализация буферов
        workingHours = new ArrayList<>();
        removedHours = new ArrayList<>();

        //получение парамтров экрана
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();

        dbHelper = new DBHelper(this);
        workWithTimeApi = new WorkWithTimeApi();

        LSApi = new WorkWithLocalStorageApi(dbHelper.getReadableDatabase());

        addButtonsOnScreen(false);

        checkCurrentTimes();

        amOrPmMyTimeSwitch.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isPm) {
                // Очищаем layout
                mainLayout.removeAllViews();
                if (isPm) {
                    buttonView.setText("Вторая половина дня");
                    // создаем кнопки с нужным временем
                    addButtonsOnScreen(true);

                } else {
                    buttonView.setText("Первая половина дня");
                    // создаем кнопки с нужным временем
                    addButtonsOnScreen(false);
                }
                // Выделяет кнопки
                checkCurrentTimes();
                // Выделяет кнопки хронящиеся в буфере рабочих дней
                checkWorkingHours();
                // Снимает выделение с кнопок хронящихся в буфере удалённых дней
                checkRemovedHours();
            }
        });

        saveBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveMyTimeBtn:
                if (statusUser.equals(WORKER)) {
                    if (workingHours.size() > 0) {
                        // Добавляем время из буфера workingHours в БД
                        addTime();
                    }
                    if (removedHours.size() > 0) {
                        // Удаляем время сохранённое в буфере removeHours в БД
                        deleteTime();
                    }
                    Toast.makeText(this, "Расписанеие обновлено", Toast.LENGTH_SHORT).show();
                } else {
                    if (workingHours.size() == 1) {
                        loadInformationAboutService(getWorkingTimeId());
                    }
                }
                break;

            default:
                Button btn = (Button) v;
                String btnText = btn.getText().toString();
                if (btnText.length() == 4) {
                    btnText = "0" + btnText;
                }
                // Проверка мой ли это сервис (я - worker)
                if (statusUser.equals(WORKER)) {
                    // Это мой сервис (я - worker)

                    if (Boolean.valueOf((btn.getTag(R.string.selectedId)).toString())) {
                        btn.setBackgroundResource(R.drawable.time_button);
                        workingHours.remove(btnText);
                        removedHours.add(btnText);
                        btn.setTag(R.string.selectedId, false);
                    } else {
                        btn.setBackgroundResource(R.drawable.pressed_button);
                        workingHours.add(btnText);
                        removedHours.remove(btnText);
                        btn.setTag(R.string.selectedId, true);
                    }
                } else {
                    // Это не мой сервис (я - User)

                    // Проверка была ли кнопка выбрана до нажатия
                    if (Boolean.valueOf((btn.getTag(R.string.selectedId)).toString())) {
                        // Кнопка была уже нажата

                        btn.setBackgroundResource(R.drawable.time_button);
                        workingHours.remove(btnText);
                        btn.setTag(R.string.selectedId, false);
                    } else {
                        // Кнопка не была нажата до клика

                        String selectedTime;
                        //Если уже существует выбранное время
                        if (workingHours.size() == 1) {
                            selectedTime = workingHours.get(0);
                            removeSelection(selectedTime);
                            workingHours.clear();
                        }
                        btn.setBackgroundResource(R.drawable.pressed_button);
                        workingHours.add(btnText);
                        btn.setTag(R.string.selectedId, true);
                    }
                }
                break;
        }
    }

    // Спрашиваем, действительно ли записать на срвис
    public void confirm(String serviceName, String dataDay, String time) {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Запись на услугу");
        dialog.setMessage("Записаться на услугу " + serviceName + " " + dataDay + " числа в " + time);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                makeOrder();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
            }
        });
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
    }

    // Подгружаем информацию о сервисе
    private void loadInformationAboutService(String workingTimeId) {

        Cursor cursor = LSApi.getServiceCursorByTimeId(workingTimeId);

        if (cursor.moveToFirst()) {
            int indexNameService = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            int indexDateDay = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS);
            int indexTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);

            String serviceName = cursor.getString(indexNameService);
            String dataDay = cursor.getString(indexDateDay);
            String time = cursor.getString(indexTime);

            confirm(serviceName, dataDay, time);
        }
    }

    // Снимает выделение с кнопок хронящихся в буфере удалённых дней
    private void checkRemovedHours() {
        for (int i = 0; i < ROWS_COUNT; i++) {
            for (int j = 0; j < COLUMNS_COUNT; j++) {
                String time = (String) timeBtns[i][j].getText();
                if (removedHours.contains(time)) {
                    timeBtns[i][j].setBackgroundResource(R.drawable.day_button);
                    timeBtns[i][j].setTag(R.string.selectedId, false);
                }
            }
        }
    }

    // Выделяет кнопки хранящиеся в буфере рабочих дней
    private void checkWorkingHours() {
        for (int i = 0; i < ROWS_COUNT; i++) {
            for (int j = 0; j < COLUMNS_COUNT; j++) {
                String time = (String) timeBtns[i][j].getText();
                if (time.length() == 4) {
                    time = "0" + time;
                }
                if (workingHours.contains(time)) {
                    timeBtns[i][j].setBackgroundResource(R.drawable.pressed_button);
                    timeBtns[i][j].setTag(R.string.selectedId, true);
                }
            }
        }
    }

    //Выделяет необходимые кнопки
    private void checkCurrentTimes() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // Проверка на то, что это мой сервис
        if (statusUser.equals(WORKER)) {
            // Это мой сервис (я - worker)
            selectBtsForWorker();
        } else {
            // Это не мой сервис (я - User)
            selectBtsForUser();
        }
    }

    // Выделяет кнопки (worker)
    private void selectBtsForWorker() {
        for (int i = 0; i < ROWS_COUNT; i++) {
            for (int j = 0; j < COLUMNS_COUNT; j++) {
                String time = (String) timeBtns[i][j].getText();
                if (time.length() == 4) {
                    time = "0" + time;
                }

                //Проверка является ли данное время рабочим
                if (checkTimeForWorker(time)) {
                    timeBtns[i][j].setBackgroundResource(R.drawable.pressed_button);
                    timeBtns[i][j].setTag(R.string.selectedId, true);


                    // Проверка записан ли кто-то на это время
                    if (!isFreeTime(time)) {
                        timeBtns[i][j].setEnabled(false);
                    }
                }
            }
        }
    }

    // Выделяет кнопки (User)
    private void selectBtsForUser() {
        // Время на которое я записан
        String myOrderTime = checkMyOrder();

        for (int i = 0; i < ROWS_COUNT; i++) {
            for (int j = 0; j < COLUMNS_COUNT; j++) {
                String time = (String) timeBtns[i][j].getText();
                if (time.length() == 4) {
                    time = "0" + time;
                }

                //Проверка на наличие записи на данный день
                if (!myOrderTime.equals("")) {
                    // Есть запись

                    // Проверка на то, что я записан на данное время
                    if (myOrderTime.equals(time)) {
                        timeBtns[i][j].setClickable(false);
                        timeBtns[i][j].setBackgroundResource(R.drawable.pressed_button);
                        timeBtns[i][j].setTag(R.string.selectedId, true);
                    } else {
                        timeBtns[i][j].setBackgroundResource(R.drawable.disabled_button);
                        timeBtns[i][j].setEnabled(false);
                    }
                } else {
                    // Записи нет

                    // Проверка является ли данное время свободным
                    if (isFreeTime(time)) {
                        timeBtns[i][j].setBackgroundResource(R.drawable.time_button);
                        timeBtns[i][j].setTag(R.string.selectedId, false);
                    } else {
                        timeBtns[i][j].setBackgroundResource(R.drawable.disabled_button);
                        timeBtns[i][j].setEnabled(false);
                    }
                }

            }
        }
    }

    //Снимает выделение с кнопки с данным временем
    private void removeSelection(String selectedTime) {
        for (int i = 0; i < ROWS_COUNT; i++) {
            for (int j = 0; j < COLUMNS_COUNT; j++) {
                String time = (String) timeBtns[i][j].getText();

                if (time.equals(selectedTime)) {
                    timeBtns[i][j].setBackgroundResource(R.drawable.time_button);
                    timeBtns[i][j].setTag(R.string.selectedId, false);
                }
            }
        }
    }

    // Добавляем время из буфера workingTime в БД
    private void addTime() {
        workingDaysId = getIntent().getStringExtra(WORKING_DAYS_ID);
        String serviceId = getIntent().getStringExtra(SERVICE_ID);

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Получает время
        // Таблицы: рабочие время
        // Условия: уточняем id рабочего дня
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_TIME_WORKING_TIME
                        + " FROM "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{String.valueOf(workingDaysId)});
        ContentValues contentValues = new ContentValues();

        for (String time : workingHours) {
            if (!checkTimeForWorker(time)) {

                FirebaseDatabase fdatabase = FirebaseDatabase.getInstance();
                DatabaseReference timeRef = fdatabase.getReference(USERS)
                        .child(userId)
                        .child(SERVICES)
                        .child(serviceId)
                        .child(WORKING_DAYS)
                        .child(workingDaysId)
                        .child(WORKING_TIME);

                Map<String, Object> items = new HashMap<>();
                items.put(TIME, time);

                String timeId = timeRef.push().getKey();
                timeRef = timeRef.child(timeId);
                timeRef.updateChildren(items);

                putDataInLocalStorage(timeId, time, contentValues, database);
            }
        }

        workingHours.clear();
        cursor.close();
    }

    private void putDataInLocalStorage(String timeId, String time, ContentValues contentValues,
                                       SQLiteDatabase database) {

        contentValues.put(DBHelper.KEY_ID, timeId);
        contentValues.put(DBHelper.KEY_TIME_WORKING_TIME, time);
        contentValues.put(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME, workingDaysId);

        database.insert(DBHelper.TABLE_WORKING_TIME, null, contentValues);
    }

    // Проверяет есть ли запись на данный день
    private String checkMyOrder() {

        SQLiteDatabase database = dbHelper.getReadableDatabase();

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
                + " AND "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + " IN (" + myTimeQuery + ")"
                + " AND "
                + "(STRFTIME('%s', 'now')+3*60*60) - (STRFTIME('%s',"
                + DBHelper.KEY_DATE_WORKING_DAYS
                + "||' '||" + DBHelper.KEY_TIME_WORKING_TIME
                + ")) <= 0";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{workingDaysId, userId});

        String time = "";
        if (cursor.moveToFirst()) {
            int timeIndex = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);
            time = cursor.getString(timeIndex);
        }

        cursor.close();
        return time;
    }

    // заносим данные о записи в БД
    private void makeOrder() {

        String workingTimeId = getWorkingTimeId();
        String serviceOwnerId = getServiceOwnerId(workingTimeId);
        String orderId = makeOrderForService(workingTimeId, serviceOwnerId);
        makeOrderForUser(orderId, serviceOwnerId);

        Toast.makeText(this, "Вы записались на услугу!", Toast.LENGTH_SHORT).show();
    }

    // Создаёт запись в разделе Сервис
    private String makeOrderForService(String workingTimeId, String serviceOwnerId) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Добавляем информацию о записи в LocalStorage и Firebase
        DatabaseReference myRef = database
                .getReference(USERS)
                .child(serviceOwnerId)
                .child(SERVICES)
                .child(serviceId)
                .child(WORKING_DAYS)
                .child(workingDaysId)
                .child(WORKING_TIME)
                .child(workingTimeId)
                .child(ORDERS);
        String orderId = myRef.push().getKey();

        String messageTime = workWithTimeApi.getDateInFormatYMDHMS(new Date());

        Map<String, Object> items = new HashMap<>();
        items.put(IS_CANCELED, false);
        items.put(USER_ID, userId);
        items.put(TIME, messageTime);

        myRef = myRef.child(orderId);
        myRef.updateChildren(items);

        addOrderInLocalStorage(orderId, workingTimeId, messageTime);

        //закрашиваем, чтобы нельзя было заисаться еще раз
        checkCurrentTimes();

        // создаём отзыв дял сервиса
        makeReview(myRef, REVIEW_FOR_SERVICE);

        return orderId;
    }

    // Создаёт запись в разделе User
    private void makeOrderForUser(String orderId, String workerId) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database
                .getReference(USERS)
                .child(userId)
                .child(ORDERS)
                .child(orderId);

        Map<String, Object> items = new HashMap<>();
        items.put(WORKER_ID, workerId);
        items.put(SERVICE_ID, serviceId);

        myRef.updateChildren(items);

        // создаём отзыв для пользователя
        makeReview(myRef, REVIEW_FOR_USER);
    }

    // создаёт пустой отзыв по указанной ссылке на запись и типу
    private void makeReview(DatabaseReference myRef, String type) {
        String orderId = myRef.getKey();

        myRef = myRef.child(REVIEWS);
        String reviewId = myRef.push().getKey();
        myRef = myRef.child(reviewId);

        Map<String, Object> items = new HashMap<>();
        items.put(RATING, 0);
        items.put(REVIEW, "");
        items.put(TYPE, type);
        myRef.updateChildren(items);

        addReviewInLocalStorage(orderId, reviewId, type);
    }

    public void addReviewInLocalStorage(String orderId, String reviewId, String type) {
        SQLiteDatabase localDatabase = dbHelper.getWritableDatabase();

        Log.d(TAG,  " | type = " + type);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ID, reviewId);
        contentValues.put(DBHelper.KEY_REVIEW_REVIEWS, "");
        contentValues.put(DBHelper.KEY_RATING_REVIEWS, "0");
        contentValues.put(DBHelper.KEY_TYPE_REVIEWS, type);
        contentValues.put(DBHelper.KEY_ORDER_ID_REVIEWS, orderId);

        localDatabase.insert(DBHelper.TABLE_REVIEWS, null, contentValues);
    }

    private String getServiceOwnerId(String workingTimeId) {

        Cursor cursor = LSApi.getServiceCursorByTimeId(workingTimeId);

        String ownerId = "";
        if (cursor.moveToFirst()) {
            int indexOwnerId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);
            ownerId = cursor.getString(indexOwnerId);
        }

        cursor.close();
        return ownerId;
    }

    private String getWorkingTimeId() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_ID
                        + " FROM "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.KEY_TIME_WORKING_TIME + " = ? AND "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{workingHours.get(0), String.valueOf(workingDaysId)});
        String timeId = "";
        if (cursor.moveToFirst()) {
            int indexTimeId = cursor.getColumnIndex(DBHelper.KEY_ID);
            timeId = cursor.getString(indexTimeId);
        }

        cursor.close();
        return timeId;
    }

    private void addOrderInLocalStorage(String orderId, String timeId, String messageTime) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_ID, orderId);
        contentValues.put(DBHelper.KEY_USER_ID, userId);
        contentValues.put(DBHelper.KEY_IS_CANCELED_ORDERS, "false");
        contentValues.put(DBHelper.KEY_WORKING_TIME_ID_ORDERS, timeId);
        contentValues.put(DBHelper.KEY_MESSAGE_TIME_ORDERS, messageTime);

        database.insert(DBHelper.TABLE_ORDERS, null, contentValues);
        workingHours.clear();
    }

    private void deleteTime() {
        workingDaysId = getIntent().getStringExtra(WORKING_DAYS_ID);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //получаю время кнопки, на которую нажал

        //получаем все время этого дня
        final DatabaseReference timeRef = database.getReference(USERS)
                .child(userId)
                .child(SERVICES)
                .child(serviceId)
                .child(WORKING_DAYS)
                .child(workingDaysId)
                .child(WORKING_TIME);

        timeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot timesSnapshot) {
                for (String hours : removedHours) {
                    for (DataSnapshot time : timesSnapshot.getChildren()) {
                        if (String.valueOf(time.child(TIME).getValue()).equals(hours)) {
                            String timeId = String.valueOf(time.getKey());

                            timeRef.child(timeId).removeValue();

                            deleteTimeFromLocalStorage();
                        }
                    }
                }
                //очищаяем массив
                removedHours.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void deleteTimeFromLocalStorage() {
        workingDaysId = getIntent().getStringExtra(WORKING_DAYS_ID);

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        for (String time : removedHours) {
            if (checkTimeForWorker(time)) {
                database.delete(
                        DBHelper.TABLE_WORKING_TIME,
                        DBHelper.KEY_TIME_WORKING_TIME + " = ? AND "
                                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = ?",
                        new String[]{time, String.valueOf(workingDaysId)});
            }
        }
    }

    // Проверяет есть ли какие-либо записи на данное время
    private boolean checkTimeForWorker(String time) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_TIME_WORKING_TIME
                        + " FROM "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = ?"
                        + " AND "
                        + DBHelper.KEY_TIME_WORKING_TIME + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{workingDaysId, time});

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }

        cursor.close();
        return false;
    }

    private boolean isFreeTime(String time) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String busyTimeQuery = "SELECT "
                + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                + " FROM "
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_ORDERS
                + " WHERE "
                + DBHelper.KEY_WORKING_TIME_ID_ORDERS + " = "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = ?"
                + " AND "
                + DBHelper.KEY_IS_CANCELED_ORDERS + " = 'false'";

        String extraCondition = "";
        if (!statusUser.equals(WORKER)) {

            // 3 часа - разница с Гринвичем
            // 2 часа - минимум времени до сеанса, чтобы за писаться
            extraCondition = " AND "
                    + " ((STRFTIME('%s', 'now')+(3+2)*60*60) - STRFTIME('%s',"
                    + DBHelper.KEY_DATE_WORKING_DAYS
                    + "||' '||" + DBHelper.KEY_TIME_WORKING_TIME
                    + ")) <= 0";
        }

        String sqlQuery = "SELECT "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " FROM "
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_WORKING_DAYS
                + " WHERE "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = ?"
                + " AND "
                + DBHelper.KEY_TIME_WORKING_TIME + " = ?"
                + " AND "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " NOT IN (" + busyTimeQuery + ")"
                + extraCondition;

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{workingDaysId, time, workingDaysId});

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private String getThisDate() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // Получает дату
        // Таблицы: рабочие дни
        // Условия: уточняем id рабочего дня
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_DATE_WORKING_DAYS
                        + " FROM "
                        + DBHelper.TABLE_WORKING_DAYS
                        + " WHERE "
                        + DBHelper.KEY_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{workingDaysId});

        String date = "-";
        if (cursor.moveToFirst()) {
            int indexDate = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS);
            date = cursor.getString(indexDate);
        }

        cursor.close();
        return date;
    }

    // Добавление кнопок со временем на экран
    private void addButtonsOnScreen(boolean isPm) {
        //Дополнительные часы (am - 0, pm - 12)
        int extraHours = 0;
        if (isPm) {
            extraHours = 12;
        }

        for (int i = 0; i < ROWS_COUNT; i++) {
            for (int j = 0; j < COLUMNS_COUNT; j++) {
                timeBtns[i][j] = new Button(this);
                // установка параметров
                timeBtns[i][j].setWidth(50);
                timeBtns[i][j].setHeight(30);
                timeBtns[i][j].setX(j * width / COLUMNS_COUNT);
                timeBtns[i][j].setY(i * height / (2 * ROWS_COUNT));
                timeBtns[i][j].setBackgroundResource(R.drawable.time_button);

                timeBtns[i][j].setTag(R.string.selectedId, false);
                timeBtns[i][j].setOnClickListener(this);
                // установка текста
                String hour = String.valueOf(extraHours + (i * COLUMNS_COUNT + j) / 2);
                String min = (j % 2 == 0) ? "00" : "30";
                timeBtns[i][j].setText(hour + ":" + min);

                if (timeBtns[i][j].getParent() != null) {
                    ((ViewGroup) timeBtns[i][j].getParent()).removeView(timeBtns[i][j]);
                }
                mainLayout.addView(timeBtns[i][j]);
            }
        }
    }
}