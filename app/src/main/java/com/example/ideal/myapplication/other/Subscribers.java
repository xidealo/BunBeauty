package com.example.ideal.myapplication.other;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.SubscriptionElement;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class Subscribers extends AppCompatActivity {

    private static final String TAG = "DBInf";
    LinearLayout mainLayout;

    private DBHelper dbHelper;

    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribers);

        manager = getSupportFragmentManager();

        mainLayout = findViewById(R.id.mainSubscribersLayout);

        dbHelper = new DBHelper(this);

        PanelBuilder panelBuilder = new PanelBuilder(this);
        panelBuilder.buildFooter(manager, R.id.footerSubscribersLayout);
        panelBuilder.buildHeader(manager, "Подписки", R.id.headerSubscribersLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mainLayout.removeAllViews();
        getMySubscribers();
    }

    private void getMySubscribers() {
        String userId = getUserId();

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String sqlQuery =
                "SELECT " + DBHelper.KEY_WORKER_ID + ", "
                        + DBHelper.KEY_NAME_USERS
                        + " FROM " + DBHelper.TABLE_CONTACTS_USERS + ", "
                        + DBHelper.TABLE_SUBSCRIBERS
                        + " WHERE " + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_USER_ID
                        + " = " + DBHelper.KEY_WORKER_ID
                        + " AND " + DBHelper.TABLE_SUBSCRIBERS + "." + DBHelper.KEY_USER_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[] {userId});

        if(cursor.moveToFirst()) {
            int indexWorkerId = cursor.getColumnIndex(DBHelper.KEY_WORKER_ID);
            int indexWorkerName = cursor.getColumnIndex(DBHelper.KEY_NAME_USERS);

           do {
               String workerId = cursor.getString(indexWorkerId);
               String workerName = cursor.getString(indexWorkerName);

               addSubscriptionToScreen(workerId, workerName);
           } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private  String getUserId(){
        return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    }

    private void addSubscriptionToScreen(String workerId, String workerName) {
        SubscriptionElement subscriptionElement = new SubscriptionElement(workerId, workerName);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.mainSubscribersLayout, subscriptionElement);
        transaction.commit();
    }

}
