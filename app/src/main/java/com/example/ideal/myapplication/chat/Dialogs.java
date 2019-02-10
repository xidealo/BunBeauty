package com.example.ideal.myapplication.chat;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.chatElements.DialogElement;
import com.example.ideal.myapplication.fragments.objects.Message;
import com.example.ideal.myapplication.fragments.objects.Order;
import com.example.ideal.myapplication.fragments.objects.RatingReview;
import com.example.ideal.myapplication.fragments.objects.User;
import com.example.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.example.ideal.myapplication.reviews.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Dialogs extends AppCompatActivity {

    private static final String TAG = "DBInf";

    private static final String FILE_NAME = "Info";
    private static final String PHONE_NUMBER = "Phone number";

    private static final String DIALOGS = "dialogs";
    private static final String FIRST_PHONE = "first phone";
    private static final String SECOND_PHONE = "second phone";

    private static final String USERS = "users";
    private static final String USER_ID = "user id";
    private static final String NAME = "name";
    private static final String CITY = "city";

    private static final String MESSAGES = "messages";
    private static final String ORDERS = "orders";
    private static final String REVIEWS = "reviews";
    private static final String RATING = "rating";
    private static final String REVIEW = "review";
    private static final String MESSAGE_TIME = "message time";
    private static final String DIALOG_ID = "dialog id";
    private static final String IS_CANCELED = "is canceled";

    private static final String WORKING_TIME= "working time";

    private static final String WORKING_TIME_ID = "working time id";
    private static final String WORKING_DAY_ID = "working day id";
    private static final String TYPE = "type";
    private static final String MESSAGE_ID = "message id";

    private static final String WORKING_DAYS = "working days";
    private static final String SERVICE_ID = "service id";

    private static final String SERVICES = "services";
    private static final String TIME = "time";

    private static final String REVIEW_FOR_SERVICE = "review for service";
    private static final String REVIEW_FOR_USER = "review for user";

    WorkWithTimeApi workWithTimeApi;
    SharedPreferences sPref;
    DBHelper dbHelper;

    LinearLayout resultLayout;
    DialogElement dElement;
    FragmentManager manager;
    FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogs);

        manager = getSupportFragmentManager();
        dbHelper = new DBHelper(this);
        workWithTimeApi = new WorkWithTimeApi();
        resultLayout = findViewById(R.id.mainDialogsLayout);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
    }

    // Подгружает все мои диалоги и всё что с ними связано из Firebase
    private void loadDialogs() {
        // Загрузка диалогов, где мой номер стоит на 1-м месте
        loadDialogsByFirstPhone();

        // Загрузка диалогов, где мой номер стоит на 2-м месте
        loadDialogsBySecondPhone();
    }

    private void loadDialogsByFirstPhone() {
        final String myPhone = getUserPhone();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        Query query1 = database.getReference(DIALOGS)
                .orderByChild(FIRST_PHONE)
                .equalTo(myPhone);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dialogs) {
                for(DataSnapshot dialog:dialogs.getChildren()) {
                    String secondPhone = String.valueOf(dialog.child(SECOND_PHONE).getValue());
                    String dialogId = dialog.getKey();

                    // Добавляет(обновляет) диалог в LocalStorage и возвращает true если это диалог новый
                    boolean isNew = addDialogInLocalStorage(dialogId, myPhone, secondPhone);

                    loadInterlocutor(secondPhone, dialogId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void loadDialogsBySecondPhone() {
        final String myPhone = getUserPhone();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        Query query2 = database.getReference(DIALOGS)
                .orderByChild(SECOND_PHONE)
                .equalTo(myPhone);
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dialogs) {
                for(DataSnapshot dialog:dialogs.getChildren()){
                    final String firstPhone = String.valueOf(dialog.child(FIRST_PHONE).getValue());
                    final String dialogId = dialog.getKey();

                    boolean isNew = addDialogInLocalStorage(dialogId, firstPhone, myPhone);

                    loadInterlocutor(firstPhone, dialogId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void loadInterlocutor(final String phone, final String dialogId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(USERS).child(phone);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {

                String name = String.valueOf(userSnapshot.child(NAME).getValue());
                String city = String.valueOf(userSnapshot.child(CITY).getValue());

                User user = new User();
                user.setPhone(phone);
                user.setName(name);
                user.setCity(city);

                addUserInLocalStorage(user);

                getAndPutMessagesInLocalStorage(dialogId);

                addToScreen(dialogId, name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void addUserInLocalStorage(User user) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String phone = user.getPhone();

        String sqlQuery = "SELECT * FROM "
                + DBHelper.TABLE_CONTACTS_USERS
                + " WHERE "
                + DBHelper.KEY_USER_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[] {phone});

        contentValues.put(DBHelper.KEY_NAME_USERS, user.getName());
        contentValues.put(DBHelper.KEY_CITY_USERS, user.getCity());

        if(cursor.moveToFirst()) {
            database.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                    DBHelper.KEY_USER_ID + " = ?",
                    new String[]{String.valueOf(phone)});
        } else {
            contentValues.put(DBHelper.KEY_USER_ID, phone);
            database.insert(DBHelper.TABLE_CONTACTS_USERS, null, contentValues);
        }
        cursor.close();
    }

    private boolean addDialogInLocalStorage(String dialogId, String firstPhone, String secondPhone) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String sqlQuery = "SELECT * FROM "
                + DBHelper.TABLE_DIALOGS
                + " WHERE "
                + DBHelper.KEY_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[] {dialogId});

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_FIRST_USER_ID_DIALOGS, firstPhone);
        contentValues.put(DBHelper.KEY_SECOND_USER_ID_DIALOGS, secondPhone);

        if(cursor.moveToFirst()) {
            database.update(DBHelper.TABLE_DIALOGS,
                    contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{String.valueOf(dialogId)});
            cursor.close();
            return false;
        } else {
            contentValues.put(DBHelper.KEY_ID, dialogId);
            database.insert(DBHelper.TABLE_DIALOGS, null, contentValues);
            cursor.close();
            return true;
        }
    }

    private void getAndPutMessagesInLocalStorage(final String dialogId) {
        //если разница между timeId 24 часа и у message isCanceled не отменено, мы генерим 2 ревью
        //message_orders
        Query messagesQuery = FirebaseDatabase.getInstance().getReference(MESSAGES)
                .orderByChild(DIALOG_ID)
                .equalTo(dialogId);

        messagesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot messages) {

                for(DataSnapshot message:messages.getChildren()){
                    // идем по всем сообщениям
                    Message myMessage = new Message();

                    String messageId = message.getKey();
                    String time = String.valueOf(message.child(MESSAGE_TIME).getValue());

                    myMessage.setId(messageId); // для отладки
                    myMessage.setMessageTime(time);
                    myMessage.setDialogId(dialogId);

                    addMessagesInLocalStorage(myMessage);
                    getAndPutOrderInLocalStorage(myMessage);
                    getAndPutReviewInLocalStorage(myMessage);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void addMessagesInLocalStorage(final Message message) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Log.d(TAG, "addMessagesInLocalStorage: ");
        // берем всю информацию из таблицы MR, чтобы либо сделать update, либо insert
        String sqlQuery = "SELECT * FROM "
                + DBHelper.TABLE_MESSAGES
                + " WHERE "
                + DBHelper.KEY_ID + " = ?";
        Cursor cursor;

        cursor = database.rawQuery(sqlQuery, new String[] {message.getId()});

        //метод, который добавляет ревью в firebase, если !isCanceled и sysdate - timeId + date > 24h

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_DIALOG_ID_MESSAGES, message.getDialogId());
        contentValues.put(DBHelper.KEY_MESSAGE_TIME_MESSAGES, message.getMessageTime());

        // update || insert
        if(cursor.moveToFirst()) {
            database.update(DBHelper.TABLE_MESSAGES, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{String.valueOf(message.getId())});
        } else {
            contentValues.put(DBHelper.KEY_ID, message.getId());
            database.insert(DBHelper.TABLE_MESSAGES, null, contentValues);
        }
        cursor.close();
    }

    private void getAndPutOrderInLocalStorage(final Message message) {
        //загружаем message reviews
        //делаем запрос в fireBase по dialogId, который получаем при загрузке страницы
        Query messagesQuery = FirebaseDatabase.getInstance().getReference(ORDERS)
                .orderByChild(MESSAGE_ID)
                .equalTo(message.getId());

        messagesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot orders) {
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                // берем всю информацию из таблицы MR, чтобы либо сделать update, либо insert
                String sqlQuery = "SELECT * FROM "
                        + DBHelper.TABLE_ORDERS
                        + " WHERE "
                        + DBHelper.KEY_ID + " = ?";
                Cursor cursor;
                for(DataSnapshot order:orders.getChildren()){
                    // идем по всем сообщениям
                    Order myOrder = new Order();
                    String orderId = order.getKey();
                    String isCanceled = String.valueOf(order.child(IS_CANCELED).getValue());
                    String workingTimeId = String.valueOf(order.child(WORKING_TIME_ID).getValue());

                    myOrder.setId(orderId); // для отладки
                    myOrder.setMessageId(message.getId());
                    myOrder.setIsCanceled(Boolean.valueOf(isCanceled));
                    myOrder.setWorkingTimeId(workingTimeId);

                    // подргужаем время, потом день и сам сервис этого сообщения
                    addTimeInLocalStorage(myOrder, null, message.getDialogId());

                    cursor = database.rawQuery(sqlQuery, new String[] {orderId});

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBHelper.KEY_IS_CANCELED_ORDERS, myOrder.getIsCanceled());
                    contentValues.put(DBHelper.KEY_MESSAGE_ID_ORDERS, myOrder.getMessageId());
                    contentValues.put(DBHelper.KEY_WORKING_TIME_ID_ORDERS, myOrder.getWorkingTimeId());

                    // update || insert
                    if(cursor.moveToFirst()) {
                        database.update(DBHelper.TABLE_ORDERS, contentValues,
                                DBHelper.KEY_ID + " = ?",
                                new String[]{String.valueOf(myOrder.getId())});
                    } else {
                        contentValues.put(DBHelper.KEY_ID, myOrder.getId());
                        database.insert(DBHelper.TABLE_ORDERS, null, contentValues);
                    }
                    cursor.close();
                    updateWorkingTimeInLocalStorage(orderId);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void getAndPutReviewInLocalStorage(final Message message) {
        //загружаем все ревью в local storage, чтобы быстрее работало
        //получаем на вход ордер, а из него берем messageId & workingTimeId
        Query reviewsQuery = FirebaseDatabase.getInstance().getReference(REVIEWS)
                .orderByChild(MESSAGE_ID)
                .equalTo(message.getId());

        reviewsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot reviews) {
                for(DataSnapshot review: reviews.getChildren()){
                    RatingReview ratingReview = new RatingReview();

                    ratingReview.setId(review.getKey());
                    ratingReview.setReview(String.valueOf(review.child(REVIEW).getValue()));
                    ratingReview.setReview(String.valueOf(review.child(RATING).getValue()));
                    String type = String.valueOf(review.child(TYPE).getValue());
                    ratingReview.setType(type);
                    ratingReview.setMessageId(message.getId());
                    String workingTimeId = String.valueOf(review.child(WORKING_TIME_ID).getValue());
                    ratingReview.setWorkingTimeId(workingTimeId);

                    addTimeInLocalStorage(null, review, message.getDialogId());

                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    String sqlQuery = "SELECT "
                            + DBHelper.KEY_USER_ID
                            + " FROM "
                            + DBHelper.TABLE_WORKING_TIME
                            + " WHERE "
                            + DBHelper.KEY_ID + " = ?";

                    Cursor cursor = database.rawQuery(sqlQuery, new String[] {workingTimeId});
                    Log.d(TAG, "onDataChange: " + cursor.getCount());

                    if(cursor.moveToFirst()) {
                        int indexUserId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);
                        String userId = cursor.getString(indexUserId);

                        String myPhone = getUserPhone();

                        if((myPhone.equals("0") || myPhone.equals(userId))) {
                            if(type.equals(REVIEW_FOR_SERVICE)) {
                                addReviewInLocalStorage(ratingReview);
                            }
                        } else {
                            if(type.equals(REVIEW_FOR_USER)) {
                                addReviewInLocalStorage(ratingReview);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addReviewInLocalStorage(RatingReview ratingReview) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String sqlQuery = "SELECT * FROM "
                + DBHelper.TABLE_REVIEWS
                + " WHERE "
                + DBHelper.KEY_ID + " = ?";
        Cursor cursor = database.rawQuery(sqlQuery, new String[] {ratingReview.getId()});

        Log.d(TAG, "addReviewInLocalStorage: " + cursor.getCount());

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_REVIEW_REVIEWS, ratingReview.getReview());
        contentValues.put(DBHelper.KEY_RATING_REVIEWS, ratingReview.getRating());
        contentValues.put(DBHelper.KEY_MESSAGE_ID_REVIEWS, ratingReview.getMessageId());
        contentValues.put(DBHelper.KEY_WORKING_TIME_ID_REVIEWS, ratingReview.getWorkingTimeId());
        contentValues.put(DBHelper.KEY_TYPE_REVIEWS, ratingReview.getType());

        // update || insert
        if(cursor.moveToFirst()) {
            database.update(DBHelper.TABLE_REVIEWS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    new String[]{String.valueOf(ratingReview.getId())});
        } else {
            contentValues.put(DBHelper.KEY_ID, ratingReview.getId());
            database.insert(DBHelper.TABLE_REVIEWS, null, contentValues);
        }
        cursor.close();
    }

    private void addTimeInLocalStorage(final Order order, final RatingReview review, final String dialogId) {
        //берем время из fireBase и сохраняем его в sqlLite

        final boolean isOrder = (review == null);
        final String timeId;
        if(isOrder) {
            timeId = order.getWorkingTimeId();
        }
        else {
            timeId = review.getWorkingTimeId();
        }

        DatabaseReference timeRef = FirebaseDatabase.getInstance().
                getReference(WORKING_TIME)
                .child(timeId);

        timeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot time) {
                //кладем в локальную бд время, которое есть в ордере
                final SQLiteDatabase database = dbHelper.getWritableDatabase();
                final ContentValues contentValues = new ContentValues();

                String sqlQuery = "SELECT * FROM "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.KEY_ID + " = ?";

                Cursor cursor = database.rawQuery(sqlQuery, new String[]{timeId});

                String myTime = String.valueOf(time.child(TIME).getValue());
                String userId = String.valueOf(time.child(USER_ID).getValue());
                String dayId = String.valueOf(time.child(WORKING_DAY_ID).getValue());

                order.setOrderTime(myTime);

                // получаем день этого сообщения
                if(isOrder) {
                    addDayInLocalStorage(dayId,order, null, dialogId);
                }
                else{
                    addDayInLocalStorage(dayId,null, review, dialogId);
                }
                contentValues.put(DBHelper.KEY_TIME_WORKING_TIME, myTime);
                contentValues.put(DBHelper.KEY_USER_ID, userId);
                contentValues.put(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME, dayId);
                if (cursor.moveToFirst()) {
                    database.update(DBHelper.TABLE_WORKING_TIME, contentValues,
                            DBHelper.KEY_ID + " = ?",
                            new String[]{String.valueOf(timeId)});
                } else {
                    contentValues.put(DBHelper.KEY_ID, timeId);
                    database.insert(DBHelper.TABLE_WORKING_TIME, null, contentValues);
                }
                cursor.close();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void addDayInLocalStorage(final String dayId, final Order order,
                                      final RatingReview review, final String dialogId) {
        // загружаем дни, которые связаны с сообщением
        final boolean isOrder = (review == null);

        DatabaseReference dayRef = FirebaseDatabase.getInstance().getReference(WORKING_DAYS)
                .child(dayId);

        dayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot day) {
                SQLiteDatabase database = dbHelper.getWritableDatabase();

                String sqlQuery = "SELECT * FROM "
                        + DBHelper.TABLE_WORKING_DAYS
                        + " WHERE "
                        + DBHelper.KEY_ID + " = ?";
                Cursor cursor = database.rawQuery(sqlQuery, new String[]{dayId});

                String date = String.valueOf(day.child("data").getValue());
                String serviceId = String.valueOf(day.child(SERVICE_ID).getValue());

                if(isOrder) {
                    long sysdate = workWithTimeApi.getSysdateLong();
                    long orderDate = workWithTimeApi.getMillisecondsStringDate(date + " " + order.getOrderTime());
                    long dayLong = 86400000;

                    //если не отменено и прошел день, то мы проверяем есть ли такие ревью уже
                    if (!order.getIsCanceled() && (sysdate - orderDate > dayLong)) {
                        checkReview(order, dialogId);
                    }
                }

                loadServiceInLocalStorage(serviceId);

                ContentValues contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_DATE_WORKING_DAYS, date);
                contentValues.put(DBHelper.KEY_SERVICE_ID_WORKING_DAYS, serviceId);
                if (cursor.moveToFirst()) {
                    database.update(DBHelper.TABLE_WORKING_DAYS, contentValues,
                            DBHelper.KEY_ID + " = ?",
                            new String[]{String.valueOf(dayId)});
                } else {
                    contentValues.put(DBHelper.KEY_ID, dayId);
                    database.insert(DBHelper.TABLE_WORKING_DAYS, null, contentValues);
                }
                cursor.close();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void checkReview(final Order order, final String dialogId) {

        //делаем запрос по working time id этого order, только у 2х ревью может быть такой timeId
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query query = database.getReference(REVIEWS)
                .orderByChild(WORKING_TIME_ID)
                .equalTo(order.getWorkingTimeId());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot times) {

                //если  = 0, значит ревью нету, мы их создаем
                if (times.getChildrenCount() == 0) {
                    String messageId = createMessage(dialogId);
                    createReview(order, "review for service", messageId);
                    createReview(order, "review for user", messageId);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void createReview(Order order, String type, String messageId){
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference(REVIEWS);
        Map<String,Object> items = new HashMap<>();

        items.put(RATING, "0");
        items.put(REVIEW, "");
        items.put(TYPE, type);
        items.put(MESSAGE_ID, messageId);
        items.put(WORKING_TIME_ID, order.getWorkingTimeId());

        String reviewId =  myRef.push().getKey();
        myRef = database.getReference(REVIEWS).child(reviewId);
        myRef.updateChildren(items);

        RatingReview ratingReview = new RatingReview();
        ratingReview.setId(reviewId);
        ratingReview.setRating("0");
        ratingReview.setReview("");
        ratingReview.setType(type);
        ratingReview.setMessageId(order.getMessageId());
        ratingReview.setWorkingTimeId(order.getWorkingTimeId());
        addReviewInLocalStorage(ratingReview);
    }

    private String createMessage(String dialogId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(MESSAGES);
        Map<String,Object> items = new HashMap<>();

        String dateNow = workWithTimeApi.getCurDateInFormatHMS();

        items.put(MESSAGE_TIME, dateNow);
        items.put(DIALOG_ID, dialogId);

        String messageId =  myRef.push().getKey();
        myRef = database.getReference(MESSAGES).child(messageId);
        myRef.updateChildren(items);
        Message message = new Message();
        message.setDialogId(dialogId);
        message.setDate(dateNow);
        message.setId(messageId);

        Log.d(TAG, "createMessage: ");
        addMessagesInLocalStorage(message);

        return messageId;
    }

    private void loadServiceInLocalStorage(final String serviceId) {
        DatabaseReference serviceRef = FirebaseDatabase.getInstance().getReference(SERVICES)
                .child(serviceId);

        serviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot service) {
                SQLiteDatabase database = dbHelper.getWritableDatabase();

                String sqlQuery = "SELECT * FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES
                        + " WHERE "
                        + DBHelper.KEY_ID + " = ?";
                Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId});

                String name = String.valueOf(service.child(NAME).getValue());

                ContentValues contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_NAME_SERVICES, name);

                if (cursor.moveToFirst()) {
                    database.update(DBHelper.TABLE_CONTACTS_SERVICES, contentValues,
                            DBHelper.KEY_ID + " = ?",
                            new String[]{String.valueOf(serviceId)});
                } else {
                    contentValues.put(DBHelper.KEY_ID, serviceId);
                    database.insert(DBHelper.TABLE_CONTACTS_SERVICES, null, contentValues);
                }
                cursor.close();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private void updateWorkingTimeInLocalStorage(String ordersId) {
        //получить id message
        //получить date (id working days)
        //сделать query по date в working time и получить id времени
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(ORDERS)
                .child(ordersId)
                .child(WORKING_TIME_ID);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dateId) {
                String date = String.valueOf(dateId.getValue());

                Query query = database.getReference(WORKING_TIME)
                        .orderByChild(WORKING_DAY_ID)
                        .equalTo(date);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot time: dataSnapshot.getChildren()) {
                            final String timeId = String.valueOf(time.getKey());
                            DatabaseReference myRef = database.getReference(WORKING_TIME)
                                    .child(timeId)
                                    .child(USER_ID);

                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    SQLiteDatabase database = dbHelper.getWritableDatabase();

                                    String phone =  String.valueOf(dataSnapshot.getValue());

                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(DBHelper.KEY_USER_ID, phone);

                                    database.update(DBHelper.TABLE_WORKING_TIME, contentValues,
                                            DBHelper.KEY_ID + " = ? ",
                                            new String[]{String.valueOf(timeId)});
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    attentionBadConnection();
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        attentionBadConnection();
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                attentionBadConnection();
            }
        });
    }

    private String getUserPhone() {
        sPref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        return  sPref.getString(PHONE_NUMBER, "-");
    }

    @Override
    protected void onResume() {
        super.onResume();
        resultLayout.removeAllViews();
        loadDialogs();
    }

    private void addToScreen(String dialogId, String name) {
        dElement = new DialogElement(dialogId, name);
        transaction = manager.beginTransaction();
        transaction.add(R.id.mainDialogsLayout, dElement);
        transaction.commit();
    }

    private void attentionBadConnection() {
        Toast.makeText(this,"Плохое соединение",Toast.LENGTH_SHORT).show();
    }
}