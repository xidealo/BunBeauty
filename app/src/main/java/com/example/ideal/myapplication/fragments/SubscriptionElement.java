package com.example.ideal.myapplication.fragments;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.helpApi.SubscriptionsApi;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.other.Profile;

@SuppressLint("ValidFragment")
public class SubscriptionElement extends Fragment implements View.OnClickListener {

    final String OWNER_ID = "owner id";

    private TextView nameText;
    private TextView subscribeText;
    private TextView unsubscribeText;
    private ImageView avatarImage;
    private SubscriptionsApi subsApi;

    private String workerId;
    private String workerName;

    @SuppressLint("ValidFragment")
    public SubscriptionElement(String _workerId, String _workerName, String status) {
        workerId = _workerId;
        workerName = _workerName;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subscription_element, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        nameText = view.findViewById(R.id.workerNameSubscriptionElementText);
        subscribeText = view.findViewById(R.id.subscribeSubscriptionElementText);
        avatarImage = view.findViewById(R.id.avatarSubscriptionElementImage);
        unsubscribeText = view.findViewById(R.id.unsubscribeSubscriptionElementText);

        nameText.setOnClickListener(this);
        avatarImage.setOnClickListener(this);
        subscribeText.setOnClickListener(this);
        unsubscribeText.setOnClickListener(this);

        LinearLayout layout = view.findViewById(R.id.subscriptionElementLayout);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.setMargins(10, 10, 10, 15);
        layout.setLayoutParams(params);

        setData();
    }

    private void setData() {
        nameText.setText(workerName);
        subscribeText.setTag(true);

        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        int width = getResources().getDimensionPixelSize(R.dimen.photo_avatar_width);
        int height = getResources().getDimensionPixelSize(R.dimen.photo_avatar_height);
        workWithLocalStorageApi.setPhotoAvatar(workerId, avatarImage, width, height);
    }

    @Override
    public void onClick(View v) {
        subsApi = new SubscriptionsApi(workerId, getContext());

        switch (v.getId()) {
            case R.id.subscribeSubscriptionElementText:
                unsubscribeText.setVisibility(View.VISIBLE);
                subscribeText.setVisibility(View.GONE);
                subsApi.unsubscribe();
                Toast.makeText(getContext(), "Подписка отменена", Toast.LENGTH_SHORT).show();
                break;

            case R.id.unsubscribeSubscriptionElementText:
                unsubscribeText.setVisibility(View.GONE);
                subscribeText.setVisibility(View.VISIBLE);
                subsApi.subscribe();
                Toast.makeText(getContext(), "Вы подписались", Toast.LENGTH_SHORT).show();
                break;

            default:
                goToProfile();
                break;
        }
    }

    private void goToProfile() {
        Intent intent = new Intent(getContext(), Profile.class);
        intent.putExtra(OWNER_ID, workerId);

        startActivity(intent);
    }
}