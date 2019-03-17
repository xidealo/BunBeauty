package com.example.ideal.myapplication.reviews;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.other.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PickedComment extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DBInf";

    private static final String USER_ID = "user id";
    private static final String USER_NAME = "user name";
    private static final String REVIEW = "review";
    private static final String RATING = "rating";

    private static final String CITY = "city";
    private static final String NAME = "name";
    private static final String USERS = "users";

    private static final String OWNER_ID = "owner id";

    private TextView userNameText;
    private TextView reviewText;
    private RatingBar ratingBar;

    private ImageView avatarImage;
    private String ownerId;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picked_comment);
        init();
        setInfo();
    }

    private  void init(){
        userNameText = findViewById(R.id.namePickedCommentText);
        reviewText = findViewById(R.id.reviewPickedCommentText);
        ratingBar = findViewById(R.id.ratingPickedCommentBar);

        avatarImage = findViewById(R.id.avatarPickedCommentImage);

        userNameText.setOnClickListener(this);
        avatarImage.setOnClickListener(this);
        dbHelper = new DBHelper(this);
        ownerId = getIntent().getStringExtra(USER_ID);

        loadOwnerAndAddToScreen(ownerId);
    }
    private void setInfo() {

        String userName = getIntent().getStringExtra(USER_NAME);
        String review = getIntent().getStringExtra(REVIEW);
        float rating = getIntent().getFloatExtra(RATING, 0);

        userNameText.setText(userName);
        reviewText.setText(review);
        ratingBar.setRating(rating);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        workWithLocalStorageApi.setPhotoAvatar(ownerId,avatarImage);
    }


    @Override
    public void onClick(View v) {
        goToProfile();
    }

    private void goToProfile() {
        String ownerId = getIntent().getStringExtra(USER_ID);

        Intent intent = new Intent(this, Profile.class);
        intent.putExtra(OWNER_ID, ownerId);

        startActivity(intent);
    }

    private void loadOwnerAndAddToScreen(String userId) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(USERS)
                .child(userId);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot user) {
                User localUser = new User();
                localUser.setPhone(ownerId);
                localUser.setName(String.valueOf(user.child(NAME).getValue()));
                localUser.setCity(String.valueOf(user.child(CITY).getValue()));
                addUserInLocalStorage(localUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addUserInLocalStorage(User localUser) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_NAME_USERS, localUser.getName());
        contentValues.put(DBHelper.KEY_CITY_USERS, localUser.getCity());

        database.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                DBHelper.KEY_USER_ID + " = ?",
                new String[]{String.valueOf(localUser.getPhone())});
    }
}
