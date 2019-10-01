package com.bunbeauty.ideal.myapplication.other;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bunbeauty.ideal.myapplication.cleanArchitecture.presentation.profile.ProfileActivity;
import com.bunbeauty.ideal.myapplication.helpApi.LoadingUserElementData;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static android.app.NotificationManager.IMPORTANCE_MAX;

public class MyService extends Service implements Runnable {

    private static final String TAG = "DBInf";

    private static final String CHANNEL_ID = "1";

    private static final String USERS = "users";
    private static final String NAME = "name";

    private static final String SUBSCRIBERS = "subscribers";

    private static final String SERVICES = "services";
    private static final String USER_ID = "user id";

    private static final String WORKING_DAYS = "working days";
    private static final String SERVICE_ID = "service id";

    private static final String WORKING_TIME = "working time";
    private static final String TIME = "time";

    private static final String ORDERS = "orders";
    private static final String IS_CANCELED = "is canceled";
    private static final String WORKER_ID = "worker id";

    private static final String REVIEWS = "reviews";
    private static final String REVIEW = "review";
    private static final String RATING = "rating";
    private static final String WORKING_DAY_ID = "working day id";
    private static final String WORKING_TIME_ID = "working time id";

    private DBHelper dbHelper;
    private WorkWithLocalStorageApi LSApi;
    private WorkWithTimeApi timeApi;

    private ArrayList<Object[]> listenerList;

    private String userId;
    private Context context;

    Thread thread;

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
        listenerList = new ArrayList<>();

        Intent notificationIntent = new Intent(this, ProfileActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("My Service")
                .setContentText("lol")
                .setContentIntent(pendingIntent)
                .setPriority(IMPORTANCE_MAX)
                .build();
        startForeground(1, notification);
        stopForeground(true);

        startMyListener();

        return START_NOT_STICKY;
    }
 
