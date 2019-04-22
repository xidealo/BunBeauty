package com.example.ideal.myapplication.other;

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
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.foundElements.foundServiceElement;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.DownloadServiceData;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainScreen extends AppCompatActivity implements View.OnClickListener {

    // добавить, чтобы не было видно своих сервисов
    // например номер юзера, возвращаемого сервиса не должен быть равен локальному
    private static final String TAG = "DBInf";

    private static final String USERS = "users";
    private static final String NAME = "name";
    private static final String PHONE = "phone";
    private static final String CITY = "city";
    private static final String REVIEW_FOR_SERVICE = "review for service";
    private static final String ORDER_ID = "order_id";

    private static final String SERVICES = "services";
    private static final String MAX_COST = "max_cost";

    private long maxCost;
    private ArrayList<Object[]> serviceList;

    private LinearLayout resultsLayout;

    private WorkWithTimeApi workWithTimeApi;
    private DBHelper dbHelper;
    private FragmentManager manager;
    private Button nailsBtn;
    private Button hairBtn;
    private Button eyeBtn;
    private Button makeUpBtn;
    private Button massageBtn;
    private Button otherBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        resultsLayout = findViewById(R.id.resultsMainScreenLayout);

        serviceList = new ArrayList<>();

        dbHelper = new DBHelper(this);
        manager = getSupportFragmentManager();
        workWithTimeApi = new WorkWithTimeApi();

        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerMainScreenLayout);
        panelBuilder.buildHeader(manager, "Главная", R.id.headerMainScreenLayout);

        nailsBtn = findViewById(R.id.nailsMainScreenBtn);

        hairBtn = findViewById(R.id.hairMainScreenBtn);

        eyeBtn = findViewById(R.id.eyeMainScreenBtn);

        makeUpBtn = findViewById(R.id.makeUpMainScreenBtn);

        massageBtn = findViewById(R.id.massageMainScreenBtn);

        otherBtn = findViewById(R.id.otherMainScreenBtn);

        nailsBtn.setOnClickListener(this);
        eyeBtn.setOnClickListener(this);
        hairBtn.setOnClickListener(this);
        makeUpBtn.setOnClickListener(this);
        massageBtn.setOnClickListener(this);
        otherBtn.setOnClickListener(this);

        nailsBtn.setTag(R.string.selectedId, false);
        hairBtn.setTag(R.string.selectedId, false);
        eyeBtn.setTag(R.string.selectedId, false);
        makeUpBtn.setTag(R.string.selectedId, false);
        massageBtn.setTag(R.string.selectedId, false);
        otherBtn.setTag(R.string.selectedId, false);

        createMainScreen("");
    }

    @Override
    public void onClick(View v) {
        String category = "";

        //очищаем прежний сервис лист
        serviceList.clear();
        Button btn = (Button) v;

        if (Boolean.valueOf((btn.getTag(R.string.selectedId)).toString())) {
            btn.setBackgroundResource(R.drawable.time_button);
            btn.setTag(R.string.selectedId, false);
            category = "";
        } else {

            nailsBtn.setTag(R.string.selectedId, false);
            hairBtn.setTag(R.string.selectedId, false);
            eyeBtn.setTag(R.string.selectedId, false);
            makeUpBtn.setTag(R.string.selectedId, false);
            massageBtn.setTag(R.string.selectedId, false);
            otherBtn.setTag(R.string.selectedId, false);
            nailsBtn.setBackgroundResource(R.drawable.day_button);
            hairBtn.setBackgroundResource(R.drawable.day_button);
            eyeBtn.setBackgroundResource(R.drawable.day_button);
            makeUpBtn.setBackgroundResource(R.drawable.day_button);
            massageBtn.setBackgroundResource(R.drawable.day_button);
            otherBtn.setBackgroundResource(R.drawable.day_button);
            btn.setBackgroundResource(R.drawable.pressed_button);

            btn.setTag(R.string.selectedId, true);

            switch (v.getId()) {
                case R.id.nailsMainScreenBtn:
                    category = nailsBtn.getText().toString();
                    break;
                case R.id.hairMainScreenBtn:
                    category = hairBtn.getText().toString();
                    break;
                case R.id.eyeMainScreenBtn:
                    category = eyeBtn.getText().toString();
                    break;
                case R.id.makeUpMainScreenBtn:
                    category = makeUpBtn.getText().toString();
                    break;
                case R.id.massageMainScreenBtn:
                    category = massageBtn.getText().toString();
                    break;
                case R.id.otherMainScreenBtn:
                    category = otherBtn.getText().toString();
                    break;
            }
        }

        createMainScreen(category);
    }

    private void createMainScreen(String category) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //получаем id пользователя
        String userId = getUserId();

        //получаем город юзера
        String userCity = getUserCity(database, userId);

        //получаем все сервисы, которые находятся в городе юзера
        getServicesInThisCity(userCity, category);
    }


    private String getUserCity(SQLiteDatabase database, String userId) {
        // Получить город юзера
        // Таблица Users
        // с фиксированным userId
        String sqlQuery =
                "SELECT " + DBHelper.KEY_CITY_USERS
                        + " FROM " + DBHelper.TABLE_CONTACTS_USERS
                        + " WHERE " + DBHelper.KEY_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{userId});

        int indexCity = cursor.getColumnIndex(DBHelper.KEY_CITY_USERS);
        // дефолтное значение
        String city = "Dubna";

        if (cursor.moveToFirst()) {
            city = cursor.getString(indexCity);
        }
        cursor.close();
        return city;
    }

    private void getServicesInThisCity(final String userCity, final String category) {

        final SQLiteDatabase database = dbHelper.getReadableDatabase();

        //возвращение всех пользователей из контретного города
        Query userQuery = FirebaseDatabase.getInstance().getReference(USERS)
                .orderByChild(CITY)
                .equalTo(userCity);

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                maxCost = getMaxCost();
                for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                    String userName = String.valueOf(userSnapshot.child(NAME).getValue());
                    String userPhone = String.valueOf(userSnapshot.child(PHONE).getValue());
                    final String userId = userSnapshot.getKey();

                    final User user = new User();
                    user.setId(userId);
                    user.setName(userName);
                    user.setPhone(userPhone);
                    user.setCity(userCity);

                    DownloadServiceData downloadServiceData = new DownloadServiceData(database);
                    downloadServiceData.loadUserInfo(userSnapshot);
                    downloadServiceData.loadSchedule(userSnapshot.child(SERVICES), userId);

                    updateServicesList(user, category);
                }

                addToMainScreen();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void updateServicesList(User user, String category) {
        //количество сервисов отображаемых на данный момент(старых)
        resultsLayout.removeAllViews();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sqlQuery;
        Cursor cursor;
        // Возвращает id, название, рэйтинг и количество оценивших
        // используем таблицу сервисы
        // уточняем юзера по его id
        if (category.equals("")) {
            sqlQuery =
                    "SELECT * FROM "
                            + DBHelper.TABLE_CONTACTS_SERVICES
                            + " WHERE "
                            + DBHelper.KEY_USER_ID + " = ? ";
            cursor = database.rawQuery(sqlQuery, new String[]{user.getId()});
        } else {
            sqlQuery =
                    "SELECT * FROM "
                            + DBHelper.TABLE_CONTACTS_SERVICES
                            + " WHERE "
                            + DBHelper.KEY_USER_ID + " = ? "
                            + " AND "
                            + DBHelper.KEY_CATEGORY_SERVICES + " = ? ";
            cursor = database.rawQuery(sqlQuery, new String[]{user.getId(), category});
        }

        //Идём с конца
        if (cursor.moveToFirst()) {
            int indexServiceId = cursor.getColumnIndex(DBHelper.KEY_ID);
            int indexServiceName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            int indexServiceCost = cursor.getColumnIndex(DBHelper.KEY_MIN_COST_SERVICES);
            int indexServiceCreationDate = cursor.getColumnIndex(DBHelper.KEY_CREATION_DATE_SERVICES);
            int indexServiceIsPremium = cursor.getColumnIndex(DBHelper.KEY_IS_PREMIUM_SERVICES);

            do {
                String serviceId = cursor.getString(indexServiceId);
                String serviceName = cursor.getString(indexServiceName);
                String serviceCost = cursor.getString(indexServiceCost);

                boolean isPremium = Boolean.valueOf(cursor.getString(indexServiceIsPremium));
                String creationDate = cursor.getString(indexServiceCreationDate);

                Service service = new Service();
                service.setId(serviceId);
                service.setName(serviceName);
                service.setCost(serviceCost);
                service.setIsPremium(isPremium);
                service.setCreationDate(creationDate);
                service.setAverageRating(figureAverageRating(serviceId));

                addToServiceList(service, user);
                //пока в курсоре есть строки и есть новые сервисы
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void addToServiceList(Service service, User user) {
        HashMap<String, Float> coefs = new HashMap<>();
        coefs.put("creation date", 0.25f);
        coefs.put("cost", 0.07f);
        coefs.put("rating", 0.68f);

        float points, creationDatePoints, costPoints, ratingPoints;

        boolean isPremium = service.getIsPremium();
        if (isPremium) {
            points = 1;
            serviceList.add(0, new Object[]{points, service, user});
        } else {
            creationDatePoints = figureCreationDatePoints(service.getCreationDate(), coefs.get("creation date"));
            costPoints = figureCostPoints(Long.valueOf(service.getCost()), coefs.get("cost"));
            ratingPoints = figureRatingPoints(service.getAverageRating(), coefs.get("rating"));
            points = creationDatePoints + costPoints + ratingPoints;
            sortAddition(new Object[]{points, service, user});
        }
    }

    private float figureCreationDatePoints(String creationDate, float coefficient) {
        float creationDatePoints;

        long dateBonus = (workWithTimeApi.getMillisecondsStringDate(creationDate) -
                workWithTimeApi.getSysdateLong()) / (3600000 * 24) + 7;
        if (dateBonus < 0) {
            creationDatePoints = 0;
        } else {
            creationDatePoints = dateBonus * coefficient / 7;
        }

        return creationDatePoints;
    }

    private float figureCostPoints(long cost, float coefficient) {
        return (1 - cost * 1f / maxCost) * coefficient;
    }

    private float figureRatingPoints(float rating, float coefficient) {
        return rating * coefficient / 5;
    }

    private void sortAddition(Object[] serviceData) {
        for (int i = 0; i < serviceList.size(); i++) {
            if ((float) (serviceList.get(i)[0]) < (float) (serviceData[0])) {
                serviceList.add(i, serviceData);
                return;
            }
        }

        serviceList.add(serviceList.size(), serviceData);
    }

    private void addToMainScreen() {
        for (Object[] serviceData : serviceList) {
            foundServiceElement fElement = new foundServiceElement((Service) serviceData[1], (User) serviceData[2]);

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.resultsMainScreenLayout, fElement);
            transaction.commit();
        }
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private long getMaxCost() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sqlQuery =
                "SELECT "
                        + " MAX(" + DBHelper.KEY_MIN_COST_SERVICES + ") AS " + MAX_COST
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES;

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{});

        long maxCost = 0;
        if (cursor.moveToFirst()) {
            maxCost = Long.valueOf(cursor.getString(cursor.getColumnIndex(MAX_COST)));
        }
        cursor.close();
        return maxCost;
    }

    private float figureAverageRating(String serviceId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sqlQuery =
                "SELECT "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + ", "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID + " AS " + ORDER_ID + ", "
                        + DBHelper.KEY_RATING_REVIEWS
                        + " FROM "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_WORKING_TIME + ", "
                        + DBHelper.TABLE_ORDERS + ", "
                        + DBHelper.TABLE_REVIEWS
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
                        + DBHelper.KEY_SERVICE_ID_WORKING_DAYS + " = ? "
                        + " AND "
                        + DBHelper.KEY_TYPE_REVIEWS + " = ? "
                        + " AND "
                        + DBHelper.KEY_RATING_REVIEWS + " != 0 "
                        + " AND "
                        + DBHelper.KEY_REVIEW_REVIEWS + " != '-'";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId, REVIEW_FOR_SERVICE});
        int countOfRates = 0;
        float avgRating = 0;
        float sumOfRates = 0;
        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        if (cursor.moveToFirst()) {
            int indexRating = cursor.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);
            int indexWorkingTimeId = cursor.getColumnIndex(DBHelper.KEY_ID);
            int indexOrderId = cursor.getColumnIndex(ORDER_ID);
            do {
                String workingTimeId = cursor.getString(indexWorkingTimeId);
                String orderId = cursor.getString(indexOrderId);

                if (workWithLocalStorageApi.isMutualReview(orderId)) {
                    sumOfRates += Float.valueOf(cursor.getString(indexRating));
                    countOfRates++;
                } else {
                    if (workWithLocalStorageApi.isAfterThreeDays(workingTimeId)) {
                        sumOfRates += Float.valueOf(cursor.getString(indexRating));
                        countOfRates++;
                    }
                }
            } while (cursor.moveToNext());

            if (countOfRates != 0) {
                avgRating = sumOfRates / countOfRates;
            }
            cursor.close();
        }

        return avgRating;
    }

    private void attentionBadConnection() {
        Toast.makeText(this, "Плохое соединение", Toast.LENGTH_SHORT).show();
    }
}