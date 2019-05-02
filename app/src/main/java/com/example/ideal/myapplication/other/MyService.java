package com.example.ideal.myapplication.other;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.helpApi.DownloadServiceData;
import com.example.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static android.app.NotificationManager.IMPORTANCE_MAX;

public class MyService extends Service {

    private static final String TAG = "DBInf";

    private static final String USERS = "users";
    private static final String ORDERS = "orders";
    private static final String REVIEWS = "reviews";
    private static final String REVIEW = "review";
    private static final String WORKER_ID = "worker id";
    private static final String NAME = "name";

    private static final String CHANNEL_ID = "1";
    private static final String SERVICES = "services";
    private static final String WORKING_DAYS = "working days";
    private static final String WORKING_TIME = "working time";
    private static final String TIME = "time";
    private static final String USER_ID = "user id";
    private static final String DATE = "date";
    private static final String RATING = "rating";

    private String userId;
    private DBHelper dbHelper;

    private String serviceName;
    private String workingDate;
    private String workingTime;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "MyService onCreate");
    }
   
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "MyService onStartCommand");

        dbHelper = new DBHelper(this);
        userId = getUserId();

        Intent notificationIntent = new Intent(this, Profile.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("My Service")
                .setContentText("lol")
                .setContentIntent(pendingIntent)
                .setPriority(IMPORTANCE_MAX)
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

        new Thread(new Runnable() {
            private final Context context = MyService.this;

            @Override
            public void run() {
                // слушает не оценил ли кто-то пользователя
                startReviewForMeListener();

                startOrderListener();
            }

            private void startOrderListener() {
                final DatabaseReference myServicesRef = FirebaseDatabase.getInstance().getReference(USERS)
                        .child(userId)
                        .child(SERVICES);

                myServicesRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot serviceSnapshot, @Nullable String s) {
                        final DatabaseReference myWorkingDaysRef = myServicesRef
                                .child(serviceSnapshot.getKey())
                                .child(WORKING_DAYS);
                        myWorkingDaysRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot workingDaySnapshot, @Nullable String s) {
                                final DatabaseReference myWorkingTimeRef = myWorkingDaysRef
                                        .child(workingDaySnapshot.getKey())
                                        .child(WORKING_TIME);
                                myWorkingTimeRef.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot workingTimeSnapshot, @Nullable String s) {
                                        final DatabaseReference myOrdersRef = myWorkingTimeRef
                                                .child(workingTimeSnapshot.getKey())
                                                .child(ORDERS);
                                        myOrdersRef.addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(@NonNull DataSnapshot orderSnapshot, @Nullable String s) {
                                                // срабатывает на добавление ордера
                                                WorkWithTimeApi timeApi = new WorkWithTimeApi();
                                                String orderTime = orderSnapshot.child(TIME).getValue(String.class);
                                                long delay = Math.abs(timeApi.getMillisecondsStringDate(orderTime)-timeApi.getSysdateLong());

                                                // Log.d(TAG, "delay: " + delay);
                                                // не срабатывает
                                                if(delay < 35000) {
                                                    String userId = orderSnapshot.child(USER_ID).getValue(String.class);

                                                    final DatabaseReference userRef = FirebaseDatabase.getInstance()
                                                            .getReference(USERS)
                                                            .child(userId);
                                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                                            SQLiteDatabase db = (new DBHelper(context)).getWritableDatabase();
                                                            DownloadServiceData downloader = new DownloadServiceData(db);
                                                            downloader.loadUserInfo(userSnapshot);

                                                            String name = userSnapshot.child(NAME).getValue(String.class);
                                                            createOrderNotification(name);
                                                            //Log.d(TAG, name + " " + serviceName + " " + workingDate + " " + workingTime);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onChildChanged(@NonNull DataSnapshot orderSnapshot, @Nullable String s) {

                                                String review = orderSnapshot
                                                        .child(REVIEWS)
                                                        .getChildren()
                                                        .iterator()
                                                        .next()
                                                        .child(REVIEW)
                                                        .getValue(String.class);

                                                String rating = orderSnapshot
                                                        .child(REVIEWS)
                                                        .getChildren()
                                                        .iterator()
                                                        .next()
                                                        .child(RATING)
                                                        .getValue(String.class);

                                                if(!review.equals("-") && !rating.equals("0")) {
                                                    String userId = orderSnapshot
                                                            .child(USER_ID)
                                                            .getValue(String.class);

                                                    DatabaseReference ownerRef = FirebaseDatabase
                                                            .getInstance()
                                                            .getReference(USERS)
                                                            .child(userId)
                                                            .child(NAME);
                                                    ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot nameSnapshot) {
                                                            createReviewForServiceNotification(nameSnapshot.getValue(String.class));
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

                                            @Override
                                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                                        });
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot workingTimeSnapshot, @Nullable String s) {
                                        workingTime = workingTimeSnapshot.child(TIME).getValue(String.class);
                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                });
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot workingDaySnapshot, @Nullable String s) {
                                workingDate = workingDaySnapshot.child(DATE).getValue(String.class);
                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot serviceSnapshot, @Nullable String s) {
                        serviceName = serviceSnapshot.child(NAME).getValue(String.class);
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }

            private void startReviewForMeListener() {
                DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference(USERS)
                        .child(userId)
                        .child(ORDERS);
                ordersRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot orderSnapshot, @Nullable String s) {
                        String review = orderSnapshot
                                .child(REVIEWS)
                                .getChildren()
                                .iterator()
                                .next()
                                .child(REVIEW)
                                .getValue(String.class);

                        String rating = orderSnapshot
                                .child(REVIEWS)
                                .getChildren()
                                .iterator()
                                .next()
                                .child(RATING)
                                .getValue(String.class);

                        if (!review.equals("-") && !rating.equals("0")) {
                            String workerId = orderSnapshot.child(WORKER_ID).getValue(String.class);

                            DatabaseReference ownerRef = FirebaseDatabase
                                    .getInstance()
                                    .getReference(USERS)
                                    .child(workerId)
                                    .child(NAME);
                            ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot nameSnapshot) {
                                    createReviewNotification(nameSnapshot.getValue(String.class));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                            });
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }

            // оповещение "На вашу услугу записались"
            private void createOrderNotification(String name) {
                //нужен, чтобы потом обратиться к нему и если что изменить, в нашем случае вроде как не нужен
                int notificationId = 1;
                Log.d(TAG, "createOrderNotification: ");

                //создание notification
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.bun_beauty)
                        .setContentTitle("BunBeauty")
                        .setContentText("Пользователь " + name
                                + " записался к вам на услугу " + serviceName
                                + ". Сеанс состоится " + workingDate
                                + " в " + workingTime + ".")
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(notificationId, builder.build());
            }

            // оповещение "Вас очценил мастер"
            public void createReviewNotification(String name) {
                //нужен, чтобы потом обратиться к нему и если что изменить, в нашем случае вроде как не нужен
                int notificationId = 1;
                Log.d(TAG, "createReviewNotification: ");

                //создание notification
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.bun_beauty)
                        .setContentTitle("BunBeauty")
                        .setContentText("Мастер " + name + " оценил вас!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(notificationId, builder.build());
            }

            // оповещение "Клиент оценил вашу услугу"
            private void createReviewForServiceNotification(String name) {
                //нужен, чтобы потом обратиться к нему и если что изменить, в нашем случае вроде как не нужен
                int notificationId = 1;
                Log.d(TAG, "createReviewForServiceNotification: ");

                //создание notification
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.bun_beauty)
                        .setContentTitle("BunBeauty")
                        .setContentText("Клиент " + name + " оценил ваш сервис \"" + serviceName + "\"")
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(notificationId, builder.build());
            }

        }).run();
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}