package com.bunbeauty.ideal.myapplication.reviews;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.entity.RatingReview;
import com.bunbeauty.ideal.myapplication.helpApi.PanelBuilder;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.bunbeauty.ideal.myapplication.other.DBHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Review extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private static final String TYPE = "type";
    private static final String REVIEW_ID = "review id";

    private static final String REVIEW = "review";
    private static final String RATING = "rating";
    private static final String REVIEW_FOR_USER = "review for user";
    private static final String SORT_TIME = "sort time";

    private static final String USERS = "users";
    private static final String SERVICES = "services";
    private static final String WORKING_DAYS = "working days";
    private static final String WORKING_TIME = "working time";
    private static final String ORDERS = "orders";
    private static final String REVIEWS = "reviews";
    private static final String AVG_RATING = "avg rating";
    private static final String COUNTS_OF_RATES = "count of rates";
    private static final String USER_ID = "user id";
    private static final String SERVICE_ID = "service id";

    private float myRating;
    private float avgRating = 0;
    private int count = 0;
    private String type;

    private EditText reviewInput;

    private RatingBar ratingBar;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review);

        Button rateReviewBtn = findViewById(R.id.rateReviewBtn);
        reviewInput = findViewById(R.id.reviewReviewInput);
        ratingBar = findViewById(R.id.ratingBarReview);
        type = getIntent().getStringExtra(TYPE);

        String userId = getIntent().getStringExtra(USER_ID);
        if (type.equals(REVIEW_FOR_USER)) {
            loadRatingForUser(userId);
        } else {
            String serviceId = getIntent().getStringExtra(SERVICE_ID);
            loadRatingForService(userId, serviceId);
        }

        myRating = 0;

        dbHelper = new DBHelper(this);

        addListenerOnRatingBar();
        rateReviewBtn.setOnClickListener(this);
    }

    private void loadRatingForUser(String userId) {
        DatabaseReference userRef = FirebaseDatabase
                .getInstance()
                .getReference(USERS)
                .child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                avgRating = dataSnapshot.child(AVG_RATING).getValue(float.class);
                count = dataSnapshot.child(COUNTS_OF_RATES).getValue(int.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void loadRatingForService(String userId, String serviceId) {
        DatabaseReference serviceRef = FirebaseDatabase
                .getInstance()
                .getReference(USERS)
                .child(userId)
                .child(SERVICES)
                .child(serviceId);

        serviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                avgRating = dataSnapshot.child(AVG_RATING).getValue(float.class);
                count = dataSnapshot.child(COUNTS_OF_RATES).getValue(int.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    @Override
    public void onClick(View v) {
        // Проверяем оценку
        if(myRating!=0){
            // Оценка поставлена
            figureRating();
            setReview();
            attentionThanksForReview();
        }
        else {
            // Оценка не поставлена
            attentionRatingIsNull();
        }
    }

    private void figureRating() {
        avgRating = (avgRating * count + myRating) / (count + 1);
        count++;
    }

    private void setReview() {

        String reviewId = getIntent().getStringExtra(REVIEW_ID);

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String query;
        if (type.equals(REVIEW_FOR_USER)) {
            query =
                    "SELECT "
                            + DBHelper.KEY_ORDER_ID_REVIEWS+ ", "
                            + DBHelper.KEY_USER_ID
                            + " FROM "
                            + DBHelper.TABLE_ORDERS + ", "
                            + DBHelper.TABLE_REVIEWS
                            + " WHERE "
                            + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID
                            + " = "
                            + DBHelper.KEY_ORDER_ID_REVIEWS
                            + " AND "
                            + DBHelper.TABLE_REVIEWS + "." + DBHelper.KEY_ID + " = ?";

        } else {
            query =
                    "SELECT "
                            + DBHelper.KEY_RATING_SERVICES + ", "
                            + DBHelper.KEY_COUNT_OF_RATES_SERVICES + ", "
                            + DBHelper.KEY_ORDER_ID_REVIEWS + ", "
                            + DBHelper.KEY_WORKING_TIME_ID_ORDERS + ", "
                            + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + ", "
                            + DBHelper.KEY_SERVICE_ID_WORKING_DAYS+ ", "
                            + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID
                            + " FROM "
                            + DBHelper.TABLE_REVIEWS + ", "
                            + DBHelper.TABLE_ORDERS + ", "
                            + DBHelper.TABLE_WORKING_TIME + ", "
                            + DBHelper.TABLE_WORKING_DAYS + ", "
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
                            + DBHelper.TABLE_REVIEWS + "." + DBHelper.KEY_ID + " = ?";
        }

        Cursor cursor = database.rawQuery(query, new String[]{reviewId});

        if (cursor.moveToFirst()) {
            int indexOrderId = cursor.getColumnIndex(DBHelper.KEY_ORDER_ID_REVIEWS);
            int indexUserId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);

            String orderId = cursor.getString(indexOrderId);
            String userId = cursor.getString(indexUserId);

            DatabaseReference myRef = FirebaseDatabase
                    .getInstance()
                    .getReference(USERS)
                    .child(userId);

            Map<String,Object> items = new HashMap<>();

            if (type.equals(REVIEW_FOR_USER)) {
                items.put(AVG_RATING, avgRating);
                items.put(COUNTS_OF_RATES, count);
                myRef.updateChildren(items);
                items.clear();

                updateUserRatingInLocalStorage(userId);

                myRef = myRef.child(ORDERS)
                        .child(orderId)
                        .child(REVIEWS)
                        .child(reviewId);
            } else {
                int indexTimeId = cursor.getColumnIndex(DBHelper.KEY_WORKING_TIME_ID_ORDERS);
                int indexDayId = cursor.getColumnIndex(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME);
                int indexServiceId = cursor.getColumnIndex(DBHelper.KEY_SERVICE_ID_WORKING_DAYS);

                String timeId = cursor.getString(indexTimeId);
                String dayId = cursor.getString(indexDayId);
                String serviceId = cursor.getString(indexServiceId);

                myRef = myRef.child(SERVICES)
                        .child(serviceId);

                items.put(AVG_RATING, avgRating);
                items.put(COUNTS_OF_RATES, count);
                myRef.updateChildren(items);
                items.clear();

                updateServiceRatingInLocalStorage(serviceId);

                myRef = myRef.child(WORKING_DAYS)
                        .child(dayId)
                        .child(WORKING_TIME)
                        .child(timeId)
                        .child(ORDERS)
                        .child(orderId)
                        .child(REVIEWS)
                        .child(reviewId);
            }

            String textOfReview = reviewInput.getText().toString();
            items.put(RATING, myRating);
            items.put(REVIEW, textOfReview);
            items.put(SORT_TIME, WorkWithTimeApi.getSysdateLong());
            myRef.updateChildren(items);
            items.clear();

            RatingReview ratingReview = new RatingReview();
            ratingReview.setId(reviewId);
            ratingReview.setReview(textOfReview);
            ratingReview.setRating(String.valueOf(myRating));

            updateReviewInLocalStorage(ratingReview);
            goToMessages();
        }
        cursor.close();
    }

    private void updateUserRatingInLocalStorage(String userId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_RATING_USERS, String.valueOf(avgRating));
        contentValues.put(DBHelper.KEY_COUNT_OF_RATES_USERS, String.valueOf(count));
        //update
        database.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{userId});
    }

    private void updateServiceRatingInLocalStorage(String serviceId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_RATING_SERVICES, String.valueOf(avgRating));
        contentValues.put(DBHelper.KEY_COUNT_OF_RATES_SERVICES, String.valueOf(count));
        //update
        database.update(DBHelper.TABLE_CONTACTS_SERVICES, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{serviceId});
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager manager = getSupportFragmentManager();

        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerReviewLayout);
        panelBuilder.buildHeader(manager, "Оценить", R.id.headerReviewLayout);

    }

    private void updateReviewInLocalStorage(RatingReview ratingReview) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_REVIEW_REVIEWS, ratingReview.getReview());
        contentValues.put(DBHelper.KEY_RATING_REVIEWS, ratingReview.getRating());
        //update
        database.update(DBHelper.TABLE_REVIEWS, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(ratingReview.getId())});
    }

    //Описываем работу слушателя изменения состояний RatingReview Bar:
    public void addListenerOnRatingBar() {
        //При смене значения рейтинга в нашем элементе RatingReview Bar,
        //это изменение будет сохраняться в myRating
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                myRating = rating;
            }
        });
    }

    private void attentionRatingIsNull() {
        Toast.makeText(this,"Пожалуйста, укажите оценку",Toast.LENGTH_SHORT).show();
    }
    private void attentionThanksForReview() {
        Toast.makeText(this,"Спасибо за улучшение качества сервиса!",Toast.LENGTH_SHORT).show();
    }
    private void goToMessages() {
        super.onBackPressed();
    }

}