package com.example.ideal.myapplication.fragments.panelElements;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.chat.Dialogs;
import com.example.ideal.myapplication.searchService.MainScreen;
import com.example.ideal.myapplication.other.Profile;


public class BottomPanel extends Fragment implements View.OnClickListener {

    private static final String OWNER_ID = "owner id";
    private static final String TAG = "DBInf";

    private boolean isMyProfile;
    private String myPhone;
    private TextView profileText;
    private TextView mainScreenText;
    private TextView chatText;

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
        profileText = view.findViewById(R.id.profileBottomPanelText);
        mainScreenText = view.findViewById(R.id.mainScreenBottomPanelText);
        chatText = view.findViewById(R.id.chatBottomPanelText);

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

        if (!isMyProfile) {
            Intent intent = new Intent(getContext(), Profile.class);
            intent.putExtra(OWNER_ID, myPhone);
            profileText.setTextColor(ContextCompat.getColor(getContext(), R.color.selectedButton));
            startActivity(intent);
        }
    }

    private void goToMainScreen() {
        if (getContext().getClass() != MainScreen.class) {
            Intent intent = new Intent(getContext(), MainScreen.class);
            mainScreenText.setTextColor(ContextCompat.getColor(getContext(), R.color.selectedButton));
            startActivity(intent);
        }
    }

    private void goToDialogs() {
        if (getContext().getClass() != Dialogs.class) {
            Intent intent = new Intent(getContext(), Dialogs.class);
            chatText.setTextColor(ContextCompat.getColor(getContext(), R.color.selectedButton));
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //при возврате перекрашиваем в белый
        chatText.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        mainScreenText.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        profileText.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

        highlightCurrentPage();
    }

    private void highlightCurrentPage() {
        if (isMyProfile) {
            profileText.setTextColor(ContextCompat.getColor(getContext(), R.color.selectedButton));
        }

        if (getContext().getClass() == MainScreen.class) {
            mainScreenText.setTextColor(ContextCompat.getColor(getContext(), R.color.selectedButton));
        }

        if (getContext().getClass() == Dialogs.class) {
            chatText.setTextColor(ContextCompat.getColor(getContext(), R.color.selectedButton));
        }
    }
}

