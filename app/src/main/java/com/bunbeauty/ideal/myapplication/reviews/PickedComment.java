package com.bunbeauty.ideal.myapplication.reviews;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.DBHelper;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.presentation.profile.ProfileActivity;
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

        userNameText.setText(WorkWithStringsApi.doubleCapitalSymbols(userName));
        reviewText.setText(review);
        ratingBar.setRating(rating);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        int width = getResources().getDimensionPixelSize(R.dimen.photo_avatar_width);
        int height = getResources().getDimensionPixelSize(R.dimen.photo_avatar_height);
        workWithLocalStorageApi.setPhotoAvatar(ownerId,avatarImage,width,height);    }


    @Override
    public void onClick(View v) {
        goToProfile();
    }

    private void loadOwnerAndAddToScreen(String userId) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(User.Companion.getUSERS())
                .child(userId);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot user) {
                User localUser = new User();
                localUser.setId(ownerId);
                localUser.setName(String.valueOf(user.child(User.Companion.getNAME()).getValue()));
                localUser.setCity(String.valueOf(user.child(User.Companion.getCITY()).getValue()));
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
                DBHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(localUser.getId())});
    }

    private void goToProfile() {
        String ownerId = getIntent().getStringExtra(USER_ID);

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(OWNER_ID, ownerId);

        startActivity(intent);
    }
}
