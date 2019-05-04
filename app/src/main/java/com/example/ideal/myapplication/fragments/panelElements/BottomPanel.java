package com.example.ideal.myapplication.fragments.panelElements;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.chat.Dialogs;
import com.example.ideal.myapplication.other.MainScreen;
import com.example.ideal.myapplication.other.Profile;


public class BottomPanel extends Fragment implements View.OnClickListener{

    private static final String OWNER_ID = "owner id";
    private static final String TAG = "DBInf";

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
        TextView profileText = view.findViewById(R.id.profileBottomPanelText);
        TextView mainScreenText = view.findViewById(R.id.mainScreenBottomPanelText);
        TextView chatText = view.findViewById(R.id.chatBottomPanelText);

        profileText.setOnClickListener(this);
        mainScreenText.setOnClickListener(this);
        chatText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profileBottomPanelText:
                goToProfile();
                break;

            case R.id.mainScreenBottomPanelText:
                goToMainScreen();
                break;

            case R.id.chatBottomPanelText:
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
}
