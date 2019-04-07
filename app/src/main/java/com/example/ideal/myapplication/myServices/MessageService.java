package com.example.ideal.myapplication.myServices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class MessageService extends Service {

    private static final String TAG = "DBInf";
    private static final String USER_ID = "user id";
    private static final String USERS = "users";

    public MessageService() {
        Log.d(TAG, "MessageService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        String userId = intent.getStringExtra(USER_ID);

        setMessageListener(userId);

        return super.onStartCommand(intent, flags, startId);
    }

    private void setMessageListener(final String userId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(USERS).child(userId);

                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                        Log.d(TAG, "something change");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }
}
