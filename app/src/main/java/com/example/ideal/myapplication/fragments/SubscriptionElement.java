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
    private static final String SUBSCRIPTIONS = "подписки";

    private TextView nameText;
    private TextView subscribeText;
    private ImageView avatarImage;

    private String workerId;
    private String workerName;
    private boolean isSubscription;

    @SuppressLint("ValidFragment")
    public SubscriptionElement(String _workerId, String _workerName, String status) {
        workerId = _workerId;
        workerName = _workerName;
        isSubscription = status.equals(SUBSCRIPTIONS);
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

        nameText.setOnClickListener(this);
        avatarImage.setOnClickListener(this);
        if(isSubscription) {
            subscribeText.setOnClickListener(this);
        }
        else {
            subscribeText.setVisibility(View.GONE);
        }
        LinearLayout layout = view.findViewById(R.id.subscriptionElementLayout);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.setMargins(10,10,10,15);
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
        workWithLocalStorageApi.setPhotoAvatar(workerId,avatarImage,width,height);    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.subscribeSubscriptionElementText:
                SubscriptionsApi subsApi = new SubscriptionsApi(workerId, getContext());
                
                if ((boolean) subscribeText.getTag()) {
                    subscribeText.setTag(false);
                    subscribeText.setText("\uf004");
                    subsApi.unsubscribe();
                    Toast.makeText(getContext(), "Подписка отменена", Toast.LENGTH_SHORT).show();
                } else {
                    subscribeText.setTag(true);
                    subscribeText.setText("<B");
                    subsApi.subscribe();
                    Toast.makeText(getContext(), "Вы подписались", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                goToProfile();
                break;
        }
    }

    private void goToProfile(){
        Intent intent = new Intent(getContext(), Profile.class);
        intent.putExtra(OWNER_ID, workerId);

        startActivity(intent);
    }
}