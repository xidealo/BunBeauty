package com.example.ideal.myapplication.fragments.panelElements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.chat.Dialogs;
import com.example.ideal.myapplication.chat.Messages;
import com.example.ideal.myapplication.editing.EditProfile;
import com.example.ideal.myapplication.editing.EditService;
import com.example.ideal.myapplication.other.GuestService;
import com.example.ideal.myapplication.other.MainScreen;
import com.example.ideal.myapplication.other.Profile;
import com.example.ideal.myapplication.other.SearchService;


public class TopPanel extends Fragment implements View.OnClickListener{

    private static final String TAG = "DBInf";

    private static final String USER_NAME = "my name";
    private static final String USER_CITY = "my city";
    private static final String OWNER_ID = "owner id";
    private static final String SERVICE_ID = "service id";

    private Button backBtn;
    private TextView titleText;
    private Button multiBtn;

    private String title;
    private boolean isMyProfile;
    private boolean isMyService;
    private String serviceId;
    private String serviceOwnerId;

    public TopPanel() {
    }

    @SuppressLint("ValidFragment")
    // Для остальных
    public TopPanel(String _title) {
        title = _title;
        isMyProfile = false;
        isMyService = false;
        serviceId = null;
        serviceOwnerId = null;
    }

    @SuppressLint("ValidFragment")
    // Для моего профиля
    public TopPanel(String _title, boolean _isMyProfile) {
        title = _title;
        isMyProfile = _isMyProfile;
        isMyService = false;
        serviceId = null;
        serviceOwnerId = null;
    }

    @SuppressLint("ValidFragment")
    // Для Messages
    public TopPanel(String _title, String _serviceOwnerId) {
        title = _title;
        isMyProfile = false;
        isMyService = false;
        serviceId = null;
        serviceOwnerId = _serviceOwnerId;
    }

    @SuppressLint("ValidFragment")
    // Для сервиса
    public TopPanel(String _title, boolean _isMyService, String _serviceId, String _serviceOwnerId) {
        title = _title;
        isMyProfile = false;
        isMyService = _isMyService;
        serviceId = _serviceId;
        serviceOwnerId = _serviceOwnerId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.top_panel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        backBtn = view.findViewById(R.id.backTopPanelBtn);
        titleText = view.findViewById(R.id.titleTopPanelText);
        multiBtn = view.findViewById(R.id.multiTopPanelBtn);

        if(super.getActivity().isTaskRoot()){
            backBtn.setVisibility(View.INVISIBLE);
        } else {
            backBtn.setOnClickListener(this);
        }

        multiBtn.setOnClickListener(this);
        // Если не мой профиль
        if(!isMyProfile) {
            // Если это страница сервиса
            if(getContext().getClass() == GuestService.class) {
                // Если это мой сервис
                if(isMyService) {
                    multiBtn.setText("Редактировать");
                // Если это чужой сервис
                } else {
                    multiBtn.setText("Мастер");
                }
            // Если это не сервис
            } else {
                // Если это редактирование сервиса
                if(getContext().getClass() == Messages.class) {
                    // Если это диалог
                    multiBtn.setText("Собеседник");
                    titleText.setOnClickListener(this);
                // Если это не диалог
                } else {
                    if(getContext().getClass() == EditService.class) {
                        multiBtn.setText("Удалить");
                    // Если это не редактирование сервиса
                    } else {
                        // Если это главная страница
                        if(getContext().getClass() == MainScreen.class) {
                            multiBtn.setText("Поиск");
                        // Если это любое другое активити
                        } else {
                            multiBtn.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        }

        setTitle();
    }

    private void setTitle() {
        titleText.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backTopPanelBtn:
                super.getActivity().onBackPressed();
                break;

            case R.id.multiTopPanelBtn:
                multiClick();
                break;

            case R.id.titleTopPanelText:
                goToProfile();
                break;
        }
    }

    private void multiClick() {
        // мой профиль
        if (isMyProfile) {
            goToEditProfile();
        }

        Class currentClass = getContext().getClass();

        // сервис
        if (currentClass == GuestService.class) {
            if (isMyService) {
                goToEditService();
            } else {
                goToProfile();
            }
        }

        if (currentClass == Messages.class) {
            goToProfile();
        }

        // главная
        if (currentClass == MainScreen.class) {
            goToSerachService();
        }

        // редактирование сервиса
        if (currentClass == EditService.class) {
            deleteService();
        }
    }

    private void goToEditProfile() {
        Intent intent = new Intent(getContext(), EditProfile.class);
        TextView nameText = super.getActivity().findViewById(R.id.nameProfileText);
        TextView cityText = super.getActivity().findViewById(R.id.cityProfileText);
        intent.putExtra(USER_NAME, nameText.getText());
        intent.putExtra(USER_CITY, cityText.getText());
        startActivity(intent);
    }

    private void goToEditService() {
        Intent intent = new Intent(getContext(), EditService.class);
        intent.putExtra(SERVICE_ID, serviceId);

        startActivity(intent);
    }

    private void goToProfile() {
        Intent intent = new Intent(getContext(), Profile.class);
        intent.putExtra(OWNER_ID, serviceOwnerId);

        startActivity(intent);
    }

    private void goToSerachService() {
        Intent intent = new Intent(getContext(), SearchService.class);
        startActivity(intent);
    }

    private void deleteService() {
       EditService activity = (EditService)this.getActivity();
       activity.deleteThisService();
    }
}
