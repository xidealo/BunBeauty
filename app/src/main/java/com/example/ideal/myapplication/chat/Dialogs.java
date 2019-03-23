package com.example.ideal.myapplication.chat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.chatElements.DialogElement;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;

public class Dialogs extends AppCompatActivity {

    private static final String TAG = "DBInf";

    private static final String USERS = "users";
    private static final String USER_ID = "user id";
    private static final String NAME = "name";
    private static final String CITY = "city";

    private static final String MESSAGES = "messages";
    private static final String ORDERS = "orders";
    private static final String REVIEWS = "reviews";
    private static final String RATING = "rating";
    private static final String REVIEW = "review";
    private static final String MESSAGE_TIME = "message time";
    private static final String DIALOG_ID = "dialog id";
    private static final String IS_CANCELED = "is canceled";

    private static final String WORKING_TIME = "working time";

    private static final String WORKING_TIME_ID = "working time id";
    private static final String WORKING_DAY_ID = "working day id";
    private static final String TYPE = "type";
    private static final String MESSAGE_ID = "message id";

    private static final String WORKING_DAYS = "working days";
    private static final String SERVICE_ID = "service id";

    private static final String SERVICES = "services";
    private static final String TIME = "time";

    private static final String REVIEW_FOR_SERVICE = "review for service";
    private static final String REVIEW_FOR_USER = "review for user";

    //PHOTOS
    private static final String PHOTOS = "photos";
    private static final String PHOTO_LINK = "photo link";
    private static final String OWNER_ID = "owner id";

    private WorkWithTimeApi workWithTimeApi;
    private WorkWithLocalStorageApi utilitiesApi;
    private DBHelper dbHelper;

    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogs);

        init();
        getDialogs();
    }

    private void init() {
        manager = getSupportFragmentManager();

        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerDialogsLayout);
        panelBuilder.buildHeader(manager, "Диалоги", R.id.headerDialogsLayout);

        dbHelper = new DBHelper(this);
        workWithTimeApi = new WorkWithTimeApi();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        utilitiesApi = new WorkWithLocalStorageApi(database);
    }

    private void getDialogs() {
        //загружаем сначала те, на которые я записан
        getWorkerOrder();
        //загружаем те, на которые записаны мои клиенты
        getClientOrder();
    }

    private void getClientOrder() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // вернуть id тех, кто записан на мои сервисы
        String ordersQuery =
                "SELECT DISTINCT "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID
                        + " FROM "
                        + DBHelper.TABLE_ORDERS + ", "
                        + DBHelper.TABLE_WORKING_TIME + ", "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                        + DBHelper.TABLE_CONTACTS_USERS
                        + " WHERE "
                        + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                        + " = "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                        + " = "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                        + " = "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID
                        + " = "
                        + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID + " = ? ";

        Cursor cursor = database.rawQuery(ordersQuery, new String[]{getUserId()});

        if (cursor.moveToFirst()) {
            int indexClientId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);
            do {
                createUser(cursor.getString(indexClientId));
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private void createUser(String userId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        Cursor userCursor = workWithLocalStorageApi.getUser(userId);

        if (userCursor.moveToFirst()) {
            int indexId = userCursor.getColumnIndex(DBHelper.KEY_ID);
            int indexName = userCursor.getColumnIndex(DBHelper.KEY_NAME_USERS);
            int indexPhone = userCursor.getColumnIndex(DBHelper.KEY_PHONE_USERS);
            int indexCity = userCursor.getColumnIndex(DBHelper.KEY_CITY_USERS);

            do {
                User user = new User();
                user.setId(userCursor.getString(indexId));
                user.setName(userCursor.getString(indexName));
                user.setPhone(userCursor.getString(indexPhone));
                user.setCity(userCursor.getString(indexCity));
                //чтобы не выводил сам себя
                if (!user.getId().equals(getUserId()))
                    addToScreen(user);
            } while (userCursor.moveToNext());
        }

    }

    private void getWorkerOrder() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // вернуть все заказы, на которые я записан
        String ordersQuery =
                "SELECT DISTINCT "
                        + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID + ", "
                        + DBHelper.KEY_NAME_USERS + ", "
                        + DBHelper.KEY_PHONE_USERS + ", "
                        + DBHelper.KEY_CITY_USERS
                        + " FROM "
                        + DBHelper.TABLE_ORDERS + ", "
                        + DBHelper.TABLE_WORKING_TIME + ", "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                        + DBHelper.TABLE_CONTACTS_USERS
                        + " WHERE "
                        + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                        + " = "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                        + " = "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                        + " = "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID
                        + " = "
                        + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + " = ? ";

        Cursor cursor = database.rawQuery(ordersQuery, new String[]{getUserId()});

        if (cursor.moveToFirst()) {

            int indexId = cursor.getColumnIndex(DBHelper.KEY_ID);
            int indexName = cursor.getColumnIndex(DBHelper.KEY_NAME_USERS);
            int indexPhone = cursor.getColumnIndex(DBHelper.KEY_PHONE_USERS);
            int indexCity = cursor.getColumnIndex(DBHelper.KEY_CITY_USERS);
            do {
                User user = new User();
                user.setId(cursor.getString(indexId));
                user.setName(cursor.getString(indexName));
                user.setPhone(cursor.getString(indexPhone));
                user.setCity(cursor.getString(indexCity));
                //чтобы не выводил сам себя
                if (!user.getId().equals(getUserId()))
                    addToScreen(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void addToScreen(User user) {
        Log.d(TAG, "addToScreen: " + user.getId());
        DialogElement dElement = new DialogElement(user);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.mainDialogsLayout, dElement);
        transaction.commit();
    }
}