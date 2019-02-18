package com.example.ideal.myapplication.reviews;

import android.annotation.SuppressLint;
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

public class CommentElement extends Fragment {

    private static final String TAG = "DBInf";

    private TextView reviewText;
    private TextView userNameText;
    private RatingBar ratingBar;

    private String review;
    private float rating;
    private String userName;

    public CommentElement() {
    }

    @SuppressLint("ValidFragment")
    public CommentElement(String _userName, String _review, float _rating) {
        userName = _userName;
        review = _review;
        rating = _rating;
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

        setData();
    }

    private void setData() {
        userNameText.setText(userName);
        reviewText.setText(review);
        ratingBar.setRating(rating);
    }
}
