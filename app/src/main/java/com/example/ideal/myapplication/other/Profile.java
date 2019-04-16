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
import com.example.ideal.myapplication.fragments.foundElements.foundServiceProfileElement;
import com.example.ideal.myapplication.fragments.objects.RatingReview;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.helpApi.DownloadServiceData;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.example.ideal.myapplication.logIn.Authorization;
import com.example.ideal.myapplication.reviews.RatingBarElement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private static final String OWNER_ID = "owner id";

    private static final String TAG = "DBInf";
    private static final String REVIEW_FOR_SERVICE = "review for service";

    private static final String USER_ID = "user id";
    private static final String REVIEW_FOR_USER = "review for user";
    private static final String ORDER_ID = "order_id";

    private static final String SERVICES = "services";
    private static final String NAME = "name";

    private static final String USERS = "users";

    private static final String SUBSCRIPTIONS = "подписки";

    private String userId;
    private String ownerId;

    private TextView nameText;
    private TextView cityText;
    private TextView withoutRatingText;

    private Button subscribersBtn;

    private ScrollView servicesScroll;
    private ScrollView ordersScroll;
    private LinearLayout servicesLayout;
    private LinearLayout ordersLayout;

    private DBHelper dbHelper;

    private WorkWithLocalStorageApi workWithLocalStorageApi;
    private ImageView avatarImage;

    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        withoutRatingText = findViewById(R.id.withoutRatingProfileText);

        Button logOutBtn = findViewById(R.id.logOutProfileBtn);
        Button addServicesBtn = findViewById(R.id.addServicesProfileBtn);
        subscribersBtn = findViewById(R.id.subscribersProfileBtn);
        avatarImage = findViewById(R.id.avatarProfileImage);

        SwitchCompat servicesOrOrdersSwitch = findViewById(R.id.servicesOrOrdersProfileSwitch);

        servicesScroll = findViewById(R.id.servicesProfileScroll);
        ordersScroll = findViewById(R.id.orderProfileScroll);
        servicesLayout = findViewById(R.id.servicesProfileLayout);
        ordersLayout = findViewById(R.id.ordersProfileLayout);

        nameText = findViewById(R.id.nameProfileText);
        cityText = findViewById(R.id.cityProfileText);


        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        manager = getSupportFragmentManager();
        userId = getUserId();

        // Получаем id владельца профиля
        ownerId = getIntent().getStringExtra(OWNER_ID);
        // Проверяем id владельца профиля
        if(ownerId == null) {
            // Если null значит пользователь только что вошёл и это его сервис
            ownerId = userId;
        }

        PanelBuilder panelBuilder = new PanelBuilder(ownerId);
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
            subscribersBtn.setOnClickListener(this);
        } else {
            // Не совпадает - чужой профиль

            // Скрываем функционал
            servicesOrOrdersSwitch.setVisibility(View.INVISIBLE);
            addServicesBtn.setVisibility(View.INVISIBLE);
            subscribersBtn.setVisibility(View.INVISIBLE);

            // Отображаем все сервисы пользователя
            ordersLayout.setVisibility(View.INVISIBLE);
            ordersScroll.setVisibility(View.INVISIBLE);
            servicesLayout.setVisibility(View.VISIBLE);
            servicesScroll.setVisibility(View.VISIBLE);
        }

        loadRatingForUser();
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

            case R.id.subscribersProfileBtn:
                goToSubscribers();
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
                        + DBHelper.KEY_ID + " = ?";
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

    private void loadServiceByWorkingDay(final String serviceId) {
        DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference(SERVICES).child(serviceId);
        serviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot serviceSnapshot) {
                String userId = String.valueOf(serviceSnapshot.child(USER_ID).getValue());
                String name = String.valueOf(serviceSnapshot.child(NAME).getValue());
                //addServiceInLocalStorage(serviceId, userId, name);

                // Подгружаем авторов ревью по сервисам
                //loadUserByService(userId);
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
                .hasSomeData(DBHelper.TABLE_CONTACTS_USERS,
                        userId);

        if (isUpdate) {
            database.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{userId});
        } else {
            contentValues.put(DBHelper.KEY_ID, userId);
            database.insert(DBHelper.TABLE_CONTACTS_USERS, null, contentValues);
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
        contentValues.put(DBHelper.KEY_ID, userId);
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
        //таким способом я получаю свои ревью, а не о себе
        String mainSqlQuery = "SELECT "
                + DBHelper.KEY_RATING_REVIEWS + ", "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + ", "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID + " AS " + ORDER_ID
                + " FROM "
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_REVIEWS + ", "
                + DBHelper.TABLE_WORKING_DAYS + ", "
                + DBHelper.TABLE_CONTACTS_USERS + ", "
                + DBHelper.TABLE_ORDERS + ", "
                + DBHelper.TABLE_CONTACTS_SERVICES
                + " WHERE "
                + DBHelper.KEY_ORDER_ID_REVIEWS
                + " = "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                + " = "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                + " = "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                + " = "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID
                + " = "
                + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + " = ? "
                + " AND "
                + DBHelper.KEY_TYPE_REVIEWS + " = ? "
                + " AND "
                + DBHelper.KEY_RATING_REVIEWS + " != 0";

        // убрать не оценненые
        final Cursor cursor = database.rawQuery(mainSqlQuery, new String[]{ownerId, REVIEW_FOR_USER});
        float sumRates = 0;
        long counter = 0;
        // если сюда не заходит, значит ревью нет
        if (cursor.moveToFirst()) {
            int indexRating = cursor.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);
            int indexWorkingTimeId= cursor.getColumnIndex(DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID);
            int indexOrderId= cursor.getColumnIndex(ORDER_ID);
            do {
                String workingTimeId = cursor.getString(indexWorkingTimeId);
                String orderId = cursor.getString(indexOrderId);
                if(workWithLocalStorageApi.isMutualReview(orderId)) {
                    sumRates += Float.valueOf(cursor.getString(indexRating));
                    counter++;
                }
                else {
                    if(workWithLocalStorageApi.isAfterThreeDays(workingTimeId)){
                        sumRates += Float.valueOf(cursor.getString(indexRating));
                        counter++;
                    }
                }
            } while (cursor.moveToNext());

            if(counter!=0){
                float avgRating = sumRates / counter;
                addRatingToScreen(avgRating, counter);
            }
            else {
                setWithoutRating();
            }
        } else {
            setWithoutRating();
        }
        cursor.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateProfileData(ownerId);

        workWithLocalStorageApi.setPhotoAvatar(ownerId,avatarImage);

        if(userId.equals(ownerId)){
            // если это мой сервис
            updateOrdersList(userId);
            updateServicesList(userId);
            
            // выводим кол-во подписок
            long subscriptionsCount = getCountOfSubscriptions();
            String btnText = SUBSCRIPTIONS;

            if (subscriptionsCount != 0) {
                btnText += " (" + subscriptionsCount + ")";
            }
            subscribersBtn.setText(btnText);
        }
        else{
            updateServicesList(ownerId);
        }
    }

    private long getCountOfSubscriptions() {
        String userId = getUserId();

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String sqlQuery =
                "SELECT " + DBHelper.KEY_WORKER_ID
                        + " FROM " + DBHelper.TABLE_SUBSCRIBERS
                        + " WHERE " + DBHelper.TABLE_SUBSCRIBERS + "." + DBHelper.KEY_USER_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[] {userId});
        return cursor.getCount();
    }

    //подгрузка сервисов на serviceList
    private void updateServicesList(String userId) {
        //количество сервисов отображаемых на данный момент(старых)
        servicesLayout.removeAllViews();
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String sqlQueryService =
                "SELECT "
                        + DBHelper.KEY_ID + ", "
                        + DBHelper.KEY_NAME_SERVICES
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES
                        + " WHERE "
                        + DBHelper.KEY_USER_ID+ " = ? ";

        Cursor cursor = database.rawQuery(sqlQueryService, new String[]{userId});
        float sumRates = 0;
        long countOfRates = 0;

        if(cursor.moveToFirst()){
            int indexServiceId = cursor.getColumnIndex(DBHelper.KEY_ID);
            int indexServiceName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            do{

                String serviceId = cursor.getString(indexServiceId);
                String serviceName = cursor.getString(indexServiceName);

                String mainSqlQuery = "SELECT "
                        + DBHelper.KEY_RATING_REVIEWS + ", "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + ", "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID + " AS " + ORDER_ID
                        + " FROM "
                        + DBHelper.TABLE_WORKING_TIME + ", "
                        + DBHelper.TABLE_REVIEWS + ", "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_CONTACTS_USERS + ", "
                        + DBHelper.TABLE_ORDERS + ", "
                        + DBHelper.TABLE_CONTACTS_SERVICES
                        + " WHERE "
                        + DBHelper.KEY_ORDER_ID_REVIEWS
                        + " = "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                        + " = "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                        + " = "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                        + " = "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID
                        + " = "
                        + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + " = ? "
                        + " AND "
                        + DBHelper.KEY_TYPE_REVIEWS + " = ? ";

                Cursor cursorWithReview = database.rawQuery(mainSqlQuery, new String[]{serviceId, REVIEW_FOR_SERVICE});

                if(cursorWithReview.moveToFirst()){
                    int indexRating = cursorWithReview.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);
                    int indexWorkingTimeId = cursorWithReview.getColumnIndex(DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID);
                    int indexOrderId= cursorWithReview.getColumnIndex(ORDER_ID);
                    do {
                        String workingTimeId = cursorWithReview.getString(indexWorkingTimeId);
                        String orderId = cursorWithReview.getString(indexOrderId);
                        if(workWithLocalStorageApi.isMutualReview(orderId)) {
                            sumRates += Float.valueOf(cursorWithReview.getString(indexRating));
                            countOfRates++;
                        }
                        else {
                            if(workWithLocalStorageApi.isAfterThreeDays(workingTimeId)){
                                sumRates += Float.valueOf(cursorWithReview.getString(indexRating));
                                countOfRates++;
                            }
                        }
                    }while (cursorWithReview.moveToNext());
                }
                cursorWithReview.close();

                Service service = new Service();
                service.setId(serviceId);
                service.setName(serviceName);
                if(countOfRates !=0){
                    float avgRating = sumRates / countOfRates;
                    addToScreenOnProfile(avgRating,service);
                    countOfRates = 0;
                    sumRates = 0;
                }else {
                    addToScreenOnProfile(0,service);
                }
            }while (cursor.moveToNext());

        }

        cursor.close();
    }

    private void addToScreenOnProfile(float avgRating, Service service) {

        foundServiceProfileElement fElement
                = new foundServiceProfileElement(avgRating,service);
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(R.id.servicesProfileLayout, fElement);

        transaction.commit();
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
                        + DBHelper.TABLE_WORKING_TIME + ", "
                        + DBHelper.TABLE_ORDERS
                        + " WHERE "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + " = " + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                        + " AND "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                        + " = " + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                        + " AND "
                        + DBHelper.KEY_IS_CANCELED_ORDERS + " = 'false'"
                        + " AND "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID + " = " + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                        + " AND "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + " = ? "
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
    private String getUserId() {
        return  FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void addRatingToScreen(Float avgRating, Long countOfRates) {
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

    public boolean checkSubscription() {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sqlQuery =
                "SELECT * FROM "
                + DBHelper.TABLE_SUBSCRIBERS
                + " WHERE "
                + DBHelper.KEY_USER_ID + " = ? AND "
                + DBHelper.KEY_WORKER_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[] {userId, ownerId});
        if(cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }

    }

    private void goToLogIn() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(this, Authorization.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void setWithoutRating(){
        withoutRatingText.setVisibility(View.VISIBLE);
    }

    private void goToAddService() {
        Intent intent = new Intent(this, AddService.class);
        startActivity(intent);
    }
  
    private void goToSubscribers() {
        Intent intent = new Intent(this, Subscribers.class);
        startActivity(intent);
    }

}