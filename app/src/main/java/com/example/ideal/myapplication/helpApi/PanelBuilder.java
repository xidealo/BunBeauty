package com.example.ideal.myapplication.helpApi;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.ideal.myapplication.fragments.panelElements.BottomPanel;
import com.example.ideal.myapplication.fragments.panelElements.TopPanel;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.Context.MODE_PRIVATE;

public class PanelBuilder {


    private static final String PHONE_NUMBER = "Phone number";
    private static final String FILE_NAME = "Info";


    private Context context;
    private SharedPreferences sPref;
    private FragmentTransaction transaction;
    private BottomPanel bottomPanel;
    private TopPanel topPanel;

    private boolean isMyProfile = false;
    private String myPhone;

    // Для профиля
    public PanelBuilder(Context _context, String ownerId) {
        context = _context;

        if(ownerId != null) {
            isMyProfile = isMyProfile(ownerId);
        }
    }

    // Для всех остальных
    public PanelBuilder(Context _context) {
        context = _context;
    }

    private boolean isMyProfile(String ownerId) {
        myPhone = getUserId();

        if(myPhone.equals(ownerId)) {
            return true;
        }

        return false;
    }

    public void buildFooter(FragmentManager manager, int layoutId) {
        bottomPanel = new BottomPanel(isMyProfile, myPhone);

        transaction = manager.beginTransaction();
        transaction.add(layoutId, bottomPanel);
        transaction.commit();
    }

    // для GuestService
    public void buildHeader (FragmentManager manager, String title, int layoutId, boolean isMyService, String serviceId, String serviceOwnerId) {
        topPanel = new TopPanel(title, isMyService, serviceId, serviceOwnerId);

        transaction = manager.beginTransaction();
        transaction.add(layoutId, topPanel);
        transaction.commit();
    }

    // для Messages
    public void buildHeader (FragmentManager manager, String title, int layoutId, String serviceOwnerId) {
        topPanel = new TopPanel(title, serviceOwnerId);

        transaction = manager.beginTransaction();
        transaction.add(layoutId, topPanel);
        transaction.commit();
    }

    // для Profile и всех остальных
    public void buildHeader (FragmentManager manager, String title, int layoutId) {
        if(isMyProfile) {
            topPanel = new TopPanel(title, true);
        } else {
            topPanel = new TopPanel(title);
        }

        transaction = manager.beginTransaction();
        transaction.add(layoutId, topPanel);
        transaction.commit();
    }

    private String getUserId(){
        return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    }


}
