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

public class SubscribtionsApi {

    private static final String TAG = "DBInf";

    private static final String USER_ID = "user id";
    private static final String WORKER_ID = "worker id";

    private static final String SUBSCRIBERS = "subscribers";

    private String userId;
    private String workerId;
    private DBHelper dbHelper;

    public SubscribtionsApi(String _workerId, Context context){
        userId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        workerId = _workerId;

        dbHelper = new DBHelper(context);
    }

    public void subscribe() {
        FirebaseDatabase fdatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = fdatabase.getReference(SUBSCRIBERS);

        Map<String,Object> items = new HashMap<>();
        items.put(USER_ID, userId);
        items.put(WORKER_ID, workerId);

        String subscriberId =  myRef.push().getKey();
        if (subscriberId != null) {
            myRef = myRef.child(subscriberId);
            myRef.updateChildren(items);
        }

        addSubscriberInLocalStorage(subscriberId);
    }

    public void unsubscribe() {
        FirebaseDatabase fdatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = fdatabase.getReference(SUBSCRIBERS);
        Query query = fdatabase.getReference(SUBSCRIBERS).orderByChild(USER_ID).equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot subscriberSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, String.valueOf(subscriberSnapshot.child(WORKER_ID).getValue().toString().equals(workerId)));
                    String workerId = String.valueOf(subscriberSnapshot.child(WORKER_ID).getValue());
                    if(workerId.equals(workerId)) {
                        String subscriberId = subscriberSnapshot.getKey();
                        Log.d(TAG, subscriberId);
                        if (subscriberId != null) {
                            myRef.child(subscriberId).removeValue();
                            deleteSubscriberInLocalStorage();
                        }

                        return;
                    }
                }
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
        Query query = fdatabase.getReference(SUBSCRIBERS).orderByChild(WORKER_ID).equalTo(workerId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String countOfSubs = String.valueOf(dataSnapshot.getChildrenCount());
                countOfSubsText.setText(countOfSubs);
                countOfSubsText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
