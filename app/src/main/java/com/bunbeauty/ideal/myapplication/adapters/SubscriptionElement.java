package com.bunbeauty.ideal.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User;
import com.bunbeauty.ideal.myapplication.helpApi.SubscriptionsApi;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.DBHelper;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.profile.ProfileActivity;

public class SubscriptionElement implements View.OnClickListener {

    private static final String OWNER_ID = "owner id";

    private TextView nameText;
    private TextView subscribeText;
    private TextView unsubscribeText;
    private ImageView avatarImage;
    private Context context;
    private View view;

    private String workerId;
    private String workerName;

    public SubscriptionElement(User user, View view, Context context) {
        workerId = user.getId();
        workerName = user.getName();
        this.context = context;
        this.view= view;
    }

    void createElement(){
        onViewCreated(view);
    }


    private void onViewCreated(@NonNull View view) {

        nameText = view.findViewById(R.id.workerNameSubscriptionElementText);
        subscribeText = view.findViewById(R.id.subscribeSubscriptionElementText);
        avatarImage = view.findViewById(R.id.avatarSubscriptionElementImage);
        unsubscribeText = view.findViewById(R.id.unsubscribeSubscriptionElementText);

        nameText.setOnClickListener(this);
        avatarImage.setOnClickListener(this);
        subscribeText.setOnClickListener(this);
        unsubscribeText.setOnClickListener(this);

        LinearLayout layout = view.findViewById(R.id.subscriptionElementLayout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        layout.setLayoutParams(params);

        setData();
    }

    private void setData() {
        nameText.setText(workerName);
        subscribeText.setTag(true);

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        int width = context.getResources().getDimensionPixelSize(R.dimen.photo_avatar_width);
        int height = context.getResources().getDimensionPixelSize(R.dimen.photo_avatar_height);
        workWithLocalStorageApi.setPhotoAvatar(workerId, avatarImage, width, height);
    }

    @Override
    public void onClick(View v) {
        SubscriptionsApi subsApi = new SubscriptionsApi(workerId, context);

        switch (v.getId()) {
            case R.id.subscribeSubscriptionElementText:
                unsubscribeText.setVisibility(View.VISIBLE);
                subscribeText.setVisibility(View.GONE);
                subsApi.unsubscribe();
                Toast.makeText(context, "Подписка отменена", Toast.LENGTH_SHORT).show();
                break;

            case R.id.unsubscribeSubscriptionElementText:
                unsubscribeText.setVisibility(View.GONE);
                subscribeText.setVisibility(View.VISIBLE);
                subsApi.subscribe();
                Toast.makeText(context, "Вы подписались", Toast.LENGTH_SHORT).show();
                break;

            default:
                goToProfile();
                break;
        }
    }

    private void goToProfile() {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(OWNER_ID, workerId);

        context.startActivity(intent);
    }
}