package com.example.ideal.myapplication.adapters.chatElements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.chat.Messages;
import com.example.ideal.myapplication.fragments.objects.Dialog;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.other.DBHelper;

public class DialogElement implements View.OnClickListener {

    private static final String USER_ID = "user id";

    private TextView nameText;
    private ImageView avatarImage;

    private String userName;
    private String userId;
    private static final String TAG = "DBInf";
    private Context context;

    public DialogElement(Dialog dialog, View view, Context context, DBHelper dbHelper) {
        userName = dialog.getUserName();
        userId= dialog.getUserId();
        this.context = context;
        onViewCreated(view);
    }

    public void onViewCreated(@NonNull View view) {
        nameText = view.findViewById(R.id.nameDialogElementText);
        avatarImage = view.findViewById(R.id.avatarDialogElementImage);
        LinearLayout layout = view.findViewById(R.id.dialogElementLayout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 20, 10, 10);
        layout.setLayoutParams(params);
        layout.setOnClickListener(this);

        setData();
    }

    private void setData() {
        nameText.setText(userName);
        DBHelper dbHelper = new DBHelper(context);

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);
        int width = context.getResources().getDimensionPixelSize(R.dimen.photo_avatar_width);
        int height = context.getResources().getDimensionPixelSize(R.dimen.photo_avatar_height);
        workWithLocalStorageApi.setPhotoAvatar(userId,avatarImage,width,height);
    }

    @Override
    public void onClick(View v) {

        goToDialog();
    }

    private void goToDialog(){
        Intent intent = new Intent(context, Messages.class);
        intent.putExtra(USER_ID, userId);
        context.startActivity(intent);
    }
}