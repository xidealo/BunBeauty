package com.bunbeauty.ideal.myapplication.helpApi;

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.FBListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListeningManager {
    private static final String TAG = "DBInf";

    static private ArrayList<FBListener> listenersList = new ArrayList<>();

    static public void addToListenerList(FBListener listener) {
        listenersList.add(listener);
    }

    static public void removeAllListeners() {
        for(FBListener fbListener : listenersList) {
            Object listener = fbListener.getListener();
            DatabaseReference reference = fbListener.getReference();
            if (listener instanceof ChildEventListener) {
                (reference).removeEventListener((ChildEventListener) listener);
                continue;
            }
            if (listener instanceof ValueEventListener) {
                (reference).removeEventListener((ValueEventListener) listener);
            }
        }

        listenersList.clear();
    }
}
