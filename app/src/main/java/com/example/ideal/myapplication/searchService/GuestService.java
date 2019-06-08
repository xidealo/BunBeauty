package com.example.ideal.myapplication.searchService;

import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.createService.MyCalendar;
import com.example.ideal.myapplication.fragments.PremiumElement;
import com.example.ideal.myapplication.fragments.objects.RatingReview;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.other.IPremium;
import com.example.ideal.myapplication.reviews.Comments;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class GuestService extends AppCompatActivity implements View.OnClickListener, IPremium {

    private static final String TAG = "DBInf";

    private static final String WORKER = "worker";
    private static final String USER = "user";
    private static final String SERVICE_ID = "service id";
    private static final String ORDER_ID = "order_id";
    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String SERVICES = "services";
    private static final String IS_PREMIUM = "is premium";
    private static final String USERS = "users";
    private static final String CODES = "codes";
    private static final String CODE = "code";
    private static final String COUNT = "count";

    private static final String STATUS_USER_BY_SERVICE = "status UserCreateService";

    private static final String REVIEW_FOR_SERVICE = "review for service";

    private String status;

    private String userId;
    private String serviceId;
    private String ownerId;
    private String serviceName;

    private TextView costText;
    private TextView addressText;
    private TextView withoutRatingText;
    private TextView descriptionText;
    private TextView countOfRatesText;
    private TextView avgRatesText;
    private TextView premiumText;
    private TextView noPremiumText;

    private RatingBar ratingBar;
    private LinearLayout ratingLL;
    private LinearLayout imageFeedLayout;
    private LinearLayout premiumLayout;
    private LinearLayout premiumIconLayout;
    private WorkWithLocalStorageApi workWithLocalStorageApi;
    private boolean isMyService;
    private boolean isPremiumLayoutSelected;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_service);

        init();
    }

    private void init() {
        FragmentManager manager = getSupportFragmentManager();

        costText = findViewById(R.id.costGuestServiceText);
        addressText = findViewById(R.id.addressGuestServiceText);
        descriptionText = findViewById(R.id.descriptionGuestServiceText);
        withoutRatingText = findViewById(R.id.withoutRatingText);
        ratingBar = findViewById(R.id.ratingBarGuestServiceRatingBar);
        countOfRatesText = findViewById(R.id.countOfRatesGuestServiceElementText);
        avgRatesText = findViewById(R.id.avgRatingGuestServiceElementText);

        premiumText = findViewById(R.id.yesPremiumGuestServiceText);
        noPremiumText = findViewById(R.id.noPremiumGuestServiceText);

        Button editScheduleBtn = findViewById(R.id.editScheduleGuestServiceBtn);
        ratingLL = findViewById(R.id.ratingGuestServiceLayout);
        premiumLayout = findViewById(R.id.premiumGuestServiceLayout);
        imageFeedLayout = findViewById(R.id.feedGuestServiceLayout);
        premiumIconLayout = findViewById(R.id.premiumIconGuestServiceLayout);

        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        serviceId = getIntent().getStringExtra(SERVICE_ID);
        //получаем данные о сервисе
        getInfoAboutService(serviceId);

        //получаем рейтинг сервиса
        getServiceRating(serviceId);
        userId = getUserId();

        // мой сервис или нет?
        isMyService = userId.equals(ownerId);

        //убрана панель премиума
        isPremiumLayoutSelected = false;
        if (isMyService) {
            status = WORKER;
            editScheduleBtn.setText("Редактировать расписание");
            premiumText.setOnClickListener(this);
            noPremiumText.setOnClickListener(this);
            premiumText.setOnClickListener(this);

            PremiumElement premiumElement = new PremiumElement();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.premiumGuestServiceLayout, premiumElement);
            transaction.commit();
        } else {
            status = USER;
            editScheduleBtn.setText("Расписание");
            premiumIconLayout.setVisibility(View.GONE);
        }
        editScheduleBtn.setOnClickListener(this);
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
            case R.id.noPremiumGuestServiceText:
                showPremium();
                break;

            case R.id.yesPremiumGuestServiceText:
                showPremium();
                break;

            case R.id.ratingGuestServiceLayout:
                goToComments();
            default:
                break;
        }
    }

    private void getInfoAboutService(String serviceId) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // все осервисе, оценка, количество оценок
        // проверка на удаленный номер

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
            int indexAddress = cursor.getColumnIndex(DBHelper.KEY_ADDRESS_SERVICES);
            int indexIsPremium = cursor.getColumnIndex(DBHelper.KEY_IS_PREMIUM_SERVICES);

            ownerId = cursor.getString(indexUserId);

            costText.setText("Цена от: " + cursor.getString(indexMinCost) + "р");
            addressText.setText("Адрес: " + cursor.getString(indexAddress));
            descriptionText.setText(cursor.getString(indexDescription));
            serviceName = cursor.getString(indexName);
            boolean isPremium = Boolean.valueOf(cursor.getString(indexIsPremium));

            if (isPremium) {
                setWithPremium();
            }
        }
        cursor.close();
    }

    private void getServiceRating(String serviceId) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // все о сервисе, оценка, количество оценок
        // проверка на удаленный номер
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_RATING_REVIEWS + ", "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + ", "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID + " AS " + ORDER_ID
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
                        + DBHelper.KEY_RATING_REVIEWS + " != 0 ";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId, REVIEW_FOR_SERVICE});

        float sumOfRates = 0;
        float avgRating = 0;
        long countOfRates = 0;
        // если сюда не заходит, значит ревью нет
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
            createRatingBar(avgRating, countOfRates);
        } else {
            setWithoutRating();
        }
        cursor.close();
    }

    private void checkScheduleAndGoToProfile() {
        if (WorkWithLocalStorageApi.hasAvailableTime(serviceId, userId, dbHelper.getReadableDatabase())) {
            goToMyCalendar(USER);
        } else {
            attentionThisScheduleIsEmpty();
        }
    }

    private void setPhotoFeed(String serviceId) {

        int width = getResources().getDimensionPixelSize(R.dimen.photo_width);
        int height = getResources().getDimensionPixelSize(R.dimen.photo_height);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        //получаем ссылку на фото по id владельца
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_PHOTO_LINK_PHOTOS
                        + " FROM "
                        + DBHelper.TABLE_PHOTOS
                        + " WHERE "
                        + DBHelper.KEY_OWNER_ID_PHOTOS + " = ?";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId});

        if (cursor.moveToFirst()) {
            do {
                int indexPhotoLink = cursor.getColumnIndex(DBHelper.KEY_PHOTO_LINK_PHOTOS);

                String photoLink = cursor.getString(indexPhotoLink);


                ImageView serviceImage = new ImageView(getApplicationContext());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        width,
                        height);
                params.setMargins(15, 15, 15, 15);
                serviceImage.setLayoutParams(params);
                serviceImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageFeedLayout.addView(serviceImage);

                //установка аватарки
                Picasso.get()
                        .load(photoLink)
                        .resize(width, height)
                        .centerCrop()
                        .into(serviceImage);
            } while (cursor.moveToNext());
        }
        cursor.close();

    }

    @Override
    protected void onResume() {
        super.onResume();
        PanelBuilder panelBuilder = new PanelBuilder();
        FragmentManager manager = getSupportFragmentManager();

        panelBuilder.buildHeader(manager, serviceName, R.id.headerGuestServiceLayout, isMyService, serviceId, ownerId);
        panelBuilder.buildFooter(manager, R.id.footerGuestServiceLayout);
        getInfoAboutService(serviceId);
        imageFeedLayout.removeAllViews();
        setPhotoFeed(serviceId);
    }

    private void attentionThisScheduleIsEmpty() {
        Toast.makeText(
                this,
                "Пользователь еще не написал расписание к этому сервису.",
                Toast.LENGTH_SHORT).show();
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void createRatingBar(float avgRating, long countOfRates) {

        if (countOfRates > 0) {
            countOfRatesText.setVisibility(View.VISIBLE);
            avgRatesText.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);

            countOfRatesText.setText("(" + countOfRates + " оценок)");

            //приводим цифры к виду, чтобы было 2 число после запятой
            String avgRatingWithFormat = new DecimalFormat("#0.00").format(avgRating);
            avgRatesText.setText(avgRatingWithFormat);

            ratingBar.setRating(avgRating);
        }
        ratingLL.setOnClickListener(this);
    }

    private void showPremium() {
        if (isPremiumLayoutSelected) {
            premiumLayout.setVisibility(View.GONE);
            isPremiumLayoutSelected = false;
        } else {
            premiumLayout.setVisibility(View.VISIBLE);
            isPremiumLayoutSelected = true;
        }
    }

    private void setWithoutRating() {
        withoutRatingText.setVisibility(View.VISIBLE);
    }

    private void setWithPremium() {
        noPremiumText.setVisibility(View.GONE);
        premiumText.setVisibility(View.VISIBLE);
    }

    private void goToMyCalendar(String status) {
        Intent intent = new Intent(this, MyCalendar.class);
        intent.putExtra(SERVICE_ID, serviceId);
        intent.putExtra(STATUS_USER_BY_SERVICE, status);

        startActivity(intent);
    }

    private void goToComments() {
        Intent intent = new Intent(this, Comments.class);
        intent.putExtra(ID, serviceId);
        intent.putExtra(TYPE, REVIEW_FOR_SERVICE);
        startActivity(intent);
    }

    @Override
    public void setPremium() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS)
                .child(getUserId())
                .child(SERVICES)
                .child(serviceId);

        Map<String, Object> items = new HashMap<>();
        items.put(IS_PREMIUM, true);

        myRef.updateChildren(items);
        setWithPremium();
        premiumLayout.setVisibility(View.GONE);
        attentionPremiumActivated();
        updatePremiumLocalStorage(serviceId);
    }

    private void updatePremiumLocalStorage(String serviceId) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_IS_PREMIUM_SERVICES, "true");
        //update
        database.update(DBHelper.TABLE_CONTACTS_SERVICES, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{serviceId});
    }

    @Override
    public void checkCode(final String code) {
        //проверка кода
        Query query = FirebaseDatabase.getInstance().getReference(CODES).
                orderByChild(CODE).
                equalTo(code);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot codesSnapshot) {
                if(codesSnapshot.getChildrenCount() == 0){
                    attentionWrongCode();
                }
                else {
                    DataSnapshot userSnapshot = codesSnapshot.getChildren().iterator().next();
                    int  count = userSnapshot.child(COUNT).getValue(int.class);
                    if(count>0){
                        setPremium();

                        String codeId = userSnapshot.getKey();

                        DatabaseReference myRef = FirebaseDatabase.getInstance()
                                .getReference(CODES)
                                .child(codeId);
                        Map<String, Object> items = new HashMap<>();
                        items.put(COUNT, count-1);
                        myRef.updateChildren(items);
                    }
                    else {
                     attentionOldCode();   
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void attentionWrongCode() {
        Toast.makeText(this, "Неверно введен код", Toast.LENGTH_SHORT).show();
    }
    private void attentionOldCode() {
        Toast.makeText(this, "Код больше не действителен", Toast.LENGTH_SHORT).show();
    }
    private void attentionPremiumActivated() {
        Toast.makeText(this, "Премиум активирован", Toast.LENGTH_LONG).show();
    }

}