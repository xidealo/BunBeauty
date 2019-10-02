package com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.profile;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.adapters.OrderAdapter;
import com.bunbeauty.ideal.myapplication.adapters.ServiceProfileAdapter;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileInteractor;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.DBHelper;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Order;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User;
import com.bunbeauty.ideal.myapplication.fragments.SwitcherElement;
import com.bunbeauty.ideal.myapplication.helpApi.PanelBuilder;
import com.bunbeauty.ideal.myapplication.helpApi.SubscriptionsApi;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.bunbeauty.ideal.myapplication.other.ISwitcher;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, ISwitcher {

    private static final String TAG = "DBInf";
    private static final String REVIEW_FOR_USER = "review for user";
    private static final String SUBSCRIPTIONS = "подписки";

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
    private Button addServicesBtn;
    private ProgressBar progressBar;
    private LinearLayout subscriptionsLayout;
    private SQLiteDatabase database;

    private ProfileInteractor profileInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        initView();

        if (profileInteractor.isFirstEnter(getIntent())) {
            profileInteractor.initFCM(Objects.requireNonNull(profileInteractor.getUserId()));
        }

        if (profileInteractor.userIsOwner(getIntent())) {
            showMyProfile();
        } else {
            showNotMyProfile();
        }
    }

    private void initView() {
        subscriptionsLayout = findViewById(R.id.subscriptionsProfileLayout);
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
        nameText = findViewById(R.id.nameProfileText);
        cityText = findViewById(R.id.cityProfileText);
        phoneText = findViewById(R.id.phoneProfileText);
        subscribersText = findViewById(R.id.subscribersProfileText);

        profileInteractor = new ProfileInteractor(this);

        serviceList = new ArrayList<>();
        orderList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManagerSecond = new LinearLayoutManager(this);
        recyclerViewService.setLayoutManager(layoutManagerSecond);
        //
        DBHelper dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();
        workWithLocalStorageApi = new WorkWithLocalStorageApi(database);
        //
        manager = getSupportFragmentManager();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addServicesProfileBtn:
                profileInteractor.goToAddService(this);
                break;

            case R.id.subscriptionsProfileLayout:
                profileInteractor.goToSubscribers(this, SUBSCRIPTIONS);
                break;

            case R.id.ratingProfileLayout:
                profileInteractor.goToUserComments(this, REVIEW_FOR_USER,
                        Objects.requireNonNull(profileInteractor.getOwnerId(getIntent())));
                break;

            default:
                break;
        }
    }

    public void showProfileData(User user) {
        showProfileText(user.getName(), user.getCity(), user.getPhone());
        createRatingBar(user.getRating());
        showAvatar();

        updateServicesList(profileInteractor.getOwnerId(getIntent()));
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
                showProfileData(user);
            }
        }
        userCursor.close();
    }

    private String getCountOfRates() {
        //получаем имя, фамилию и город пользователя по его id
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_COUNT_OF_RATES_USERS
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_USERS
                        + " WHERE "
                        + DBHelper.KEY_ID + " = ?";
        Cursor userCursor = database.rawQuery(sqlQuery, new String[]{profileInteractor.getOwnerId(getIntent())});

        if (userCursor.moveToFirst()) {
            int indexCountOfRates = userCursor.getColumnIndex(DBHelper.KEY_COUNT_OF_RATES_USERS);

            return countOfRates = userCursor.getString(indexCountOfRates);
        }
        userCursor.close();
        return "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderList.clear();
        createPanels();

        //был я здесь или нет (переписать)
        if (profileInteractor.getUserId().equals(profileInteractor.getOwnerId(getIntent()))) {
            updateProfileData(profileInteractor.getOwnerId(getIntent()));
        } else {
            if (!userIdsFirstSetProfile.contains(profileInteractor.getOwnerId(getIntent()))) {
                //загрузка из фб
                profileInteractor.loadProfile(Objects.requireNonNull(profileInteractor.getOwnerId(getIntent())));
                userIdsFirstSetProfile.add(profileInteractor.getOwnerId(getIntent()));
            } else {
                updateProfileData(profileInteractor.getOwnerId(getIntent()));
            }
        }

        if (profileInteractor.userIsOwner(getIntent())) {
            // если это мой сервис
            updateOrdersList(profileInteractor.getUserId());
            // выводим кол-во подписок
            showSubscriptions();
            showSubscribers();
        }
    }

    private void showSubscriptions(){
        long subscriptionsCount = SubscriptionsApi.getCountOfSubscriptions(database, profileInteractor.getUserId());
        String subscriptionText = "Подписки";

        if (subscriptionsCount != 0) {
            subscriptionText += " (" + subscriptionsCount + ")";
        }
        subscriptionsText.setText(subscriptionText);
    }

    private void showSubscribers(){
        String subscribersBtnText = "Подписчики:";
        long subscribersCount = getCountOfSubscribers();

        if (subscribersCount != 0) {
            subscribersBtnText += " " + subscribersCount;
        }
        subscribersText.setText(subscribersBtnText);
    }

    private long getCountOfSubscribers() {
        String sqlQuery =
                "SELECT " + DBHelper.KEY_SUBSCRIBERS_COUNT_USERS
                        + " FROM " + DBHelper.TABLE_CONTACTS_USERS
                        + " WHERE " + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{profileInteractor.getUserId()});
        if (cursor.moveToFirst()) {
            int indexSubscribersCount = cursor.getColumnIndex(DBHelper.KEY_SUBSCRIBERS_COUNT_USERS);
            return Long.valueOf(cursor.getString(indexSubscribersCount));
        }
        cursor.close();
        return 0;
    }

    private void createRatingBar(float rating) {
        if (rating == 0) {
            setWithoutRating();
        } else {
            addRatingToScreen(rating);
        }
    }
    private void showAvatar() {
        int width = getResources().getDimensionPixelSize(R.dimen.photo_width);
        int height = getResources().getDimensionPixelSize(R.dimen.photo_height);
        workWithLocalStorageApi.setPhotoAvatar(profileInteractor.getOwnerId(getIntent()), avatarImage, width, height);
    }
    private void showProfileText(String name, String city, String phone){
        nameText.setText(name);
        cityText.setText(city);
        phoneText.setText(phone);
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

    //wtf VALENTOS
    public boolean checkSubscription() {
        String sqlQuery =
                "SELECT * FROM "
                        + DBHelper.TABLE_SUBSCRIBERS
                        + " WHERE "
                        + DBHelper.KEY_USER_ID + " = ? AND "
                        + DBHelper.KEY_WORKER_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{profileInteractor.getUserId(), profileInteractor.getOwnerId(getIntent())});
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    private void showMyProfile() {
        recyclerViewService.setVisibility(View.GONE);
        createSwitcher();
        addServicesBtn.setOnClickListener(this);
        subscriptionsLayout.setOnClickListener(this);
    }

    private void showNotMyProfile() {
        hideView();
        recyclerViewService.setVisibility(View.VISIBLE);
    }

    private void createSwitcher() {
        SwitcherElement switcherElement = new SwitcherElement("Записи", "Услуги");
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.switcherProfileLayout, switcherElement);
        transaction.commit();
    }

    private void hideView() {
        addServicesBtn.setVisibility(View.GONE);
        subscriptionsLayout.setVisibility(View.INVISIBLE);
        subscribersText.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
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

    private void createPanels() {
        PanelBuilder panelBuilder = new PanelBuilder(profileInteractor.getOwnerId(getIntent()));
        panelBuilder.buildHeader(manager, "Профиль", R.id.headerProfileLayout);
        panelBuilder.buildFooter(manager, R.id.footerProfileLayout);
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