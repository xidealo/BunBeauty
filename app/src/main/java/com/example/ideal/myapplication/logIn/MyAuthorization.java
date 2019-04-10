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
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.other.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MyAuthorization {

    private static final String TAG = "DBInf";

    private static final String USERS = "users";
    private static final String SERVICES = "services";
    private static final String ORDERS = "orders";
    private static final String REVIEWS = "reviews";
    private static final String SUBSCRIPTIONS = "subscriptions";

    private static final String PHONE = "phone";
    private static final String NAME = "name";
    private static final String SERVICE_ID = "service id";
    private static final String WORKER_ID = "worker id";

    private DBHelper dbHelper;

    private Context context;
    private String myPhoneNumber;

    private DownloadServiceData downloadServiceData;

    MyAuthorization(Context _context, String _myPhoneNumber) {
        context = _context;
        myPhoneNumber = _myPhoneNumber;

        dbHelper = new DBHelper(context);
        SQLiteDatabase localDatabase = dbHelper.getWritableDatabase();
        downloadServiceData = new DownloadServiceData(localDatabase, "Authorization");
    }

    void authorizeUser() {
        // скарываем Views и запукаем прогресс бар

        Query query = FirebaseDatabase.getInstance().getReference(USERS).
                orderByChild(PHONE).
                equalTo(myPhoneNumber);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
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

                        downloadServiceData.loadUserInfo(userSnapshot);

                        // Добавляем подписки пользователя
                        loadUserSubscriptions(userSnapshot);

                        // Загружаем сервисы пользователя из FireBase
                        downloadServiceData.loadSchedule(
                                userSnapshot.child(SERVICES),
                                userSnapshot.getKey());

                        // Загружаем пользователей записанных на мои сервисы
                        loadMyServiceOrders();

                        // Загружаем записи пользователя
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

    private void loadMyServiceOrders() {

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

        if(cursor.moveToFirst()) {
            int indexUserId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);

            do {
                loadUserById(cursor.getString(indexUserId));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void loadUserSubscriptions(DataSnapshot userSnapshot) {

        DataSnapshot subscriptionSnapshot = userSnapshot.child(SUBSCRIPTIONS);
        for (DataSnapshot subSnapshot : subscriptionSnapshot.getChildren()) {
            String id = subSnapshot.getKey();
            String workerId = String.valueOf(subSnapshot.child(WORKER_ID).getValue());
            loadUserById(workerId);

            addUserSubscriptionInLocalStorage(id, workerId);
        }
    }

    private void loadUserById(final String userId) {

        final DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference(USERS)
                .child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                // Получаем остальные данные о пользователе
                Object name = userSnapshot.child(NAME).getValue();
                if (name == null) {
                    // Имя в БД отсутствует, значит пользователь не до конца зарегистрировался
                    goToRegistration();
                } else {
                    downloadServiceData.loadUserInfo(userSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void addUserSubscriptionInLocalStorage(String id, String workerId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ID, id);
        contentValues.put(DBHelper.KEY_USER_ID, getUserId());
        contentValues.put(DBHelper.KEY_WORKER_ID, workerId);
        database.insert(DBHelper.TABLE_SUBSCRIBERS, null, contentValues);
    }

    // Получается загружаем все, о человеке, с которым можем взаимодействовать из профиля, возможно в ордереде стоит хранить дату,
    // чтобы считать ее прсорочена она или нет и уже от этого делать onDataChange, если дата просрочена,
    // то мы никак через профиль не взаимодействуем с этим человеком
    private void loadMyOrders(DataSnapshot _ordersSnapshot) {

        if(_ordersSnapshot.getChildrenCount() == 0) {
            goToProfile();
        }

        for(final DataSnapshot orderSnapshot: _ordersSnapshot.getChildren()) {
            //получаем "путь" к мастеру, на сервис которого мы записаны
            final String workerId = String.valueOf(orderSnapshot.child(WORKER_ID).getValue());
            final String serviceId = String.valueOf(orderSnapshot.child(SERVICE_ID).getValue());
            DatabaseReference userReference = FirebaseDatabase.getInstance()
                    .getReference(USERS)
                    .child(workerId);

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                    // Загружаем данные о пользователе
                    downloadServiceData.loadUserInfo(userSnapshot);

                    // Загружаем данные о сервисе на который записаны
                    downloadServiceData.addServiceInLocalStorage(userSnapshot.child(SERVICES).child(serviceId), workerId);

                    // Загружаем данные о ревтю для пользователя
                    downloadServiceData.addReviewInLocalStorage(orderSnapshot.child(REVIEWS), orderSnapshot.getKey());

                    goToProfile();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

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

    private void goToRegistration() {
        Intent intent = new Intent(context, Registration.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(PHONE, myPhoneNumber);
        context.startActivity(intent);
    }

    private void goToProfile() {

        Intent intent = new Intent(context, Profile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private void attentionBadConnection() {
        Toast.makeText(context, "Плохое соединение", Toast.LENGTH_SHORT).show();
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
