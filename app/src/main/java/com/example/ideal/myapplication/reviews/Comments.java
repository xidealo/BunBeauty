package com.example.ideal.myapplication.reviews;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.other.DBHelper;

public class Comments extends AppCompatActivity {

    private static final String TAG = "DBInf";

    private static final String ID = "id";
    private static final String TYPE = "type";

    private LinearLayout mainLayout;

    private  DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments);

        mainLayout = findViewById(R.id.mainCommentsLayout);

        dbHelper = new DBHelper(this);

        addCommentsToScreen();
    }

    private void addCommentsToScreen() {
        String type = getIntent().getStringExtra(TYPE);
        String id = getIntent().getStringExtra(ID);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String sqlQuery = "SELECT "
                + DBHelper.KEY_REVIEW_REVIEWS + ", "
                + DBHelper.KEY_NAME_USERS
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

        final Cursor cursor = database.rawQuery(sqlQuery, new String[]{id, type});

        if(cursor.moveToFirst()) {
            int reviewIndex = cursor.getColumnIndex(DBHelper.KEY_REVIEW_REVIEWS);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME_USERS);

            do {
                String comment = cursor.getString(reviewIndex) + " " + cursor.getString(nameIndex);
                Log.d(TAG, "addCommentsToScreen: " + comment);
            } while (cursor.moveToNext());

        }
    }
}
