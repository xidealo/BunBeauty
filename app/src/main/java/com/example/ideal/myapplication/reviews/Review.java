package com.example.ideal.myapplication.reviews;

import android.content.ContentValues;
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

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.objects.RatingReview;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Review extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private static final String MESSAGE_ID = "message id";
    private static final String REVIEWS = "reviews";
    private static final String REVIEW = "review";
    private static final String RATING = "rating";
    private static final String TYPE = "type";

    private float myRating;

    private EditText reviewInput;

    private RatingBar ratingBar;
    private DBHelper dbHelper;
    private String messageId;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review);

        Button rateReviewBtn = findViewById(R.id.rateReviewBtn);
        reviewInput = findViewById(R.id.reviewReviewInput);
        ratingBar = findViewById(R.id.ratingBarReview);
        messageId = getIntent().getStringExtra(MESSAGE_ID);
        type = getIntent().getStringExtra(TYPE);
        myRating = 0;

        FragmentManager manager = getSupportFragmentManager();
        PanelBuilder panelBuilder = new PanelBuilder(this);
        panelBuilder.buildFooter(manager, R.id.footerReviewLayout);
        panelBuilder.buildHeader(manager, "Оценить", R.id.headerReviewLayout);

        dbHelper = new DBHelper(this);

        addListenerOnRatingBar();
        rateReviewBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Проверяем оценку
        if(myRating!=0){
            // Оценка поставлена
            setReview();
        }
        else {
            // Оценка не поставлена
            attentionRatingIsNull();
        }
    }

    private void setReview() {
        final String leftBehindReview = reviewInput.getText().toString();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        Query query = database.getReference(REVIEWS)
                .orderByChild(MESSAGE_ID)
                .equalTo(messageId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot reviews) {
                for(DataSnapshot review: reviews.getChildren()) {
                    // Берём оценку
                    // Загружаем оценку в Firebase в зависимости от того, принадлежит ли сервис человеку
                    String fReviewId = review.getKey();
                    String fMessageType = String.valueOf(review.child(TYPE).getValue());
                    if (fMessageType.equals(type)) {
                        //оставляется отзыв в ревью, в котором свободные поля
                        RatingReview ratingReview = new RatingReview();
                        ratingReview.setId(fReviewId);
                        ratingReview.setReview(leftBehindReview);
                        ratingReview.setRating(String.valueOf(myRating));
                        Map<String,Object> items = new HashMap<>();

                        items.put(RATING, myRating);
                        items.put(REVIEW, leftBehindReview);
                        DatabaseReference myRef = database.getReference(REVIEWS).child(fReviewId);
                        myRef.updateChildren(items);
                        updateReviewLocalStorage(ratingReview);
                    } else {
                        //ситуация невозможна, потмоу что сюда нельзя придти,
                        // тк в messages не должно быть кнопки "оценить"
                    }

                    // Возвращаемся в диалоги
                    goToMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateReviewLocalStorage(RatingReview ratingReview) {

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

    private void goToMessages() {
        super.onBackPressed();
    }

}