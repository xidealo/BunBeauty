package com.example.ideal.myapplication.other;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.adapters.OrderAdapter;
import com.example.ideal.myapplication.adapters.ServiceProfileAdapter;
import com.example.ideal.myapplication.createService.AdditionService;
import com.example.ideal.myapplication.entity.FBListener;
import com.example.ideal.myapplication.fragments.SwitcherElement;
import com.example.ideal.myapplication.fragments.objects.Order;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.ListeningManager;
import com.example.ideal.myapplication.helpApi.LoadingProfileData;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.SubscriptionsApi;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.example.ideal.myapplication.reviews.Comments;
import com.example.ideal.myapplication.subscriptions.Subscribers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class Profile extends AppCompatActivity implements View.OnClickListener, ISwitcher {

    private static final String TAG = "DBInf";
    private static final String OWNER_ID = "owner id";
    private static final String USERS = "users";
    private static final String REVIEW_FOR_USER = "review for user";
    private static final String STATUS = "status";
    private static final String SUBSCRIPTIONS = "подписки";
    private static final String SERVICE_OWNER_ID = "service owner id";
    private static final String SERVICES = "services";
    private static final String COUNT_OF_RATES = "count of rates";
    private static final String AVG_RATING = "avg rating";

    private static final String CITY = "city";
    private static final String NAME = "name";
    private static final String PHONE = "phone";

    private static final String TYPE = "type";
    private static final String TOKEN = "token";

    private String userId;
    private String ownerId;
    private String countOfRates;

    private TextView nameText;
    private TextView cityText;
    private TextView phoneText;
    private TextView withoutRatingText;
    private TextView subscribersText;
    private TextView subscriptionsText;

    private RatingBar ratingBar;

    private LinearLayout ratingForUserLayout;
    private LinearLayout mainLayout;

    private WorkWithLocalStorageApi workWithLocalStorageApi;
    private ImageView avatarImage;

    private FragmentManager manager;

    private ArrayList<Order> orderList;
    private ArrayList<Service> serviceList;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewService;

    private static ArrayList<String> userIdsFirstSetProfile = new ArrayList<>();
    private LinearLayoutManager layoutManagerSecond;
    private Button addServicesBtn;
    private int pastVisibleItems, visibleItemCount, totalItemCount, startIndexOfDownload;
    private ProgressBar progressBar;

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        init();
    }

    private void init() {
        LinearLayout subscriptionsLayout = findViewById(R.id.subscriptionsProfileLayout);
        subscriptionsText = findViewById(R.id.subscriptionsProfileText);

        avatarImage = findViewById(R.id.avatarProfileImage);

        withoutRatingText = findViewById(R.id.withoutRatingProfileText);
        ratingBar = findViewById(R.id.ratingBarProfile);
        progressBar = findViewById(R.id.progressBarProfile);
        ratingForUserLayout = findViewById(R.id.ratingProfileLayout);
        addServicesBtn = findViewById(R.id.addServicesProfileBtn);
        mainLayout = findViewById(R.id.mainLayoutProfile);

        recyclerView = findViewById(R.id.resultsProfileRecycleView);
        recyclerViewService = findViewById(R.id.servicesProfileRecyclerView);
        serviceList = new ArrayList<>();
        orderList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        layoutManagerSecond = new LinearLayoutManager(this);
        recyclerViewService.setLayoutManager(layoutManagerSecond);

        nameText = findViewById(R.id.nameProfileText);
        cityText = findViewById(R.id.cityProfileText);
        phoneText = findViewById(R.id.phoneProfileText);
        subscribersText = findViewById(R.id.subscribersProfileText);

        DBHelper dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();
        workWithLocalStorageApi = new WorkWithLocalStorageApi(database);
        startIndexOfDownload = 0;
        manager = getSupportFragmentManager();
        userId = getUserId();

        // Получаем id владельца профиля
        ownerId = getIntent().getStringExtra(OWNER_ID);
        // Проверяем id владельца профиля
        if (ownerId == null) {
            // Если null значит пользователь только что вошёл и это его сервис
            ownerId = userId;
            initFCM();
        }
        // Проверяем совпадают id пользователя и владельца
        if (userId.equals(ownerId)) {
            // Совпадают - это мой профиль
            recyclerViewService.setVisibility(View.GONE);

            SwitcherElement switcherElement = new SwitcherElement("Записи", "Услуги");
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.switcherProfileLayout, switcherElement);
            transaction.commit();

            addServicesBtn.setOnClickListener(this);
            subscriptionsLayout.setOnClickListener(this);
        } else {
            // Не совпадает - чужой профиль
            // Скрываем функционал
            addServicesBtn.setVisibility(View.GONE);
            subscriptionsLayout.setVisibility(View.INVISIBLE);
            subscribersText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);

            // Отображаем все сервисы пользователя
            recyclerViewService.setVisibility(View.VISIBLE);
        }

        avatarImage.setOnClickListener(this);
    }

    private void initFCM() {
        String token = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(USERS)
                .child(ownerId)
                .child(TOKEN)
                .setValue(token);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.addServicesProfileBtn:
                goToAddService();
                break;

            case R.id.subscriptionsProfileLayout:
                goToSubscribers(SUBSCRIPTIONS);
                break;

            case R.id.ratingProfileLayout:
                goToUserComments(REVIEW_FOR_USER);
                break;

            default:
                break;
        }
    }

    private void loadProfileData(final String ownerId) {
        DatabaseReference userReference = FirebaseDatabase.getInstance()
                .getReference(USERS)
                .child(ownerId);
        ValueEventListener userListener = userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                LoadingProfileData.loadUserInfo(userSnapshot, database);

                LoadingProfileData.loadUserServices(userSnapshot.child(SERVICES),
                        ownerId,
                        database);

                String name = WorkWithStringsApi.doubleCapitalSymbols(userSnapshot.child(NAME).getValue(String.class));
                String city = WorkWithStringsApi.firstCapitalSymbol(userSnapshot.child(CITY).getValue(String.class));

                User user = new User();
                user.setName(name);
                user.setCity(city);
                user.setPhone(userSnapshot.child(PHONE).getValue(String.class));
                user.setCountOfRates(userSnapshot.child(COUNT_OF_RATES).getValue(Long.class));
                user.setRating(userSnapshot.child(AVG_RATING).getValue(Float.class));
                //for intent
                countOfRates = userSnapshot.child(COUNT_OF_RATES).getValue(Long.class).toString();
                setProfile(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        ListeningManager.addToListenerList(new FBListener(userReference, userListener));
    }

    // получаем данные о пользователе и отображаем в прфоиле
    private void updateProfileData(String ownerId) {

        //получаем имя, фамилию и город пользователя по его id
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_NAME_USERS + ", "
                        + DBHelper.KEY_PHONE_USERS + ", "
                        + DBHelper.KEY_COUNT_OF_RATES_USERS + ", "
                        + DBHelper.KEY_RATING_USERS + ", "
                        + DBHelper.KEY_CITY_USERS
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_USERS
                        + " WHERE "
                        + DBHelper.KEY_ID + " = ?";
        Cursor userCursor = database.rawQuery(sqlQuery, new String[]{ownerId});

        if (userCursor.moveToFirst()) {
            if (userCursor.getString(userCursor.getColumnIndex(DBHelper.KEY_PHONE_USERS)) != null) {
                int indexName = userCursor.getColumnIndex(DBHelper.KEY_NAME_USERS);
                int indexCity = userCursor.getColumnIndex(DBHelper.KEY_CITY_USERS);
                int indexPhone = userCursor.getColumnIndex(DBHelper.KEY_PHONE_USERS);
                int indexCountOfRates = userCursor.getColumnIndex(DBHelper.KEY_COUNT_OF_RATES_USERS);
                int indexRating = userCursor.getColumnIndex(DBHelper.KEY_RATING_USERS);

                String name = WorkWithStringsApi.doubleCapitalSymbols(userCursor.getString(indexName));
                String city = WorkWithStringsApi.firstCapitalSymbol(userCursor.getString(indexCity));

                User user = new User();
                user.setName(name);
                user.setCity(city);
                user.setPhone(userCursor.getString(indexPhone));
                user.setCountOfRates(userCursor.getLong(indexCountOfRates));
                user.setRating(userCursor.getFloat(indexRating));
                //for intent
                countOfRates = userCursor.getString(indexCountOfRates);

                setProfile(user);
            }
        }
        userCursor.close();
    }

    private void setProfile(User user) {

        nameText.setText(user.getName());
        cityText.setText(user.getCity());
        phoneText.setText(user.getPhone());
        //ratingBar
        float rating = user.getRating();
        if ( rating == 0) {
            setWithoutRating();
        } else {
            addRatingToScreen(rating);
        }

        //загрузка аватарки
        int width = getResources().getDimensionPixelSize(R.dimen.photo_width);
        int height = getResources().getDimensionPixelSize(R.dimen.photo_height);
        workWithLocalStorageApi.setPhotoAvatar(ownerId, avatarImage, width, height);

        updateServicesList(ownerId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderList.clear();
        if (userId.equals(ownerId)) {
            updateProfileData(ownerId);
        } else {
            if (!userIdsFirstSetProfile.contains(ownerId)) {
                //загрузка из фб
                loadProfileData(ownerId);
                userIdsFirstSetProfile.add(ownerId);
            } else {
                updateProfileData(ownerId);
            }
        }

        PanelBuilder panelBuilder = new PanelBuilder(ownerId);
        panelBuilder.buildHeader(manager, "Профиль", R.id.headerProfileLayout);
        panelBuilder.buildFooter(manager, R.id.footerProfileLayout);

        if (userId.equals(ownerId)) {
            // если это мой сервис
            updateOrdersList(userId);

            // выводим кол-во подписок
            long subscriptionsCount = SubscriptionsApi.getCountOfSubscriptions(database, userId);
            String subscriptionText = "Подписки";

            if (subscriptionsCount != 0) {
                subscriptionText += " (" + subscriptionsCount + ")";
            }
            subscriptionsText.setText(subscriptionText);
            String subscribersBtnText = "Подписчики:";
            long subscribersCount = getCountOfSubscribers();

            if (subscribersCount != 0) {
                subscribersBtnText += " " + subscribersCount;
            }
            subscribersText.setText(subscribersBtnText);
        }
    }

    private long getCountOfSubscribers() {

        String sqlQuery =
                "SELECT " + DBHelper.KEY_SUBSCRIBERS_COUNT_USERS
                        + " FROM " + DBHelper.TABLE_CONTACTS_USERS
                        + " WHERE " + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{userId});
        if (cursor.moveToFirst()) {
            int indexSubscribersCount = cursor.getColumnIndex(DBHelper.KEY_SUBSCRIBERS_COUNT_USERS);
            return Long.valueOf(cursor.getString(indexSubscribersCount));
        }
        cursor.close();
        return 0;
    }

    //подгрузка сервисов на serviceList
    private void updateServicesList(String ownerId) {
        serviceList.clear();

        //количество сервисов отображаемых на данный момент(старых)
        String sqlQueryService =
                "SELECT "
                        + DBHelper.KEY_ID + ", "
                        + DBHelper.KEY_NAME_SERVICES + ", "
                        + DBHelper.KEY_RATING_SERVICES
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES
                        + " WHERE "
                        + DBHelper.KEY_USER_ID + " = ? "
                        + " AND "
                        + DBHelper.KEY_RATING_SERVICES + " IS NOT NULL";

        Cursor cursor = database.rawQuery(sqlQueryService, new String[]{ownerId});

        if (cursor.moveToFirst()) {
            int indexServiceId = cursor.getColumnIndex(DBHelper.KEY_ID);
            int indexServiceName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            int indexServiceRating = cursor.getColumnIndex(DBHelper.KEY_RATING_SERVICES);
            do {

                String serviceId = cursor.getString(indexServiceId);
                String serviceName = cursor.getString(indexServiceName);
                float serviceRating = Float.valueOf(cursor.getString(indexServiceRating));

                Service service = new Service();
                service.setId(serviceId);
                service.setName(serviceName);
                service.setAverageRating(serviceRating);

                serviceList.add(service);
            } while (cursor.moveToNext());
        }
        ServiceProfileAdapter serviceAdapter = new ServiceProfileAdapter(serviceList.size(), serviceList);
        recyclerViewService.setAdapter(serviceAdapter);
        progressBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        cursor.close();
    }

    //добавляет вновь добавленные записи (обновление ordersList)
    private void updateOrdersList(String userId) {
        // количство записей отображаемых на данный момент(старых)
        int visibleCount = orderList.size();

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

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{userId});

        int cursorCount = cursor.getCount();

        //если есть новые записи
        if (cursorCount > visibleCount) {
            //Идём с конца
            if (cursor.moveToLast()) {
                int indexServiceId = cursor.getColumnIndex(DBHelper.KEY_ID);
                int indexServiceName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
                int indexDate = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS);
                int indexTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);

                do {
                    Order order = new Order();
                    order.setOrderId(cursor.getString(indexServiceId));
                    order.setOrderName(cursor.getString(indexServiceName));
                    order.setOrderDate(cursor.getString(indexDate));
                    order.setOrderTime(cursor.getString(indexTime));
                    orderList.add(order);
                    visibleCount++;
                    //пока в курсоре есть строки и есть новые записи
                } while (cursor.moveToPrevious() && (cursorCount > visibleCount));
            }
        }

        OrderAdapter orderAdapter = new OrderAdapter(orderList.size(), orderList);
        recyclerView.setAdapter(orderAdapter);
        cursor.close();
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public boolean checkSubscription() {

        String sqlQuery =
                "SELECT * FROM "
                        + DBHelper.TABLE_SUBSCRIBERS
                        + " WHERE "
                        + DBHelper.KEY_USER_ID + " = ? AND "
                        + DBHelper.KEY_WORKER_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{userId, ownerId});
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    private void addRatingToScreen(float avgRating) {
        ratingBar.setVisibility(View.VISIBLE);
        ratingBar.setRating(avgRating);
        ratingForUserLayout.setOnClickListener(this);
    }

    private void setWithoutRating() {
        ratingBar.setVisibility(View.GONE);
        withoutRatingText.setVisibility(View.VISIBLE);
    }

    private void goToAddService() {
        Intent intent = new Intent(this, AdditionService.class);
        startActivity(intent);
    }

    private void goToSubscribers(String status) {
        Intent intent = new Intent(this, Subscribers.class);
        intent.putExtra(STATUS, status);
        startActivity(intent);
    }

    private void goToUserComments(String status) {
        Intent intent = new Intent(this, Comments.class);
        intent.putExtra(SERVICE_OWNER_ID, ownerId);
        intent.putExtra(COUNT_OF_RATES, countOfRates);
        intent.putExtra(TYPE, status);
        startActivity(intent);
    }

    @Override
    public void firstSwitcherAct() {
        recyclerViewService.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        addServicesBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void secondSwitcherAct() {
        addServicesBtn.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        recyclerViewService.setVisibility(View.VISIBLE);
    }
}