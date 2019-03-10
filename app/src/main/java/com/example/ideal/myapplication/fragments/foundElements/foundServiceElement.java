package com.example.ideal.myapplication.fragments.foundElements;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Rating;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.other.GuestService;
import com.example.ideal.myapplication.other.MainScreen;

@SuppressLint("ValidFragment")
public class foundServiceElement extends Fragment implements View.OnClickListener {

    final String SERVICE_ID = "service id";

    private TextView nameUserText;
    private TextView city;
    private TextView nameServiceText;
    private TextView costText;
    private ImageView avatarImage;
    private RatingBar ratingBar;

    private float avgRating;

    private String serviceId;
    private String nameUserString;
    private String cityString;
    private String nameServiceString;
    private String costString;
    private String userId;

    @SuppressLint("ValidFragment")
    public foundServiceElement(Float _avgRating, Service service, User user) {
        serviceId = service.getId();
        nameUserString = user.getName();
        cityString = user.getCity();
        nameServiceString = service.getName();
        costString = service.getCost();
        userId = user.getPhone();

        avgRating = _avgRating;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.found_service_element, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        nameUserText = view.findViewById(R.id.userNameFoundServiceElementText);
        city = view.findViewById(R.id.cityFoundServiceElementText);
        nameServiceText = view.findViewById(R.id.serviceNameFoundServiceElementText);
        costText = view.findViewById(R.id.costFoundServiceElementText);
        ratingBar = view.findViewById(R.id.ratingBarFondServiceElement);
        avatarImage = view.findViewById(R.id.avatarFoundServiceElementImage);

        nameUserText.setOnClickListener(this);
        city.setOnClickListener(this);
        nameServiceText.setOnClickListener(this);
        costText.setOnClickListener(this);
        setData();
    }

    private void setData() {
        nameUserText.setText(nameUserString);
        city.setText(cityString);
        nameServiceText.setText(nameServiceString);
        costText.setText(costString);
        ratingBar.setRating(avgRating);

        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        workWithLocalStorageApi.setPhotoAvatar(userId,avatarImage);
    }

    @Override
    public void onClick(View v) {
        goToGuestService();
    }

    private void goToGuestService(){
        Intent intent = new Intent(this.getContext(), GuestService.class);
        intent.putExtra(SERVICE_ID, serviceId);
        startActivity(intent);
    }
}