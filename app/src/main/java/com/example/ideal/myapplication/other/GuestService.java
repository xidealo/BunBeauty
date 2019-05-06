package com.example.ideal.myapplication.other;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.createService.MyCalendar;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.reviews.Comments;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class GuestService extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private static final String WORKER = "worker";
    private static final String USER = "user";
    private static final String SERVICE_ID = "service id";
    private static final String ORDER_ID = "order_id";
    private static final String ID = "id";
    private static final String TYPE = "type";

    private static final String STATUS_USER_BY_SERVICE = "status User";

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
    private LinearLayout imageFeedLayout;
    private RatingBar ratingBar;
    private LinearLayout ratingLL;
    private WorkWithLocalStorageApi workWithLocalStorageApi;

    private DBHelper dbHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_service);

        init();
    }

    private void init(){
        costText = findViewById(R.id.costGuestServiceText);
        addressText = findViewById(R.id.addressGuestServiceText);
        descriptionText = findViewById(R.id.descriptionGuestServiceText);
        withoutRatingText = findViewById(R.id.withoutRatingText);
        ratingBar = findViewById(R.id.ratingBarGuestServiceRatingBar);
        countOfRatesText = findViewById(R.id.countOfRatesGuestServiceElementText);
        avgRatesText = findViewById(R.id.avgRatingGuestServiceElementText);

        Button editScheduleBtn = findViewById(R.id.editScheduleGuestServiceBtn);
        Button premiumBtn = findViewById(R.id.premiumGuestServiceBtn);
        ratingLL = findViewById(R.id.ratingGuestServiceLayout);

        FragmentManager manager = getSupportFragmentManager();

        imageFeedLayout = findViewById(R.id.feedGuestServiceLayout);

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
        boolean isMyService = userId.equals(ownerId);

        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildHeader(manager, serviceName, R.id.headerGuestServiceLayout, isMyService, serviceId, ownerId);
        panelBuilder.buildFooter(manager, R.id.footerGuestServiceLayout);

        if (isMyService) {
            status = WORKER;
            editScheduleBtn.setText("Редактировать расписание");
            //premiumBtn.setVisibility(View.VISIBLE);
            //premiumBtn.setOnClickListener(this);
        } else {
            status = USER;
            editScheduleBtn.setText("Расписание");
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
            case R.id.premiumGuestServiceBtn:
                goToPremium();
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

            ownerId = cursor.getString(indexUserId);

            costText.setText("Цена от: " + cursor.getString(indexMinCost) + "р");
            descriptionText.setText(cursor.getString(indexDescription));
            addressText.setText(cursor.getString(indexAddress));
            serviceName = cursor.getString(indexName);
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
            int indexWorkingTimeId= cursor.getColumnIndex(DBHelper.KEY_ID);
            int indexOrderId= cursor.getColumnIndex(ORDER_ID);
            do {
                String workingTimeId = cursor.getString(indexWorkingTimeId);
                String orderId = cursor.getString(indexOrderId);

                if(workWithLocalStorageApi.isMutualReview(orderId)) {
                    sumOfRates += Float.valueOf(cursor.getString(indexRating));
                    countOfRates++;
                }
                else {
                    if(workWithLocalStorageApi.isAfterThreeDays(workingTimeId)){
                        sumOfRates += Float.valueOf(cursor.getString(indexRating));
                        countOfRates++;
                    }
                }
            } while (cursor.moveToNext());

            if(countOfRates!=0){
                avgRating = sumOfRates / countOfRates;
            }
            createRatingBar(avgRating,countOfRates);
        } else {
            setWithoutRating();
        }
        cursor.close();
    }

    private void checkScheduleAndGoToProfile(){

        // Получаем всё время данного сервиса, которое доступно данному юзеру
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String busyTimeQuery = "SELECT "
                + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                + " FROM "
                + DBHelper.TABLE_WORKING_DAYS + ", "
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_ORDERS
                + " WHERE "
                + DBHelper.KEY_SERVICE_ID_WORKING_DAYS + " = ?"
                + " AND "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_WORKING_TIME_ID_ORDERS + " = "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_IS_CANCELED_ORDERS + " = 'false'";

        String myTimeQuery = "SELECT "
                + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                + " FROM "
                + DBHelper.TABLE_WORKING_DAYS + ", "
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_ORDERS
                + " WHERE "
                + DBHelper.KEY_SERVICE_ID_WORKING_DAYS + " = ?"
                + " AND "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_WORKING_TIME_ID_ORDERS + " = "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_USER_ID + " = ?"
                + " AND "
                + DBHelper.KEY_IS_CANCELED_ORDERS + " = 'false'";

        String sqlQuery = "SELECT "
                + DBHelper.KEY_TIME_WORKING_TIME
                + " FROM "
                + DBHelper.TABLE_WORKING_DAYS + ", "
                + DBHelper.TABLE_WORKING_TIME
                + " WHERE "
                + DBHelper.KEY_SERVICE_ID_WORKING_DAYS + " = ?"
                + " AND "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                + " AND ((("
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " NOT IN (" + busyTimeQuery + ")"
                + " AND ("
                // 3 часа - разница с Гринвичем
                // 2 часа - минимум времени до сеанса, чтобы за писаться
                + "(STRFTIME('%s', 'now')+(3+2)*60*60) - STRFTIME('%s',"
                + DBHelper.KEY_DATE_WORKING_DAYS
                + "||' '||" + DBHelper.KEY_TIME_WORKING_TIME
                + ") <= 0)"
                + ") OR (("
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + " IN (" + myTimeQuery + ")"
                + ") AND ("
                + "(STRFTIME('%s', 'now')+3*60*60) - (STRFTIME('%s',"
                + DBHelper.KEY_DATE_WORKING_DAYS
                + "||' '||" + DBHelper.KEY_TIME_WORKING_TIME
                + ")) <= 0))))";

        Cursor cursor = database.rawQuery(sqlQuery, new String[] {serviceId, serviceId, serviceId, userId});

        if (cursor.moveToFirst()) {
            goToMyCalendar(USER);
        } else {
            attentionThisScheduleIsEmpty();
        }
        cursor.close();
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
        Cursor cursor = database.rawQuery(sqlQuery,new String[] {serviceId});

        if(cursor.moveToFirst()){
            do {
                int indexPhotoLink = cursor.getColumnIndex(DBHelper.KEY_PHOTO_LINK_PHOTOS);

                String photoLink = cursor.getString(indexPhotoLink);


                ImageView serviceImage = new ImageView(getApplicationContext());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        width,
                        height);
                params.setMargins(15,15,15,15);
                serviceImage.setLayoutParams(params);
                serviceImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageFeedLayout.addView(serviceImage);

                //установка аватарки
                Picasso.get()
                        .load(photoLink)
                        .resize(width,height)
                        .centerCrop()
                        .into(serviceImage);
            }while (cursor.moveToNext());
        }
        cursor.close();

    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private  String getUserId(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void createRatingBar(float avgRating, long countOfRates){

        if (countOfRates > 0) {
            countOfRatesText.setVisibility(View.VISIBLE);
            avgRatesText.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);

            countOfRatesText.setText("("+ countOfRates + " оценок)");
            avgRatesText.setText(String.valueOf(avgRating));
            ratingBar.setRating(avgRating);
        }
        ratingLL.setOnClickListener(this);
    }

    private void setWithoutRating(){
        withoutRatingText.setVisibility(View.VISIBLE);
    }
    private void goToMyCalendar(String status) {
        Intent intent = new Intent(this, MyCalendar.class);
        intent.putExtra(SERVICE_ID, serviceId);
        intent.putExtra(STATUS_USER_BY_SERVICE, status);

        startActivity(intent);
    }

    private void goToPremium() {
        Intent intent = new Intent(this,Premium.class);
        intent.putExtra(SERVICE_ID, serviceId);
        startActivity(intent);
    }

    private void goToComments() {
        Intent intent = new Intent(this, Comments.class);
        intent.putExtra(ID, serviceId);
        intent.putExtra(TYPE, REVIEW_FOR_SERVICE);
        startActivity(intent);
    }

}