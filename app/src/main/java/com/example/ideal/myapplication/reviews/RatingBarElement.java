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

public class RatingBarElement extends Fragment implements View.OnClickListener {



    public RatingBarElement() {
    }

    @SuppressLint("ValidFragment")
    public RatingBarElement(float _avgRating, long _countOfRates, String _serviceId, String _type) {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rating_bar_element, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
    }

}
