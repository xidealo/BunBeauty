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
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.createService.AddService;
import com.example.ideal.myapplication.fragments.foundElements.foundOrderElement;
import com.example.ideal.myapplication.fragments.objects.RatingReview;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.logIn.Authorization;
import com.example.ideal.myapplication.helpApi.DownloadServiceData;
import com.example.ideal.myapplication.reviews.RatingBarElement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private static final String PHONE_NUMBER = "Phone number";
    private static final String OWNER_ID = "owner id";
    private static final String FILE_NAME = "Info";

    private static final String USER_NAME = "my name";
    private static final String USER_CITY = "my city";
    private static final String TAG = "DBInf";

    private static final String WORKING_TIME = "working time";
    private static final String USER_ID = "user id";
    private static final String TIME = "time";
    private static final String WORKING_DAY_ID = "working day id";

    private static final String REVIEWS = "reviews";
    private static final String WORKING_TIME_ID = "working time id";
    private static final String RATING = "rating";
    private static final String REVIEW = "review";
    private static final String TYPE = "type";
    private static final String REVIEW_FOR_USER = "review for user";
    private static final String MESSAGE_ID = "message id";

    private static final String WORKING_DAYS = "working days";
    private static final String SERVICE_ID = "service id";
    private static final String DATE = "data";

    private static final String SERVICES = "services";
    private static final String NAME = "name";

    private static final String USERS = "users";

    private float sumRates;
    private long countOfRates;
    private long counter;
    private boolean isAddToScreen;

    private TextView nameText;
    private TextView cityText;
    private TextView withoutRatingText;
    private ProgressBar progressBar;

    private ScrollView servicesScroll;
    private ScrollView ordersScroll;
    private LinearLayout servicesLayout;
    private LinearLayout ordersLayout;
    private LinearLayout ratingLayout;

    private SharedPreferences sPref;
    private DBHelper dbHelper;
    private String ownerId;
    private WorkWithLocalStorageApi workWithLocalStorageApi;
    private ImageView avatarImage;

    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        withoutRatingText = findViewById(R.id.withoutRatingProfileText);
        progressBar = findViewById(R.id.progressBarProfile);

        Button logOutBtn = findViewById(R.id.logOutProfileBtn);
        Button addServicesBtn = findViewById(R.id.addServicesProfileBtn);
        avatarImage = findViewById(R.id.avatarProfileImage);

        SwitchCompat servicesOrOrdersSwitch = findViewById(R.id.servicesOrOrdersProfileSwitch);


        servicesScroll = findViewById(R.id.servicesProfileScroll);
        ordersScroll = findViewById(R.id.orderProfileScroll);
        servicesLayout = findViewById(R.id.servicesProfileLayout);
        ordersLayout = findViewById(R.id.ordersProfileLayout);
        ratingLayout = findViewById(R.id.ratingLayout);

        nameText = findViewById(R.id.nameProfileText);
        cityText = findViewById(R.id.cityProfileText);

        countOfRates = 0;
        sumRates = 0;
        counter =0;

        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        manager = getSupportFragmentManager();
        //получаем id пользователя
        String userId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        // Получаем id владельца профиля
        ownerId = getIntent().getStringExtra(OWNER_ID);
        // Проверяем id владельца профиля
        if(ownerId == null) {
            // Если null значит пользователь только что вошёл и это его сервис
            ownerId = userId;
        }

        Log.d(TAG, "onCreate: " + ownerId);
        PanelBuilder panelBuilder = new PanelBuilder(this, ownerId);
        panelBuilder.buildHeader(manager, "Профиль", R.id.headerProfileLayout);
        panelBuilder.buildFooter(manager, R.id.footerProfileLayout);

        //установка аватарки
        loadServiceByWorkingDay(ownerId);

        // Проверяем совпадают id пользователя и владельца
        if(userId.equals(ownerId)){
            // Совпадают - это мой профиль

            servicesLayout.setVisibility(View.INVISIBLE);
            servicesScroll.setVisibility(View.INVISIBLE);

            servicesOrOrdersSwitch.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        buttonView.setText("My services");
                        ordersLayout.setVisibility(View.INVISIBLE);
                        ordersScroll.setVisibility(View.INVISIBLE);
                        servicesLayout.setVisibility(View.VISIBLE);
                        servicesScroll.setVisibility(View.VISIBLE);
                    } else {
                        buttonView.setText("My orders");
                        servicesLayout.setVisibility(View.INVISIBLE);
                        servicesScroll.setVisibility(View.INVISIBLE);
                        ordersLayout.setVisibility(View.VISIBLE);
                        ordersScroll.setVisibility(View.VISIBLE);
                    }
                }
            });
            addServicesBtn.setOnClickListener(this);
        } else {
            // Не совпадает - чужой профиль

            // Скрываем функционал
            servicesOrOrdersSwitch.setVisibility(View.INVISIBLE);
            addServicesBtn.setVisibility(View.INVISIBLE);

            // Отображаем все сервисы пользователя
            ordersLayout.setVisibility(View.INVISIBLE);
            ordersScroll.setVisibility(View.INVISIBLE);
            servicesLayout.setVisibility(View.VISIBLE);
            servicesScroll.setVisibility(View.VISIBLE);
        }

        logOutBtn.setOnClickListener(this);
        avatarImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addServicesProfileBtn:
                goToAddService();
                break;
            case R.id.logOutProfileBtn:
                goToLogIn();
                break;

            default:
                break;
        }
    }

    // получаем данные о пользователе и отображаем в прфоиле
    private void updateProfileData(String userId){

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        //получаем имя, фамилию и город пользователя по его id
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_NAME_USERS + ", "
                        + DBHelper.KEY_CITY_USERS
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_USERS
                        + " WHERE "
                        + DBHelper.KEY_USER_ID + " = ?";
        Cursor cursor = database.rawQuery(sqlQuery,new String[] {userId});

        if(cursor.moveToFirst()){
            int indexName = cursor.getColumnIndex(DBHelper.KEY_NAME_USERS);
            int indexCity = cursor . getColumnIndex(DBHelper.KEY_CITY_USERS);
            String[] names = cursor.getString(indexName).split(" ");
            for (int i=0; i<names.length; i++) {
                names[i] = names[i].substring(0, 1).toUpperCase() + names[i].substring(1);
            }
            String name = names[0] + " " + names[1];

            String city = cursor.getString(indexCity).substring(0, 1).toUpperCase()
                    + cursor.getString(indexCity).substring(1);
            nameText.setText(name);
            cityText.setText(city);
            cursor.close();
        }
    }

    private void loadTimeForReviews() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        Query query = database.getReference(WORKING_TIME)
                .orderByChild(USER_ID)
                .equalTo(ownerId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 0){
                    setWithoutRating();
                }

                for(DataSnapshot workingTimeSnapshot:dataSnapshot.getChildren()){
                    String timeId = String.valueOf(workingTimeSnapshot.getKey());
                    String time = String.valueOf(workingTimeSnapshot.child(TIME).getValue());
                    String timeUserId = String.valueOf(workingTimeSnapshot.child(USER_ID).getValue());
                    String timeWorkingDayId = String.valueOf(workingTimeSnapshot.child(WORKING_DAY_ID).getValue());

                    addTimeInLocalStorage(timeId, time, timeUserId, timeWorkingDayId);
                }
                // Подгружает оценки
                loadRatingForUser();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }});
    }

    private void loadDaysByTime() {
        isAddToScreen = false;
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String sqlQuery = "SELECT DISTINCT "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                + " FROM "
                + DBHelper.TABLE_WORKING_TIME
                + " WHERE "
                + DBHelper.KEY_USER_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{ownerId});

        if (cursor.moveToFirst()) {
            int indexDayId = cursor.getColumnIndex(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME);

            do {
                final String dayId = cursor.getString(indexDayId);

                DatabaseReference dayRef = FirebaseDatabase.getInstance().getReference(WORKING_DAYS).child(dayId);
                dayRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot workingDaySnapshot) {
                        String date = String.valueOf(workingDaySnapshot.child(DATE).getValue());
                        String serviceId = String.valueOf(workingDaySnapshot.child(SERVICE_ID).getValue());
                        addDayInLocalStorage(dayId, date, serviceId);

                        // Подгружаем сервисы по дням >> авторов ревью по сервисам
                        loadServiceByWorkingDay(serviceId);

                        if(!isAddToScreen) {
                            isAddToScreen = true;
                            addRatingToScreen();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void loadServiceByWorkingDay(final String serviceId) {
        DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference(SERVICES).child(serviceId);
        serviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot serviceSnapshot) {
                String userId = String.valueOf(serviceSnapshot.child(USER_ID).getValue());
                String name = String.valueOf(serviceSnapshot.child(NAME).getValue());
                addServiceInLocalStorage(serviceId, userId, name);

                // Подгружаем авторов ревью по сервисам
                loadUserByService(userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void loadUserByService(final String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(USERS).child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                String name = String.valueOf(userSnapshot.child(NAME).getValue());

                addUserInLocalStorage(userId, name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addUserInLocalStorage(String userId, String name) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_NAME_USERS, name);

        boolean isUpdate = workWithLocalStorageApi
                .hasSomeDataForUsers(DBHelper.TABLE_CONTACTS_USERS,
                        userId);

        if (isUpdate) {
            database.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                    DBHelper.KEY_USER_ID + " = ?",
                    new String[]{userId});
        } else {
            contentValues.put(DBHelper.KEY_USER_ID, userId);
            database.insert(DBHelper.TABLE_CONTACTS_USERS, null, contentValues);
        }
    }

    private void addServiceInLocalStorage(String serviceId, String userId, String name) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_USER_ID, userId);
        contentValues.put(DBHelper.KEY_NAME_SERVICES, name);

        boolean isUpdate = workWithLocalStorageApi
                .hasSomeData(DBHelper.TABLE_CONTACTS_SERVICES,
                        serviceId);

        if (isUpdate) {
            database.update(DBHelper.TABLE_CONTACTS_SERVICES, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{serviceId});
        } else {
            contentValues.put(DBHelper.KEY_ID, serviceId);
            database.insert(DBHelper.TABLE_CONTACTS_SERVICES, null, contentValues);
        }
    }

    private void addDayInLocalStorage(String dayId, String date, String serviceId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_DATE_WORKING_DAYS, date);
        contentValues.put(DBHelper.KEY_SERVICE_ID_WORKING_DAYS, serviceId);

        boolean isUpdate = workWithLocalStorageApi
                .hasSomeData(DBHelper.TABLE_WORKING_DAYS,
                        dayId);

        if (isUpdate) {
            database.update(DBHelper.TABLE_WORKING_DAYS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{dayId});
        } else {
            contentValues.put(DBHelper.KEY_ID, dayId);
            database.insert(DBHelper.TABLE_WORKING_DAYS, null, contentValues);
        }
    }

    private void addTimeInLocalStorage(String timeId, String time,
                                       String userId, String workingDayId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_TIME_WORKING_TIME, time);
        contentValues.put(DBHelper.KEY_USER_ID, userId);
        contentValues.put(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME, workingDayId);

        boolean isUpdate = workWithLocalStorageApi
                .hasSomeData(DBHelper.TABLE_WORKING_TIME,
                        timeId);

        if (isUpdate) {
            database.update(DBHelper.TABLE_WORKING_TIME, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{timeId});
        } else {
            contentValues.put(DBHelper.KEY_ID, timeId);
            database.insert(DBHelper.TABLE_WORKING_TIME, null, contentValues);
        }
    }

    private void loadRatingForUser() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String sqlQuery = "SELECT "
                + DBHelper.KEY_ID
                + " FROM "
                + DBHelper.TABLE_WORKING_TIME
                + " WHERE "
                + DBHelper.KEY_USER_ID + " = ?";

        final Cursor cursor = database.rawQuery(sqlQuery, new String[]{ownerId});

        if(cursor.moveToFirst()) {
            int indexId = cursor.getColumnIndex(DBHelper.KEY_ID);

            sumRates = 0;
            countOfRates = 0;
            do{
                final String workingTimeId = cursor.getString(indexId);
                Query query = FirebaseDatabase.getInstance().getReference(REVIEWS)
                        .orderByChild(WORKING_TIME_ID)
                        .equalTo(workingTimeId);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot reviewsSnapshot) {

                        RatingReview ratingReview = new RatingReview();

                        for (DataSnapshot reviewSnapshot : reviewsSnapshot.getChildren()) {
                            String type = String.valueOf(reviewSnapshot.child(TYPE).getValue());
                            float rating = Float.valueOf(String.valueOf(reviewSnapshot.child(RATING).getValue()));

                            if(type.equals(REVIEW_FOR_USER) && rating>0) {
                                countOfRates++;
                                sumRates += rating;
                                ratingReview.setId(String.valueOf(reviewSnapshot.getKey()));
                                ratingReview.setReview(String.valueOf(reviewSnapshot.child(REVIEW).getValue()));
                                ratingReview.setRating(String.valueOf(reviewSnapshot.child(RATING).getValue()));
                                ratingReview.setType(String.valueOf(reviewSnapshot.child(TYPE).getValue()));
                                ratingReview.setWorkingTimeId(workingTimeId);
                                ratingReview.setMessageId(String.valueOf(reviewSnapshot.child(MESSAGE_ID).getValue()));

                                addReviewInLocalStorage(ratingReview);

                                // Подгружаем дни по времени >> сервисы по дням >> авторов ревью по сервисам
                                loadDaysByTime();
                            }
                        }

                        counter++;
                        if(counter==cursor.getCount() && (countOfRates==0)){
                            setWithoutRating();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void addReviewInLocalStorage(RatingReview ratingReview) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String reviewId = ratingReview.getId();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_REVIEW_REVIEWS, ratingReview.getReview());
        contentValues.put(DBHelper.KEY_RATING_REVIEWS, ratingReview.getRating());
        contentValues.put(DBHelper.KEY_TYPE_REVIEWS, ratingReview.getType());
        contentValues.put(DBHelper.KEY_WORKING_TIME_ID_REVIEWS, ratingReview.getWorkingTimeId());

        boolean isUpdate = workWithLocalStorageApi
                .hasSomeData(DBHelper.TABLE_REVIEWS,
                        reviewId);

        if (isUpdate) {
            database.update(DBHelper.TABLE_REVIEWS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{reviewId});
        } else {
            contentValues.put(DBHelper.KEY_ID, reviewId);
            database.insert(DBHelper.TABLE_REVIEWS, null, contentValues);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        loadTimeForReviews();
        updateProfileData(ownerId);

        if(userId.equals(ownerId)){
            // если это мой сервис
            updateOrdersList(userId);
            updateServicesList(userId);
        }
        else{
            updateServicesList(ownerId);
        }
        workWithLocalStorageApi.setPhotoAvatar(ownerId,avatarImage);
    }

    //подгрузка сервисов на serviceList
    private void updateServicesList(String userId) {
        //количество сервисов отображаемых на данный момент(старых)
        servicesLayout.removeAllViews();
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // Возвращает id, название, рэйтинг и количество оценивших
        // используем таблицу сервисы
        // уточняем юзера по его id
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_ID + ", "
                        + DBHelper.KEY_NAME_SERVICES
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES
                        + " WHERE "
                        + DBHelper.KEY_USER_ID + " = ? ";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{userId});
        //Идём с конца
            if(cursor.moveToFirst()){
                int indexId = cursor.getColumnIndex(DBHelper.KEY_ID);
                int indexNameService = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
                do{
                    String foundId = cursor.getString(indexId);
                    String foundNameService = cursor.getString(indexNameService);
                    Service service = new Service();
                    service.setId(foundId);
                    service.setName(foundNameService);

                    DownloadServiceData downloadServiceData = new DownloadServiceData();
                    downloadServiceData.loadSchedule(service.getId(),database,
                            "Profile",manager);

                    //пока в курсоре есть строки и есть новые сервисы
                }while (cursor.moveToNext());
            }
        cursor.close();
    }

    //добавляет вновь добавленные записи (обновление ordersList)
    private void updateOrdersList(String userId) {
        // количство записей отображаемых на данный момент(старых)
        int visibleCount = ordersLayout.getChildCount();

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // получаем id сервиса, имя сервиса, дату и время всех записей
        // Из 3-х таблиц: сервисы, рабочие дни, рабочие время
        // Условие: связка таблиц по id сервиса и id рабочего дня; уточняем пользователя по id
        String sqlQuery =
                "SELECT "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + ", "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_NAME_SERVICES + ", "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_DATE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_TIME_WORKING_TIME
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + " = " + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                        + " AND "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID + " = " + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                        + " AND "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_USER_ID + " = ?"
                        + " AND "
                        + " STRFTIME('%s', " + DBHelper.KEY_DATE_WORKING_DAYS
                        + ")>=STRFTIME('%s', DATE('now'))";

        Cursor cursor = database.rawQuery(sqlQuery, new String[] {userId});

        int cursorCount = cursor.getCount();

        //если есть новые записи
        if(cursorCount > visibleCount) {
            //Идём с конца
            if(cursor.moveToLast()){
                int indexServiceId = cursor.getColumnIndex(DBHelper.KEY_ID);
                int indexServiceName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
                int indexDate = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS);
                int indexTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);

                do{
                    String foundId = cursor.getString(indexServiceId);
                    String foundName = cursor.getString(indexServiceName);
                    String foundDate = cursor.getString(indexDate);
                    String foundTime = cursor.getString(indexTime);

                    addOrderToScreen(foundId, foundName, foundDate, foundTime);
                    visibleCount++;

                    //пока в курсоре есть строки и есть новые записи
                }while (cursor.moveToPrevious() && (cursorCount > visibleCount));
            }
        }
        cursor.close();
    }

    private void addRatingToScreen() {
        ratingLayout.removeAllViews();

        float avgRating = sumRates/countOfRates;
        RatingBarElement ratingElement
                = new RatingBarElement(avgRating, countOfRates, ownerId, REVIEW_FOR_USER);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.ratingLayout, ratingElement);
        transaction.commit();
    }

    private void addOrderToScreen(String id, String name, String date, String time) {
        foundOrderElement fOrderElement = new foundOrderElement(id, name, date, time);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.ordersProfileLayout, fOrderElement);
        transaction.commit();
    }

    private void goToLogIn() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(this, Authorization.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void setWithoutRating(){
        progressBar.setVisibility(View.GONE);
        withoutRatingText.setVisibility(View.VISIBLE);
    }

    private void goToAddService() {
        Intent intent = new Intent(this, AddService.class);
        startActivity(intent);
    }
}