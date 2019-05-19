package com.example.ideal.myapplication.reviews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.objects.Comment;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.other.DBHelper;

public class CommentElement extends Fragment implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private static final String USER_ID = "user id";
    private static final String USER_NAME = "user name";
    private static final String REVIEW = "review";
    private static final String RATING = "rating";
    private static final String REVIEW_FOR_USER = "review for user";

    private TextView reviewText;
    private TextView userNameText;
    private TextView serviceNameText;
    private ImageView avatarImage;
    private RatingBar ratingBar;

    private String userId;
    private String userName;
    private String review;
    private String serviceName;
    private float rating;

    public CommentElement() {
    }

    @SuppressLint("ValidFragment")
    public CommentElement(Comment comment) {
        userId = comment.getUserId();
        userName = comment.getUserName();
        review = comment.getReview();
        rating = comment.getRating();
        serviceName = comment.getServiceName();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comment_element, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        userNameText = view.findViewById(R.id.nameCommentElementText);
        reviewText = view.findViewById(R.id.reviewCommentElementText);
        ratingBar = view.findViewById(R.id.ratingCommentElementBar);
        avatarImage = view.findViewById(R.id.avatarCommentElementImage);
        serviceNameText = view.findViewById(R.id.serviceNameCommentElementText);

        LinearLayout layout = view.findViewById(R.id.commentElementLayout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.setMargins(10,10,10,15);
        layout.setLayoutParams(params);
        layout.setOnClickListener(this);

        setData();
    }

    private void setData() {
        String abbreviatedReview;
        if (review.length() > 26) {
            abbreviatedReview = review.substring(0, 25) + "...";
        } else {
            abbreviatedReview = review;
        }

        userNameText.setText(userName);
        reviewText.setText(abbreviatedReview);
        ratingBar.setRating(rating);

        //если там не лежит константа, а лежит имя сервиса
        if(!serviceName.equals(REVIEW_FOR_USER)){
            serviceNameText.setText(serviceName);
            serviceNameText.setVisibility(View.VISIBLE);
        }

        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        int width = getResources().getDimensionPixelSize(R.dimen.photo_avatar_width);
        int height = getResources().getDimensionPixelSize(R.dimen.photo_avatar_height);
        workWithLocalStorageApi.setPhotoAvatar(userId, avatarImage, width, height);
    }

    @Override
    public void onClick(View v) {
        goToThisComment();
    }

    private void goToThisComment() {
        Intent intent = new Intent(this.getContext(), PickedComment.class);
        intent.putExtra(USER_ID, userId);
        intent.putExtra(USER_NAME, userName);
        intent.putExtra(REVIEW, review);
        intent.putExtra(RATING, rating);
        startActivity(intent);
    }
}
