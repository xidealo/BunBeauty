package com.example.ideal.myapplication.helpApi;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SubscriptionsApi {

    private static final String TAG = "DBInf";

    private static final String WORKER_ID = "worker id";
    private static final String USERS = "users";
    private static final String USER_ID = "user id";

    private static final String SUBSCRIBERS = "subscribers";
    private static final String SUBSCRIPTIONS = "subscriptions";

    private String userId;
    private String workerId;
    private DBHelper dbHelper;
    private String subscriberId;

    public SubscriptionsApi(String _workerId, Context context){
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        workerId = _workerId;

        dbHelper = new DBHelper(context);
    }

    public void subscribe() {
        createSubscriber();
        createSubscription();
    }

    private void createSubscriber() {
        //мои подписки
        FirebaseDatabase fdatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = fdatabase.getReference(USERS)
                .child(userId)
                .child(SUBSCRIPTIONS);

        Map<String,Object> items = new HashMap<>();
        items.put(WORKER_ID, workerId);

        subscriberId =  myRef.push().getKey();
        if (subscriberId != null) {
            myRef = myRef.child(subscriberId);
            myRef.updateChildren(items);
        }

    }
    private void createSubscription() {
        //подписки воркера
        FirebaseDatabase fdatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = fdatabase.getReference(USERS)
                .child(workerId)
                .child(SUBSCRIBERS);

        Map<String,Object> items = new HashMap<>();
        items.put(USER_ID, userId);

        if (subscriberId != null) {
            myRef = myRef.child(subscriberId);
            myRef.updateChildren(items);
        }
        addSubscriberInLocalStorage(subscriberId);
    }

    public void unsubscribe() {
        Log.d(TAG, "unsubscribe: ");
       deleteSubscription();
       deleteSubscriber();
    }

    private void deleteSubscription() {
        final FirebaseDatabase fdatabase = FirebaseDatabase.getInstance();

        Query query = fdatabase.getReference(USERS)
                .child(userId)
                .child(SUBSCRIPTIONS)
                .orderByChild(WORKER_ID)
                .equalTo(workerId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot subscriptionsSnapshot) {
                //если 0 значит мы отписываем подписчика
                DataSnapshot subscriptionSnapshot = subscriptionsSnapshot.getChildren().iterator().next();

                    String subscriptionId = subscriptionSnapshot.getKey();

                    DatabaseReference myRef = fdatabase.getReference(USERS)
                            .child(userId)
                            .child(SUBSCRIPTIONS);

                    myRef.child(subscriptionId).removeValue();

                deleteSubscriberInLocalStorage();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void deleteSubscriber() {

        final FirebaseDatabase fdatabase = FirebaseDatabase.getInstance();

        Query query = fdatabase.getReference(USERS)
                .child(workerId)
                .child(SUBSCRIBERS)
                .orderByChild(USER_ID)
                .equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot subscribersSnapshot) {

                DataSnapshot subscriptionSnapshot = subscribersSnapshot.getChildren().iterator().next();

                String subId = subscriptionSnapshot.getKey();
                DatabaseReference myRef = fdatabase.getReference(USERS)
                        .child(workerId)
                        .child(SUBSCRIBERS);

                myRef.child(subId).removeValue();

                deleteSubscriberInLocalStorage();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void deleteSubscriberInLocalStorage() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.delete(
                DBHelper.TABLE_SUBSCRIBERS,
                DBHelper.KEY_USER_ID + " = ? AND "
                        + DBHelper.KEY_WORKER_ID + " = ?",
                new String[]{userId, workerId});
    }

    private void addSubscriberInLocalStorage(String subscriberId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_ID, subscriberId);

        contentValues.put(DBHelper.KEY_USER_ID, userId);

        contentValues.put(DBHelper.KEY_WORKER_ID, workerId);

        database.insert(DBHelper.TABLE_SUBSCRIBERS, null, contentValues);
    }

    public void loadCountOfSubscribers(final TextView countOfSubsText) {
        FirebaseDatabase fdatabase = FirebaseDatabase.getInstance();
        DatabaseReference subscribersRef = fdatabase.getReference(USERS)
                .child(workerId)
                .child(SUBSCRIBERS);
        subscribersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot subscribersSnapshot) {
                String countOfSubs = String.valueOf(subscribersSnapshot.getChildrenCount());
                countOfSubsText.setText("Подисчики " +countOfSubs);
                countOfSubsText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
