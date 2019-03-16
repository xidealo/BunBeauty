package com.example.ideal.myapplication.chat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.chatElements.MessageOrderElement;
import com.example.ideal.myapplication.fragments.chatElements.MessageReviewElement;
import com.example.ideal.myapplication.fragments.objects.Message;
import com.example.ideal.myapplication.helpApi.PanelBuilder;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;

public class Messages extends AppCompatActivity {

    private static final String TAG = "DBInf";

    private static final String DIALOG_ID = "dialog id";

    private static final String SERVICE_ID = "service_id";

    private static final String REVIEW_FOR_SERVICE = "review for service";
    private static final String REVIEW_FOR_USER = "review for user";

    private String myPhone;
    private String dialogId;
    private String senderName;
    private DBHelper dbHelper;

    private FragmentManager manager;

    private LinearLayout messagesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);

        dbHelper = new DBHelper(this);

        dialogId = getIntent().getStringExtra(DIALOG_ID);
        myPhone = getUserId();
        // получаем телефон нашего собеседеника
        String senderPhone = getSenderPhone(dialogId);
        senderName = getSenderName(senderPhone);

        manager = getSupportFragmentManager();

        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerEditServiceLayout);
        panelBuilder.buildHeader(manager, senderName, R.id.headerEditServiceLayout, senderPhone);

        messagesLayout = findViewById(R.id.resultsMessagesLayout);
    }

    private String getSenderPhone(String dialogId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // Получает телефоны из диалога
        // Таблицы: dialogs
        // Условия: уточняем id диалога
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_FIRST_USER_ID_DIALOGS + ", "
                        + DBHelper.KEY_SECOND_USER_ID_DIALOGS
                        + " FROM "
                        + DBHelper.TABLE_DIALOGS
                        + " WHERE "
                        + DBHelper.KEY_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{dialogId});

        if(cursor.moveToFirst()) {
            int indexFirstPhone = cursor.getColumnIndex(DBHelper.KEY_FIRST_USER_ID_DIALOGS);
            int indexSecondPhone = cursor.getColumnIndex(DBHelper.KEY_SECOND_USER_ID_DIALOGS);

            String firstPhone = cursor.getString(indexFirstPhone);
            String secondPhone = cursor.getString(indexSecondPhone);
            cursor.close();
            if(firstPhone.equals(myPhone)){
                return secondPhone;
            }
            else {
                return firstPhone;
            }
        }
        cursor.close();
        return "0";
    }

    private String getSenderName(String senderPhone) {

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
                        + DBHelper.KEY_USER_ID + " = ?";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{senderPhone});
        if(cursor.moveToFirst()){
            int indexName = cursor.getColumnIndex(DBHelper.KEY_NAME_USERS);
            return cursor.getString(indexName);
        }
        cursor.close();
        return "";
    }

    private void createMessages() {

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Message message = new Message();
        // Получает id сообщения, время сообщения
        // Таблицы: messages
        // Условия: уточняем id диалога
        String messageQuery =
                "SELECT "
                        + DBHelper.KEY_ID + ", "
                        + DBHelper.KEY_MESSAGE_TIME_MESSAGES
                        + " FROM "
                        + DBHelper.TABLE_MESSAGES
                        + " WHERE "
                        + DBHelper.KEY_DIALOG_ID_MESSAGES + " = ?"
                        + " ORDER BY "
                        + DBHelper.KEY_MESSAGE_TIME_MESSAGES;
        Cursor messageCursor = database.rawQuery(messageQuery, new String[]{dialogId});

        if (messageCursor.moveToFirst()) {
            int indexMessageId = messageCursor.getColumnIndex(DBHelper.KEY_ID);
            int indexMessageTime = messageCursor.getColumnIndex(DBHelper.KEY_MESSAGE_TIME_MESSAGES);

            // Цикл по всем сообщениям в диалоге пользователя
            do {
                String messageId = messageCursor.getString(indexMessageId);
                String messageTime = messageCursor.getString(indexMessageTime);
                message.setMessageTime(messageTime);
                message.setDialogId(dialogId);
                message.setId(messageId);

                // Проверяем какую информацию содержит сообщение (запись или ревью)
                if(!isThisMessageContainsOrder(message)){
                    isThisMessageContainsReview(message);
                }
            } while (messageCursor.moveToNext());
            messageCursor.close();
        }
    }

    private boolean isThisMessageContainsOrder(Message message) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String orderQuery =
                "SELECT "
                        + DBHelper.KEY_ID + ", "
                        + DBHelper.KEY_IS_CANCELED_ORDERS + ", "
                        + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                        + " FROM "
                        + DBHelper.TABLE_ORDERS
                        + " WHERE "
                        + DBHelper.KEY_MESSAGE_ID_ORDERS + " = ?";
        Cursor orderCursor = database.rawQuery(orderQuery, new String[]{message.getId()});

        if (orderCursor.moveToFirst()) {
            boolean isCanceled = Boolean.valueOf(orderCursor.getString(orderCursor.getColumnIndex(DBHelper.KEY_IS_CANCELED_ORDERS)));
            String timeId = orderCursor.getString(orderCursor.getColumnIndex(DBHelper.KEY_WORKING_TIME_ID_ORDERS));
            String time = getTime(timeId);
            String date = getDate(timeId);
            String serviceId = getServiceId(timeId);
            boolean isMyService = isMyService(serviceId);
            String serviceName = getServiceName(serviceId);
            String orderId = orderCursor.getString(orderCursor.getColumnIndex(DBHelper.KEY_ID));

            message.setIsCanceled(isCanceled);
            message.setIsMyService(isMyService);
            message.setOrderTime(time);
            message.setDate(date);
            message.setServiceName(serviceName);
            message.setUserName(senderName);
            message.setTimeId(timeId);
            message.setOrderId(orderId);

            addMessageOrderToScreen(message);
            orderCursor.close();
            return true;
        }
        orderCursor.close();
        return false;
    }

    private void isThisMessageContainsReview(Message message) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String reviewQuery =
                "SELECT "
                        + DBHelper.KEY_RATING_REVIEWS + ", "
                        + DBHelper.KEY_TYPE_REVIEWS + ", "
                        + DBHelper.KEY_WORKING_TIME_ID_REVIEWS
                        + " FROM "
                        + DBHelper.TABLE_REVIEWS
                        + " WHERE "
                        + DBHelper.KEY_MESSAGE_ID_REVIEWS + " = ?";
        Cursor reviewCursor = database.rawQuery(reviewQuery, new String[]{message.getId()});

        if (reviewCursor.moveToFirst()) {
            //int indexReview = reviewCursor.getColumnIndex(DBHelper.KEY_WORKING_TIME_ID_REVIEWS);
            int indexRating = reviewCursor.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);
            int indexType = reviewCursor.getColumnIndex(DBHelper.KEY_TYPE_REVIEWS);
            int indexTimeId = reviewCursor.getColumnIndex(DBHelper.KEY_WORKING_TIME_ID_REVIEWS);

            do{
                String timeId = reviewCursor.getString(indexTimeId);
                String serviceId = getServiceId(timeId);
                boolean isMyService = isMyService(serviceId);
                String type = reviewCursor.getString(indexType);

                if((type.equals(REVIEW_FOR_USER) && isMyService) || (type.equals(REVIEW_FOR_SERVICE) && !isMyService)) {
                    boolean isRate = !reviewCursor.getString(indexRating).equals("0");
                    boolean isCanceled = getIsCanceled(timeId);
                    String time = getTime(timeId);
                    String date = getDate(timeId);
                    String serviceName = getServiceName(serviceId);

                    message.setIsCanceled(isCanceled);
                    message.setIsRate(isRate);
                    message.setType(type);
                    message.setOrderTime(time);
                    message.setDate(date);
                    message.setServiceName(serviceName);
                    message.setUserName(senderName);

                    addMessageReviewToScreen(message);
                }
            } while (reviewCursor.moveToNext());
        }

        reviewCursor.close();
    }

    private String getDate(String timeId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // Получает id сервиса
        // Таблицы:  working time, working days, services
        // Условия: уточняем id времени и id сервиса, объеденяем таблицы по id дня и id сервиса
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_DATE_WORKING_DAYS
                        + " FROM "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + " = ? "
                        + " AND "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                        + " = "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID;

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{timeId});

        String date = "";
        if(cursor.moveToFirst()) {
            date = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS));
        }

        cursor.close();
        return date;
    }

    private String getTime(String timeId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // Получает время
        // Таблицы:  working time
        // Условия: уточняем id времени
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_TIME_WORKING_TIME
                        + " FROM "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.KEY_ID + " = ? ";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{timeId});

        String time = "";
        if(cursor.moveToFirst()) {
            time = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME));
        }

        cursor.close();
        return time;
    }

    private String getServiceId(String timeId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // Получает id сервиса
        // Таблицы:  working time, working days, services
        // Условия: уточняем id времени и id сервиса, объеденяем таблицы по id дня и id сервиса
        String sqlQuery =
                "SELECT "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID
                        + " AS " + SERVICE_ID
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID + " = ? "
                        + " AND "
                        + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                        + " = "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                        + " = "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID;

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{timeId});

        String serviceId = "";
        if(cursor.moveToFirst()) {
            serviceId = cursor.getString(cursor.getColumnIndex(SERVICE_ID));
        }

        return serviceId;
    }

    private boolean getIsCanceled(String timeId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // Получает телефон юзера
        // Таблицы:  working time
        // Условия: уточняем id времени
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_USER_ID
                        + " FROM "
                        + DBHelper.TABLE_WORKING_TIME
                        + " WHERE "
                        + DBHelper.KEY_ID + " = ? ";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{timeId});

        String phone = "";
        if(cursor.moveToFirst()) {
            phone = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_USER_ID));
        }
        cursor.close();
        return phone.equals("0");
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

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId, myPhone});
        return cursor.moveToFirst();
    }

    private String getServiceName(String serviceId) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // Получает имя сервиса
        // Таблицы: services
        // Условия: уточняем id сервиса
        String sqlQuery =
                "SELECT "
                        + DBHelper.KEY_NAME_SERVICES
                        + " FROM "
                        + DBHelper.TABLE_CONTACTS_SERVICES
                        + " WHERE "
                        + DBHelper.KEY_ID + " = ?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{serviceId});

        if(cursor.moveToFirst()){
            int indexName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);

            return cursor.getString(indexName);
        }
        cursor.close();
        return "";
    }

    private void addMessageOrderToScreen(Message message) {

            MessageOrderElement fElement = new MessageOrderElement(message);

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.resultsMessagesLayout, fElement);
            transaction.commit();
    }

    private void addMessageReviewToScreen(Message message) {
        if (!message.getServiceName().isEmpty() && !message.getServiceName().equals("null")) {
            MessageReviewElement fElement = new MessageReviewElement(message);

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.resultsMessagesLayout, fElement);
            transaction.commit();
        }
    }

    private String getUserId(){
        return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    }

    @Override
    protected void onResume() {
        super.onResume();

        messagesLayout.removeAllViews();
        createMessages();
    }
}
