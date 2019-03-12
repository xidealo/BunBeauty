package com.example.ideal.myapplication.createService;

import android.content.ContentValues;
import android.content.Context;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyTime extends AppCompatActivity  implements View.OnClickListener {

    private static final String TAG = "DBInf";
    private static final String WORKING_DAYS_ID = "working day id";
    private static final String WORKING_TIME = "working time";
    private static final String WORKING_DAYS = "working days";
    private static final String SERVICES = "services";

    private static final String DIALOGS = "dialogs";
    private static final String MESSAGES = "messages";
    private static final String USER_ID = "user id";

    private static final String SERVICE_ID = "service id";
    private static final String STATUS_USER_BY_SERVICE = "status User";
    private static final String FIRST_PHONE = "first phone";
    private static final String SECOND_PHONE = "second phone";
    private static final String MESSAGE_TIME = "message time";
    private static final String TIME = "time";

    private static final String WORKER = "worker";

    private static final int ROWS_COUNT = 6;
    private static final int COLUMNS_COUNT = 4;
    private static final String WORKING_TIME_ID = "working time id";
    private static final String ORDERS = "orders";
    private static final String IS_CANCELED = "is canceled";
    private static final String MESSAGE_ID = "message id";
    private static final String DIALOG_ID = "dialog id";

    private String statusUser;
    private String userId;
    private String workingDaysId;
    private String date;
    private String timeId;
    private String serviceName;
    private String time;
    private String dataDay;
    private int width;
    private int height;
    private String dialogId = "";
    private WorkWithTimeApi workWithTimeApi;

    private Button[][] timeBtns;
    private Button saveBtn;

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

        mainLayout = findViewById(R.id.mainMyTimeLayout);

        timeBtns = new Button[ROWS_COUNT][COLUMNS_COUNT];
        saveBtn = findViewById(R.id.saveMyTimeBtn);

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
        workingDaysId = getIntent().getStringExtra(WORKING_DAYS_ID);
        date = getThisDate();

        addButtonsOnScreen(false);

        checkCurrentTimes();

        amOrPmMyTimeSwitch.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isPm) {
                // Очищаем layout
                mainLayout.removeAllViews();
                if(isPm) {
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
                if(statusUser.equals(WORKER)) {
                    if (workingHours.size() > 0) {
                        // Добавляем время из буфера workingHours в БД
                        addTime();
                    }
                    if (removedHours.size() > 0) {
                        // Удаляем время сохранённое в буфере removeHours в БД
                        deleteTime();
                    }
                    Toast.makeText(this, "Расписанеие обновлено", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (workingHours.size() == 1) {
                        // Обновляем id пользователя в таблице рабочего времени
                        loadCurrentTimeId();
                      
                        Toast.makeText(this, "Вы записались на услугу!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default:
                Button btn = (Button) v;
                String btnText = btn.getText().toString();
                if(btnText.length() == 4) {
                    btnText = "0" + btnText;
                }
                // Проверка мой ли это сервис (я - worker)
                if(statusUser.equals(WORKER)){
                    // Это мой сервис (я - worker)

                    if(Boolean.valueOf((btn.getTag(R.string.selectedId)).toString())) {
                        btn.setBackgroundResource(R.drawable.time_button);
                        workingHours.remove(btnText);
                        removedHours.add(btnText);
                        btn.setTag(R.string.selectedId, false);
                    } else {
                        Log.d(TAG, "onClick: " + btnText);
                        btn.setBackgroundResource(R.drawable.pressed_button);
                        workingHours.add(btnText);
                        removedHours.remove(btnText);
                        btn.setTag(R.string.selectedId, true);
                    }
                }
                else {
                    // Это не мой сервис (я - User)

                    // Проверка была ли кнопка выбрана до нажатия
                    if(Boolean.valueOf((btn.getTag(R.string.selectedId)).toString())) {
                        // Кнопка была уже нажата

                        btn.setBackgroundResource(R.drawable.time_button);
                        workingHours.remove(btnText);
                        btn.setTag(R.string.selectedId, false);
                    } else {
                        // Кнопка не была нажата до клика

                        String selectedTime;
                        //Если уже существует выбранное время
                        if(workingHours.size() == 1){
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

    public void confirm(Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
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

    private void loadCurrentTimeId(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //получаем все время этого дня и кнопки на которую нажал
        final Query query = database.getReference(WORKING_TIME)
                .orderByChild(WORKING_DAYS_ID)
                .equalTo(workingDaysId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot workingTimeSnapshot) {
                //делаем запрос по такому дню, такому времени
                for (DataSnapshot time : workingTimeSnapshot.getChildren()) {
                    if(String.valueOf(time.child("time").getValue()).equals(workingHours.get(0))) {
                        timeId = String.valueOf(time.getKey());

                        loadInformationAboutService(timeId);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void loadInformationAboutService(String workingTimeId) {

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(dbHelper.getReadableDatabase());

        Cursor cursor = workWithLocalStorageApi.getServiceCursorByTimeId(workingTimeId);

        if(cursor.moveToFirst()){
            int indexNameService = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            int indexDateDay = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS);
            int indexTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);

            serviceName = cursor.getString(indexNameService);
            dataDay = cursor.getString(indexDateDay);
            time = cursor.getString(indexTime);
            confirm(this);
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
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Получает время и id пользователя который записан на это время
        // Таблицы: рабочие время
        // Условия: уточняем id рабочего дня
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_TIME_WORKING_TIME + ", "
                        + DBHelper.KEY_USER_ID
                        + " FROM "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{workingDaysId});

        // Проверка на то, что это мой сервис
        if (statusUser.equals(WORKER)) {
            // Это мой сервис (я - worker)
            selectBtsForWorker(cursor);
        } else {
            // Это не мой сервис (я - User)
            selectBtsForUser(cursor);
        }
    }

    // Выделяет кнопки (worker)
    private void selectBtsForWorker(Cursor cursor) {
        for (int i = 0; i < ROWS_COUNT; i++) {
            for (int j = 0; j < COLUMNS_COUNT; j++) {
                String time = (String) timeBtns[i][j].getText();
                if (time.length() == 4) {
                    time = "0" + time;
                }

                //Проверка является ли данное время рабочим
                if (checkTimeForWorker(cursor, time)) {
                    timeBtns[i][j].setBackgroundResource(R.drawable.pressed_button);
                    timeBtns[i][j].setTag(R.string.selectedId, true);

                    // Проверка записан ли кто-то на это время
                    if (!isFreeTime(cursor, time)) {
                        timeBtns[i][j].setEnabled(false);
                    }
                }
            }
        }
    }

    // Выделяет кнопки (User)
    private void selectBtsForUser(Cursor cursor) {
        // Время на которое я записан
        String myOrderTime = checkMyOrder(cursor);

        for (int i = 0; i < ROWS_COUNT; i++) {
            for (int j = 0; j < COLUMNS_COUNT; j++) {
                String time = (String) timeBtns[i][j].getText();
                if(time.length() == 4) {
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
                    if (isFreeTime(cursor, time)) {
                        timeBtns[i][j].setBackgroundResource(R.drawable.time_button);
                        timeBtns[i][j].setTag(R.string.selectedId, false);
                        // Проверка осталось ли больше 2х часов до данного времени
                        if (!hasMoreThenTwoHours(time)) {
                            timeBtns[i][j].setEnabled(false);
                        }
                    } else {
                        timeBtns[i][j].setBackgroundResource(R.drawable.disabled_button);
                        timeBtns[i][j].setEnabled(false);
                    }
                }

            }
        }
    }

    private boolean hasMoreThenTwoHours(String time) {
        long twoHours = 2*60*60*1000;

        long sysdateLong = workWithTimeApi.getSysdateLong();
        long currentLong = workWithTimeApi.getMillisecondsStringDate(date + " " + time);

        return currentLong - sysdateLong >= twoHours;
    }

    //Снимает выделение с кнопки с данным временем
    private void removeSelection(String selectedTime){
        for (int i = 0; i < ROWS_COUNT; i++) {
            for (int j = 0; j < COLUMNS_COUNT; j++) {
                String time = (String) timeBtns[i][j].getText();

                if(time.equals(selectedTime)){
                    timeBtns[i][j].setBackgroundResource(R.drawable.time_button);
                    timeBtns[i][j].setTag(R.string.selectedId, false);
                }
            }
        }
    }

    // Добавляем время из буфера workingTime в БД
    private void addTime(){
        workingDaysId = getIntent().getStringExtra(WORKING_DAYS_ID);

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

        for (String time: workingHours) {
            if(!checkTimeForWorker(cursor, time)) {

                FirebaseDatabase fdatabase = FirebaseDatabase.getInstance();
                DatabaseReference myRef = fdatabase.getReference(WORKING_TIME);

                Map<String,Object> items = new HashMap<>();
                items.put(TIME,time);
                items.put(USER_ID, "0");
                items.put(WORKING_DAYS_ID, workingDaysId);

                String timeId =  myRef.push().getKey();
                myRef = fdatabase.getReference(WORKING_TIME).child(timeId);
                myRef.updateChildren(items);

                putDataInLocalStorage(timeId, time,contentValues,database);
            }
        }

        workingHours.clear();
        cursor.close();
    }

    private void putDataInLocalStorage(String timeId, String time, ContentValues contentValues,
                                       SQLiteDatabase database) {

        contentValues.put(DBHelper.KEY_ID, timeId);
        contentValues.put(DBHelper.KEY_TIME_WORKING_TIME, time);
        contentValues.put(DBHelper.KEY_USER_ID,"0");
        contentValues.put(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME, workingDaysId);

        database.insert(DBHelper.TABLE_WORKING_TIME,null,contentValues);
    }

    // Проверяет есть ли запись на данный день
    private String checkMyOrder(Cursor cursor) {
        if(cursor.moveToFirst()) {
            int indexUserId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);
            int indexTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);
            String userId = getUserId();
            do {
                if(cursor.getString(indexUserId).equals(userId)) {
                    String orderTime = cursor.getString(indexTime);

                    long sysdateLong = workWithTimeApi.getSysdateLong();
                    long currentLong = workWithTimeApi.getMillisecondsStringDate(date + " " + orderTime);

                    if(sysdateLong<currentLong) {
                        return orderTime;
                    }
                }
            } while (cursor.moveToNext());
        }
        return "";
    }

    // Записаться на данное время

    private void makeOrder() {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Вписываем телефон в working time (localStorage)
        // До того onDataChange
        updateLocalStorageTime();

        //закрашиваем, чтобы нельзя было заисаться еще раз
        checkCurrentTimes();

        // Вписываем телефон в working time (firebase)
        DatabaseReference myRef = database.getReference(WORKING_TIME).child(timeId);
        Map<String, Object> items = new HashMap<>();
        items.put(USER_ID, getUserId());
        myRef.updateChildren(items);

        // Создаём диалог
        createDialog(workingDaysId);
        //не очень хорошо, тк может записать в локалку но не записать в фб? (пропал инет)
    }

    private void createDialog(final String workingDaysId) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dayReference = database.getReference(WORKING_DAYS).child(workingDaysId);
        dayReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot day) {
                String serviceId = String.valueOf(day.child(SERVICE_ID).getValue());
                addDayInLocalStorage(serviceId);

                DatabaseReference serviceReference = database.getReference(SERVICES).child(serviceId);
                serviceReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot service) {
                        String workerId =  String.valueOf(service.child(USER_ID).getValue());

                        // Если диалог между 2 пользователями уже существует, получить его id
                        checkDialogsByNumbers(workerId);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        attentionBadConnection();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void checkDialogsByNumbers(final String workerId) {
        final Query firstPhoneQuery = FirebaseDatabase.getInstance().getReference(DIALOGS)
                .orderByChild(FIRST_PHONE)
                .equalTo(workerId);
        firstPhoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dialogs) {
                String secondPhone;

                for(DataSnapshot dialog:dialogs.getChildren()) {
                    secondPhone = String.valueOf(dialog.child(SECOND_PHONE).getValue());

                    if(secondPhone.equals(userId)) {
                        dialogId = dialog.getKey();
                        break;
                    }
                }
                if(dialogId.isEmpty()) {
                    checkSecondPhone(workerId);
                } else {
                    createMessage(dialogId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void checkSecondPhone(final String workerId) {
        Query secondPhoneQuery = FirebaseDatabase.getInstance().getReference(DIALOGS)
                .orderByChild(SECOND_PHONE)
                .equalTo(workerId);
        secondPhoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dialogs) {
                String firstPhone;

                for(DataSnapshot dialog:dialogs.getChildren()) {
                    firstPhone = String.valueOf(dialog.child(FIRST_PHONE).getValue());

                    if(firstPhone.equals(userId)) {
                        dialogId = dialog.getKey();
                        break;
                    }
                }

                // Если id пустое, значит диалога нет, создаём новый
                if(dialogId.isEmpty()) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(DIALOGS);
                    dialogId =  reference.push().getKey();
                    reference = reference.child(dialogId);

                    Map<String,Object> items = new HashMap<>();
                    items.put(FIRST_PHONE, workerId);
                    items.put(SECOND_PHONE, userId);

                    reference.updateChildren(items);
                }

                createMessage(dialogId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void createMessage(final String dialogId) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference messageRef = database.getReference(MESSAGES);
        Map<String, Object> items = new HashMap<>();

        String dateNow = workWithTimeApi.getCurDateInFormatYMDHMS();

        items.put(DIALOG_ID, dialogId);
        items.put(MESSAGE_TIME, dateNow);

        String messageId =  messageRef.push().getKey();
        messageRef = database.getReference(MESSAGES).child(messageId);
        messageRef.updateChildren(items);

        createOrder(messageId);
    }

    private void createOrder(String messageId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference orderRef = database.getReference(ORDERS);
        Map<String, Object> items = new HashMap<>();

        items.put(IS_CANCELED, false);
        items.put(WORKING_TIME_ID, timeId);
        items.put(MESSAGE_ID, messageId);

        String orderId =  orderRef.push().getKey();
        orderRef = database.getReference(ORDERS).child(orderId);
        orderRef.updateChildren(items);
    }

    private void updateLocalStorageTime() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // Получает время
        // Таблицы: рабочие время
        // Условия: уточняем id рабочего дня
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_ID + ", "
                        + DBHelper.KEY_TIME_WORKING_TIME
                        + " FROM "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.KEY_TIME_WORKING_TIME + " = ? AND "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{workingHours.get(0), String.valueOf(workingDaysId)});
        if(cursor.moveToFirst()) {
            int indexTimeId = cursor.getColumnIndex(DBHelper.KEY_ID);
            timeId = cursor.getString(indexTimeId);

            ContentValues contentValues = new ContentValues();
            String userId = getUserId();

            contentValues.put(DBHelper.KEY_USER_ID, userId);
            database.update(DBHelper.TABLE_WORKING_TIME, contentValues,
                    DBHelper.KEY_ID + " = ? ",
                    new String[]{String.valueOf(timeId)});
            workingHours.clear();
            cursor.close();
        }
    }

    private void addDayInLocalStorage(String serviceId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String sqlQuery = "SELECT * FROM "
                + DBHelper.TABLE_WORKING_DAYS
                + " WHERE "
                + DBHelper.KEY_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[] {workingDaysId});

        contentValues.put(DBHelper.KEY_DATE_WORKING_DAYS, date);
        contentValues.put(DBHelper.KEY_SERVICE_ID_WORKING_DAYS, serviceId);

        if(cursor.moveToFirst()) {
            database.update(DBHelper.TABLE_WORKING_DAYS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{String.valueOf(workingDaysId)});
            cursor.close();
        } else {
            contentValues.put(DBHelper.KEY_ID, workingDaysId);
            database.insert(DBHelper.TABLE_WORKING_DAYS, null, contentValues);
            cursor.close();
        }
    }

    private void deleteTime(){
        workingDaysId = getIntent().getStringExtra(WORKING_DAYS_ID);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //получаю время кнопки, на которую нажал

        //получаем все время этого дня

        final Query query = database.getReference(WORKING_TIME)
                .orderByChild(WORKING_DAYS_ID)
                .equalTo(workingDaysId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //делаем запрос по такому дню, такому времени
                //удаляем время из базы данных
                for (String hours : removedHours) {
                    for (DataSnapshot time : dataSnapshot.getChildren()) {
                        if (String.valueOf(time.child("time").getValue()).equals(hours)) {
                            String timeId = String.valueOf(time.getKey());

                            //возвращает все дни определенного сервиса
                            DatabaseReference myRef = database.getReference(WORKING_TIME).child(timeId);

                            Map<String, Object> items = new HashMap<>();
                            items.put(USER_ID, null);
                            items.put("time", null);
                            items.put(WORKING_DAYS_ID, null);

                            myRef.updateChildren(items);
                            deleteTimeFromLocalStorage();
                        }
                    }
                }
                //очищаяем массив
                removedHours.clear();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void deleteTimeFromLocalStorage() {
        workingDaysId = getIntent().getStringExtra(WORKING_DAYS_ID);

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

        for (String time: removedHours) {
            if(checkTimeForWorker(cursor, time)) {
                database.delete(
                        DBHelper.TABLE_WORKING_TIME,
                        DBHelper.KEY_TIME_WORKING_TIME + " = ? AND "
                                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = ?",
                        new String[]{time, String.valueOf(workingDaysId)});
            }
        }
    }

    // Проверяет есть ли какие-либо записи на данное время
    private boolean checkTimeForWorker(Cursor cursor, String time) {
        if(cursor.moveToFirst()) {
            int indexTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);
            do {
                if (time.equals(cursor.getString(indexTime))) {
                    return true;
                }
            } while (cursor.moveToNext());
        }
        return false;
    }

    // Проверяет свободно ли данное время
    private boolean isFreeTime(Cursor cursor, String time) {
        if(cursor.moveToFirst()) {
            int indexTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);
            int indexUserId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);
            do {
                if (cursor.getString(indexUserId).equals("0") && time.equals(cursor.getString(indexTime))) {
                    return true;
                }
            } while (cursor.moveToNext());
        }
        return false;
    }

    // Получает
    private String getUserId(){
        return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
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
                        + DBHelper.KEY_ID+ " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{workingDaysId});
        String date = "-";
        if(cursor.moveToFirst()) {
            int indexDate = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS);
            date = cursor.getString(indexDate);
            cursor.close();
        }
        cursor.close();
        return date;
    }

    // Добавление кнопок со временем на экран
    private void addButtonsOnScreen(boolean isPm){
        //Дополнительные часы (am - 0, pm - 12)
        int extraHours = 0;
        if(isPm) {
            extraHours = 12;
        }

        for (int i=0; i<ROWS_COUNT; i++) {
            for (int j=0; j<COLUMNS_COUNT; j++) {
                timeBtns[i][j]= new Button(this);
                // установка параметров
                timeBtns[i][j].setWidth(50);
                timeBtns[i][j].setHeight(30);
                timeBtns[i][j].setX(j*width/COLUMNS_COUNT);
                timeBtns[i][j].setY(i*height/(2*ROWS_COUNT));
                timeBtns[i][j].setBackgroundResource(R.drawable.time_button);

                timeBtns[i][j].setTag(R.string.selectedId, false);
                timeBtns[i][j].setOnClickListener(this);
                // установка текста
                String hour = String.valueOf(extraHours + (i * COLUMNS_COUNT + j) / 2);
                String min = (j % 2 == 0) ? "00" : "30";
                timeBtns[i][j].setText(hour + ":" + min);

                if(timeBtns[i][j].getParent() != null) {
                    ((ViewGroup)timeBtns[i][j].getParent()).removeView(timeBtns[i][j]);
                }
                mainLayout.addView(timeBtns[i][j]);
            }
        }
    }

    private void attentionBadConnection() {
        Toast.makeText(this,"Плохое соединение",Toast.LENGTH_SHORT).show();
    }
}