    public void onDestroy() {
        super.onDestroy();
        thread.interrupt();
        for(Object[] listener : listenerList) {
            if (listener[0] instanceof ChildEventListener) {
                ((DatabaseReference)listener[1]).removeEventListener((ChildEventListener)listener[0]);
            }
            if (listener[0] instanceof ValueEventListener) {
                ((DatabaseReference)listener[1]).removeEventListener((ValueEventListener)listener[0]);
            }

        }

        /*
        HashMap<String, String> m = new HashMap<>();
        for(String ix : m.keySet()) {
        }*/

        Log.d(TAG, "MyService onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void startMyListener() {
        context = MyService.this;

        try {
            FirebaseInstanceId.getInstance().getToken("", "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        thread = new Thread(new Thread(new Runnable() {

            @Override
            public void run() {
                // слушает не оценил ли кто-то пользователя
                // startReviewForMeListener();

                // слушает изменение моих услуг
                startServicesListener();

                // слушает не подписался ли кто-то на меня
                // startSubscribersListener();

                // слушает не отказался ли кто-то от пользователя
                startMyOrdersListener();
            }

            private void startServicesListener() {
                final DatabaseReference myServicesRef = FirebaseDatabase.getInstance().getReference(USERS)
                        .child(userId)
                        .child(SERVICES);

                ChildEventListener serviceListener = myServicesRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot serviceSnapshot, @Nullable String s) {
                        final String serviceId = serviceSnapshot.getKey();
                        //serviceName = serviceSnapshot.child(NAME).getValue(String.class);

                        final DatabaseReference myWorkingDaysRef = myServicesRef
                                .child(serviceSnapshot.getKey())
                                .child(WORKING_DAYS);
                        ChildEventListener workingDaysListener = myWorkingDaysRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull final DataSnapshot workingDaySnapshot, @Nullable String s) {
                                final String workingDayId = workingDaySnapshot.getKey();
                                final DatabaseReference myWorkingTimeRef = myWorkingDaysRef
                                        .child(workingDayId)
                                        .child(WORKING_TIME);
                                ChildEventListener workingTimeListener = myWorkingTimeRef.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull final DataSnapshot workingTimeSnapshot, @Nullable String s) {
                                        final String workingTimeId = workingTimeSnapshot.getKey();
                                        final DatabaseReference myOrdersRef = myWorkingTimeRef
                                                .child(workingTimeId)
                                                .child(ORDERS);
                                        addOrdersListener(myOrdersRef, serviceId, workingDayId, workingTimeId);
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

                                listenerList.add(new Object[]{workingTimeListener, myWorkingTimeRef});
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

                        listenerList.add(new Object[]{workingDaysListener, myWorkingDaysRef});
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
                listenerList.add(new Object[]{serviceListener, myServicesRef});
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

                                   /* NotificationYouAreRated notification = new NotificationYouAreRated(context, name, workerId);
                                    notification.createNotification();*/
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
                                           /* NotificationSubscribers notificationSubscribers = new NotificationSubscribers(context,
                                                    dataSnapshot.getValue().toString());
                                            notificationSubscribers.createNotification();*/
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
                DatabaseReference myOrdersRef = FirebaseDatabase.getInstance()
                        .getReference(USERS)
                        .child(userId)
                        .child(ORDERS);

                ChildEventListener orderListener = myOrdersRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot orderSnapshot, @Nullable String s) {
                        String orderId = orderSnapshot.getKey();
                        String workerId = orderSnapshot.child(WORKER_ID).getValue(String.class);
                        String serviceId = orderSnapshot.child(SERVICE_ID).getValue(String.class);
                        String workingDayId = orderSnapshot.child(WORKING_DAY_ID).getValue(String.class);
                        String workingTimeId = orderSnapshot.child(WORKING_TIME_ID).getValue(String.class);

                        final DatabaseReference orderRef = FirebaseDatabase.getInstance()
                                .getReference(USERS)
                                .child(workerId)
                                .child(SERVICES)
                                .child(serviceId)
                                .child(WORKING_DAYS)
                                .child(workingDayId)
                                .child(WORKING_TIME)
                                .child(workingTimeId)
                                .child(ORDERS)
                                .child(orderId);

                        addOrderListener(orderRef, serviceId, workingDayId, workingTimeId);
                        /*SQLiteDatabase database = dbHelper.getReadableDatabase();
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
                        }*/

                        // можно добавить условие на актуальность записи
                        /*DatabaseReference isCanceledRef = FirebaseDatabase.getInstance()
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

                        isCanceledRef.addValueEventListener(new ValueEventListener() {
                            //boolean firstFlag = true;

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
                        });*/
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

                listenerList.add(new Object[]{orderListener, myOrdersRef});
            }

        }));
        thread.run();
    }

    private void addOrdersListener(DatabaseReference myOrdersRef, final String serviceId, final String workingDayId, final String workingTimeId) {
        ChildEventListener orderListener = myOrdersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot orderSnapshot, @Nullable String s) {

                operateWithNewOrder(orderSnapshot, serviceId, workingDayId, workingTimeId);
                /*// срабатывает на добавление ордера
                WorkWithTimeApi timeApi = new WorkWithTimeApi();
                String orderCreationTime = orderSnapshot.child(TIME).getValue(String.class);
                long delay = Math.abs(timeApi.getMillisecondsStringDateWithSeconds(orderCreationTime)-timeApi.getSysdateLong());
                String orderId = orderSnapshot.getKey();
                //DataSnapshot workingTimeSnapshot = workingDaySnapshot.child(WORKING_TIME).child(workingTimeId);
                *//*final String date = workingDaySnapshot.child(DATE).getValue(String.class);
                final String time = workingTimeSnapshot.child(TIME).getValue(String.class);*//*

                // устанавливаем таймер, чтобы через день после обслуживания дать оценить
                *//*if (!orderSnapshot.child(IS_CANCELED).getValue(Boolean.class)) {
                    setTimerForReview(orderId, date, time, serviceName, false);
                }*//*

                if(delay < 10000) {
                    String userId = orderSnapshot.child(USER_ID).getValue(String.class);

                    WorkWithLocalStorageApi.addDialogInfoInLocalStorage(serviceId,
                            workingDayId,
                            workingTimeId,
                            orderId,
                            userId,
                            orderCreationTime,
                            null);

                    loadUserData(userId);
                }*/
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot orderSnapshot, String s) {
                operateWithExistingOrder(orderSnapshot);
                /*checkCanceled(orderSnapshot);

                DataSnapshot reviewSnapshot = orderSnapshot
                        .child(REVIEWS)
                        .getChildren()
                        .iterator()
                        .next();
                checkReview(reviewSnapshot);*/
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        listenerList.add(new Object[]{orderListener, myOrdersRef});
    }

