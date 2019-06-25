package com.example.ideal.myapplication.chat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.adapters.MessageAdapter;
import com.example.ideal.myapplication.fragments.objects.Message;
import com.example.ideal.myapplication.helpApi.LoadingMessages;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.example.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Messages extends AppCompatActivity {

    private static final String TAG = "DBInf";

    private static final String REVIEW_FOR_SERVICE = "review for service";
    private static final String REVIEW_FOR_USER = "review for user";
    private static final String USER_ID = "user id";

    private static final String ORDER_ID = "order_id";
    private static final String SERVICE_ID = "service_id";
    private static final String WORKING_DAY_ID = "working_day_id";
    private static final String WORKING_TIME_ID = "working_time_id";
    private static final String REVIEW_ID = "review_id";
    private static final String ORDER_STATUS = "order status";
    private static final String OWNER_ID = "owner_id";
    private static final String USERS = "users";
    private static final String WORKING_DAYS = "working days";

    private static final String ORDERS = "orders";

    private static final String REVIEWS = "reviews";
    private static final String SERVICES = "services";

    private String senderId;
    private String userId;
    private String senderName;
    private DBHelper dbHelper;
    private FragmentManager manager;
    private SQLiteDatabase database;

    private ArrayList<Message> messageList;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private ProgressBar progressBar;
    private int counter;
    private int orderCount;
    private static ArrayList<String> senderIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        recyclerView = findViewById(R.id.resultsMessagesRecycleView);
        progressBar = findViewById(R.id.progressBarMessages);
        messageList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // получаем телефон нашего собеседеника
        manager = getSupportFragmentManager();

        userId = getUserId();
        senderId = getIntent().getStringExtra(USER_ID);
        senderName = getSenderName(senderId);
    }

    @Override
    protected void onResume() {

        super.onResume();
        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerEditServiceLayout);
        panelBuilder.buildHeader(manager, senderName, R.id.headerEditServiceLayout, senderId);

        // заменить на список id
        if (senderIds.contains(senderId)) {
            getMessages();
        } else {
            loadMessages();
            senderIds.add(senderId);
        }
    }

    private String getSenderName(String senderId) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // Получает имя пользователя, который отправил нам сообщение
        // Таблицы: Users
        // Условия: уточняем id пользователя
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_NAME_USERS
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_USERS
                        + " WHERE "
                        + DBHelper.KEY_ID + " = ?";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{senderId});
        if (cursor.moveToFirst()) {
            int indexName = cursor.getColumnIndex(DBHelper.KEY_NAME_USERS);
            return cursor.getString(indexName);
        }
        cursor.close();
        return "";
    }

    private void loadMessages() {

        final Cursor orderCursor = createOrderCursor();

        if (orderCursor.moveToFirst()) {
            int indexOwnerId = orderCursor.getColumnIndex(OWNER_ID);
            int indexServiceId = orderCursor.getColumnIndex(DBHelper.KEY_SERVICE_ID_WORKING_DAYS);
            int indexWorkingDayId = orderCursor.getColumnIndex(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME);
            int indexWorkingTimeId = orderCursor.getColumnIndex(DBHelper.KEY_WORKING_TIME_ID_ORDERS);
            int indexOrderId = orderCursor.getColumnIndex(ORDER_ID);

            orderCount = orderCursor.getCount();
            counter = 0;
            do {
                final String ownerId = orderCursor.getString(indexOwnerId);
                final String serviceId = orderCursor.getString(indexServiceId);
                final String workingDayId = orderCursor.getString(indexWorkingDayId);
                final String workingTimeId = orderCursor.getString(indexWorkingTimeId);
                final String orderId = orderCursor.getString(indexOrderId);

                if (ownerId.equals(senderId)) {
                    DatabaseReference serviceReference = FirebaseDatabase.getInstance()
                            .getReference(USERS)
                            .child(senderId)
                            .child(SERVICES)
                            .child(serviceId);

                    serviceReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot serviceSnapshot) {
                            LoadingMessages.addServiceInLocalStorage(serviceSnapshot, ownerId, database);

                            DataSnapshot workingDaySnapshot = serviceSnapshot.child(WORKING_DAYS).child(workingDayId);
                            LoadingMessages.load(workingDaySnapshot,
                                    serviceId,
                                    workingDayId,
                                    workingTimeId,
                                    orderId,
                                    database);

                            loadReviewForUser(userId, orderId, database);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else {
                    DatabaseReference workingDayReference = FirebaseDatabase.getInstance()
                            .getReference(USERS)
                            .child(userId)
                            .child(SERVICES)
                            .child(serviceId)
                            .child(WORKING_DAYS)
                            .child(workingDayId);

                    workingDayReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot workingDaySnapshot) {
                            LoadingMessages.load(workingDaySnapshot,
                                    serviceId,
                                    workingDayId,
                                    workingTimeId,
                                    orderId,
                                    database);

                            loadReviewForUser(senderId, orderId, database);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            } while (orderCursor.moveToNext());

            orderCursor.close();
        }
    }

    private Cursor createOrderCursor() {
        String ordersQuery =
                "SELECT DISTINCT "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID + " AS " + OWNER_ID + ", "
                        + DBHelper.KEY_SERVICE_ID_WORKING_DAYS + ", "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME + ", "
                        + DBHelper.KEY_WORKING_TIME_ID_ORDERS + ", "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID + " AS " + ORDER_ID
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
                        + " AND "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID + " = ?"
                        + " OR "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID + " = ?"
                        + " AND "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + " = ? )";

        Cursor cursor = database.rawQuery(ordersQuery, new String[]{senderId, userId, senderId, userId});
        return cursor;
    }

    // получить все ордеры, у которых userId = userId собеседника.
    private void getMessages() {
        messageList.clear();

        //добавить еще проверку есть ли поля у ревью этого ордера, если да, то добавляем ревью, если нет то ордер
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // вернуть заказы тех, кто записан на мои сервисы
        // нужно ли еще уточнять в serviceTable, что айди владельца равно нашему?
        String orderQuery =
                "SELECT "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID + " AS " + ORDER_ID + ", "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_IS_CANCELED_ORDERS + ", "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_MESSAGE_TIME_ORDERS + ", "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + " AS " + WORKING_TIME_ID + ","
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_TIME_WORKING_TIME + ","
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID + " AS " + WORKING_DAY_ID + ","
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_DATE_WORKING_DAYS + ","
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + " AS " + SERVICE_ID + ","
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_NAME_SERVICES + ", "
                        + DBHelper.TABLE_REVIEWS + "." + DBHelper.KEY_ID + " AS " + REVIEW_ID + ","
                        + DBHelper.TABLE_REVIEWS + "." + DBHelper.KEY_TYPE_REVIEWS + ","
                        + DBHelper.KEY_REVIEW_REVIEWS + ","
                        + DBHelper.KEY_RATING_REVIEWS
                        + " FROM "
                        + DBHelper.TABLE_ORDERS + ", "
                        + DBHelper.TABLE_WORKING_TIME + ", "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                        + DBHelper.TABLE_REVIEWS
                        + " WHERE "
                        + DBHelper.KEY_ORDER_ID_REVIEWS
                        + " = "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID
                        + " AND "
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
                        + " AND "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID + " = ?"
                        + " OR "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID + " = ?"
                        + " AND "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + " = ? )" +
                        " ORDER BY "
                        + DBHelper.KEY_MESSAGE_TIME_ORDERS;

        Cursor cursor = database.rawQuery(orderQuery, new String[]{senderId, userId, senderId, userId});
        if (cursor.moveToFirst()) {

            int indexMessageIsCanceled = cursor.getColumnIndex(DBHelper.KEY_IS_CANCELED_ORDERS);

            int indexMessageServiceName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            int indexMessageServiceDate = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS);
            int indexMessageWorkingTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);
            int indexMessageTime = cursor.getColumnIndex(DBHelper.KEY_MESSAGE_TIME_ORDERS);

            int indexMessageServiceId = cursor.getColumnIndex(SERVICE_ID);
            int indexMessageWorkingDayId = cursor.getColumnIndex(WORKING_DAY_ID);
            int indexMessageWorkingTimeId = cursor.getColumnIndex(WORKING_TIME_ID);
            int indexMessageOrderId = cursor.getColumnIndex(ORDER_ID);
            int indexMessageReviewId = cursor.getColumnIndex(REVIEW_ID);

            int indexMessageRatingReview = cursor.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);
            int indexMessageTypeReview = cursor.getColumnIndex(DBHelper.KEY_TYPE_REVIEWS);
            int indexMessageReview = cursor.getColumnIndex(DBHelper.KEY_REVIEW_REVIEWS);

            do {
                boolean isCanceled = Boolean.valueOf(cursor.getString(indexMessageIsCanceled));
                String serviceId = cursor.getString(indexMessageServiceId);
                boolean isMyService = isMyService(serviceId);

                String date = cursor.getString(indexMessageServiceDate);
                String time = cursor.getString(indexMessageWorkingTime);

                Message message = new Message();

                message.setIsCanceled(isCanceled);
                message.setIsMyService(isMyService);

                message.setUserId(senderId);
                message.setServiceId(serviceId);
                message.setUserName(senderName);
                message.setServiceName(cursor.getString(indexMessageServiceName));
                message.setWorkingDay(date);
                message.setWorkingTime(time);
                message.setMessageTime(WorkWithStringsApi.dateTimeToUserFormat(cursor.getString(indexMessageTime)));

                message.setReviewId(cursor.getString(indexMessageReviewId));

                String type = cursor.getString(indexMessageTypeReview);
                String review = cursor.getString(indexMessageReview);

                // Если сообщение связано с услугой на которую я записался
                // проверяем, если у ордера время записи прошло + 24 часа, тогда сорздаем не ордер, а ревью.
                if ((isAfterOrderTime(date, time) && !isCanceled) || (isCanceled && !review.equals("-"))) {
                    if (isMyService && type.equals(REVIEW_FOR_USER) || !isMyService && type.equals(REVIEW_FOR_SERVICE)) {
                        message.setRatingReview(cursor.getString(indexMessageRatingReview));
                        message.setType(type);
                        message.setStatus("no");
                        messageList.add(message);
                    }
                } else {
                    if (isMyService && type.equals(REVIEW_FOR_USER) || !isMyService && type.equals(REVIEW_FOR_SERVICE)) {
                        message.setWorkingTimeId(cursor.getString(indexMessageWorkingTimeId));
                        message.setWorkingDayId(cursor.getString(indexMessageWorkingDayId));
                        message.setOrderId(cursor.getString(indexMessageOrderId));
                        message.setStatus(ORDER_STATUS);

                        messageList.add(message);
                    }
                }
            } while (cursor.moveToNext());
        }
        messageAdapter = new MessageAdapter(messageList.size(), messageList);
        //опускаемся к полседнему элементу
        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        recyclerView.setAdapter(messageAdapter);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        cursor.close();
    }

    private void loadReviewForUser(String userId, final String orderId, final SQLiteDatabase database) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(USERS)
                .child(userId)
                .child(ORDERS)
                .child(orderId)
                .child(REVIEWS);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot reviewsSnapshot) {
                LoadingMessages.addReviewInLocalStorage(reviewsSnapshot, orderId, database);
                counter++;
                if (counter == orderCount) {
                    getMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private boolean isAfterOrderTime(String date, String time) {
        WorkWithTimeApi workWithTimeApi = new WorkWithTimeApi();
        //3600000 * 24 = 24 часа
        String commonDate = date + " " + time;
        Long orderDateLong = workWithTimeApi.getMillisecondsStringDate(commonDate) + 3600000 * 24;
        Long sysdate = workWithTimeApi.getSysdateLong();

        return sysdate > orderDateLong;
    }

    // Пo id working time узнаёт ренадлежит ли мне скевис
    private boolean isMyService(String serviceId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // Получает id сервиса
        // Таблицы: services
        // Условия: уточняем телефон пользователя и id сервиса
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_ID
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES
                        + " WHERE "
                        + DBHelper.KEY_ID + " = ? "
                        + " AND "
                        + DBHelper.KEY_USER_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId, getUserId()});
        return cursor.moveToFirst();
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
