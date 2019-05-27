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
import com.example.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;

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

    private String senderId;
    private String senderName;
    private DBHelper dbHelper;
    private FragmentManager manager;

    LinearLayout resultsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);

        dbHelper = new DBHelper(this);
        resultsLayout = findViewById(R.id.resultsMessagesLayout);

        // получаем телефон нашего собеседеника
        manager = getSupportFragmentManager();
        senderId = getIntent().getStringExtra(USER_ID);
        senderName = getSenderName(senderId);
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
        if(cursor.moveToFirst()){
            int indexName = cursor.getColumnIndex(DBHelper.KEY_NAME_USERS);
            return cursor.getString(indexName);
        }
        cursor.close();
        return "";
    }

    // получить все ордеры, у которых userId = userId собеседника.
    private void addMessages(String senderId) {

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

        String myId = getUserId();
        Cursor cursor = database.rawQuery(orderQuery, new String[]{senderId, myId, senderId, myId});

        if (cursor.moveToFirst()) {

            int indexMessageIsCanceled = cursor.getColumnIndex(DBHelper.KEY_IS_CANCELED_ORDERS);

            int indexMessageServiceName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            int indexMessageServiceDate = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS);
            int indexMessageWorkingTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);
            int indexMessageTime = cursor.getColumnIndex(DBHelper.KEY_MESSAGE_TIME_ORDERS);
            //int indexMessageUserId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);

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

                message.setUserName(senderName);
                message.setServiceName(cursor.getString(indexMessageServiceName));
                message.setWorkingDay(date);
                message.setWorkingTime(time);
                message.setMessageTime(cursor.getString(indexMessageTime));
                //message.setUserId(cursor.getString(indexMessageUserId));

                message.setReviewId(cursor.getString(indexMessageReviewId));

                String type = cursor.getString(indexMessageTypeReview);
                String review = cursor.getString(indexMessageReview);

                // Если сообщение связано с услугой на которую я записался
                // проверяем, если у ордера время записи прошло + 24 часа, тогда сорздаем не ордер, а ревью.
                if ((isAfterOrderTime(date, time) && !isCanceled) || (isCanceled && !review.equals("-"))) {
                    if (isMyService && type.equals(REVIEW_FOR_USER) || !isMyService && type.equals(REVIEW_FOR_SERVICE)) {
                        message.setRatingReview(cursor.getString(indexMessageRatingReview));
                        message.setType(type);
                        addMessageReviewToScreen(message);
                    }
                } else {
                    if (isMyService && type.equals(REVIEW_FOR_USER) || !isMyService && type.equals(REVIEW_FOR_SERVICE)) {
                        message.setUserId(senderId);
                        message.setServiceId(serviceId);
                        message.setWorkingTimeId(cursor.getString(indexMessageWorkingTimeId));
                        message.setWorkingDayId(cursor.getString(indexMessageWorkingDayId));
                        message.setOrderId(cursor.getString(indexMessageOrderId));

                        addMessageOrderToScreen(message);
                    }
                }
            } while (cursor.moveToNext());

        }
        cursor.close();
    }

    private boolean isAfterOrderTime(String date, String time) {
        WorkWithTimeApi workWithTimeApi = new WorkWithTimeApi();
        //3600000 * 24 = 24 часа
        String commonDate = date + " " + time;
        Long orderDateLong = workWithTimeApi.getMillisecondsStringDate(commonDate) +3600000*24;
        Long sysdate = workWithTimeApi.getSysdateLong();

        return sysdate>orderDateLong;
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

    private  String getUserId(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onResume() {

        super.onResume();
        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerEditServiceLayout);
        panelBuilder.buildHeader(manager, senderName, R.id.headerEditServiceLayout, senderId);

        resultsLayout.removeAllViews();
        addMessages(senderId);
    }
}
