package com.example.ideal.myapplication.logIn;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.ideal.myapplication.helpApi.DownloadServiceData;
import com.example.ideal.myapplication.helpApi.LoadingProfileData;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.other.MyService;
import com.example.ideal.myapplication.other.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class MyAuthorization {

    private static final String TAG = "DBInf";

    private static final String USERS = "users";
    private static final String SERVICES = "services";
    private static final String ORDERS = "orders";
    private static final String REVIEWS = "reviews";
    private static final String SUBSCRIPTIONS = "subscriptions";
    private static final String SUBSCRIBERS = "subscribers";
    private static final String USER_ID = "user id";
    private static final String IS_CANCELED = "is canceled";

    private static final String WORKING_DAY_ID = "working day id";
    private static final String WORKING_TIME_ID = "working time id";
    private static final String WORKING_DAYS = "working days";
    private static final String WORKING_TIME = "working time";
    private static final String TIME = "time";
    private static final String DATE = "date";

    private static final String PHONE = "phone";
    private static final String NAME = "name";
    private static final String SERVICE_ID = "service id";
    private static final String WORKER_ID = "worker id";

    private DBHelper dbHelper;

    private Context context;
    private String myPhoneNumber;

    private int counter;

    private Thread dayThread = new Thread();
    private Thread timeThread = new Thread();
    private Thread orderThread = new Thread();

    MyAuthorization(Context _context, String _myPhoneNumber) {
        context = _context;
        myPhoneNumber = _myPhoneNumber;

        dbHelper = new DBHelper(context);
    }

    void authorizeUser() {
        // скарываем Views и запукаем прогресс бар

        Query userQuery = FirebaseDatabase.getInstance().getReference(USERS).
                orderByChild(PHONE).
                equalTo(myPhoneNumber);

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                if (usersSnapshot.getChildrenCount() == 0) {
                    goToRegistration();
                } else {
                    // Получаем остальные данные о пользователе
                    DataSnapshot userSnapshot = usersSnapshot.getChildren().iterator().next();
                    Object name = userSnapshot.child(NAME).getValue();
                    if (name == null) {
                        // Имя в БД отсутствует, значит пользователь не до конца зарегистрировался
                        goToRegistration();
                    } else {
                        clearSQLite();

                        SQLiteDatabase localDatabase = dbHelper.getWritableDatabase();
                        LoadingProfileData.loadMyInfo(userSnapshot, localDatabase);
                        loadMyOrders(userSnapshot.child(ORDERS));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

   /* private void loadMyServiceOrders() {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String ordersQuery =
                "SELECT DISTINCT "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID +
                        " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_WORKING_TIME + ", "
                        + DBHelper.TABLE_ORDERS
                        + " WHERE "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID + " = ? "
                        + " AND "
                        + DBHelper.KEY_SERVICE_ID_WORKING_DAYS + " = "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + " = "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.KEY_WORKING_TIME_ID_ORDERS + " = "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID;

        Cursor cursor = database.rawQuery(ordersQuery, new String[]{getUserId()});

        if (cursor.moveToFirst()) {
            int indexUserId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);

            do {
                loadUserById(cursor.getString(indexUserId));
            } while (cursor.moveToNext());
        }
        Log.d(TAG, "loadMyServiceOrders: ");
        cursor.close();
    }*/


    /*private void loadUserSubscribers(DataSnapshot userSnapshot) {

        DataSnapshot subscriptionSnapshot = userSnapshot.child(SUBSCRIBERS);
        for (DataSnapshot subSnapshot : subscriptionSnapshot.getChildren()) {
            String id = subSnapshot.getKey();
            Log.d(TAG, "loadUserSubscribers: " + id);
            String userId = String.valueOf(subSnapshot.child(USER_ID).getValue());

            //если мы владелец старнички то только тогда загружаем инфу о наших подписчиках
            loadUserById(userId);

            addUserSubscriberInLocalStorage(id, userId);
        }
    }*/

    /*private void loadUserById(final String userId) {

        final DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference(USERS)
                .child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                // Получаем остальные данные о пользователе
                Object name = userSnapshot.child(NAME).getValue();
                if (name == null) {
                    if (userId.equals(getUserId())) {
                        // Имя пользователя в БД отсутствует, значит пользователь не до конца зарегистрировался
                        goToRegistration();
                    }
                } else {
                    //загрузка данных о пользователе
                    downloadServiceData.loadUserInfo(userSnapshot);
                    //загрузка данных о сервисах пользователя, и оценок о нем
                    downloadServiceData.loadSchedule(userSnapshot.child(SERVICES), userSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }*/



    /*private void addUserSubscriberInLocalStorage(String id, String userId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ID, id);
        contentValues.put(DBHelper.KEY_USER_ID, userId);
        contentValues.put(DBHelper.KEY_WORKER_ID, getUserId());
        database.insert(DBHelper.TABLE_SUBSCRIBERS, null, contentValues);
    }*/

    private void loadMyOrders(DataSnapshot _ordersSnapshot) {

        if (_ordersSnapshot.getChildrenCount() == 0) {
                goToProfile();
            }

            counter = 0;
            final long childrenCount = _ordersSnapshot.getChildrenCount();
            for (final DataSnapshot orderSnapshot : _ordersSnapshot.getChildren()) {
                //получаем "путь" к мастеру, на сервис которого мы записаны
                final String orderId = orderSnapshot.getKey();
                final String workerId = String.valueOf(orderSnapshot.child(WORKER_ID).getValue());
                final String serviceId = String.valueOf(orderSnapshot.child(SERVICE_ID).getValue());
                final String workingDayId = String.valueOf(orderSnapshot.child(WORKING_DAY_ID).getValue());
                final String workingTimeId = String.valueOf(orderSnapshot.child(WORKING_TIME_ID).getValue());
            DatabaseReference serviceReference = FirebaseDatabase.getInstance()
                    .getReference(USERS)
                    .child(workerId)
                    .child(SERVICES)
                    .child(serviceId);

            serviceReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot serviceSnapshot) {
                    //получаем данные для нашего ордера
                    final DataSnapshot daySnapshot = serviceSnapshot.child(WORKING_DAYS)
                            .child(workingDayId);

                    if (isActualOrder(daySnapshot, workingTimeId, orderId)) {

                        dayThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                addWorkingDaysInLocalStorage(daySnapshot, serviceId);
                            }
                        });
                        dayThread.start();

                        final DataSnapshot timeSnapshot = daySnapshot.child(WORKING_TIME)
                                .child(workingTimeId);
                        timeThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                addTimeInLocalStorage(timeSnapshot, workingDayId);
                            }
                        });
                        timeThread.start();

                        final DataSnapshot orderSnapshot = timeSnapshot.child(ORDERS)
                                .child(orderId);
                        orderThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                addOrderInLocalStorage(orderSnapshot, workingTimeId);
                            }
                        });
                        orderThread.start();

                        String serviceName = serviceSnapshot.child(NAME).getValue(String.class);
                        addServiceInLocalStorage(serviceId, serviceName, workerId);
                    }

                    counter++;
                    if (counter == childrenCount) {
                        if (dayThread.isAlive() || timeThread.isAlive() || orderThread.isAlive()) {
                            try {
                                dayThread.join();
                                timeThread.join();
                                orderThread.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            goToProfile();
                        } else {
                            goToProfile();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    attentionBadConnection();
                }
            });
        }
    }

    private boolean isActualOrder(DataSnapshot daySnapshot, String timeId, String orderId) {
        String date = daySnapshot.child(DATE).getValue(String.class);
        String time = daySnapshot.child(WORKING_TIME).child(timeId).child(TIME).getValue(String.class);
        boolean isCanceled = daySnapshot.child(WORKING_TIME)
                .child(timeId)
                .child(ORDERS)
                .child(orderId)
                .child(IS_CANCELED)
                .getValue(Boolean.class);

        WorkWithTimeApi workWithTimeApi = new WorkWithTimeApi();
        //3600000 * 24 = 24 часа
        String commonDate = date + " " + time;
        long orderDate = workWithTimeApi.getMillisecondsStringDate(commonDate) + 3600000;
        long sysdate = workWithTimeApi.getSysdateLong();

        if ((orderDate < sysdate) || isCanceled) {
            return false;
        }

        return true;
    }

    private void addServiceInLocalStorage(String serviceId, String serviceName, String workerId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ID, serviceId);
        contentValues.put(DBHelper.KEY_NAME_SERVICES, serviceName);
        contentValues.put(DBHelper.KEY_USER_ID, workerId);

        database.insert(DBHelper.TABLE_CONTACTS_SERVICES, null, contentValues);
    }

    private void addWorkingDaysInLocalStorage(DataSnapshot workingDaySnapshot, String serviceId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        String dayId = workingDaySnapshot.getKey();
        String date = String.valueOf(workingDaySnapshot.child(DATE).getValue());
        contentValues.put(DBHelper.KEY_ID, dayId);
        contentValues.put(DBHelper.KEY_DATE_WORKING_DAYS, date);
        contentValues.put(DBHelper.KEY_SERVICE_ID_WORKING_DAYS, serviceId);

        database.insert(DBHelper.TABLE_WORKING_DAYS, null, contentValues);

        dayThread.interrupt();
    }

    private void addTimeInLocalStorage(DataSnapshot timeSnapshot, String workingDayId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        String timeId = timeSnapshot.getKey();
        contentValues.put(DBHelper.KEY_ID, timeId);
        contentValues.put(DBHelper.KEY_TIME_WORKING_TIME, String.valueOf(timeSnapshot.child(TIME).getValue()));
        contentValues.put(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME, workingDayId);

        database.insert(DBHelper.TABLE_WORKING_TIME, null, contentValues);

        timeThread.interrupt();
    }

    private void addOrderInLocalStorage(DataSnapshot orderSnapshot, String timeId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String orderId = orderSnapshot.getKey();
        String userId = String.valueOf(orderSnapshot.child(USER_ID).getValue());

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ID, orderId);
        contentValues.put(DBHelper.KEY_USER_ID, userId);
        contentValues.put(DBHelper.KEY_IS_CANCELED_ORDERS, "false");
        contentValues.put(DBHelper.KEY_WORKING_TIME_ID_ORDERS, timeId);

        database.insert(DBHelper.TABLE_ORDERS, null, contentValues);

        orderThread.interrupt();
    }

    /*private String updateMessageTime(String timeId) {
        String updatedTime = "";

        Cursor cursor = WorkWithLocalStorageApi.getServiceCursorByTimeId(timeId);

        if (cursor.moveToFirst()) {
            int indexTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);
            int indexDate = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS);

            String time = cursor.getString(indexTime);
            String date = cursor.getString(indexDate);

            WorkWithTimeApi workWithTimeApi = new WorkWithTimeApi();
            //3600000 * 24 = 24 часа
            String commonDate = date + " " + time;
            long messageDateLong = workWithTimeApi.getMillisecondsStringDate(commonDate) + 3600000 * 24;
            long sysdate = workWithTimeApi.getSysdateLong();

            if (sysdate > messageDateLong) {
                // вычитаем 3 часа, т.к. метод работает с датой по Гринвичу
                updatedTime = WorkWithTimeApi.getDateInFormatYMDHMS(new Date(messageDateLong - 3600000 * 3));
            }
        }
        cursor.close();
        return updatedTime;
    }*/

    // Удаляет все данные о пользователях, сервисах, рабочих днях и рабочем времени из SQLite
    private void clearSQLite() {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.delete(DBHelper.TABLE_CONTACTS_USERS, null, null);
        database.delete(DBHelper.TABLE_CONTACTS_SERVICES, null, null);
        database.delete(DBHelper.TABLE_WORKING_DAYS, null, null);
        database.delete(DBHelper.TABLE_WORKING_TIME, null, null);

        database.delete(DBHelper.TABLE_PHOTOS, null, null);
        database.delete(DBHelper.TABLE_SUBSCRIBERS, null, null);

        database.delete(DBHelper.TABLE_REVIEWS, null, null);
        database.delete(DBHelper.TABLE_ORDERS, null, null);
    }

    private void attentionBadConnection() {
        Toast.makeText(context, "Плохое соединение", Toast.LENGTH_SHORT).show();
    }

    private void goToRegistration() {
        Intent intent = new Intent(context, Registration.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(PHONE, myPhoneNumber);
        context.startActivity(intent);
    }

    private void goToProfile() {
        // тоже самое необходимо прописать для перехода с регистрации
        //ContextCompat.startForegroundService(context, new Intent(context, MyService.class));

        Intent intent = new Intent(context, Profile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        context.startService(new Intent(context, MyService.class));
    }

}
