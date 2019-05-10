package com.example.ideal.myapplication.other;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.helpApi.DownloadServiceData;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.example.ideal.myapplication.notifications.NotificationCancel;
import com.example.ideal.myapplication.notifications.NotificationConstructor;
import com.example.ideal.myapplication.notifications.NotificationOrder;
import com.example.ideal.myapplication.notifications.NotificationReviewForService;
import com.example.ideal.myapplication.notifications.NotificationReviewForUser;
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

import java.lang.invoke.WrongMethodTypeException;
import java.util.HashMap;

import static android.app.NotificationManager.IMPORTANCE_MAX;

public class MyService extends Service {

    private static final String TAG = "DBInf";

    private static final String CHANNEL_ID = "1";

    private static final String USERS = "users";
    private static final String NAME = "name";

    private static final String SUBSCRIBERS = "subscribers";

    private static final String SERVICES = "services";
    private static final String USER_ID = "user id";

    private static final String WORKING_DAYS = "working days";
    private static final String DATE = "date";
    private static final String SERVICE_ID = "service id";

    private static final String WORKING_TIME = "working time";
    private static final String TIME = "time";

    private static final String ORDERS = "orders";
    private static final String IS_CANCELED = "is canceled";
    private static final String WORKER_ID = "worker id";

    private static final String REVIEWS = "reviews";
    private static final String REVIEW = "review";
    private static final String RATING = "rating";

    private DBHelper dbHelper;
    private WorkWithLocalStorageApi LSApi;
    private WorkWithTimeApi timeApi;

    private String userId;
    private String serviceName;
    private String workingDate;
    private String workingTime;

    // таймеры для оповешений о возможности оценить спустя сутки
    private HashMap<String, CountDownTimer> CDTimers;

