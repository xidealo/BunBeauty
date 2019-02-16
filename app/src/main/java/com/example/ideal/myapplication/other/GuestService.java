package com.example.ideal.myapplication.other;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.createService.MyCalendar;
import com.example.ideal.myapplication.editing.EditService;
import com.example.ideal.myapplication.fragments.objects.RatingReview;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.UtilitiesApi;
import com.example.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.example.ideal.myapplication.reviews.RatingBarForServiceElement;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class GuestService extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private static final String PHONE_NUMBER = "Phone number";
    private static final String CITY = "city";
    private static final String NAME = "name";
    private static final String WORKER = "worker";
    private static final String USER = "user";
    private static final String FILE_NAME = "Info";
    private static final String SERVICE_ID = "service id";
    private static final String USER_ID = "user id";
    private static final String REVIEW = "review";
    private static final String REVIEWS = "reviews";
    private static final String USERS = "users";
    private static final String TIME = "time";
    private static final String TYPE = "type";

    private static final String WORKING_DAYS = "working days";
    private static final String DATA = "data";

    private static final String WORKING_TIME = "working time";
    private static final String WORKING_TIME_ID = "working time id";
    private static final String WORKING_DAY_ID = "working day id";
    private static final String RATING = "rating";

    private static final String STATUS_USER_BY_SERVICE = "status User";
    private static final String OWNER_ID = "owner id";

    private static final String REVIEW_FOR_SERVICE = "review for service";
    private static final String MESSAGE_ID = "message id";

    private long currentCountOfDays;

    private String status;
    private float sumRates;
    private int counter;
    private long countOfRates;
    private boolean addToScreen;


    private String userId;
    private String serviceId;
    private String ownerId;

    private TextView nameText;
    private TextView costText;
    private TextView withoutRatingText;
    private ProgressBar progressBar;
    private TextView descriptionText;
    private WorkWithTimeApi workWithTimeApi;

    private FragmentManager manager;
    private LinearLayout ratingLayout;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_service);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editScheduleGuestServiceBtn:
                // если мой сервис, то иду, как воркер
                checkScheduleAndGoToProfile();
                break;
            case R.id.editServiceGuestServiceBtn:
                goToEditService();
                break;
            case R.id.profileGuestServiceBtn:
                goToProfile();
            default:
                break;
        }
    }

    private void init(){
        nameText = findViewById(R.id.nameGuestServiceText);
        costText = findViewById(R.id.costGuestServiceText);
        descriptionText = findViewById(R.id.descriptionGuestServiceText);
        withoutRatingText = findViewById(R.id.withoutRatingText);

        progressBar = findViewById(R.id.progressBarGuestService);

        Button editScheduleBtn = findViewById(R.id.editScheduleGuestServiceBtn);
        Button editServiceBtn = findViewById(R.id.editServiceGuestServiceBtn);
        Button profileBtn = findViewById(R.id.profileGuestServiceBtn);
        manager = getSupportFragmentManager();

        ratingLayout = findViewById(R.id.resultGuestServiceLayout);

        dbHelper = new DBHelper(this);
        workWithTimeApi = new WorkWithTimeApi();
        serviceId = getIntent().getStringExtra(SERVICE_ID);
        //получаем данные о сервисе
        getDataAboutService(serviceId);
        //получаем рейтинг сервиса
        userId = getUserId();

        sumRates = 0;
        countOfRates = 0;
        addToScreen = false;
        //нужен для определния есть ли оценки или нет
        counter = 0;

        // мой сервис или нет?
        boolean isMyService = userId.equals(ownerId);

        if (isMyService) {
            status = WORKER;
            editScheduleBtn.setText("Редактировать расписание");
            editServiceBtn.setVisibility(View.VISIBLE);
            editServiceBtn.setText("Редактировать сервис");
        } else {
            status = USER;
            editScheduleBtn.setText("Расписание");
        }

        editScheduleBtn.setOnClickListener(this);
        editServiceBtn.setOnClickListener(this);
        profileBtn.setOnClickListener(this);
    }

    private void getDataAboutService(String serviceId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // получаем сервис с указанным ID
        String sqlQuery =
                "SELECT " + DBHelper.TABLE_CONTACTS_SERVICES + ".*"
                        + " FROM " + DBHelper.TABLE_CONTACTS_SERVICES
                        + " WHERE " + DBHelper.KEY_ID + " = ? ";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId});

        if (cursor.moveToFirst()) {
            int indexName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            int indexMinCost = cursor.getColumnIndex(DBHelper.KEY_MIN_COST_SERVICES);
            int indexDescription = cursor.getColumnIndex(DBHelper.KEY_DESCRIPTION_SERVICES);
            int indexUserId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);

            ownerId = cursor.getString(indexUserId);

            nameText.setText(cursor.getString(indexName));
            costText.setText(cursor.getString(indexMinCost));
            descriptionText.setText(cursor.getString(indexDescription));
        }

        cursor.close();
    }
    private void loadSchedule(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        //возвращает все дни определенного сервиса
        final Query query = database.getReference(WORKING_DAYS).
                orderByChild(SERVICE_ID).
                equalTo(serviceId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot workingDaysSnapshot) {
                final long countOfDays = workingDaysSnapshot.getChildrenCount();
                currentCountOfDays = 0;
                //если нет дней, убираем прогерсс бар и устанавливаем надпись, что нет оценок
                if(countOfDays == 0) {
                    setWithoutRating();
                }

                for(DataSnapshot workingDay: workingDaysSnapshot.getChildren()){
                    final String dayId = String.valueOf(workingDay.getKey());
                    String dayDate = String.valueOf(workingDay.child(DATA).getValue());
                    addScheduleInLocalStorage(dayId, dayDate);

                    final Query queryTime = database.getReference(WORKING_TIME).
                            orderByChild(WORKING_DAY_ID).
                            equalTo(dayId);
                    queryTime.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot workingTimesSnapshot) {
                            //если нет времени
                            if(workingTimesSnapshot.getChildrenCount() == 0){
                                setWithoutRating();
                            }

                            for (DataSnapshot workingTimeSnapshot : workingTimesSnapshot.getChildren()) {
                                String timeId = String.valueOf(workingTimeSnapshot.getKey());
                                String timeDate = String.valueOf(workingTimeSnapshot.child(TIME).getValue());
                                String timeUserId = String.valueOf(workingTimeSnapshot.child(USER_ID).getValue());
                                String timeWorkingDayId = String.valueOf(workingTimeSnapshot.child(WORKING_DAY_ID).getValue());
                                addTimeInLocalStorage(timeId, timeDate, timeUserId, timeWorkingDayId);
                            }
                            currentCountOfDays++;

                            if(currentCountOfDays == countOfDays) {
                                loadRating();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void checkScheduleAndGoToProfile(){
        int countOfDate = 0;
        boolean haveTime = false;
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String sqlQuery = "SELECT * FROM "
                + DBHelper.TABLE_WORKING_DAYS
                + " WHERE "
                + DBHelper.KEY_SERVICE_ID_WORKING_DAYS + " = ?";

        Cursor cursorWorkingDay = database.rawQuery(sqlQuery, new String[] {serviceId});
        if(cursorWorkingDay.moveToFirst()){
            do{
                int indexWorkingDayId = cursorWorkingDay.getColumnIndex(DBHelper.KEY_ID);
                String workingDayId = cursorWorkingDay.getString(indexWorkingDayId);

                String sqlQueryWorkingTime = "SELECT * FROM "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = ?";

                Cursor cursorWorkingTime = database.rawQuery(sqlQueryWorkingTime, new String[] {workingDayId});
                //проверяем часы работы (фича с 2 часами и тд)
                if(cursorWorkingTime.moveToFirst()){
                    do {
                        countOfDate++;
                        if (status.equals(USER) && !haveTime) {
                            if (hasSomeTime(workingDayId)) {
                                haveTime = true;
                            }
                        }
                        //если прошли по всем дням, идем в календарь
                        if ((cursorWorkingDay.getCount() == countOfDate)) {
                            if (status.equals(WORKER)) {
                                goToMyCalendar(WORKER);
                            }

                            if (status.equals(USER)) {
                                if (haveTime) {
                                    goToMyCalendar(USER);
                                } else {
                                    attentionThisScheduleIsEmpty();
                                }
                            }
                        }
                    }while (cursorWorkingTime.moveToNext());
                }

                cursorWorkingTime.close();
            }while (cursorWorkingDay.moveToNext());
        }
        else {
            //если воркер и нет расписания
            if(status.equals(WORKER)){
                goToMyCalendar(status);
            }
            if(status.equals(USER)){
                attentionThisScheduleIsEmpty();
            }
        }
        cursorWorkingDay.close();
    }

    // Возвращает есть ли в рабочем дне рабочие часы
    private boolean hasSomeTime(String dayId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // Получает id рабочего дня
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
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = ? "
                        + " AND ("
                        + DBHelper.KEY_USER_ID + " = 0"
                        + " OR "
                        + DBHelper.KEY_USER_ID + " = ?)";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{dayId, userId});

        if (cursor.moveToFirst()) {
            int indexDate = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS);
            int indexTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);
            String date, time;

            do {
                date = cursor.getString(indexDate);
                time = cursor.getString(indexTime);
                if (hasMoreThenTwoHours(date, time)) {
                    cursor.close();
                    return true;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return false;
    }

    private boolean hasMoreThenTwoHours(String date, String time) {
        long twoHours = 2 * 60 * 60 * 1000;
        long sysdateLong = workWithTimeApi.getSysdateLong();
        long currentLong = workWithTimeApi.getMillisecondsStringDate(date + " " + time);


        return currentLong - sysdateLong >= twoHours;
    }

    private void addScheduleInLocalStorage(String dayId, String dayDate) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String sqlQuery = "SELECT * FROM "
                + DBHelper.TABLE_WORKING_DAYS
                + " WHERE "
                + DBHelper.KEY_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{dayId});

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_DATE_WORKING_DAYS, dayDate);
        contentValues.put(DBHelper.KEY_SERVICE_ID_WORKING_DAYS, serviceId);
        if (cursor.moveToFirst()) {
            database.update(DBHelper.TABLE_WORKING_DAYS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{String.valueOf(dayId)});
        } else {
            contentValues.put(DBHelper.KEY_ID, dayId);
            database.insert(DBHelper.TABLE_WORKING_DAYS, null, contentValues);
        }

        cursor.close();
    }

    private void addTimeInLocalStorage(String timeId, String timeDate,
                                       String timeUserId, String timeWorkingDayId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String sqlQuery = "SELECT * FROM "
                + DBHelper.TABLE_WORKING_TIME
                + " WHERE "
                + DBHelper.KEY_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{timeId});

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_TIME_WORKING_TIME, timeDate);
        contentValues.put(DBHelper.KEY_USER_ID, timeUserId);
        contentValues.put(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME, timeWorkingDayId);

        if (cursor.moveToFirst()) {
            database.update(DBHelper.TABLE_WORKING_TIME, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{String.valueOf(timeId)});
        } else {
            contentValues.put(DBHelper.KEY_ID, timeId);
            database.insert(DBHelper.TABLE_WORKING_TIME, null, contentValues);
        }
        cursor.close();
    }

    private void loadProfileData() {
        DatabaseReference userReference = FirebaseDatabase.getInstance()
                .getReference(USERS)
                .child(ownerId);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                String name = String.valueOf(userSnapshot.child(NAME).getValue());
                String city = String.valueOf(userSnapshot.child(CITY).getValue());
                String phone = userSnapshot.getKey();

                User user = new User();
                user.setName(name);
                user.setCity(city);
                user.setPhone(phone);
                addUserInLocalStorage(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }
    private void loadRating(){
        //зашружаю среднюю оценку, складываю все и делю их на количество
        // также усталавниваю количество оценок
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        SQLiteDatabase localDatabase = dbHelper.getReadableDatabase();
        //получаю все id working time заданного сервиса
        String sqlQuery =
                "SELECT "
                        + DBHelper.TABLE_WORKING_TIME +"."+ DBHelper.KEY_ID + ", "
                        + DBHelper.TABLE_WORKING_TIME +"."+ DBHelper.KEY_USER_ID
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "."+DBHelper.KEY_ID + " = ? "
                        + " AND "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                        + " = "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                        + " = "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID ;

        final Cursor cursor = localDatabase.rawQuery(sqlQuery, new String[]{serviceId});

        if(cursor.moveToFirst()) {
            do {
                int indexWorkingTimeId = cursor.getColumnIndex(DBHelper.KEY_ID);
                int indexUserId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);
                final String workingTimeId = cursor.getString(indexWorkingTimeId);
                final String userId = cursor.getString(indexUserId);

                    Query query = database.getReference(REVIEWS)
                            .orderByChild(WORKING_TIME_ID)
                            .equalTo(workingTimeId);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot rates) {
                            RatingReview ratingReview = new RatingReview();
                            for (DataSnapshot rate : rates.getChildren()) {
                                String type = String.valueOf(rate.child(TYPE).getValue());
                                String rating = String.valueOf(rate.child(RATING).getValue());
                                if (type.equals(REVIEW_FOR_SERVICE) && (!rating.equals("0"))){
                                    // только ревью для сервисов
                                    countOfRates++;
                                    sumRates += Float.valueOf(String.valueOf(rate.child(RATING).getValue()));
                                    ratingReview.setId(String.valueOf(rate.getKey()));
                                    ratingReview.setReview(String.valueOf(rate.child(REVIEW).getValue()));
                                    ratingReview.setRating(rating);
                                    ratingReview.setMessageId(String.valueOf(rate.child(MESSAGE_ID).getValue()));
                                    ratingReview.setType(type);
                                    ratingReview.setWorkingTimeId(workingTimeId);
                                    //добавление ревью в локальную бд
                                    addReviewForServiceInLocalStorage(ratingReview);
                                    // загружать инфу о пользователе
                                    getUserFromReviewForService(userId);
                                }
                            }
                            counter++;
                            //есть время, но оценок еще нет
                            if(counter==cursor.getCount() && (countOfRates==0)){
                                setWithoutRating();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

            }while (cursor.moveToNext());
        }

        cursor.close();
    }

    private void addReviewForServiceInLocalStorage(RatingReview ratingReview) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_REVIEW_REVIEWS, ratingReview.getReview());
        contentValues.put(DBHelper.KEY_RATING_REVIEWS, ratingReview.getRating());
        contentValues.put(DBHelper.KEY_TYPE_REVIEWS, ratingReview.getType());
        contentValues.put(DBHelper.KEY_WORKING_TIME_ID_REVIEWS, ratingReview.getWorkingTimeId());
        contentValues.put(DBHelper.KEY_MESSAGE_ID_REVIEWS, ratingReview.getMessageId());

        //для проверки на update || insert в таблицу
        UtilitiesApi utilitiesApi = new UtilitiesApi(database);

        boolean isUpdate =  utilitiesApi
               .hasSomeDataWithThisTableInThisId(DBHelper.TABLE_REVIEWS,
                       ratingReview.getId());
        if(isUpdate){
            database.update(DBHelper.TABLE_REVIEWS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{String.valueOf(ratingReview.getId())});
        }
        else {
            contentValues.put(DBHelper.KEY_ID, ratingReview.getId());
            database.insert(DBHelper.TABLE_REVIEWS, null, contentValues);
        }
    }

    private void addUserInLocalStorage(User localUser) {
        SQLiteDatabase database= dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String sqlQuery = "SELECT * FROM "
                + DBHelper.TABLE_CONTACTS_USERS
                + " WHERE "
                + DBHelper.KEY_USER_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{localUser.getPhone()});

        contentValues.put(DBHelper.KEY_NAME_USERS,localUser.getName());
        contentValues.put(DBHelper.KEY_CITY_USERS,localUser.getCity());

        if (cursor.moveToFirst()) {
            database.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                    DBHelper.KEY_USER_ID + " = ?",
                    new String[]{String.valueOf(localUser.getPhone())});
        } else {
            contentValues.put(DBHelper.KEY_USER_ID, localUser.getPhone());
            database.insert(DBHelper.TABLE_CONTACTS_USERS, null, contentValues);
        }
        cursor.close();
    }

    private void getUserFromReviewForService(final String valuingPhone) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS)
                .child(valuingPhone);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot user) {
                User localUser = new User();
                localUser.setPhone(valuingPhone);
                localUser.setName(String.valueOf(user.child(NAME).getValue()));
                localUser.setCity(String.valueOf(user.child(CITY).getValue()));
                addUserInLocalStorage(localUser);

                if(!addToScreen) {
                    addToScreen();
                    addToScreen = true;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
    private void addToScreen() {
        float avgRating = sumRates / countOfRates;

        ratingLayout.removeAllViews();

        RatingBarForServiceElement fElement
                = new RatingBarForServiceElement(avgRating, countOfRates, serviceId, REVIEW_FOR_SERVICE);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.resultGuestServiceLayout, fElement);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        init();
        loadSchedule();
        loadProfileData();
    }

    private void attentionThisScheduleIsEmpty() {
        Toast.makeText(
                this,
                "Пользователь еще не написал расписание к этому сервису.",
                Toast.LENGTH_SHORT).show();
    }

    private String getUserId() {
        SharedPreferences sPref;
        sPref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        return sPref.getString(PHONE_NUMBER, "0");
    }

    private void goToMyCalendar(String status) {
        Intent intent = new Intent(this, MyCalendar.class);
        intent.putExtra(SERVICE_ID, serviceId);
        intent.putExtra(STATUS_USER_BY_SERVICE, status);

        startActivity(intent);
    }

    private void setWithoutRating(){
        progressBar.setVisibility(View.GONE);
        withoutRatingText.setVisibility(View.VISIBLE);
    }

    private void goToEditService() {
        Intent intent = new Intent(this, EditService.class);
        intent.putExtra(SERVICE_ID, serviceId);

        startActivity(intent);
    }

    private void goToProfile() {
        Intent intent = new Intent(this, Profile.class);
        intent.putExtra(OWNER_ID, ownerId);

        startActivity(intent);
    }

    private void attentionBadConnection() {
        Toast.makeText(this, "Плохое соединение", Toast.LENGTH_SHORT).show();
    }
}