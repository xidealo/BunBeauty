package com.example.ideal.myapplication.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.helpApi.SubscribtionsApi;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.other.GuestService;
import com.example.ideal.myapplication.other.Profile;

@SuppressLint("ValidFragment")
public class SubscriptionElement extends Fragment implements View.OnClickListener {

    final String OWNER_ID = "owner id";

    private TextView nameText;
    private Button subscribeBtn;
    private ImageView avatarImage;

    String workerId;
    String workerName;

    @SuppressLint("ValidFragment")
    public SubscriptionElement(String _workerId, String _workerName) {
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
        subscribeBtn = view.findViewById(R.id.subscribeSubscriptionElementBtn);
        avatarImage = view.findViewById(R.id.avatarSubscriptionElementImage);

        nameText.setOnClickListener(this);
        avatarImage.setOnClickListener(this);
        subscribeBtn.setOnClickListener(this);
        setData();
    }

    private void setData() {
        nameText.setText(workerName);
        subscribeBtn.setTag(true);

        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        workWithLocalStorageApi.setPhotoAvatar(workerId,avatarImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.subscribeSubscriptionElementBtn:
                SubscribtionsApi subsApi = new SubscribtionsApi(workerId, getContext());
                
                if ((boolean) subscribeBtn.getTag()) {
                    subscribeBtn.setTag(false);
                    subscribeBtn.setText("<3");
                    subsApi.unsubscribe();
                    Toast.makeText(getContext(), "Подписка отменена", Toast.LENGTH_SHORT).show();
                } else {
                    subscribeBtn.setTag(true);
                    subscribeBtn.setText("<B");
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