package com.example.ideal.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.objects.Comment;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.reviews.PickedComment;

public class CommentElement implements View.OnClickListener {

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
    private Context context;
    private View view;
    private float rating;

    public CommentElement(Comment comment, View view, Context context) {
        userId = comment.getUserId();
        userName = comment.getUserName();
        review = comment.getReview();
        rating = comment.getRating();
        serviceName = comment.getServiceName();
        this.context = context;
        this.view= view;
    }

    public void createElement(){
        onViewCreated(view);
    }

    private void onViewCreated(@NonNull View view) {

        userNameText = view.findViewById(R.id.nameCommentElementText);
        reviewText = view.findViewById(R.id.reviewCommentElementText);
        ratingBar = view.findViewById(R.id.ratingCommentElementBar);
        avatarImage = view.findViewById(R.id.avatarCommentElementImage);
        serviceNameText = view.findViewById(R.id.serviceNameCommentElementText);

        LinearLayout layout = view.findViewById(R.id.commentElementLayout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        layout.setLayoutParams(params);

        layout.setOnClickListener(this);
        setData();
    }

    private void setData() {
        String abbreviatedReview;
        abbreviatedReview = WorkWithStringsApi.cutString(review,25);

        userNameText.setText(WorkWithStringsApi.cutString(WorkWithStringsApi.doubleCapitalSymbols(userName),18));
        reviewText.setText(abbreviatedReview);
        ratingBar.setRating(rating);

        //если там не лежит константа, а лежит имя сервиса
        if(!serviceName.equals(REVIEW_FOR_USER)){
            serviceNameText.setText(WorkWithStringsApi.cutString(serviceName,8));
            serviceNameText.setVisibility(View.VISIBLE);
        }

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        int width = context.getResources().getDimensionPixelSize(R.dimen.photo_avatar_width);
        int height = context.getResources().getDimensionPixelSize(R.dimen.photo_avatar_height);
        workWithLocalStorageApi.setPhotoAvatar(userId, avatarImage, width, height);
    }

    @Override
    public void onClick(View v) {
        goToThisComment();
    }

    private void goToThisComment() {
        Intent intent = new Intent(context, PickedComment.class);
        intent.putExtra(USER_ID, userId);
        intent.putExtra(USER_NAME, userName);
        intent.putExtra(REVIEW, review);
        intent.putExtra(RATING, rating);
        context.startActivity(intent);
    }
}