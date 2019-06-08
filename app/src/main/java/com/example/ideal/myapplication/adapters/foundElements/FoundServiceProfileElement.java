package com.example.ideal.myapplication.adapters.foundElements;

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
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.example.ideal.myapplication.searchService.GuestService;

@SuppressLint("ValidFragment")
public class FoundServiceProfileElement extends Fragment implements View.OnClickListener {

    final String SERVICE_ID = "service id";
    private static final String TAG = "DBInf";

    private TextView nameText;
    private RatingBar ratingBar;

    private String idString;
    private String nameString;
    private float avgRating;
    private WorkWithStringsApi workWithStringsApi;

    @SuppressLint("ValidFragment")
    public FoundServiceProfileElement(float _avgRating, Service service) {
        idString = service.getId();
        nameString = service.getName();
        avgRating = _avgRating;
        workWithStringsApi = new WorkWithStringsApi();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.found_service_profile_element, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        nameText = view.findViewById(R.id.serviceNameFoundServiceProfileElementText);
        ratingBar = view.findViewById(R.id.ratingBarFondServiceProfileElement);
        nameText.setOnClickListener(this);
        LinearLayout layout = view.findViewById(R.id.foundServiceProfileElementLayout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.setMargins(10,0,10,10);
        layout.setLayoutParams(params);

        layout.setOnClickListener(this);
        setData();

    }

    private void setData() {
        nameText.setText(workWithStringsApi.cutString(nameString,27));
        ratingBar.setRating(avgRating);
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
