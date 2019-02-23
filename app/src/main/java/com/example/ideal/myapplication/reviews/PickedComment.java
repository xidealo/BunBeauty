package com.example.ideal.myapplication.reviews;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.other.Profile;

public class PickedComment extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DBInf";

    private static final String USER_ID = "user id";
    private static final String USER_NAME = "user name";
    private static final String REVIEW = "review";
    private static final String RATING = "rating";

    private static final String OWNER_ID = "owner id";


    TextView userNameText;
    TextView reviewText;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picked_comment);

        userNameText = findViewById(R.id.namePickedCommentText);
        reviewText = findViewById(R.id.reviewPickedCommentText);
        ratingBar = findViewById(R.id.ratingPickedCommentBar);

        userNameText.setOnClickListener(this);

        setInfo();
    }

    private void setInfo() {

        String userName = getIntent().getStringExtra(USER_NAME);
        String review = getIntent().getStringExtra(REVIEW);
        float rating = getIntent().getFloatExtra(RATING, 0);

        userNameText.setText(userName);
        reviewText.setText(review);
        ratingBar.setRating(rating);
    }


    @Override
    public void onClick(View v) {
        goToProfile();
    }

    private void goToProfile() {
        String ownerId = getIntent().getStringExtra(USER_ID);

        Intent intent = new Intent(this, Profile.class);
        intent.putExtra(OWNER_ID, ownerId);

        startActivity(intent);
    }
}
