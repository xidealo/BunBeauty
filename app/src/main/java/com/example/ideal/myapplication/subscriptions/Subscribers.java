package com.example.ideal.myapplication.subscriptions;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.adapters.MessageAdapter;
import com.example.ideal.myapplication.adapters.SubscriptionAdapter;
import com.example.ideal.myapplication.adapters.SubscriptionElement;
import com.example.ideal.myapplication.fragments.objects.Message;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Subscribers extends AppCompatActivity {

    private static final String TAG = "DBInf";
    private static final String STATUS = "status";
    private static final String SUBSCRIPTIONS = "подписки";

    private TextView subsText;
    private DBHelper dbHelper;
    private boolean isSubscription;
    private FragmentManager manager;

    private ArrayList<User> userList;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribers);

        manager = getSupportFragmentManager();

        subsText = findViewById(R.id.subsCountSubscribersText);

        recyclerView = findViewById(R.id.resultsSubscribersRecycleView);
        userList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        dbHelper = new DBHelper(this);
        String status = getIntent().getStringExtra(STATUS);

        isSubscription = status.equals(SUBSCRIPTIONS);

    }

    @Override
    protected void onResume() {
        super.onResume();
        userList.clear();
        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerSubscribersLayout);
        panelBuilder.buildHeader(manager, "Подписки", R.id.headerSubscribersLayout);

        if(isSubscription) {
            updateSubscriptionText();
            getMySubscriptions();
        }
        /*else {
            updateSubscribersText();
            getMySubscribers();
        }*/
    }



    private Cursor createSubscriberCursor() {
        String userId = getUserId();

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String sqlQuery =
                "SELECT " + DBHelper.KEY_USER_ID+ ", "
                        + DBHelper.KEY_NAME_USERS
                        + " FROM " + DBHelper.TABLE_CONTACTS_USERS + ", "
                        + DBHelper.TABLE_SUBSCRIBERS
                        + " WHERE "
                        + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID
                        + " = "
                        + DBHelper.KEY_USER_ID
                        + " AND "
                        + DBHelper.TABLE_SUBSCRIBERS + "." + DBHelper.KEY_WORKER_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[] {userId});
        return cursor;
    }
    //мои подписчики (в разработке)
    private void getMySubscribers() {
        Cursor subsCursor = createSubscriberCursor();
        if(subsCursor.moveToFirst()) {
            int indexSubId = subsCursor.getColumnIndex(DBHelper.KEY_USER_ID);
            int indexSubName = subsCursor.getColumnIndex(DBHelper.KEY_NAME_USERS);

            do {
                String subId = subsCursor.getString(indexSubId);
                String subName = subsCursor.getString(indexSubName);

            } while (subsCursor.moveToNext());
        }
    }

    private void updateSubscribersText() {
        Cursor subsCursor = createSubscriberCursor();
        long subsCount = subsCursor.getCount();
        String subs = "У вас пока нет подписок";
        if (subsCount != 0) {
            subs = "Подписки: " + subsCount;
        }
        subsText.setText(subs);
    }

    private void updateSubscriptionText() {
        Cursor subsCursor = createSubscriptionCursor();
        long subsCount = subsCursor.getCount();
        String subs = "У вас пока нет подписок";
        if (subsCount != 0) {
            subs = "Подписки: " + subsCount;
        }
        subsText.setText(subs);
    }

    private Cursor createSubscriptionCursor() {
        String userId = getUserId();

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String sqlQuery =
                "SELECT " + DBHelper.KEY_WORKER_ID + ", "
                        + DBHelper.KEY_NAME_USERS
                        + " FROM " + DBHelper.TABLE_CONTACTS_USERS + ", "
                        + DBHelper.TABLE_SUBSCRIBERS
                        + " WHERE "
                        + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID
                        + " = "
                         + DBHelper.KEY_WORKER_ID
                        + " AND "
                        + DBHelper.TABLE_SUBSCRIBERS + "." + DBHelper.KEY_USER_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[] {userId});
        return cursor;
    }

    private void getMySubscriptions() {
        Cursor subsCursor = createSubscriptionCursor();

        if(subsCursor.moveToFirst()) {
            int indexWorkerId = subsCursor.getColumnIndex(DBHelper.KEY_WORKER_ID);
            int indexWorkerName = subsCursor.getColumnIndex(DBHelper.KEY_NAME_USERS);

           do {
               User user = new User();
               user.setId(subsCursor.getString(indexWorkerId));
               user.setName(subsCursor.getString(indexWorkerName));
               userList.add(user);
           } while (subsCursor.moveToNext());
        }
        SubscriptionAdapter subscribersAdapter = new SubscriptionAdapter(userList.size(), userList);
        //опускаемся к полседнему элементу
        recyclerView.setAdapter(subscribersAdapter);
    }

    private  String getUserId(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}
