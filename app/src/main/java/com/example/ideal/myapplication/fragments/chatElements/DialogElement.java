package com.example.ideal.myapplication.fragments.chatElements;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.chat.Messages;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.other.DBHelper;

@SuppressLint("ValidFragment")
public class DialogElement extends Fragment implements View.OnClickListener {

    private TextView nameText;
    private ImageView avatarImage;

    private String userName;
    private String userId;

    private static final String TAG = "DBInf";

    @SuppressLint("ValidFragment")
    public DialogElement(User user) {
        userName = user.getName();
        userId= user.getId();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_element, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        nameText = view.findViewById(R.id.nameDialogElementText);
        avatarImage = view.findViewById(R.id.avatarDialogElementImage);
        nameText.setOnClickListener(this);
        setData();
    }

    private void setData() {
        nameText.setText(userName);

        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        WorkWithLocalStorageApi workWithLocalStorageApi = new WorkWithLocalStorageApi(database);

        workWithLocalStorageApi.setPhotoAvatar(userId,avatarImage);
    }

    @Override
    public void onClick(View v) {
        goToDialog();
    }

    private void goToDialog(){
        Intent intent = new Intent(this.getContext(), Messages.class);
        startActivity(intent);
    }
}