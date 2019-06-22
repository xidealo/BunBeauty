package com.example.ideal.myapplication.createService;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.createService.user.UserCreateService;
import com.example.ideal.myapplication.createService.worker.WorkerCreateService;
import com.example.ideal.myapplication.fragments.SwitcherElement;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.other.ISwitcher;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MyTime extends AppCompatActivity implements View.OnClickListener, ISwitcher {

    private static final String TAG = "DBInf";
    private static final String WORKING_DAYS_ID = "working day id";
    private static final String SERVICE_ID = "service id";
    private static final String STATUS_USER_BY_SERVICE = "status UserCreateService";
    private static final String WORKER = "worker";

    private static final int ROWS_COUNT = 6;
    private static final int COLUMNS_COUNT = 4;

    private String statusUser;
    private String userId;
    private String workingDaysId;

    private Display display;
    private Button[][] timeBtns;

    //временный буфер добавленного рабочего времени
    private ArrayList<String> workingHours;
    //временный буфер удалённого рабочего времени
    private ArrayList<String> removedHours;

    private DBHelper dbHelper;
    private RelativeLayout mainLayout;
    private WorkerCreateService workerCreateService;
    private UserCreateService userCreateService;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_time);
        init();
    }

    private void init(){
        statusUser = getIntent().getStringExtra(STATUS_USER_BY_SERVICE);
        userId = getUserId();
        String serviceId = getIntent().getStringExtra(SERVICE_ID);
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        workingDaysId = getIntent().getStringExtra(WORKING_DAYS_ID);

        if (statusUser.equals(WORKER)) {
            workerCreateService = new WorkerCreateService(userId, serviceId, dbHelper);
        } else {
            userCreateService = new UserCreateService(userId, serviceId, dbHelper, workingDaysId);
        }

        mainLayout = findViewById(R.id.mainMyTimeLayout);

        display = getWindowManager().getDefaultDisplay();
        timeBtns = new Button[ROWS_COUNT][COLUMNS_COUNT];
        Button saveBtn = findViewById(R.id.saveMyTimeBtn);

        FragmentManager manager = getSupportFragmentManager();
        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerMyTimeLayout);
        panelBuilder.buildHeader(manager, "Расписание", R.id.headerMyTimeLayout);

        SwitcherElement switcherElement = new SwitcherElement("1-я половина", "2-я половина");
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.swicherMyTimeLayout, switcherElement);
        transaction.commit();

        //инициализация буферов
        workingHours = new ArrayList<>();
        removedHours = new ArrayList<>();

        addButtonsOnScreen(false);

        checkCurrentTimes();

        saveBtn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveMyTimeBtn:
                if (statusUser.equals(WORKER)) {
                    if (workingHours.size() > 0) {
                        // Добавляем время из буфера workingHours в БД
                        workerCreateService.addTime(workingDaysId, workingHours);
                        workingHours.clear();
                    }
                    if (removedHours.size() > 0) {
                        // Удаляем время сохранённое в буфере removeHours в БД
                        workerCreateService.deleteTime(workingDaysId, removedHours);
                        // removedHours.clear(); перенесен в воркера из-за потоков
                    }
                    Toast.makeText(this, "Расписанеие обновлено", Toast.LENGTH_SHORT).show();
                } else {
                    if (workingHours.size() == 1) {
                        loadInformationAboutService(WorkWithLocalStorageApi.getWorkingTimeId(workingHours.get(0), workingDaysId));
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
                    // Это не мой сервис (я - UserCreateService)

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
    public void confirm(String serviceName, String dataDay, final String time, final String workingTimeId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Запись на услугу");
        dialog.setMessage("Записаться на услугу " + serviceName + " " + dataDay + " числа в " + time);
        dialog.setCancelable(false);

        dialog.setPositiveButton(Html.fromHtml("<b><font color='#FF7F27'>Да</font></b>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                if (isFreeTime(time)) {
                    userCreateService.makeOrder(workingTimeId);
                    //закрашиваем, чтобы нельзя было записаться еще раз
                    checkCurrentTimes();
                    workingHours.clear();
                    attentionSuccessfulOrder();
                } else {
                    selectBtsForUser();
                    attentionTimeIsBusy();
                }

            }
        });
        dialog.setNegativeButton(Html.fromHtml("<b><font color='#FF7F27'>Нет</font></b>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
    }

    private void attentionSuccessfulOrder(){
        Toast.makeText(this, "Вы успешно записались", Toast.LENGTH_SHORT).show();
    }

    private void attentionTimeIsBusy(){
        Toast.makeText(this, "Данное время уже занято", Toast.LENGTH_SHORT).show();
    }

    // Подгружаем информацию о сервисе
    private void loadInformationAboutService(String workingTimeId) {

        Cursor cursor = WorkWithLocalStorageApi.getServiceCursorByTimeId(workingTimeId);

        if (cursor.moveToFirst()) {
            int indexNameService = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            int indexDateDay = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS);
            int indexTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);

            String serviceName = cursor.getString(indexNameService);
            String dataDay = cursor.getString(indexDateDay);
            String time = cursor.getString(indexTime);

            confirm(serviceName, dataDay, time, workingTimeId);
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
        // Проверка на то, что это мой сервис
        if (statusUser.equals(WORKER)) {
            // Это мой сервис (я - worker)
            selectBtsForWorker();
        } else {
            // Это не мой сервис (я - UserCreateService)
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
                if (WorkWithLocalStorageApi.checkTimeForWorker(workingDaysId, time,database)) {
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

    // Выделяет кнопки (UserCreateService)
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

    // Проверяет есть ли запись на данный день
    private String checkMyOrder() {

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

    private boolean isFreeTime(String time) {

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

    @Override
    protected void onResume() {
        super.onResume();
        //очищаем буфер, если перезаходим на активити
        workingHours.clear();
        removedHours.clear();

        FragmentManager manager = getSupportFragmentManager();
        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerMyTimeLayout);
        panelBuilder.buildHeader(manager, "Расписание", R.id.headerMyTimeLayout);
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // Добавление кнопок со временем на экран
    private void addButtonsOnScreen(boolean isPm) {
        //Дополнительные часы (am - 0, pm - 12)
        int extraHours = 0;
        if (isPm) {
            extraHours = 12;
        }

        //получение парамтров экрана
        int margin = 8;
        int btnWidth = (display.getWidth() - (COLUMNS_COUNT + 1) * margin) / COLUMNS_COUNT;
        int btnHeight = (display.getHeight() / 2 - (ROWS_COUNT + 1) * 6) / ROWS_COUNT;


        for (int i = 0; i < ROWS_COUNT; i++) {
            for (int j = 0; j < COLUMNS_COUNT; j++) {
                timeBtns[i][j] = new Button(this);
                // установка параметров
                timeBtns[i][j].setWidth(50);
                timeBtns[i][j].setHeight(30);
                timeBtns[i][j].setX(j * (btnWidth + margin) + margin);
                timeBtns[i][j].setY(i * (btnHeight + margin) + margin);
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

    @Override
    public void firstSwitcherAct() {
        switchTime(false);
    }

    @Override
    public void secondSwitcherAct() {
        switchTime(true);
    }

    private void switchTime(boolean isPm) {
        mainLayout.removeAllViews();
        addButtonsOnScreen(isPm);
        // Выделяет кнопки
        checkCurrentTimes();
        // Выделяет кнопки хронящиеся в буфере рабочих дней
        checkWorkingHours();
        // Снимает выделение с кнопок хронящихся в буфере удалённых дней
        checkRemovedHours();
    }
}