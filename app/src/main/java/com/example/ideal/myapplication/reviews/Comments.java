package com.example.ideal.myapplication.reviews;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.objects.Comment;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Comments extends AppCompatActivity {

    private static final String TAG = "DBInf";

    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String REVIEW_FOR_USER = "review for user";
    private static final String REVIEW_FOR_SERVICE = "review for service";
    private static final String USERS = "users";
    private static final String ORDER_ID = "order_id";
    private static final String OWNER_ID = "owner_id";
    private static final String NAME = "name";

    private DBHelper dbHelper;
    private FragmentManager manager;
    private WorkWithLocalStorageApi workWithLocalStorageApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments);

        dbHelper = new DBHelper(this);
        manager = getSupportFragmentManager();

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        workWithLocalStorageApi = new WorkWithLocalStorageApi(database);
        loadComments();
    }

    private void loadComments() {
        String type = getIntent().getStringExtra(TYPE);
        String id = getIntent().getStringExtra(ID);

        if (type.equals(REVIEW_FOR_USER)) {
            loadCommentsForUser(id);
        }

        if (type.equals(REVIEW_FOR_SERVICE)) {
            loadCommentsForService(id);
        }
    }

    private void loadCommentsForService(String _serviceId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String mainSqlQuery = "SELECT "
                + DBHelper.KEY_REVIEW_REVIEWS + ", "
                + DBHelper.KEY_RATING_REVIEWS + ", "
                + DBHelper.KEY_NAME_SERVICES + ", "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + ", "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + " AS " + OWNER_ID + ", "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID + " AS " + ORDER_ID
                + " FROM "
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_REVIEWS + ", "
                + DBHelper.TABLE_WORKING_DAYS + ", "
                + DBHelper.TABLE_CONTACTS_USERS + ", "
                + DBHelper.TABLE_ORDERS + ", "
                + DBHelper.TABLE_CONTACTS_SERVICES
                + " WHERE "
                + DBHelper.KEY_ORDER_ID_REVIEWS
                + " = "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID
                + " AND "
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
                + DBHelper.KEY_SERVICE_ID_WORKING_DAYS + " = ? "
                + " AND "
                + DBHelper.KEY_TYPE_REVIEWS + " = ? "
                + " AND "
                + DBHelper.KEY_RATING_REVIEWS + " != 0 ";

        Cursor cursor = database.rawQuery(mainSqlQuery, new String[]{_serviceId, REVIEW_FOR_SERVICE});

        if (cursor.moveToFirst()) {
            int indexWorkingTimeId = cursor.getColumnIndex(DBHelper.KEY_ID);
            int indexOrderId = cursor.getColumnIndex(ORDER_ID);
            do {
                String workingTimeId = cursor.getString(indexWorkingTimeId);
                String orderId = cursor.getString(indexOrderId);

                if (workWithLocalStorageApi.isMutualReview(orderId)) {
                    createServiceComment(cursor);
                } else {
                    if (workWithLocalStorageApi.isAfterThreeDays(workingTimeId)) {
                        createServiceComment(cursor);
                    }
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void createServiceComment(final Cursor mainCursor) {

        final int reviewIndex = mainCursor.getColumnIndex(DBHelper.KEY_REVIEW_REVIEWS);
        final int ratingIndex = mainCursor.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);
        final int ownerIdIndex = mainCursor.getColumnIndex(OWNER_ID);
        final int nameServiceIndex = mainCursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);

        do {
            String ownerId = mainCursor.getString(ownerIdIndex);

            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(USERS)
                    .child(ownerId);
            final Comment comment = new Comment();
            comment.setUserId(mainCursor.getString(ownerIdIndex));
            comment.setReview(mainCursor.getString(reviewIndex));
            comment.setRating(mainCursor.getFloat(ratingIndex));
            comment.setServiceName(mainCursor.getString(nameServiceIndex));

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                    comment.setUserName(String.valueOf(userSnapshot.child(NAME).getValue()));
                    addCommentToScreen(comment);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } while (mainCursor.moveToNext());
    }


    private void createUserComment(final Cursor mainCursor) {
        final int reviewIndex = mainCursor.getColumnIndex(DBHelper.KEY_REVIEW_REVIEWS);
        final int ratingIndex = mainCursor.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);
        final int ownerIdIndex = mainCursor.getColumnIndex(OWNER_ID);
        do {
            String ownerId = mainCursor.getString(ownerIdIndex);

            DatabaseReference myRef = FirebaseDatabase
                    .getInstance()
                    .getReference(USERS)
                    .child(ownerId);
            final Comment comment = new Comment();
            comment.setUserId(mainCursor.getString(ownerIdIndex));
            comment.setReview(mainCursor.getString(reviewIndex));
            comment.setRating(mainCursor.getFloat(ratingIndex));
            comment.setServiceName(REVIEW_FOR_USER);

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                    comment.setUserName(String.valueOf(userSnapshot.child(NAME).getValue()));
                    addCommentToScreen(comment);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } while (mainCursor.moveToNext());
    }

    private void loadCommentsForUser(String _userId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String mainSqlQuery = "SELECT "
                + DBHelper.KEY_RATING_REVIEWS + ", "
                + DBHelper.KEY_REVIEW_REVIEWS + ", "
                + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_NAME_USERS + ", "
                + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID + " AS " + OWNER_ID + ", "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + ", "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + ", "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID + " AS " + ORDER_ID
                + " FROM "
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_REVIEWS + ", "
                + DBHelper.TABLE_WORKING_DAYS + ", "
                + DBHelper.TABLE_CONTACTS_USERS + ", "
                + DBHelper.TABLE_ORDERS + ", "
                + DBHelper.TABLE_CONTACTS_SERVICES
                + " WHERE "
                + DBHelper.KEY_ORDER_ID_REVIEWS
                + " = "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID
                + " AND "
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
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + " = ? "
                + " AND "
                + DBHelper.KEY_TYPE_REVIEWS + " = ? "
                + " AND "
                + DBHelper.KEY_RATING_REVIEWS + " != 0";
        // убрать не оценненые
        final Cursor cursor = database.rawQuery(mainSqlQuery, new String[]{_userId, REVIEW_FOR_USER});
        if (cursor.moveToFirst()) {
            int indexWorkingTimeId = cursor.getColumnIndex(DBHelper.KEY_ID);
            int indexOrderId = cursor.getColumnIndex(ORDER_ID);
            do {

                String workingTimeId = cursor.getString(indexWorkingTimeId);
                String orderId = cursor.getString(indexOrderId);

                if (workWithLocalStorageApi.isMutualReview(orderId)) {
                    createUserComment(cursor);
                } else {
                    if (workWithLocalStorageApi.isAfterThreeDays(workingTimeId)) {
                        createUserComment(cursor);
                    }
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerCommentsLayout);
        panelBuilder.buildHeader(manager, "Отзывы", R.id.headerCommentsLayout);
    }

    private void addCommentToScreen(Comment comment) {
        CommentElement cElement = new CommentElement(comment);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.mainCommentsLayout, cElement);
        transaction.commit();
    }
}
