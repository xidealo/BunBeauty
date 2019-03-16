package com.example.ideal.myapplication.other;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.createService.MyCalendar;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.reviews.RatingBarElement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class GuestService extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private static final String CITY = "city";
    private static final String NAME = "name";
    private static final String WORKER = "worker";
    private static final String USER = "user";
    private static final String SERVICE_ID = "service id";
    private static final String USERS = "users";

    private static final String STATUS_USER_BY_SERVICE = "status User";

    private static final String REVIEW_FOR_SERVICE = "review for service";

    private String status;

    private String userId;
    private String serviceId;
    private String ownerId;
    private String serviceName;

    private TextView nameText;
    private TextView costText;
    private TextView withoutRatingText;
    private ProgressBar progressBar;
    private TextView descriptionText;
    private FragmentManager manager;
    private LinearLayout ratingLayout;
    private LinearLayout imageFeedLayout;

    private WorkWithLocalStorageApi workWithLocalStorageApi;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_service);

        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editScheduleGuestServiceBtn:
                if (status.equals(WORKER)) {
                    // если мой сервис, я - воркер
                    // сразу идём редактировать расписание
                    goToMyCalendar(WORKER);
                } else {
                    // если не мой сервис, я - юзер
                    // проверяем какие дни мне доступны
                    checkScheduleAndGoToProfile();
                }
                break;

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
        manager = getSupportFragmentManager();

        ratingLayout = findViewById(R.id.resultGuestServiceLayout);
        imageFeedLayout = findViewById(R.id.feedGuestServiceLayout);

        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        serviceId = getIntent().getStringExtra(SERVICE_ID);
        //получаем данные о сервисе
        getDataAboutService(serviceId);

        if(ownerId == null){
            loadOwner(serviceId);
        }

        //получаем рейтинг сервиса
        userId = getUserId();

        // мой сервис или нет?
        boolean isMyService = userId.equals(ownerId);

        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildHeader(manager, serviceName, R.id.headerGuestServiceLayout, isMyService, serviceId, ownerId);
        panelBuilder.buildFooter(manager, R.id.footerGuestServiceLayout);

        if (isMyService) {
            status = WORKER;
            editScheduleBtn.setText("Редактировать расписание");
        } else {
            status = USER;
            editScheduleBtn.setText("Расписание");
        }
        editScheduleBtn.setOnClickListener(this);
    }

    private void getDataAboutService(String serviceId) {

            SQLiteDatabase database = dbHelper.getWritableDatabase();
            // все о сервисе, оценка, количество оценок
            //проверка на удаленный номер

            String sqlQuery =
                    "SELECT "
                            + DBHelper.KEY_NAME_SERVICES + ", "
                            + DBHelper.KEY_MIN_COST_SERVICES + ", "
                            + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + ", "
                            + DBHelper.KEY_DESCRIPTION_SERVICES + ", "
                            + DBHelper.TABLE_CONTACTS_SERVICES + "." +DBHelper.KEY_USER_ID + ", "
                            + DBHelper.KEY_RATING_REVIEWS
                            + " FROM " + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                            + DBHelper.TABLE_WORKING_DAYS + ", "
                            + DBHelper.TABLE_WORKING_TIME + ", "
                            + DBHelper.TABLE_REVIEWS
                            + " WHERE "
                            + DBHelper.TABLE_CONTACTS_SERVICES + "."+DBHelper.KEY_ID + " = " +  DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                            + " AND "
                            + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID + " = " + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                            + " AND "
                            + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + " = " + DBHelper.KEY_WORKING_TIME_ID_REVIEWS
                            + " AND "
                            + DBHelper.TABLE_CONTACTS_SERVICES + "." +DBHelper.KEY_ID + " = ? "
                            + " AND "
                            + DBHelper.KEY_TYPE_REVIEWS + " = ? "
                            + " AND "
                            + DBHelper.KEY_RATING_REVIEWS + " != 0";

            Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId, REVIEW_FOR_SERVICE});

            float sumRates = 0;
            long counter = 0;
            // если сюда не заходит, значит ревью нет
            if (cursor.moveToFirst()) {
                int indexName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
                int indexMinCost = cursor.getColumnIndex(DBHelper.KEY_MIN_COST_SERVICES);
                int indexDescription = cursor.getColumnIndex(DBHelper.KEY_DESCRIPTION_SERVICES);
                int indexUserId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);
                ownerId = cursor.getString(indexUserId);
                serviceName = cursor.getString(indexName);

                nameText.setText(serviceName);
                costText.setText(cursor.getString(indexMinCost));
                descriptionText.setText(cursor.getString(indexDescription));
                do {
                    //у каждого ревью беру timeId и смотрю по нему есть ли оценка 
                    int indexWorkingTimeId = cursor.getColumnIndex(DBHelper.KEY_ID);
                    if(workWithLocalStorageApi.isMutualReview(cursor.getString(indexWorkingTimeId))) {
                        int indexRating = cursor.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);
                        sumRates += Float.valueOf(cursor.getString(indexRating));
                        counter++;
                    }else {
                        if(workWithLocalStorageApi.isAfterWeek(cursor.getString(indexWorkingTimeId))){
                            int indexRating = cursor.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);
                            sumRates += Float.valueOf(cursor.getString(indexRating));
                            counter++;
                        }
                    }
                } while (cursor.moveToNext());
            } else {
                setWithoutRating();
            }
            if (counter!=0) {
                float avgRating = sumRates / counter;
                addToScreenOnGuestService(avgRating, counter);
            }
            cursor.close();
        }
        
    private  void loadOwner(String serviceId) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // все осервисе, оценка, количество оценок
        //проверка на удаленный номер

        String sqlQuery =
                "SELECT *"
                        + " FROM " + DBHelper.TABLE_CONTACTS_SERVICES
                        + " WHERE "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + " = ? ";

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

    private void addUserInLocalStorage(User localUser) {
        SQLiteDatabase database= dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_NAME_USERS,localUser.getName());
        contentValues.put(DBHelper.KEY_CITY_USERS,localUser.getCity());

        boolean isUpdate = workWithLocalStorageApi
                .hasSomeDataForUsers(DBHelper.TABLE_CONTACTS_USERS,
                        localUser.getPhone());

        if (isUpdate) {
            database.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                    DBHelper.KEY_USER_ID + " = ?",
                    new String[]{String.valueOf(localUser.getPhone())});
        } else {
            contentValues.put(DBHelper.KEY_USER_ID, localUser.getPhone());
            database.insert(DBHelper.TABLE_CONTACTS_USERS, null, contentValues);
        }
    }

    private void checkScheduleAndGoToProfile(){

        // Получаем всё время данного сервиса, которое доступно данному юзеру
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String sqlQuery = "SELECT " + DBHelper.KEY_TIME_WORKING_TIME
                + " FROM "
                + DBHelper.TABLE_WORKING_DAYS + ", "
                + DBHelper.TABLE_WORKING_TIME
                + " WHERE "
                + DBHelper.KEY_SERVICE_ID_WORKING_DAYS + " = ?"
                + " AND "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                + " AND ((("
                + DBHelper.KEY_USER_ID + " = 0)"
                + " AND ("
                // 3 часа - разница с Гринвичем
                // 2 часа - минимум времени до сеанса, чтобы за писаться
                + "(STRFTIME('%s', 'now')+(3+2)*60*60) - STRFTIME('%s',"
                + DBHelper.KEY_DATE_WORKING_DAYS
                + "||' '||" + DBHelper.KEY_TIME_WORKING_TIME
                + ") <= 0)"
                + ") OR (("
                + DBHelper.KEY_USER_ID + " = ?"
                + ") AND ("
                + "(STRFTIME('%s', 'now')+3*60*60) - (STRFTIME('%s',"
                + DBHelper.KEY_DATE_WORKING_DAYS
                + "||' '||" + DBHelper.KEY_TIME_WORKING_TIME
                + ")) <= 0)))";

        Cursor cursor = database.rawQuery(sqlQuery, new String[] {serviceId, userId});

        if (cursor.moveToFirst()) {
            goToMyCalendar(USER);
        } else {
            attentionThisScheduleIsEmpty();
        }
        cursor.close();
    }

    private void setPhotoFeed(String serviceId) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        //получаем ссылку на фото по id владельца
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_PHOTO_LINK_PHOTOS
                        + " FROM "
                        + DBHelper.TABLE_PHOTOS
                        + " WHERE "
                        + DBHelper.KEY_OWNER_ID_PHOTOS + " = ?";
        Cursor cursor = database.rawQuery(sqlQuery,new String[] {serviceId});

        if(cursor.moveToFirst()){
            do {
                int indexPhotoLink = cursor.getColumnIndex(DBHelper.KEY_PHOTO_LINK_PHOTOS);

                String photoLink = cursor.getString(indexPhotoLink);

                ImageView serviceImage = new ImageView(getApplicationContext());
                serviceImage.setLayoutParams(new ViewGroup.LayoutParams(250,250));
                imageFeedLayout.addView(serviceImage);

                //установка аватарки
                Picasso.get()
                        .load(photoLink)
                        .into(serviceImage);
            }while (cursor.moveToNext());
        }
        cursor.close();

    }

    @Override
    protected void onResume() {
        super.onResume();
        imageFeedLayout.removeAllViews();
        loadProfileData();
        setPhotoFeed(serviceId);
    }

    private void attentionThisScheduleIsEmpty() {
        Toast.makeText(
                this,
                "Пользователь еще не написал расписание к этому сервису.",
                Toast.LENGTH_SHORT).show();
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    }

    private void goToMyCalendar(String status) {
        Intent intent = new Intent(this, MyCalendar.class);
        intent.putExtra(SERVICE_ID, serviceId);
        intent.putExtra(STATUS_USER_BY_SERVICE, status);

        startActivity(intent);
    }

    private void addToScreenOnGuestService(float avgRating, long countOfRates) {

        ratingLayout.removeAllViews();
        RatingBarElement fElement
                = new RatingBarElement(avgRating, countOfRates, serviceId, REVIEW_FOR_SERVICE);
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(R.id.resultGuestServiceLayout, fElement);

        transaction.commit();
    }

    private void setWithoutRating(){
        progressBar.setVisibility(View.GONE);
        withoutRatingText.setVisibility(View.VISIBLE);
    }

    private void attentionBadConnection() {
        Toast.makeText(this, "Плохое соединение", Toast.LENGTH_SHORT).show();
    }
}