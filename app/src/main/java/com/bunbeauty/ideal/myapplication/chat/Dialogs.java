package com.bunbeauty.ideal.myapplication.chat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.adapters.DialogAdapter;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.DBHelper;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog;
import com.bunbeauty.ideal.myapplication.helpApi.LoadingUserElementData;
import com.bunbeauty.ideal.myapplication.helpApi.PanelBuilder;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithLocalStorageApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Dialogs extends AppCompatActivity {

    private static final String TAG = "DBInf";

    private static final String USERS = "users";
    private static final String SERVICES = "services";
    private static final String WORKING_DAYS = "working days";
    private static final String WORKING_TIME = "working time";
    private static final String ORDERS = "orders";
    private static final String MESSAGE_TIME = "time";
    private static final String WORKER_ID = "worker id";
    private static final String SERVICE_ID = "service id";
    private static final String WORKING_DAY_ID = "working day id";
    private static final String WORKING_TIME_ID = "working time id";

    private static final String USER_ID = "user id";

    private static final String OWNER_ID = "owner_id";
    private static final String ORDER_ID = "order_id";

    private String userId;
    private int userCount;
    private int counter;
    private WorkWithLocalStorageApi LSApi;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private ProgressBar progressBar;

    private FragmentManager manager;

    private ArrayList<Dialog> dialogList;
    private RecyclerView recyclerView;
    private DialogAdapter dialogAdapter;
    private TextView noDialogsText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogs);

        init();
    }

    private void init() {
        userId = getUserId();

        recyclerView = findViewById(R.id.resultsDialogsRecycleView);
        progressBar = findViewById(R.id.progressBarDialogs);
        noDialogsText = findViewById(R.id.noDialogsDialogsText);

        dialogList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        manager = getSupportFragmentManager();
        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();
        LSApi = new WorkWithLocalStorageApi(database);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dialogList.clear();

        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerDialogsLayout);
        panelBuilder.buildHeader(manager, "Диалоги", R.id.headerDialogsLayout);

        //Каждый раз загружаем
        loadOrders();
    }

    private void loadOrders() {
        // берем все мои записи
        DatabaseReference orderReference = FirebaseDatabase.getInstance()
                .getReference(USERS)
                .child(userId)
                .child(ORDERS);

        orderReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ordersSnapshot) {
                if (ordersSnapshot.getChildrenCount() == 0) {
                    loadMyServiceOrders();
                }

                counter = 0;
                final long childrenCount = ordersSnapshot.getChildrenCount();
                for (final DataSnapshot myOrderSnapshot : ordersSnapshot.getChildren()) {
                    //получаем "путь" к мастеру, на сервис которого мы записаны
                    final String orderId = myOrderSnapshot.getKey();
                    final String workerId = String.valueOf(myOrderSnapshot.child(WORKER_ID).getValue());
                    final String serviceId = String.valueOf(myOrderSnapshot.child(SERVICE_ID).getValue());
                    final String workingDayId = String.valueOf(myOrderSnapshot.child(WORKING_DAY_ID).getValue());
                    final String workingTimeId = String.valueOf(myOrderSnapshot.child(WORKING_TIME_ID).getValue());
                    DatabaseReference serviceReference = FirebaseDatabase.getInstance()
                            .getReference(USERS)
                            .child(workerId)
                            .child(SERVICES)
                            .child(serviceId)
                            .child(WORKING_DAYS)
                            .child(workingDayId)
                            .child(WORKING_TIME)
                            .child(workingTimeId)
                            .child(ORDERS)
                            .child(orderId);

                    serviceReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot orderSnapshot) {
                            String messageTime = orderSnapshot.child(MESSAGE_TIME).getValue(String.class);
                            WorkWithLocalStorageApi.addDialogInfoInLocalStorage(serviceId,
                                    workingDayId,
                                    workingTimeId,
                                    orderId,
                                    userId,
                                    messageTime,
                                    workerId);


                            DatabaseReference userReference = FirebaseDatabase.getInstance()
                                    .getReference(USERS)
                                    .child(workerId);

                            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                    LoadingUserElementData.loadUserNameAndPhoto(userSnapshot, database);
                                    counter++;
                                    if (counter == childrenCount) {
                                        loadMyServiceOrders();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                getMyDialogs();
            }
        });
    }

    private void loadMyServiceOrders() {
        userCount = 0;
        counter = 0;

        DatabaseReference servicesReference = FirebaseDatabase.getInstance()
                .getReference(USERS)
                .child(userId)
                .child(SERVICES);

        servicesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot servicesSnapshot) {
                for (DataSnapshot serviceSnapshot : servicesSnapshot.getChildren()) {
                    String serviceId = serviceSnapshot.getKey();
                    for (DataSnapshot workingDaySnapshot : serviceSnapshot.child(WORKING_DAYS).getChildren()) {
                        String workingDayId = workingDaySnapshot.getKey();
                        for (DataSnapshot workingTimeSnapshot : workingDaySnapshot.child(WORKING_TIME).getChildren()) {
                            String workingTimeId = workingTimeSnapshot.getKey();
                            for (DataSnapshot orderSnapshot : workingTimeSnapshot.child(ORDERS).getChildren()) {
                                userCount++;

                                String orderId = orderSnapshot.getKey();
                                String orderUserId = orderSnapshot.child(USER_ID).getValue(String.class);
                                String messageTime = orderSnapshot.child(MESSAGE_TIME).getValue(String.class);
                                // добавляем данные в Local Storage
                                WorkWithLocalStorageApi.addDialogInfoInLocalStorage(serviceId,
                                        workingDayId,
                                        workingTimeId,
                                        orderId,
                                        orderUserId,
                                        messageTime,
                                        null);


                                DatabaseReference userReference = FirebaseDatabase.getInstance()
                                        .getReference(USERS)
                                        .child(orderUserId);

                                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                        LoadingUserElementData.loadUserNameAndPhoto(userSnapshot, database);
                                        counter++;
                                        if (counter == userCount) {
                                            getMyDialogs();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                    }
                }
                if (userCount == 0) {
                    getMyDialogs();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void getMyDialogs() {

        Cursor dialogsCursor = createDialogsCursor();

        if (dialogsCursor.moveToFirst()) {
            ArrayList<String> createdDialogs = new ArrayList<>();

            int indexOrderId = dialogsCursor.getColumnIndex(ORDER_ID);
            int indexOwnerId = dialogsCursor.getColumnIndex(OWNER_ID);
            int indexMessageTime = dialogsCursor.getColumnIndex(DBHelper.KEY_MESSAGE_TIME_ORDERS);
            do {
                String orderId = dialogsCursor.getString(indexOrderId);
                String ownerId = dialogsCursor.getString(indexOwnerId);
                String messageTime = dialogsCursor.getString(dialogsCursor.getColumnIndex(DBHelper.KEY_MESSAGE_TIME_ORDERS));

                // Проверка где лежит мой id
                if (userId.equals(orderId)) {
                    // Если я записывался на услугу
                    // Проверяем не создан ли уже диалог с владельцем сервиса
                    if (!createdDialogs.contains(ownerId)) {
                        // Если нет создаём и заносим в соданные
                        createDialogWithUser(ownerId,messageTime);
                        createdDialogs.add(ownerId);
                    }
                } else {
                    // Если ко мне записывались на услугу
                    // Проверяем не создан ли уже диалог с клиентом
                    if (!createdDialogs.contains(orderId)) {
                        // Если нет создаём и заносим в соданные
                        createDialogWithUser(orderId,messageTime);
                        createdDialogs.add(orderId);
                    }
                }
            } while (dialogsCursor.moveToNext());

            dialogAdapter = new DialogAdapter(dialogList.size(), dialogList);
            recyclerView.setAdapter(dialogAdapter);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            dialogsCursor.close();
        } else {
            setNoDialogs();
            dialogsCursor.close();
        }
    }

    private Cursor createDialogsCursor() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        //берем все мои ордеры
        String dialogsQuery =
                "SELECT DISTINCT "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + " AS " + ORDER_ID + ", "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID + " AS " + OWNER_ID + ", "
                        + DBHelper.KEY_MESSAGE_TIME_ORDERS
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
                        + " ORDER BY " + DBHelper.KEY_MESSAGE_TIME_ORDERS + " DESC";

        Cursor cursor = database.rawQuery(dialogsQuery, new String[]{userId, userId});
        return cursor;
    }

    private void createDialogWithUser(String userId, String messageTime) {

        Cursor cursor = LSApi.getUser(userId);

        if (cursor.moveToNext()) {
            String userName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NAME_USERS));
            Dialog dialog = new Dialog();
            /*dialog.userId = userId;
            dialog.userName = userName;
            dialog.messageTime = messageTime;*/
            dialogList.add(dialog);
        }
    }

    private void setNoDialogs(){
        progressBar.setVisibility(View.GONE);
        noDialogsText.setVisibility(View.VISIBLE);
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}