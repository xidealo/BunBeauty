package com.example.ideal.myapplication.chat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    private static final String DIALOG_ID = "dialog id";

    private static final String SERVICE_ID = "service_id";

    private static final String REVIEW_FOR_SERVICE = "review for service";
    private static final String REVIEW_FOR_USER = "review for user";
    private static final String USER_ID = "user id";
    private String senderName;
    private DBHelper dbHelper;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages);

        dbHelper = new DBHelper(this);

        // получаем телефон нашего собеседеника
        manager = getSupportFragmentManager();
        String senderId = getIntent().getStringExtra(USER_ID);
        senderName = getSenderName(senderId);

        PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.buildFooter(manager, R.id.footerEditServiceLayout);
        panelBuilder.buildHeader(manager, senderName, R.id.headerEditServiceLayout, senderId);
        //кто-то записан к нам
        addMessage(senderId);
        //мы записаны к кому-то
        addMessageSecond(senderId);
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
    private void addMessage(String senderId) {

    //добавить еще проверку есть ли поля у ревью этого ордера, если да, то добавляем ревью, если нет то ордер
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // вернуть заказы тех, кто записан на мои сервисы
        // нужно ли еще уточнять в serviceTable, что айди владельца равно нашему?
        String orderQuery =
                "SELECT DISTINCT "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID +", "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID +", "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_IS_CANCELED_ORDERS +", "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_MESSAGE_TIME_ORDERS +", "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_WORKING_TIME_ID_ORDERS + ","
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_TIME_WORKING_TIME + ","
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_DATE_WORKING_DAYS + ","
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + ","
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_NAME_SERVICES + ","
                        + DBHelper.TABLE_REVIEWS + "." + DBHelper.KEY_RATING_REVIEWS
                        + " FROM "
                        + DBHelper.TABLE_ORDERS + ", "
                        + DBHelper.TABLE_WORKING_TIME + ", "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                        + DBHelper.TABLE_REVIEWS + ", "
                        + DBHelper.TABLE_CONTACTS_USERS
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
                        + " AND "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID
                        + " = "
                        + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + " = ? ";

        Cursor cursor = database.rawQuery(orderQuery, new String[]{senderId});

        if (cursor.moveToFirst()) {
            int indexMessageId = cursor.getColumnIndex(DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID );
            int indexMessageUserId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);
            int indexMessageIsCanceled = cursor.getColumnIndex(DBHelper.KEY_IS_CANCELED_ORDERS);
            int indexMessageOrderTime = cursor.getColumnIndex(DBHelper.KEY_MESSAGE_TIME_ORDERS);
            int indexMessageWorkingTimeId = cursor.getColumnIndex(DBHelper.KEY_WORKING_TIME_ID_ORDERS);
            int indexMessageServiceTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);
            int indexMessageServiceDate = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS);
            int indexMessageServiceName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            int indexMessageServiceId = cursor.getColumnIndex(DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID);
            int indexMessageRatingReview = cursor.getColumnIndex(DBHelper.KEY_RATING_REVIEWS);

            do {
                String date = cursor.getString(indexMessageServiceDate);
                String time = cursor.getString(indexMessageServiceTime);
                String commonDate = date + " " + time;

                String serviceId = cursor.getString(indexMessageServiceId);
                Message message = new Message();
                message.setId(cursor.getString(indexMessageId));
                message.setUserId(cursor.getString(indexMessageUserId));
                message.setIsCanceled(Boolean.valueOf(cursor.getString(indexMessageIsCanceled)));
                message.setMessageTime(cursor.getString(indexMessageOrderTime));
                message.setWorkingTimeId(cursor.getString(indexMessageWorkingTimeId));
                message.setServiceTime(time);
                message.setMessageDate(date);
                message.setServiceName(cursor.getString(indexMessageServiceName));
                message.setUserName(senderName);
                message.setIsMyService(isMyService(serviceId));

                // проверяем, если у ордера время записи прошло + 24 часа, тогда сорздаем не ордер, а ревью.
                if(isAfterOrderTime(commonDate)){
                    message.setType(REVIEW_FOR_USER);
                    //если рейтинг не 0, значит считаем, что оценен
                    message.setRatingReview(cursor.getString(indexMessageRatingReview));
                    addMessageReviewToScreen(message);
                }
                else {
                    addMessageOrderToScreen(message);
                }
            }while (cursor.moveToNext());

        }
        cursor.close();
    }

    private void addMessageSecond(String senderId) {

        //добавить еще проверку есть ли поля у ревью этого ордера, если да, то добавляем ревью, если нет то ордер
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // вернуть заказы тех, кто записан на мои сервисы
        String orderQuery =
                "SELECT DISTINCT "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID +", "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID +", "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_IS_CANCELED_ORDERS +", "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_MESSAGE_TIME_ORDERS +", "
                        + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_WORKING_TIME_ID_ORDERS + ","
                        + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_TIME_WORKING_TIME + ","
                        + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_DATE_WORKING_DAYS + ","
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID + ","
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_NAME_SERVICES
                        + " FROM "
                        + DBHelper.TABLE_ORDERS + ", "
                        + DBHelper.TABLE_WORKING_TIME + ", "
                        + DBHelper.TABLE_WORKING_DAYS + ", "
                        + DBHelper.TABLE_CONTACTS_SERVICES + ", "
                        + DBHelper.TABLE_CONTACTS_USERS
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
                        + " AND "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID
                        + " = "
                        + DBHelper.TABLE_CONTACTS_USERS + "." + DBHelper.KEY_ID
                        + " AND "
                        + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID + " = ? ";

        Cursor cursor = database.rawQuery(orderQuery, new String[]{senderId});

        if (cursor.moveToFirst()) {
            int indexMessageId = cursor.getColumnIndex(DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_ID );
            int indexMessageUserId = cursor.getColumnIndex(DBHelper.KEY_USER_ID);
            int indexMessageIsCanceled = cursor.getColumnIndex(DBHelper.KEY_IS_CANCELED_ORDERS);
            int indexMessageOrderTime = cursor.getColumnIndex(DBHelper.KEY_MESSAGE_TIME_ORDERS);
            int indexMessageWorkingTimeId = cursor.getColumnIndex(DBHelper.KEY_WORKING_TIME_ID_ORDERS);
            int indexMessageTime = cursor.getColumnIndex(DBHelper.KEY_TIME_WORKING_TIME);
            int indexMessageDate = cursor.getColumnIndex(DBHelper.KEY_DATE_WORKING_DAYS);
            int indexMessageServiceName = cursor.getColumnIndex(DBHelper.KEY_NAME_SERVICES);
            int indexMessageServiceId = cursor.getColumnIndex(DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID);

            do {
                String serviceId = cursor.getString(indexMessageServiceId);
                Message message = new Message();
                message.setId(cursor.getString(indexMessageId));
                message.setUserId(cursor.getString(indexMessageUserId));
                message.setIsCanceled(Boolean.valueOf(cursor.getString(indexMessageIsCanceled)));
                message.setMessageTime(cursor.getString(indexMessageOrderTime));
                message.setWorkingTimeId(cursor.getString(indexMessageWorkingTimeId));
                message.setServiceTime(cursor.getString(indexMessageTime));
                message.setMessageDate(cursor.getString(indexMessageDate));
                message.setServiceName(cursor.getString(indexMessageServiceName));
                message.setUserName(senderName);
                message.setIsMyService(isMyService(serviceId));

                // проверяем, если у ордера время записи прошло + 24 часа, тогда сорздаем не ордер, а ревью.
                addMessageOrderToScreen(message);

            }while (cursor.moveToNext());

        }
        cursor.close();
    }

    private boolean isAfterOrderTime(String commonDate) {
        WorkWithTimeApi workWithTimeApi = new WorkWithTimeApi();
        //3600000 * 24 = 24 часа
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

    private  String getUserId(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private String getUserMyPhone(){
        return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
