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
import com.example.ideal.myapplication.notifications.NotificationOrder;
import com.example.ideal.myapplication.notifications.NotificationSubscribers;
import com.example.ideal.myapplication.notifications.NotificationYouAreRated;
import com.example.ideal.myapplication.notifications.NotificationYourServiceIsRated;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private static final String SUBSCRIBERS = "subscribers";

    private String userId;
    private DBHelper dbHelper;

    private String serviceName;
    private String workingDate;
    private String workingTime;

    private long counterForSubscribers;
    private long countOfSubscribers;

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

                startServicesListener();

                startSubscribersListener();
            }

            private void startServicesListener() {
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

                                                            NotificationOrder notificationOrder = new NotificationOrder(context, name, serviceName, workingDate, workingTime);
                                                            notificationOrder.createNotification();
                                                            // createOrderNotification(name);
                                                            // Log.d(TAG, name + " " + serviceName + " " + workingDate + " " + workingTime);
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

                                                            String name = nameSnapshot.getValue(String.class);
                                                            NotificationYourServiceIsRated notification = new NotificationYourServiceIsRated(context, name, serviceName);
                                                            notification.createNotification();
                                                            // createReviewForServiceNotification();
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
                                    String name = nameSnapshot.getValue(String.class);

                                    NotificationYouAreRated notification = new NotificationYouAreRated(context, name);
                                    notification.createNotification();
                                    //createReviewNotification();
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

            private void startSubscribersListener() {
                final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(USERS)
                        .child(userId)
                        .child(SUBSCRIBERS);

                //получаю изначальное количество подписчиков для флага
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        countOfSubscribers = dataSnapshot.getChildrenCount();
                        counterForSubscribers = 0;

                        //слушаем подписчиков
                        myRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                //чтобы не выводить notification о старых подписчиках
                                if(counterForSubscribers == countOfSubscribers){
                                    String userId = dataSnapshot.child(USER_ID).getValue().toString();

                                    DatabaseReference userRef = FirebaseDatabase
                                            .getInstance()
                                            .getReference(USERS)
                                            .child(userId)
                                            .child(NAME);

                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            NotificationSubscribers notificationSubscribers = new NotificationSubscribers(context,
                                                    dataSnapshot.getValue().toString());
                                            notificationSubscribers.createNotification();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    countOfSubscribers++;
                                }
                                counterForSubscribers++;
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                                //кто-то отписался
                                counterForSubscribers--;
                                countOfSubscribers--;
                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }

        }).run();
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}