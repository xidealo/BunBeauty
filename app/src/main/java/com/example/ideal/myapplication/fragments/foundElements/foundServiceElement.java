package com.example.ideal.myapplication.fragments.foundElements;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.searchService.GuestService;

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
    private WorkWithStringsApi workWithStringsApi;
    private String userId;

    private boolean isPremium;

    @SuppressLint("ValidFragment")
    public foundServiceElement(Service service, User user) {
        serviceId = service.getId();
        nameUserString = user.getName();
        cityString = user.getCity();
        nameServiceString = service.getName();
        costString = service.getCost();
        userId = user.getId();
        isPremium = service.getIsPremium();
        workWithStringsApi = new WorkWithStringsApi();

        avgRating = service.getAverageRating();
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
        if (isPremium) {
            nameServiceText.setBackgroundColor(Color.YELLOW);
        }
        costText = view.findViewById(R.id.costFoundServiceElementText);
        ratingBar = view.findViewById(R.id.ratingBarFondServiceElement);
        avatarImage = view.findViewById(R.id.avatarFoundServiceElementImage);
        LinearLayout layout = view.findViewById(R.id.foundServiceElementLayout);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.setMargins(10,10,10,15);
        layout.setLayoutParams(params);

        layout.setOnClickListener(this);
        setData();
    }
    private static final String TAG = "DBInf";

    private void setData() {
        nameUserText.setText(workWithStringsApi.cutString(nameUserString.toUpperCase(),18));
        city.setText(cityString.substring(0, 1).toUpperCase() + cityString.substring(1).toLowerCase());
        nameServiceText.setText(workWithStringsApi.cutString(nameServiceString,9));
        costText.setText("Цена \n" + costString);
        ratingBar.setRating(avgRating);
        Log.d(TAG, "setData: " + avgRating);

        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        int width = getResources().getDimensionPixelSize(R.dimen.photo_avatar_width);
        int height = getResources().getDimensionPixelSize(R.dimen.photo_avatar_height);

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);
        workWithLocalStorageApi.setPhotoAvatar(userId,avatarImage,width,height);
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