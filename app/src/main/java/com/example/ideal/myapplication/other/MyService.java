package com.example.ideal.myapplication.other;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.ideal.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyService extends Service {

    private static final String TAG = "DBInf";

    private static final String USERS = "users";
    private static final String ORDERS = "orders";
    private static final String REVIEWS = "reviews";
    private static final String REVIEW = "review";
    private static final String WORKER_ID = "worker id";
    private static final String NAME = "name";

    private static final String CHANNEL_ID = "1";

    private String userId;
 
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "MyService onCreate");
    }
   
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "MyService onStartCommand");
        userId = getUserId();

        Intent notificationIntent = new Intent(this, Profile.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("My Service")
                .setContentText("lol")
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        startMyListener();

        return START_NOT_STICKY;
    }
 
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MyService onDestroy");
    }
 
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "MyService onBind");
    return null;
    }

    void startMyListener() {
        /*DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(USERS)
                .child(userId)
                .child(ORDERS);
        */

        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(USERS)
                        .child(userId)
                        .child(ORDERS);
                myRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String review = dataSnapshot.child(REVIEWS).getChildren().iterator().next().child(REVIEW).getValue().toString();
                        //createNotification(review);
                        String workerId =  dataSnapshot.child(WORKER_ID).getValue().toString();

                        DatabaseReference ownerRef = FirebaseDatabase
                                .getInstance()
                                .getReference(USERS)
                                .child(workerId)
                                .child(NAME);
                        ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                createNotification(dataSnapshot.getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).run();
    }

    public void createNotification(String s) {
        //нужен, чтобы потом обратиться к нему и если что изменить, в нашем случае вроде как не нужен
        int notificationId = 1;

        //создание notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.bun_beauty)
                .setContentTitle("BunBeauty")
                .setContentText("Мастер " + s + " оценил вас!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}