    private long counterForSubscribers;
    private long countOfSubscribers;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "MyService onCreate");
    }
   
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "MyService onStartCommand");

        dbHelper = new DBHelper(this);
        LSApi = new WorkWithLocalStorageApi(dbHelper.getReadableDatabase());
        timeApi = new WorkWithTimeApi();
        userId = getUserId();
        CDTimers = new HashMap<>();

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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void startMyListener() {

        new Thread(new Runnable() {
            private final Context context = MyService.this;

            @Override
            public void run() {
                // слушает не оценил ли кто-то пользователя
                startReviewForMeListener();

                // слушает изменение моих услуг
                startServicesListener();

                // слушает не подписался ли кто-то на меня
                startSubscribersListener();

                // слушает не отказался ли кто-то от пользователя
                startMyOrdersListener();
            }

            private void startServicesListener() {
                final DatabaseReference myServicesRef = FirebaseDatabase.getInstance().getReference(USERS)
                        .child(userId)
                        .child(SERVICES);

                myServicesRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot serviceSnapshot, @Nullable String s) {
                        serviceName = serviceSnapshot.child(NAME).getValue(String.class);

                        final DatabaseReference myWorkingDaysRef = myServicesRef
                                .child(serviceSnapshot.getKey())
                                .child(WORKING_DAYS);
                        myWorkingDaysRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot workingDaySnapshot, @Nullable String s) {
                                workingDate = workingDaySnapshot.child(DATE).getValue(String.class);

                                final DatabaseReference myWorkingTimeRef = myWorkingDaysRef
                                        .child(workingDaySnapshot.getKey())
                                        .child(WORKING_TIME);
                                myWorkingTimeRef.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot workingTimeSnapshot, @Nullable String s) {
                                        workingTime = workingTimeSnapshot.child(TIME).getValue(String.class);

                                        final DatabaseReference myOrdersRef = myWorkingTimeRef
                                                .child(workingTimeSnapshot.getKey())
                                                .child(ORDERS);
                                        myOrdersRef.addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(@NonNull DataSnapshot orderSnapshot, @Nullable String s) {
                                                // срабатывает на добавление ордера
                                                WorkWithTimeApi timeApi = new WorkWithTimeApi();
                                                String orderCreationTime = orderSnapshot.child(TIME).getValue(String.class);
                                                long delay = Math.abs(timeApi.getMillisecondsStringDateWithSeconds(orderCreationTime)-timeApi.getSysdateLong());

                                                String orderId = orderSnapshot.getKey();
                                                // устанавливаем таймер, чтобы через день после обслуживания дать оценить
                                                if (!orderSnapshot.child(IS_CANCELED).getValue(Boolean.class)) {
                                                    setTimerForReview(orderId, workingDate, workingTime, serviceName, false);
                                                }

                                                Log.d(TAG, delay + "");
                                                // исправить на 5000
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

                                                            String userName = userSnapshot.child(NAME).getValue(String.class);

                                                            NotificationOrder notificationOrder = new NotificationOrder(context,
                                                                    userName,
                                                                    serviceName,
                                                                    workingDate,
                                                                    workingTime);
                                                            notificationOrder.createNotification();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onChildChanged(@NonNull DataSnapshot orderSnapshot, @Nullable String s) {
                                                boolean isCanceled = orderSnapshot.child(IS_CANCELED).getValue(Boolean.class);
                                                if (isCanceled) {
                                                    String orderId = orderSnapshot.getKey();
                                                    CDTimers.get(orderId).cancel();
                                                    CDTimers.remove(orderId);
                                                }

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
                                                        .getValue()
                                                        .toString();

                                                if(!review.equals("-") && !rating.equals("0")) {
                                                    final String userId = orderSnapshot
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
                                                            NotificationYourServiceIsRated notification =
                                                                    new NotificationYourServiceIsRated(context, name, serviceName,userId);
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
                                    public void onChildChanged(@NonNull DataSnapshot workingTimeSnapshot, @Nullable String s) { }

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
                    public void onChildChanged(@NonNull DataSnapshot serviceSnapshot, @Nullable String s) { }

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

                        long rating = orderSnapshot
                                .child(REVIEWS)
                                .getChildren()
                                .iterator()
                                .next()
                                .child(RATING)
                                .getValue(Long.class);

                        if (!review.equals("-") && rating!=0) {
                            final String workerId = orderSnapshot.child(WORKER_ID).getValue(String.class);

                            DatabaseReference ownerRef = FirebaseDatabase
                                    .getInstance()
                                    .getReference(USERS)
                                    .child(workerId)
                                    .child(NAME);
                            ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot nameSnapshot) {
                                    String name = nameSnapshot.getValue(String.class);

                                    NotificationYouAreRated notification = new NotificationYouAreRated(context, name, workerId);
                                    notification.createNotification();
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
                                if(countOfSubscribers==counterForSubscribers) {
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

            private void startMyOrdersListener() {
                DatabaseReference ordersRef = FirebaseDatabase.getInstance()
                        .getReference(USERS)
                        .child(userId)
                        .child(ORDERS);

                ordersRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot orderSnapshot, @Nullable String s) {
                        final String orderId = orderSnapshot.getKey();
                        String workerId = orderSnapshot.child(WORKER_ID).getValue(String.class);
                        String serviceId = orderSnapshot.child(SERVICE_ID).getValue(String.class);

                        SQLiteDatabase database = dbHelper.getReadableDatabase();
                        String orderQuery = "SELECT "
                                + DBHelper.KEY_WORKING_TIME_ID_ORDERS + ", "
                                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + ", "
                                + DBHelper.KEY_TIME_WORKING_TIME + ", "
                                + DBHelper.KEY_DATE_WORKING_DAYS
                                + " FROM "
                                + DBHelper.TABLE_ORDERS + ", "
                                + DBHelper.TABLE_WORKING_TIME + ", "
                                + DBHelper.TABLE_WORKING_DAYS
                                + " WHERE "
                                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID + " = ? AND "
                                + DBHelper.KEY_WORKING_TIME_ID_ORDERS + " = "
                                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                                + " AND "
                                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = "
                                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID;

                        Cursor orderCursor = database.rawQuery(orderQuery, new String[]{orderId});

                        String workingTimeId = "";
                        String workingDayId = "";
                        String orderDate = "";
                        String orderTime = "";
                        if(orderCursor.moveToFirst()) {
                            workingTimeId = orderCursor.getString(orderCursor.getColumnIndex(DBHelper.KEY_WORKING_TIME_ID_ORDERS));
                            workingDayId = orderCursor.getString(orderCursor.getColumnIndex(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME));
                            orderDate = orderCursor.getString(orderCursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS));
                            orderTime = orderCursor.getString(orderCursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME));
                        }

                        Cursor userCursor = LSApi.getUser(workerId);
                        String workerName = "";
                        if (userCursor.moveToFirst()) {
                            workerName = userCursor.getString(userCursor.getColumnIndex(DBHelper.KEY_NAME_USERS));
                        }

                        Cursor serviceCursor = LSApi.getService(serviceId);
                        String serviceName = "";
                        if (serviceCursor.moveToFirst()) {
                            serviceName = serviceCursor.getString(serviceCursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES));
                        }

                        // можно добавить условие на актуальность записи
                        DatabaseReference isCanceledRef = FirebaseDatabase.getInstance()
                                .getReference(USERS)
                                .child(workerId)
                                .child(SERVICES)
                                .child(serviceId)
                                .child(WORKING_DAYS)
                                .child(workingDayId)
                                .child(WORKING_TIME)
                                .child(workingTimeId)
                                .child(ORDERS)
                                .child(orderId)
                                .child(IS_CANCELED);

                        final String finalOrderDate = orderDate;
                        final String finalOrderTime = orderTime;
                        final String finalWorkerName = workerName;
                        final String finalServiceName = serviceName;

                        isCanceledRef.addValueEventListener(new ValueEventListener() {
                            boolean firstFlag = true;

                            @Override
                            public void onDataChange(@NonNull DataSnapshot isCanceledSnapshot) {
                                // отправляет сообщение об отказе, тк. соответвующий isCanceled поменялся на false
                                if(!firstFlag) {
                                    if (isCanceledSnapshot.getValue(Boolean.class)) {
                                        NotificationCancel notification = new NotificationCancel(context,
                                                finalWorkerName,
                                                finalServiceName,
                                                finalOrderDate,
                                                finalOrderTime);
                                        notification.createNotification();
                                        CDTimers.get(orderId).cancel();
                                        CDTimers.remove(orderId);
                                    }
                                } else {
                                    firstFlag = false;

                                    if (!isCanceledSnapshot.getValue(Boolean.class)) {
                                        setTimerForReview(orderId, finalOrderDate, finalOrderTime, finalServiceName, true);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });


                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }

            private void setTimerForReview(final String orderId, String date, String time, final String commentedName, final boolean isForService) {
                long timeLeftInMillis = timeApi.getMillisecondsStringDate(date + " " + time)
                        - timeApi.getSysdateLong()
                        + 24*60*60*1000; // Время до сеанса + сутки

                if (timeLeftInMillis > 0) {
                    // Настраиваем таймер
                    CountDownTimer CDTimer = new CountDownTimer(timeLeftInMillis, 60*1000) {
                        @Override
                        public void onTick(long millisUntilFinished) { }

                        @Override
                        public void onFinish() {
                            // создаётся необходимое оповещение дял Услуги ил для Клиента
                            NotificationConstructor notification;
                            if (isForService) {
                                notification = new NotificationReviewForService(context, commentedName);
                            } else {
                                notification = new NotificationReviewForUser(context, commentedName);
                            }

                            notification.createNotification();
                            // удаляется из Мапы, тк оповещение отправлено
                            CDTimers.remove(orderId);
                        }
                    }.start();
                    // кладём таймер в Мапу, чтобы если что удалить
                    CDTimers.put(orderId, CDTimer);
                }

            }

        }).run();
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}