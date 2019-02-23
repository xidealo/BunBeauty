package com.example.ideal.myapplication.reviews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.chat.Messages;
import com.example.ideal.myapplication.fragments.objects.Comment;

public class CommentElement extends Fragment implements View.OnClickListener{

    private static final String TAG = "DBInf";

    private static final String USER_ID = "user id";
    private static final String USER_NAME = "user name";
    private static final String REVIEW = "review";
    private static final String RATING = "rating";

    private TextView reviewText;
    private TextView userNameText;
    private RatingBar ratingBar;

    private String userId;
    private String userName;
    private String review;
    private float rating;


    public CommentElement() {
    }

    @SuppressLint("ValidFragment")
    public CommentElement(Comment comment) {
        userId = comment.getUserId();
        userName = comment.getUserName();
        review = comment.getReview();
        rating = comment.getRating();
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

        userNameText.setOnClickListener(this);
        reviewText.setOnClickListener(this);

        setData();
    }

    private void setData() {
        String abbreviatedReview = review.substring(0, 2) + "...";

        userNameText.setText(userName);
        reviewText.setText(abbreviatedReview);
        ratingBar.setRating(rating);
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
