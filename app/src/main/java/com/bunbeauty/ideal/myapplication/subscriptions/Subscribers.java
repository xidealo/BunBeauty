package com.bunbeauty.ideal.myapplication.subscriptions;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.adapters.SubscriptionAdapter;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.FBListener;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User;
import com.bunbeauty.ideal.myapplication.helpApi.ListeningManager;
import com.bunbeauty.ideal.myapplication.helpApi.LoadingUserElementData;
import com.bunbeauty.ideal.myapplication.helpApi.PanelBuilder;
import com.bunbeauty.ideal.myapplication.helpApi.SubscriptionsApi;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.DBHelper;
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
    private static final String STATUS = "status";
    private static final String SUBSCRIPTIONS = "subscriptions";
    private static final String USERS = "users";
    private static final String WORKER_ID = "worker id";

    private TextView subsText;
    private DBHelper dbHelper;
    private FragmentManager manager;
    private long countOfLoadedUser = 0;

    private ArrayList<User> userList;
    private RecyclerView recyclerView;
    private SQLiteDatabase database;
    private ProgressBar progressBar;
    public static boolean isFirst = true;

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

        String status = getIntent().getStringExtra(STATUS);
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
        if (SubscriptionsApi.getCountOfSubscriptions(database, getUserId()) == 0) {
            setWithoutSubscriptions();
        }

        //повесить другой листенер, который срабатывает на полседнего, который только добавился
        ChildEventListener userListener = userRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot userSnapshot, @Nullable String s) {
                String id = userSnapshot.getKey();
                String workerId = String.valueOf(userSnapshot.child(WORKER_ID).getValue());
                loadUserById(workerId);
                addUserSubscriptionInLocalStorage(id, workerId);
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
                SubscriptionsApi.updateLocalCountOfSubs(countOfLoadedUser, getUserId(), database);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot userSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ListeningManager.addToListenerList(new FBListener(userRef, userListener));
    }

    private void setWithoutSubscriptions() {
        subsText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
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
                long currentCountOfSub = SubscriptionsApi.getCountOfSubscriptions(database, getUserId());
                if (countOfLoadedUser >= currentCountOfSub) {
                    if (countOfLoadedUser != currentCountOfSub) {
                        SubscriptionsApi.updateLocalCountOfSubs(countOfLoadedUser, getUserId(), database);
                    }
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
        userList.clear();
        if (SubscriptionsApi.getCountOfSubscriptions(database, getUserId()) == 0) {
            setWithoutSubscriptions();
            return;
        }

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
        //опускаемся к полседнему элементу
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
        subsText.setText(subs);
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