    private void addOrderListener(DatabaseReference myOrderRef, final String serviceId,
                                   final String workingDayId, final String workingTimeId) {
        ValueEventListener orderListener = myOrderRef.addValueEventListener(new ValueEventListener() {
            boolean isFirst = true;

            @Override
            public void onDataChange(@NonNull DataSnapshot orderSnapshot) {
                if (isFirst) {
                    isFirst = false;
                    operateWithNewOrder(orderSnapshot, serviceId, workingDayId, workingTimeId);

                } else {
                    operateWithExistingOrder(orderSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        listenerList.add(new Object[]{orderListener, myOrderRef});
    }

    private void operateWithExistingOrder(DataSnapshot orderSnapshot) {
        checkCanceled(orderSnapshot);

        DataSnapshot reviewSnapshot = orderSnapshot
                .child(REVIEWS)
                .getChildren()
                .iterator()
                .next();
        checkReview(reviewSnapshot);
    }

    private void operateWithNewOrder(DataSnapshot orderSnapshot, final String serviceId,
                                     final String workingDayId, final String workingTimeId) {
        String orderCreationTime = orderSnapshot.child(TIME).getValue(String.class);
        long delay = Math.abs(WorkWithTimeApi.getMillisecondsStringDateWithSeconds(orderCreationTime)
                - WorkWithTimeApi.getSysdateLong());
        String orderId = orderSnapshot.getKey();

        if(delay < 10000) {
            String userId = orderSnapshot.child(USER_ID).getValue(String.class);

            WorkWithLocalStorageApi.addDialogInfoInLocalStorage(serviceId,
                    workingDayId,
                    workingTimeId,
                    orderId,
                    userId,
                    orderCreationTime,
                    null);

            loadUserData(userId);
        }
    }


    private void checkReview(DataSnapshot reviewSnapshot) {
        String reviewId = reviewSnapshot.getKey();

        String review = reviewSnapshot
                .child(REVIEW)
                .getValue(String.class);

        String rating = reviewSnapshot
                .child(RATING)
                .getValue(Float.class)
                .toString();

        if(!review.equals("-") && !rating.equals("0")) {
            WorkWithLocalStorageApi.addReviewInLocalStorage(reviewId, review, rating);
        }
    }


    private void checkCanceled(DataSnapshot orderSnapshot) {
        boolean isCanceled = orderSnapshot.child(IS_CANCELED).getValue(Boolean.class);
        if (isCanceled) {
                                                    /*String orderId = orderSnapshot.getKey();
                                                    if (CDTimers.get(orderId) != null) {
                                                        CDTimers.get(orderId).cancel();
                                                        CDTimers.remove(orderId);
                                                    }*/
            String orderId = orderSnapshot.getKey();
            WorkWithLocalStorageApi.addIsCanceledInLocalStorage(orderId, "true");
        }
    }

    private void loadUserData(String userId) {
        final DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference(USERS)
                .child(userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                SQLiteDatabase database = (new DBHelper(context)).getWritableDatabase();

                LoadingUserElementData.loadUserNameAndPhoto(userSnapshot, database);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void run() {

    }
}