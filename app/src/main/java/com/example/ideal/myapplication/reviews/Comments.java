package com.example.ideal.myapplication.reviews;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.objects.Comment;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.other.DBHelper;

public class Comments extends AppCompatActivity {

    private static final String TAG = "DBInf";

    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String REVIEW_FOR_USER = "review for user";
    private static final String REVIEW_FOR_SERVICE = "review for service";

    private static final String ORDER_ID = "order_id";
    private static final String OWNER_ID = "owner_id";

    private LinearLayout mainLayout;

    private  DBHelper dbHelper;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments);

        mainLayout = findViewById(R.id.mainCommentsLayout);

        dbHelper = new DBHelper(this);
        manager = getSupportFragmentManager();

        PanelBuilder panelBuilder = new PanelBuilder(this);
        panelBuilder.buildFooter(manager, R.id.footerCommentsLayout);
        panelBuilder.buildHeader(manager, "Отзывы", R.id.headerCommentsLayout);

        loadComments();
    }

    private void loadComments() {
        String type = getIntent().getStringExtra(TYPE);
        String id = getIntent().getStringExtra(ID);

        if(type.equals(REVIEW_FOR_USER)) {
            loadCommentsForUser(id);
        }

        if(type.equals(REVIEW_FOR_SERVICE)) {
            loadCommentsForService(id);
        }
    }

    private void loadCommentsForService(String _serviceId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String mainSqlQuery = "SELECT "
                + DBHelper.KEY_REVIEW_REVIEWS + ", "
                + DBHelper.KEY_RATING_REVIEWS + ", "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_USER_ID + " AS " + ORDER_ID + ", "
                + DBHelper.KEY_MESSAGE_ID_REVIEWS + ", "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID + " AS " + OWNER_ID
                + " FROM "
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_REVIEWS + ", "
                + DBHelper.TABLE_WORKING_DAYS + ", "
                + DBHelper.TABLE_CONTACTS_SERVICES
                + " WHERE "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + " = ?"
                + " AND "
                + DBHelper.KEY_TYPE_REVIEWS + " = ?"
                + " AND "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " = " + DBHelper.KEY_WORKING_TIME_ID_REVIEWS
                + " AND "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                + " = " + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                + " AND "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID
                + " = " + DBHelper.KEY_SERVICE_ID_WORKING_DAYS;

        String chatSqlQuery = "SELECT "
                + DBHelper.KEY_FIRST_USER_ID_DIALOGS + ", "
                + DBHelper.KEY_SECOND_USER_ID_DIALOGS
                + " FROM "
                + DBHelper.TABLE_MESSAGES + ", "
                + DBHelper.TABLE_DIALOGS
                + " WHERE "
                + DBHelper.TABLE_MESSAGES + "." + DBHelper.KEY_ID + " = ?"
                + " AND "
                + DBHelper.TABLE_DIALOGS + "." + DBHelper.KEY_ID
                + " = " + DBHelper.KEY_DIALOG_ID_MESSAGES;

        String userSqlQuery = "SELECT "
                + DBHelper.KEY_NAME_USERS
                + " FROM "
                + DBHelper.TABLE_CONTACTS_USERS
                + " WHERE "
                + DBHelper.KEY_USER_ID + " = ?";

        Cursor mainCursor = database.rawQuery(mainSqlQuery, new String[]{_serviceId, REVIEW_FOR_SERVICE});

        if(mainCursor.moveToFirst()) {
            int reviewIndex = mainCursor.getColumnIndex(DBHelper.KEY_REVIEW_REVIEWS);
            int ratingIndex = mainCursor.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);
            int orderIdIndex = mainCursor.getColumnIndex(ORDER_ID);
            int messageIdIndex = mainCursor.getColumnIndex(DBHelper.KEY_MESSAGE_ID_REVIEWS);
            int ownerIdIndex = mainCursor.getColumnIndex(OWNER_ID);

            do {
                String name = "";
                String orderId = mainCursor.getString(orderIdIndex);
                String review = mainCursor.getString(reviewIndex);
                float rating = Float.valueOf(mainCursor.getString(ratingIndex));

                if(orderId.equals("0")) {
                    String messageId = mainCursor.getString(messageIdIndex);
                    String ownerId = mainCursor.getString(ownerIdIndex);

                    Log.d(TAG, "loadCommentsForService: " + messageId);
                    Cursor chatCursor = database.rawQuery(chatSqlQuery, new String[]{messageId});

                    if(chatCursor.moveToFirst()) {
                        String firstPhone = chatCursor.getString(
                                chatCursor.getColumnIndex(DBHelper.KEY_FIRST_USER_ID_DIALOGS));
                        String secondPhone = chatCursor.getString(
                                chatCursor.getColumnIndex(DBHelper.KEY_SECOND_USER_ID_DIALOGS));

                        Cursor userCursor;
                        if(firstPhone != ownerId) {
                            orderId = firstPhone;
                            userCursor = database.rawQuery(userSqlQuery, new String[]{firstPhone});
                        } else {
                            orderId = secondPhone;
                            userCursor = database.rawQuery(userSqlQuery, new String[]{secondPhone});
                        }

                        if(userCursor.moveToFirst()) {
                            name = userCursor.getString(userCursor.getColumnIndex(DBHelper.KEY_NAME_USERS));
                        }
                    }
                } else {
                    Cursor userCursor = database.rawQuery(userSqlQuery, new String[]{orderId});

                    if(userCursor.moveToFirst()) {
                        name = userCursor.getString(userCursor.getColumnIndex(DBHelper.KEY_NAME_USERS));
                    }
                }

                Comment comment = new Comment();
                comment.setUserId(orderId);
                comment.setUserName(name);
                comment.setReview(review);
                comment.setRating(rating);

                addCommentToScreen(comment);
            } while (mainCursor.moveToNext());
        }
    }

    private void loadCommentsForUser(String _userId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String sqlQuery = "SELECT "
                + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_USER_ID + ", "
                + DBHelper.KEY_NAME_USERS + ", "
                + DBHelper.KEY_REVIEW_REVIEWS + ", "
                + DBHelper.KEY_RATING_REVIEWS
                + " FROM "
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_REVIEWS + ", "
                + DBHelper.TABLE_WORKING_DAYS + ", "
                + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                + DBHelper.TABLE_CONTACTS_USERS
                + " WHERE "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_USER_ID + " = ?"
                + " AND "
                + DBHelper.KEY_TYPE_REVIEWS + " = ?"
                + " AND "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " = " + DBHelper.KEY_WORKING_TIME_ID_REVIEWS
                + " AND "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                + " = " + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                + " AND "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID
                + " = " + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                + " AND "
                + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_USER_ID
                + " = " + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID;

        final Cursor cursor = database.rawQuery(sqlQuery, new String[]{_userId, REVIEW_FOR_USER});

        if(cursor.moveToFirst()) {
            int userIdIndex = cursor.getColumnIndex(DBHelper.KEY_USER_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME_USERS);
            int reviewIndex = cursor.getColumnIndex(DBHelper.KEY_REVIEW_REVIEWS);
            int ratingIndex = cursor.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);

            do {
                String userId = cursor.getString(userIdIndex);
                String name = cursor.getString(nameIndex);
                String review = cursor.getString(reviewIndex);
                float rating = Float.valueOf(cursor.getString(ratingIndex));

                Comment comment = new Comment();
                comment.setUserId(userId);
                comment.setUserName(name);
                comment.setReview(review);
                comment.setRating(rating);

                addCommentToScreen(comment);
            } while (cursor.moveToNext());
        }
    }

    private void addCommentToScreen(Comment comment) {
        CommentElement cElement = new CommentElement(comment);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.mainCommentsLayout, cElement);
        transaction.commit();
    }
}
