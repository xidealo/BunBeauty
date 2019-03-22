package com.example.ideal.myapplication.reviews;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.objects.Comment;
import com.example.ideal.myapplication.fragments.objects.Photo;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Comments extends AppCompatActivity {

    private static final String TAG = "DBInf";

    private static final String ID = "id";
    private static final String TYPE = "type";
    private static final String REVIEW_FOR_USER = "review for user";
    private static final String REVIEW_FOR_SERVICE = "review for service";

    private static final String PHOTOS = "photos";
    private static final String PHOTO_LINK = "photo link";

    private static final String ORDER_ID = "order_id";
    private static final String OWNER_ID = "owner_id";

    private static final String OWNER_ID_FB = "owner id";

    private  DBHelper dbHelper;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments);

        dbHelper = new DBHelper(this);
        manager = getSupportFragmentManager();

        PanelBuilder panelBuilder = new PanelBuilder();
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

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        String mainSqlQuery = "SELECT "
                + DBHelper.KEY_REVIEW_REVIEWS + ", "
                + DBHelper.KEY_RATING_REVIEWS + ", "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + ", "
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
                + DBHelper.KEY_RATING_REVIEWS + " != 0"
                + " AND "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                + " = " + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                + " AND "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID
                + " = " + DBHelper.KEY_SERVICE_ID_WORKING_DAYS;


        Cursor mainCursor = database.rawQuery(mainSqlQuery, new String[]{_serviceId, REVIEW_FOR_SERVICE});

        if(mainCursor.moveToFirst()) {
            int indexWorkingTimeId = mainCursor.getColumnIndex(DBHelper.KEY_ID);
            do {
                if(workWithLocalStorageApi.isMutualReview(mainCursor.getString(indexWorkingTimeId))) {
                    createServiceComment(mainCursor);
                }
                else {
                    if(workWithLocalStorageApi.isAfterWeek(mainCursor.getString(indexWorkingTimeId))){
                        createServiceComment(mainCursor);
                    }
                }

            } while (mainCursor.moveToNext());
        }
        mainCursor.close();
    }

    private void createServiceComment(Cursor mainCursor){

    }

    private void loadCommentsForUser(String _userId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);
        Log.d(TAG, "loadCommentsForUser: ");
        String sqlQuery = "SELECT "
                + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_USER_ID + ", "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + ", "
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
                + DBHelper.KEY_RATING_REVIEWS + " != 0"
                + " AND "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                + " = " + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                + " AND "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID
                + " = " + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                + " AND "
                + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_USER_ID
                + " = " + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID;

        // убрать не оценненые
        final Cursor cursor = database.rawQuery(sqlQuery, new String[]{_userId, REVIEW_FOR_USER});

        if(cursor.moveToFirst()) {
            Log.d(TAG, "IN CURSOR");
            int indexWorkingTimeId = cursor.getColumnIndex(DBHelper.KEY_ID);
            do {
                Log.d(TAG, "loadCommentsForUser: " + cursor.getString(indexWorkingTimeId));
                if (workWithLocalStorageApi.isMutualReview(cursor.getString(indexWorkingTimeId))) {
                 createUserComment(cursor);
                    Log.d(TAG, "loadCommentsForUser: ");
                }
                else {
                    Log.d(TAG, "loadCommentsForUser: ");
                    if(workWithLocalStorageApi.isAfterWeek(cursor.getString(indexWorkingTimeId))){
                        createUserComment(cursor);
                        Log.d(TAG, "loadCommentsForUser: ");
                    }
                }
            }
            while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void createUserComment(Cursor cursor){
        Log.d(TAG, "createUserComment: ");
        int userIdIndex = cursor.getColumnIndex(DBHelper.KEY_USER_ID);
        int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME_USERS);
        int reviewIndex = cursor.getColumnIndex(DBHelper.KEY_REVIEW_REVIEWS);
        int ratingIndex = cursor.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);

        String userId = cursor.getString(userIdIndex);
        String name = cursor.getString(nameIndex);
        String review = cursor.getString(reviewIndex);
        float rating = Float.valueOf(cursor.getString(ratingIndex));

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setUserName(name);
        comment.setReview(review);
        comment.setRating(rating);

        loadPhotosByPhoneNumber(comment);
    }

    private void loadPhotosByPhoneNumber(final Comment comment) {

        Query photosQuery = FirebaseDatabase.getInstance().getReference(PHOTOS)
                .orderByChild(OWNER_ID_FB)
                .equalTo(comment.getUserId());
        photosQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot photosSnapshot) {

                for(DataSnapshot fPhoto: photosSnapshot.getChildren()){

                    Photo photo = new Photo();

                    photo.setPhotoId(fPhoto.getKey());
                    photo.setPhotoLink(String.valueOf(fPhoto.child(PHOTO_LINK).getValue()));
                    photo.setPhotoOwnerId(String.valueOf(fPhoto.child(OWNER_ID_FB).getValue()));
                    addPhotoInLocalStorage(photo,comment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void addPhotoInLocalStorage(Photo photo, Comment comment) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.KEY_ID, photo.getPhotoId());
        contentValues.put(DBHelper.KEY_PHOTO_LINK_PHOTOS, photo.getPhotoLink());
        contentValues.put(DBHelper.KEY_OWNER_ID_PHOTOS,photo.getPhotoOwnerId());

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);
        boolean isUpdate = workWithLocalStorageApi
                .hasSomeData(DBHelper.TABLE_PHOTOS,
                        photo.getPhotoId());

        if(isUpdate){
            database.update(DBHelper.TABLE_PHOTOS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{photo.getPhotoId()});
        }
        else {
            contentValues.put(DBHelper.KEY_ID, photo.getPhotoId());
            database.insert(DBHelper.TABLE_PHOTOS, null, contentValues);
        }

        addCommentToScreen(comment);
    }

    private void addCommentToScreen(Comment comment) {
        CommentElement cElement = new CommentElement(comment);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.mainCommentsLayout, cElement);
        transaction.commit();
    }
}
