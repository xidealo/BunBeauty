package com.example.ideal.myapplication.other;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.chat.Dialogs;
import com.example.ideal.myapplication.createService.AddService;
import com.example.ideal.myapplication.editing.EditProfile;
import com.example.ideal.myapplication.fragments.objects.RatingReview;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.fragments.foundElements.foundOrderElement;
import com.example.ideal.myapplication.fragments.foundElements.foundServiceProfileElement;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.example.ideal.myapplication.logIn.Authorization;
import com.example.ideal.myapplication.reviews.Comments;
import com.example.ideal.myapplication.reviews.RatingBarForServiceElement;
import com.example.ideal.myapplication.reviews.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.cert.TrustAnchor;

import static android.provider.Telephony.BaseMmsColumns.DATE;
import static android.provider.Telephony.BaseMmsColumns.MESSAGE_ID;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private static final String PHONE_NUMBER = "Phone number";
    private static final String OWNER_ID = "owner id";
    private static final String FILE_NAME = "Info";
    private static final String STATUS = "status";
    private static final String USER_NAME = "my name";
    private static final String USER_CITY = "my city";
    private static final String ID = "id";
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
    private boolean isAddToScreen;

    private  Button logOutBtn;
    private  Button findServicesBtn;
    private  Button addServicesBtn;
    private  Button mainScreenBtn;
    private  Button editProfileBtn;
    private  Button dialogsBtn;

    private  TextView nameText;
    private  TextView cityText;

    private  ScrollView servicesScroll;
    private  ScrollView ordersScroll;
    private  LinearLayout servicesLayout;
    private  LinearLayout ordersLayout;
    private  LinearLayout ratingLayout;

    private  TextView ratingText;
    private  RatingBar ratingBar;

    private  SwitchCompat servicesOrOrdersSwitch;

    private  SharedPreferences sPref;
    private  DBHelper dbHelper;
    private  String ownerId;
    private  WorkWithTimeApi workWithTimeApi;

    private foundServiceProfileElement fServiceElement;
    private foundOrderElement fOrderElement;
    private FragmentManager manager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        logOutBtn = findViewById(R.id.logOutProfileBtn);
        findServicesBtn = findViewById(R.id.findServicesProfileBtn);
        addServicesBtn = findViewById(R.id.addServicesProfileBtn);
        mainScreenBtn = findViewById(R.id.mainScreenProfileBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        dialogsBtn = findViewById(R.id.dialogsProfileBtn);

        servicesOrOrdersSwitch = findViewById(R.id.servicesOrOrdersProfileSwitch);

        servicesScroll = findViewById(R.id.servicesProfileScroll);
        ordersScroll = findViewById(R.id.orderProfileScroll);
        servicesLayout = findViewById(R.id.servicesProfileLayout);
        ordersLayout = findViewById(R.id.ordersProfileLayout);
        ratingLayout = findViewById(R.id.ratingLayout);

        ratingText = findViewById(R.id.ratingProfileText);
        ratingBar = findViewById(R.id.profileRatingBar);

        ratingText.setVisibility(View.VISIBLE);
        ratingBar.setVisibility(View.VISIBLE);

        nameText = findViewById(R.id.nameProfileText);
        cityText = findViewById(R.id.cityProfileText);

        countOfRates = 0;
        sumRates = 0;

        dbHelper = new DBHelper(this);
        workWithTimeApi = new WorkWithTimeApi();
        manager = getSupportFragmentManager();

        //получаем id пользователя
        String userId = getUserId();

        // Получаем id владельца профиля
        ownerId = getIntent().getStringExtra(OWNER_ID);
        // Проверяем id владельца профиля
        if(ownerId == null) {
            // Если null значит пользователь только что вошёл и это его сервис
            ownerId = userId;
        }

        // Добавляем данные о пользователе
        updateProfileData(ownerId);

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
            editProfileBtn.setOnClickListener(this);
        } else {
            // Не совпадает - чужой профиль

            // Скрываем функционал
            servicesOrOrdersSwitch.setVisibility(View.INVISIBLE);
            addServicesBtn.setVisibility(View.INVISIBLE);
            editProfileBtn.setVisibility(View.INVISIBLE);

            // Отображаем все сервисы пользователя
            ordersLayout.setVisibility(View.INVISIBLE);
            ordersScroll.setVisibility(View.INVISIBLE);
            servicesLayout.setVisibility(View.VISIBLE);
            servicesScroll.setVisibility(View.VISIBLE);
        }

        logOutBtn.setOnClickListener(this);
        findServicesBtn.setOnClickListener(this);
        mainScreenBtn.setOnClickListener(this);
        dialogsBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addServicesProfileBtn:
                goToAddService();
                break;
            case R.id.findServicesProfileBtn:
                goToSearchService();
                break;
            case R.id.logOutProfileBtn:
                annulStatus();
                goToLogIn();
                break;
            case R.id.mainScreenProfileBtn:
                goToMainScreen();
                break;
            case R.id.editProfileBtn:
                goToEditProfile();
                break;
            case R.id.dialogsProfileBtn:
                goToDialogs(); // DIALOGS
                break;
            default:
                break;
        }
    }

    //получить id-phone пользователя
    private String getUserId(){
        // возваращает id текущего пользователя
        sPref = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        String userId = String.valueOf(sPref.getString(PHONE_NUMBER, "0"));

        return userId;
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
                for(DataSnapshot workingTimeSnapshot:dataSnapshot.getChildren()){
                    String timeId = String.valueOf(workingTimeSnapshot.getKey());
                    String time = String.valueOf(workingTimeSnapshot.child(TIME).getValue());
                    String timeUserId = String.valueOf(workingTimeSnapshot.child(USER_ID).getValue());
                    String timeWorkingDayId = String.valueOf(workingTimeSnapshot.child(WORKING_DAY_ID).getValue());

                    addTimeInLocalStorage(timeId, time, timeUserId, timeWorkingDayId);
                }
                // Подгружает оценки
                loadRating();
                // Подгружаем дни по времени >> сервисы по дням >> авторов ревью по сервисам
                loadDaysByTime();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
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

        String sqlQuery = "SELECT * FROM "
                + DBHelper.TABLE_CONTACTS_USERS
                + " WHERE "
                + DBHelper.KEY_USER_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{userId});

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_NAME_USERS, name);

        if (cursor.moveToFirst()) {
            database.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                    DBHelper.KEY_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)});
        } else {
            contentValues.put(DBHelper.KEY_USER_ID, userId);
            database.insert(DBHelper.TABLE_CONTACTS_USERS, null, contentValues);
        }

        cursor.close();
    }

    private void addServiceInLocalStorage(String serviceId, String userId, String name) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String sqlQuery = "SELECT * FROM "
                + DBHelper.TABLE_CONTACTS_SERVICES
                + " WHERE "
                + DBHelper.KEY_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId});

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_USER_ID, userId);
        contentValues.put(DBHelper.KEY_NAME_SERVICES, name);

        if (cursor.moveToFirst()) {
            database.update(DBHelper.TABLE_CONTACTS_SERVICES, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{String.valueOf(serviceId)});
        } else {
            contentValues.put(DBHelper.KEY_ID, serviceId);
            database.insert(DBHelper.TABLE_CONTACTS_SERVICES, null, contentValues);
        }

        cursor.close();
    }

    private void addDayInLocalStorage(String dayId, String date, String serviceId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String sqlQuery = "SELECT * FROM "
                + DBHelper.TABLE_WORKING_DAYS
                + " WHERE "
                + DBHelper.KEY_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{dayId});

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_DATE_WORKING_DAYS, date);
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

    private void addTimeInLocalStorage(String id, String time,
                                       String userId, String workingDayId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String sqlQuery = "SELECT * FROM "
                + DBHelper.TABLE_WORKING_TIME
                + " WHERE "
                + DBHelper.KEY_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{id});

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_TIME_WORKING_TIME, time);
        contentValues.put(DBHelper.KEY_USER_ID, userId);
        contentValues.put(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME, workingDayId);

        if (cursor.moveToFirst()) {
            database.update(DBHelper.TABLE_WORKING_TIME, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{String.valueOf(id)});
        } else {
            contentValues.put(DBHelper.KEY_ID, id);
            database.insert(DBHelper.TABLE_WORKING_TIME, null, contentValues);
        }
        cursor.close();
    }

    private void loadRating() {
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
                final String workingTimeid = cursor.getString(indexId);
                Query query = FirebaseDatabase.getInstance().getReference(REVIEWS)
                        .orderByChild(WORKING_TIME_ID)
                        .equalTo(workingTimeid);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        RatingReview ratingReview = new RatingReview();

                        for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                            String type = String.valueOf(reviewSnapshot.child(TYPE).getValue());
                            float rating = Float.valueOf(String.valueOf(reviewSnapshot.child(RATING).getValue()));

                            if(type.equals(REVIEW_FOR_USER) && rating>0) {
                                countOfRates++;
                                sumRates += rating;
                                ratingReview.setId(String.valueOf(reviewSnapshot.getKey()));
                                ratingReview.setReview(String.valueOf(reviewSnapshot.child(REVIEW).getValue()));
                                ratingReview.setRating(String.valueOf(reviewSnapshot.child(RATING).getValue()));
                                ratingReview.setType(String.valueOf(reviewSnapshot.child(TYPE).getValue()));
                                ratingReview.setWorkingTimeId(workingTimeid);
                                ratingReview.setMessageId(String.valueOf(reviewSnapshot.child(MESSAGE_ID).getValue()));

                                addReviewInLocalStorage(ratingReview);
                            }
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

        String sqlQuery = "SELECT * FROM "
                + DBHelper.TABLE_REVIEWS
                + " WHERE "
                + DBHelper.KEY_ID + " = ?";

        String reviewId = ratingReview.getId();
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{reviewId});

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_REVIEW_REVIEWS, ratingReview.getReview());
        contentValues.put(DBHelper.KEY_RATING_REVIEWS, ratingReview.getRating());
        contentValues.put(DBHelper.KEY_TYPE_REVIEWS, ratingReview.getType());
        contentValues.put(DBHelper.KEY_WORKING_TIME_ID_REVIEWS, ratingReview.getWorkingTimeId());

        if (cursor.moveToFirst()) {
            database.update(DBHelper.TABLE_REVIEWS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{String.valueOf(reviewId)});
        } else {
            contentValues.put(DBHelper.KEY_ID, reviewId);
            database.insert(DBHelper.TABLE_REVIEWS, null, contentValues);
        }
        cursor.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String userId = getUserId();

        loadTimeForReviews();

        if(userId.equals(ownerId)){
            // если это мой сервис
            updateOrdersList(userId);
            updateServicesList(userId);
            updateProfileData(userId);
        }
    }

    //добавляет вновь добавленный сервис (обновление serviceList)
    private void updateServicesList(String userId) {
        //количество сервисов отображаемых на данный момент(старых)
        int visibleCount = servicesLayout.getChildCount();

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

        // Реальное кол-во сервисов
        int cursorCount = cursor.getCount();

        //Проверка на наличие вновь добавленных сервисов
        if(cursorCount > visibleCount) {
            //Идём с конца
            if(cursor.moveToLast()){
                int indexId = cursor.getColumnIndex(DBHelper.KEY_ID);
                int indexNameService = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
                int newServicesCount = 0;

                do{
                    String foundId = cursor.getString(indexId);
                    String foundNameService = cursor.getString(indexNameService);
                    Service service = new Service();
                    service.setId(foundId);
                    service.setName(foundNameService);

                    addServiceToScreen(service);
                    newServicesCount++;

                    //пока в курсоре есть строки и есть новые сервисы
                }while (cursor.moveToPrevious() && newServicesCount<(cursorCount - visibleCount));
            }
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
                        //+ "||' '||" + DBHelper.KEY_TIME_WORKING_TIME
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
        /*ratingBar.setVisibility(View.GONE);
        ratingText.setVisibility(View.GONE);*/

        float avgRating = sumRates/countOfRates;
        RatingBarForServiceElement ratingElement
                = new RatingBarForServiceElement(avgRating, countOfRates, ownerId, REVIEW_FOR_USER);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.ratingLayout, ratingElement);
        transaction.commit();
    }

    private void addServiceToScreen(Service service) {
        fServiceElement = new foundServiceProfileElement(service);

        transaction = manager.beginTransaction();
        transaction.add(R.id.servicesProfileLayout, fServiceElement);
        transaction.commit();
    }

    private void addOrderToScreen(String id, String name, String date, String time) {
        fOrderElement = new foundOrderElement(id, name, date, time);

        transaction = manager.beginTransaction();
        transaction.add(R.id.ordersProfileLayout, fOrderElement);
        transaction.commit();
    }

    //Анулировать статус при выходе
    private void annulStatus() {
        sPref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.remove(STATUS);
        editor.apply();
    }

    private void goToLogIn() {
        Intent intent = new Intent(this, Authorization.class);
        startActivity(intent);
        finish();
    }
    private void goToAddService() {
        Intent intent = new Intent(this, AddService.class);
        startActivity(intent);
    }

    private void goToSearchService() {
        Intent intent = new Intent(this, SearchService.class);
        startActivity(intent);
    }

    private void goToMainScreen() {
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
    }

    private void goToEditProfile() {
        Intent intent = new Intent(this, EditProfile.class);
        intent.putExtra(USER_NAME, nameText.getText());
        intent.putExtra(USER_CITY, cityText.getText());
        startActivity(intent);
    }

    private void goToDialogs() {
        Intent intent = new Intent(this, Dialogs.class);
        startActivity(intent);
    }

    /*private void goToComments() {
        Intent intent = new Intent(this, Comments.class);
        intent.putExtra(ID, ownerId);
        intent.putExtra(TYPE, REVIEW_FOR_USER);
        startActivity(intent);
    }*/

}