package com.example.ideal.myapplication.reviews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ideal.myapplication.R;

public class RatingBarForServiceElement extends Fragment implements View.OnClickListener {
    private static final String ID = "id";
    private static final String TYPE = "type";

    private TextView countOfRatesText;
    private TextView avgRatesText;
    private RatingBar ratingBar;

    private long countOfRates;
    private float avgRates;

    private String id;
    private String type;

    public RatingBarForServiceElement() {
    }

    @SuppressLint("ValidFragment")
    public RatingBarForServiceElement(float _avgRating, long _countOfRates, String _id, String _type) {
        countOfRates = _countOfRates;
        avgRates = _avgRating;
        id = _id;
        type = _type;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rating_bar_for_service_element, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        countOfRatesText = view.findViewById(R.id.countOfRatesRatingBarElementText);
        avgRatesText = view.findViewById(R.id.avgRatingRatingBarElementText);
        ratingBar = view.findViewById(R.id.ratingBarRatingBarForService);

        avgRatesText.setOnClickListener(this);
        ratingBar.setOnClickListener(this);
        countOfRatesText.setOnClickListener(this);

        setData();
    }

    @Override
    public void onClick(View v) {
        goToComments();
    }

    private void goToComments() {
        Intent intent = new Intent(getContext(), Comments.class);
        intent.putExtra(ID, id);
        intent.putExtra(TYPE, type);
        startActivity(intent);
    }

    private void setData() {
        if (countOfRates > 0) {
            countOfRatesText.setText(String.valueOf(countOfRates));
            avgRatesText.setText(String.valueOf(avgRates));
            ratingBar.setRating(avgRates);
        }
    }
}
