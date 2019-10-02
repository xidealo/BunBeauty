package com.bunbeauty.ideal.myapplication.helpApi;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bunbeauty.ideal.myapplication.fragments.panelElements.BottomPanel;
import com.bunbeauty.ideal.myapplication.fragments.panelElements.TopPanel;
import com.google.firebase.auth.FirebaseAuth;

public class PanelBuilder {

    private FragmentTransaction transaction;
    private TopPanel topPanel;

    private boolean isMyProfile = false;
    private String myId;

    // Для профиля
    public PanelBuilder(String ownerId) {
        if(ownerId != null) {
            isMyProfile = isMyProfile(ownerId);
        }
    }

    // Для всех остальных
    public PanelBuilder() {

    }

    private boolean isMyProfile(String ownerId) {
        myId = getUserId();
        return myId.equals(ownerId);
    }

    public void buildFooter(FragmentManager manager, int layoutId) {
        BottomPanel bottomPanel = new BottomPanel(isMyProfile, myId);
        Object object = new Object();
        object.equals(2);
        transaction = manager.beginTransaction();
        transaction.add(layoutId, bottomPanel);
        transaction.commit();
    }
    private static final String TAG = "DBInf";

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

    // для ProfileActivity и всех остальных
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
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


}
