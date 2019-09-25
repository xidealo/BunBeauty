package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity;

import com.google.firebase.database.DatabaseReference;

public class FBListener {

    private DatabaseReference reference;
    private Object listener;

    public FBListener(DatabaseReference reference, Object listener) {
        this.reference = reference;
        this.listener = listener;
    }

    public DatabaseReference getReference() {
        return reference;
    }

    public Object getListener() {
        return listener;
    }
}
