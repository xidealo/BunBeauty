package com.example.ideal.myapplication.chat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.adapters.DialogAdapter;
import com.example.ideal.myapplication.fragments.objects.Dialog;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Dialogs extends AppCompatActivity {

    private static final String TAG = "DBInf";

    private static final String OWNER_ID = "owner_id";
    private static final String ORDER_ID = "order_id";

    private WorkWithLocalStorageApi LSApi;
    private DBHelper dbHelper;

    private FragmentManager manager;

    private ArrayList<Dialog> dialogList;
    private RecyclerView recyclerView;
    private DialogAdapter dialogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogs);

        init();
        getDialogs();
    }

    private void init() {
        manager = getSupportFragmentManager();

        recyclerView = findViewById(R.id.resultsDialogsRecycleView);
        dialogList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        dbHelper = new DBHelper(this);
        // TimeApi = new WorkWithTimeApi();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        LSApi = new WorkWithLocalStorageApi(database);
    }

    private void getDialogs() {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //берем все мои ордеры
        String ordersQuery =
                "SELECT DISTINCT "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + " AS " + ORDER_ID + ", "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID + " AS " + OWNER_ID
                        + " FROM "
                        + DBHelper.TABLE_ORDERS + ", "
                        + DBHelper.TABLE_WORKING_TIME + ", "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_CONTACTS_SERVICES
                        + " WHERE "
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
                        + " AND ("
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + " = ? "
                        + " OR "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID + " = ?)"
                        + " ORDER BY " + DBHelper.KEY_MESSAGE_TIME_ORDERS;

        String myId = getUserId();
        Cursor cursor = database.rawQuery(ordersQuery, new String[]{myId, myId});

        if (cursor.moveToFirst()) {
            ArrayList<String> createdDialogs = new ArrayList<>();

            int indexOrderId = cursor.getColumnIndex(ORDER_ID);
            int indexOwnerId = cursor.getColumnIndex(OWNER_ID);

            do {
                String orderId = cursor.getString(indexOrderId);
                String ownerId = cursor.getString(indexOwnerId);

                // Проверка где лежит мой id
                if (myId.equals(orderId)) {
                    // Если я записывался на услугу
                    // Проверяем не создан ли уже диалог с владельцем сервиса
                    if (!createdDialogs.contains(ownerId)) {
                        // Если нет создаём и заносим в соданные
                        createDialogWithUser(ownerId);
                        createdDialogs.add(ownerId);
                    }
                } else {
                    // Если ко мне записывались на услугу
                    // Проверяем не создан ли уже диалог с клиентом
                    if (!createdDialogs.contains(orderId)) {
                        // Если нет создаём и заносим в соданные
                        createDialogWithUser(orderId);
                        createdDialogs.add(orderId);
                    }

                }
            } while (cursor.moveToNext());
        }

        dialogAdapter = new DialogAdapter(dialogList.size(),dialogList);
        recyclerView.setAdapter(dialogAdapter);

        cursor.close();
    }

    private void createDialogWithUser(String userId) {

        Cursor cursor = LSApi.getUser(userId);

        if (cursor.moveToNext()) {
            String userName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NAME_USERS));
            Dialog dialog = new Dialog();
            dialog.setUserId(userId);
            dialog.setUserName(userName);
            dialogList.add(dialog);
        }
    }


    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onResume() {
        super.onResume();

        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerDialogsLayout);
        panelBuilder.buildHeader(manager, "Диалоги", R.id.headerDialogsLayout);

    }
}