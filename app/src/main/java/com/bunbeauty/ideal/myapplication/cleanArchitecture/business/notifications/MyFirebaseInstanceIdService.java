package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.notifications;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseInstanceIdService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseIIDService";
    private static final String USERS = "users";
    private static final String TOKEN = "token";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            reference.child(USERS)
                    .child(userId)
                    .child(TOKEN)
                    .setValue(token);
        }
    }
}
