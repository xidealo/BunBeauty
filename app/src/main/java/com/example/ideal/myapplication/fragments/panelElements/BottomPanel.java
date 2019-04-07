package com.example.ideal.myapplication.fragments.panelElements;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.chat.Dialogs;
import com.example.ideal.myapplication.myServices.MessageService;
import com.example.ideal.myapplication.other.MainScreen;
import com.example.ideal.myapplication.other.Profile;
import com.google.firebase.auth.FirebaseAuth;


public class BottomPanel extends Fragment implements View.OnClickListener{

    private static final String OWNER_ID = "owner id";
    private static final String TAG = "DBInf";
    private static final String USER_ID = "user id";

    private Button profileBtn;
    private Button mainScreenBtn;
    private Button chatBtn;

    private boolean isMyProfile;
    private String myPhone;

    public BottomPanel() {
    }

    @SuppressLint("ValidFragment")
    public BottomPanel(boolean _isMyProfile, String _myPhone) {
        isMyProfile = _isMyProfile;
        myPhone = _myPhone;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_panel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        profileBtn = view.findViewById(R.id.profileBottomPanelBtn);
        mainScreenBtn = view.findViewById(R.id.mainScreenBottomPanelBtn);
        chatBtn = view.findViewById(R.id.chatBottomPanelBtn);

        // включаем сервис
        Intent intent = new Intent(getContext(), MessageService.class);
        intent.putExtra(USER_ID, getUserId());
        getContext().startService(intent);

        profileBtn.setOnClickListener(this);
        mainScreenBtn.setOnClickListener(this);
        chatBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profileBottomPanelBtn:
                goToProfile();
                break;

            case R.id.mainScreenBottomPanelBtn:
                goToMainScreen();
                break;

            case R.id.chatBottomPanelBtn:
                goToDialogs();
                break;
        }
    }

    private void goToProfile() {

        if(!isMyProfile) {
            Intent intent = new Intent(getContext(), Profile.class);
            intent.putExtra(OWNER_ID, myPhone);

            startActivity(intent);
        }
    }

    private void goToMainScreen() {
        if(getContext().getClass() != MainScreen.class){
            Intent intent = new Intent(getContext(), MainScreen.class);
            startActivity(intent);
        }
    }

    private void goToDialogs() {
        if(getContext().getClass() != Dialogs.class) {
            Intent intent = new Intent(getContext(), Dialogs.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // выключаем сервис
        Intent intent = new Intent(getContext(), MessageService.class);
        getContext().stopService(intent);
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
