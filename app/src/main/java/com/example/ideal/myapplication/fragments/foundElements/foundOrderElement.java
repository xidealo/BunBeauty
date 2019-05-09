package com.example.ideal.myapplication.fragments.foundElements;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.searchService.GuestService;

@SuppressLint("ValidFragment")
public class foundOrderElement extends Fragment implements View.OnClickListener {

    final String SERVICE_ID = "service id";

    private TextView nameText;
    private TextView timeText;

    private String idString;
    private String nameString;
    private String dateString;
    private String timeString;

    @SuppressLint("ValidFragment")
    public foundOrderElement(String id, String name, String date, String time) {
        idString = id;
        nameString = name;
        dateString = date;
        timeString = time;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.found_order_element, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        nameText = view.findViewById(R.id.nameFoundOrderElementText);
        timeText = view.findViewById(R.id.descriptionFoundOrderElementText);

        nameText.setOnClickListener(this);
        timeText.setOnClickListener(this);

        LinearLayout layout = view.findViewById(R.id.foundOrderProfileElementLayout);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.setMargins(10,10,10,15);
        layout.setLayoutParams(params);

        layout.setOnClickListener(this);
        setData();
    }

    private void setData() {
        nameText.setText(nameString);
        timeText.setText(dateString + " "+ timeString);
    }

    @Override
    public void onClick(View v) {
        goToGuestService();
    }

    private void goToGuestService(){
        Intent intent = new Intent(this.getContext(), GuestService.class);
        intent.putExtra(SERVICE_ID, idString);
        startActivity(intent);
    }
}