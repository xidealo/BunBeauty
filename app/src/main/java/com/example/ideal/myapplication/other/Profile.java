package com.example.ideal.myapplication.other;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.createService.AddService;
import com.example.ideal.myapplication.fragments.foundElements.foundOrderElement;
import com.example.ideal.myapplication.fragments.foundElements.foundServiceProfileElement;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.subscriptions.Subscribers;
import com.google.firebase.auth.FirebaseAuth;

public class Profile extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "DBInf";
    private static final String OWNER_ID = "owner id";
    private static final String REVIEW_FOR_SERVICE = "review for service";

    private static final String REVIEW_FOR_USER = "review for user";
    private static final String ORDER_ID = "order_id";

    private static final String STATUS = "status";

    private static final String SUBSCRIPTIONS = "подписки";
    private static final String SUBSCRIBERS = "подписчики";

    private String userId;
    private String ownerId;

    private TextView nameText;
    private TextView cityText;
    private TextView phoneText;
    private TextView withoutRatingText;
    private TextView subscribersText;

    private Button subscriptionsBtn;

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

        Button addServicesBtn = findViewById(R.id.addServicesProfileBtn);
        subscriptionsBtn = findViewById(R.id.subscriptionsProfileBtn);
        avatarImage = findViewById(R.id.avatarProfileImage);

        SwitchCompat servicesOrOrdersSwitch = findViewById(R.id.servicesOrOrdersProfileSwitch);

        servicesScroll = findViewById(R.id.servicesProfileScroll);
        ordersScroll = findViewById(R.id.orderProfileScroll);
        servicesLayout = findViewById(R.id.servicesProfileLayout);
        ordersLayout = findViewById(R.id.ordersProfileLayout);

        nameText = findViewById(R.id.nameProfileText);
        cityText = findViewById(R.id.cityProfileText);
        phoneText = findViewById(R.id.phoneProfileText);
        subscribersText = findViewById(R.id.subscribersProfileText);

        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        manager = getSupportFragmentManager();
        userId = getUserId();

        // Получаем id владельца профиля
        ownerId = getIntent().getStringExtra(OWNER_ID);
        // Проверяем id владельца профиля
        if (ownerId == null) {
            // Если null значит пользователь только что вошёл и это его сервис
            ownerId = userId;
        }

        PanelBuilder panelBuilder = new PanelBuilder(ownerId);
        panelBuilder.buildHeader(manager, "Профиль", R.id.headerProfileLayout);
        panelBuilder.buildFooter(manager, R.id.footerProfileLayout);

        // Проверяем совпадают id пользователя и владельца
        if (userId.equals(ownerId)) {
            // Совпадают - это мой профиль
            servicesLayout.setVisibility(View.GONE);
            servicesScroll.setVisibility(View.GONE);

            servicesOrOrdersSwitch.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        buttonView.setText("My services");
                        ordersLayout.setVisibility(View.GONE);
                        ordersScroll.setVisibility(View.GONE);
                        servicesLayout.setVisibility(View.VISIBLE);
                        servicesScroll.setVisibility(View.VISIBLE);
                    } else {
                        buttonView.setText("My orders");
                        servicesLayout.setVisibility(View.GONE);
                        servicesScroll.setVisibility(View.GONE);
                        ordersLayout.setVisibility(View.VISIBLE);
                        ordersScroll.setVisibility(View.VISIBLE);
                    }
                }
            });
            addServicesBtn.setOnClickListener(this);
            subscriptionsBtn.setOnClickListener(this);
        } else {
            // Не совпадает - чужой профиль

            // Скрываем функционал
            servicesOrOrdersSwitch.setVisibility(View.GONE);
            addServicesBtn.setVisibility(View.GONE);
            subscriptionsBtn.setVisibility(View.GONE);
            subscribersText.setVisibility(View.GONE);

            // Отображаем все сервисы пользователя
            ordersLayout.setVisibility(View.GONE);
            ordersScroll.setVisibility(View.GONE);
            servicesLayout.setVisibility(View.VISIBLE);
            servicesScroll.setVisibility(View.VISIBLE);
        }

        loadRatingForUser();
        avatarImage.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addServicesProfileBtn:
                goToAddService();
                break;
            case R.id.subscriptionsProfileBtn:
                goToSubscribers(SUBSCRIPTIONS);
                break;

           /* case R.id.subscribersProfileBtn:
                goToSubscribers(SUBSCRIBERS);
                break;*/

            default:
                break;
        }
    }

    // получаем данные о пользователе и отображаем в прфоиле
    private void updateProfileData(String userId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //получаем имя, фамилию и город пользователя по его id
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_NAME_USERS + ", "
                        + DBHelper.KEY_PHONE_USERS + ", "
                        + DBHelper.KEY_CITY_USERS
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_USERS
                        + " WHERE "
                        + DBHelper.KEY_ID + " = ?";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{userId});

        if (cursor.moveToFirst()) {
            int indexName = cursor.getColumnIndex(DBHelper.KEY_NAME_USERS);
            int indexCity = cursor.getColumnIndex(DBHelper.KEY_CITY_USERS);
            int indexPhone = cursor.getColumnIndex(DBHelper.KEY_PHONE_USERS);

            String[] names = cursor.getString(indexName).split(" ");
            for (int i = 0; i < names.length; i++) {
                names[i] = names[i].substring(0, 1).toUpperCase() + names[i].substring(1);
            }
            String name = names[0] + " " + names[1];

            String city = cursor.getString(indexCity).substring(0, 1).toUpperCase()
                    + cursor.getString(indexCity).substring(1);
            String phone = cursor.getString(indexPhone);
            nameText.setText(name);
            cityText.setText(city);
            phoneText.setText(phone);
            cursor.close();
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
            int indexWorkingTimeId = cursor.getColumnIndex(DBHelper.KEY_ID);
            int indexOrderId = cursor.getColumnIndex(ORDER_ID);
            do {
                String workingTimeId = cursor.getString(indexWorkingTimeId);
                String orderId = cursor.getString(indexOrderId);
                if (workWithLocalStorageApi.isMutualReview(orderId)) {
                    sumRates += Float.valueOf(cursor.getString(indexRating));
                    counter++;
                } else {
                    if (workWithLocalStorageApi.isAfterThreeDays(workingTimeId)) {
                        sumRates += Float.valueOf(cursor.getString(indexRating));
                        counter++;
                    }
                }
            } while (cursor.moveToNext());

            if (counter != 0) {
                float avgRating = sumRates / counter;
                //addRatingToScreen(avgRating, counter);
                //метод, который добалвяет оценку
                Log.d(TAG, "loadRatingForUser: " + avgRating);
            } else {
                //setWithoutRating();
            }
        } else {
            //setWithoutRating();
        }
        cursor.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateProfileData(ownerId);

        int width = getResources().getDimensionPixelSize(R.dimen.photo_width);
        int height = getResources().getDimensionPixelSize(R.dimen.photo_height);
        workWithLocalStorageApi.setPhotoAvatar(ownerId,avatarImage,width,height);

        if (userId.equals(ownerId)) {
            // если это мой сервис
            updateOrdersList(userId);

            // выводим кол-во подписок
            long subscriptionsCount = getCountOfSubscriptions();
            String btnText = SUBSCRIPTIONS;

            if (subscriptionsCount != 0) {
                btnText += " (" + subscriptionsCount + ")";
            }
            subscriptionsBtn.setText(btnText);
            String subscribersBtnText = "Кол-во подписчиков:";
            long subscribersCount = getCountOfSubscribers();

            if (subscribersCount != 0) {
                subscribersBtnText += " " + subscribersCount;
            }
            subscribersText.setText(subscribersBtnText);
        }

        updateServicesList(userId);

    }

    private long getCountOfSubscriptions() {
        String userId = getUserId();

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String sqlQuery =
                "SELECT " + DBHelper.KEY_WORKER_ID
                        + " FROM " + DBHelper.TABLE_SUBSCRIBERS
                        + " WHERE " + DBHelper.TABLE_SUBSCRIBERS + "." + DBHelper.KEY_USER_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{userId});
        return cursor.getCount();
    }

    private long getCountOfSubscribers() {

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String sqlQuery =
                "SELECT " + DBHelper.KEY_WORKER_ID
                        + " FROM " + DBHelper.TABLE_SUBSCRIBERS
                        + " WHERE " + DBHelper.TABLE_SUBSCRIBERS + "." + DBHelper.KEY_WORKER_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{ownerId});
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
                        + DBHelper.KEY_USER_ID + " = ? ";

        Cursor cursor = database.rawQuery(sqlQueryService, new String[]{ownerId});
        float sumRates = 0;
        long countOfRates = 0;

        if (cursor.moveToFirst()) {
            int indexServiceId = cursor.getColumnIndex(DBHelper.KEY_ID);
            int indexServiceName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            do {

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
                        + DBHelper.KEY_TYPE_REVIEWS + " = ? "
                        + " AND "
                        + DBHelper.KEY_RATING_REVIEWS + " != 0 ";

                Cursor cursorWithReview = database.rawQuery(mainSqlQuery, new String[]{serviceId, REVIEW_FOR_SERVICE});

                if (cursorWithReview.moveToFirst()) {
                    int indexRating = cursorWithReview.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);
                    int indexWorkingTimeId = cursorWithReview.getColumnIndex(DBHelper.KEY_ID);
                    int indexOrderId = cursorWithReview.getColumnIndex(ORDER_ID);
                    do {
                        String workingTimeId = cursorWithReview.getString(indexWorkingTimeId);
                        String orderId = cursorWithReview.getString(indexOrderId);
                        if (workWithLocalStorageApi.isMutualReview(orderId)) {
                            sumRates += Float.valueOf(cursorWithReview.getString(indexRating));
                            countOfRates++;
                        } else {
                            if (workWithLocalStorageApi.isAfterThreeDays(workingTimeId)) {
                                sumRates += Float.valueOf(cursorWithReview.getString(indexRating));
                                countOfRates++;
                            }
                        }
                    } while (cursorWithReview.moveToNext());
                }
                cursorWithReview.close();

                Service service = new Service();
                service.setId(serviceId);
                service.setName(serviceName);
                if (countOfRates != 0) {
                    float avgRating = sumRates / countOfRates;
                    Log.d(TAG, "updateServicesList: " + avgRating);
                    addToScreenOnProfile(avgRating, service);
                    countOfRates = 0;
                    sumRates = 0;
                } else {
                    addToScreenOnProfile(0, service);
                }
            } while (cursor.moveToNext());

        }

        cursor.close();
    }

    private void addToScreenOnProfile(float avgRating, Service service) {

        foundServiceProfileElement fElement
                = new foundServiceProfileElement(avgRating, service);
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
                    String foundId = cursor.getString(indexServiceId);
                    String foundName = cursor.getString(indexServiceName);
                    String foundDate = cursor.getString(indexDate);
                    String foundTime = cursor.getString(indexTime);

                    addOrderToScreen(foundId, foundName, foundDate, foundTime);
                    visibleCount++;

                    //пока в курсоре есть строки и есть новые записи
                } while (cursor.moveToPrevious() && (cursorCount > visibleCount));
            }
        }
        cursor.close();
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
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

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{userId, ownerId});
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }

    }

    private void goToAddService() {
        Intent intent = new Intent(this, AddService.class);
        startActivity(intent);
    }

    private void goToSubscribers(String status) {
        Intent intent = new Intent(this, Subscribers.class);
        intent.putExtra(STATUS, status);
        startActivity(intent);
    }

}