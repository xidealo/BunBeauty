package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.WorkWithLocalStorageApi;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.WorkWithStringsApi;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.DBHelper;
import com.bunbeauty.ideal.myapplication.reviews.PickedComment;

public class CommentElement implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private static final String USER_ID = "user id";
    private static final String USER_NAME = "user name";
    private static final String REVIEW = "creation_comment";
    private static final String RATING = "rating";

    private TextView reviewText;
    private TextView userNameText;
    private ImageView avatarImage;
    private RatingBar ratingBar;

    private String userId;
    private String userName;
    private String review;
    private Context context;
    private View view;
    private float rating;

    CommentElement(UserComment userComment, View view, Context context) {
        this.context = context;
        this.view= view;
    }

    public void createElement(){
        onViewCreated(view);
    }

    private void onViewCreated(@NonNull View view) {

        userNameText = view.findViewById(R.id.nameCommentElementText);
        reviewText = view.findViewById(R.id.reviewCommentElementText);
        ratingBar = view.findViewById(R.id.ratingCommentElementBar);
        avatarImage = view.findViewById(R.id.avatarCommentElementImage);

        LinearLayout layout = view.findViewById(R.id.commentElementLayout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        layout.setLayoutParams(params);

        layout.setOnClickListener(this);
        setData();
    }

    private void setData() {
        String abbreviatedReview;
        abbreviatedReview = WorkWithStringsApi.cutString(review,25);

        userNameText.setText(WorkWithStringsApi.cutString(WorkWithStringsApi.doubleCapitalSymbols(userName),18));
        reviewText.setText(abbreviatedReview);
        ratingBar.setRating(rating);

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        int width = context.getResources().getDimensionPixelSize(R.dimen.photo_avatar_width);
        int height = context.getResources().getDimensionPixelSize(R.dimen.photo_avatar_height);
        workWithLocalStorageApi.setPhotoAvatar(userId, avatarImage, width, height);
    }

    @Override
    public void onClick(View v) {
        goToThisComment();
    }

    private void goToThisComment() {
        Intent intent = new Intent(context, PickedComment.class);
        intent.putExtra(USER_ID, userId);
        intent.putExtra(USER_NAME, userName);
        intent.putExtra(REVIEW, review);
        intent.putExtra(RATING, rating);
        context.startActivity(intent);
    }
}
