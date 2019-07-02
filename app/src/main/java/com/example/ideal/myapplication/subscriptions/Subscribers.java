package com.example.ideal.myapplication.subscriptions;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.adapters.SubscriptionAdapter;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.LoadingUserElementData;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.SubscriptionsApi;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Subscribers extends AppCompatActivity {

    private static final String TAG = "DBInf";
    private static final String SUBSCRIPTIONS = "subscriptions";
    private static final String USERS = "users";
    private static final String WORKER_ID = "worker id";

    private TextView subsText;
    private DBHelper dbHelper;
    private FragmentManager manager;
    private long countOfLoadedUser;
    private long currentCountOfSub;

    private ArrayList<User> userList;
    private RecyclerView recyclerView;
    private SQLiteDatabase database;
    private ProgressBar progressBar;
    private static boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribers);

        manager = getSupportFragmentManager();
        subsText = findViewById(R.id.subsCountSubscribersText);
        progressBar = findViewById(R.id.progressBarSubscribers);

        recyclerView = findViewById(R.id.resultsSubscribersRecycleView);
        userList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();
        currentCountOfSub = SubscriptionsApi.getCountOfSubscriptions(database, getUserId());

        if (currentCountOfSub == 0) {
            subsText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        userList.clear();
        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerSubscribersLayout);
        panelBuilder.buildHeader(manager, "Подписки", R.id.headerSubscribersLayout);
        if (isFirst) {
            loadUserSubscriptions();
            isFirst = false;
        } else {
            getMySubscriptions();
        }

        /*else {
            updateSubscribersText();
            getMySubscribers();
        }*/
    }

    private void loadUserSubscriptions() {
        final String myUserId = getUserId();
        final DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference(USERS)
                .child(myUserId)
                .child(SUBSCRIPTIONS);

        //повесить другой листенер, который срабатывает на полседнего, который только добавился
        userRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot userSnapshot, @Nullable String s) {
                userList.clear();
                String id = userSnapshot.getKey();
                String workerId = String.valueOf(userSnapshot.child(WORKER_ID).getValue());
                addUserSubscriptionInLocalStorage(id, workerId);
                loadUserById(workerId);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot userSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot userSnapshot) {
                String id = userSnapshot.getKey();
                database.delete(
                        DBHelper.TABLE_SUBSCRIBERS,
                        DBHelper.KEY_ID + " = ?",
                        new String[]{id});
                countOfLoadedUser--;
                updateLocalCountOfSubs(countOfLoadedUser);
                updateSubscriptionText();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot userSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateLocalCountOfSubs(long subscriptionsCount) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_SUBSCRIPTIONS_COUNT_USERS, subscriptionsCount);

        database.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{getUserId()});
    }

    private void loadUserById(final String userId) {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        final DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference(USERS)
                .child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                //загрузка данных о пользователе
                LoadingUserElementData.loadUserNameAndPhoto(userSnapshot, database);
                countOfLoadedUser++;
                if (countOfLoadedUser >= currentCountOfSub) {
                    updateLocalCountOfSubs(countOfLoadedUser);
                    getMySubscriptions();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addUserSubscriptionInLocalStorage(String id, String workerId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ID, id);
        contentValues.put(DBHelper.KEY_USER_ID, getUserId());
        contentValues.put(DBHelper.KEY_WORKER_ID, workerId);

        boolean hasSomeData = WorkWithLocalStorageApi.hasSomeData(DBHelper.TABLE_SUBSCRIBERS, id);
        if (hasSomeData) {
            database.update(DBHelper.TABLE_SUBSCRIBERS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{id});
        } else {
            contentValues.put(DBHelper.KEY_ID, id);
            database.insert(DBHelper.TABLE_SUBSCRIBERS, null, contentValues);
        }
    }

    private Cursor createSubscriberCursor() {
        String userId = getUserId();

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String sqlQuery =
                "SELECT " + DBHelper.KEY_USER_ID + ", "
                        + DBHelper.KEY_NAME_USERS
                        + " FROM " + DBHelper.TABLE_CONTACTS_USERS + ", "
                        + DBHelper.TABLE_SUBSCRIBERS
                        + " WHERE "
                        + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID
                        + " = "
                        + DBHelper.KEY_USER_ID
                        + " AND "
                        + DBHelper.TABLE_SUBSCRIBERS + "." + DBHelper.KEY_WORKER_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{userId});
        return cursor;
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

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{userId});
        return cursor;
    }

    private void getMySubscriptions() {
        Cursor subsCursor = createSubscriptionCursor();
        //надпись сверху
        updateSubscriptionText();
        if (subsCursor.moveToFirst()) {
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
        recyclerView.setAdapter(subscribersAdapter);
        progressBar.setVisibility(View.GONE);
    }

    //мои подписчики (в разработке)
    private void getMySubscribers() {
        Cursor subsCursor = createSubscriberCursor();
        if (subsCursor.moveToFirst()) {
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
        long subsCount = SubscriptionsApi.getCountOfSubscriptions(database, getUserId());
        String subs = "У вас пока нет подписок";
        if (subsCount != 0) {
            subs = "Подписки: " + subsCount;
        }
        Log.d(TAG, "SUBS" + subs);
        subsText.setText(subs);
        subsText.setVisibility(View.VISIBLE);
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
