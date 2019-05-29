package com.example.ideal.myapplication.fragments.chatElements;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.objects.Message;
import com.example.ideal.myapplication.helpApi.WorkWithTimeApi;
import com.example.ideal.myapplication.other.DBHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MessageOrderElement extends Fragment implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private static final String REVIEWS = "reviews";
    private static final String REVIEW = "review";

    private static final String REVIEW_FOR_SERVICE = "'review for service'";

    private static final String USERS = "users";
    private static final String SERVICES = "services";
    private static final String WORKING_DAYS = "working days";
    private static final String WORKING_TIME = "working time";
    private static final String ORDERS = "orders";

    private static final String IS_CANCELED = "is canceled";
    private static final String TIME = "time";

    private Boolean messageIsCanceled;
    private Boolean messageIsMyService;

    private String text;
    private String messageWorkingDay;
    private String messageWorkingTime;

    private String userId;
    private String serviceId;
    private String workingTimeId;
    private String workingDayId;
    private String orderId;
    private String reviewId;

    private WorkWithTimeApi workWithTimeApi;

    private TextView messageText;
    private Button canceledBtn;

    private DBHelper dbHelper;

    public MessageOrderElement() {
    }

    @SuppressLint("ValidFragment")
    public MessageOrderElement(Message message) {
        // для условий
        messageIsCanceled = message.getIsCanceled();
        messageIsMyService = message.getIsMyService();

        // содержание сообщения
        String messageUserName = message.getUserName();
        String messageServiceName = message.getServiceName();
        messageWorkingDay = message.getWorkingDay();
        messageWorkingTime = message.getWorkingTime();
        String messageTime = message.getMessageTime();

        // для Reference
        userId = message.getUserId();
        serviceId = message.getServiceId();
        workingDayId = message.getWorkingDayId();
        workingTimeId = message.getWorkingTimeId();
        orderId = message.getOrderId();
        reviewId = message.getReviewId();

        if (messageIsCanceled) {
            if (messageIsMyService) {
                text = "(" + messageTime + ") \n"
                        + "Вы отказали пользователю " + messageUserName
                        + " в предоставлении услуги " + messageServiceName
                        + ". Сеанс на " + messageWorkingDay
                        + " в " + messageWorkingTime + " отменён.";
            } else {
                text = "(" + messageTime + ") \n"
                        + "Пользователь " + messageUserName
                        + " отказал Вам в придоставлении услуги " + messageServiceName
                        + ". Сеанс на " + messageWorkingDay
                        + " в " + messageWorkingTime + " отменён.";
            }
        } else {
            if (messageIsMyService) {
                text = "(" + messageTime + ") \n"
                        + "Пользователь " + messageUserName
                        + " записался к вам на услугу " + messageServiceName
                        + ". Сеанс состоится " + messageWorkingDay
                        + " в " + messageWorkingTime
                        + ". Вы можете отказаться, указав причину, однако, если вы сделаете это слишком поздно, у пользователя будет возможность оценить Ваш сервис.";
            } else {
                text = "(" + messageTime + ") \n"
                        + "Вы записались к " + messageUserName
                        + " на услугу " + messageServiceName
                        + ". Сеанс состоится " + messageWorkingDay
                        + " в " + messageWorkingTime;
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_order_element, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        messageText = view.findViewById(R.id.messageMessageOrderElementText);
        canceledBtn = view.findViewById(R.id.canceledMessageOrderElementBtn);
        canceledBtn.setOnClickListener(this);

        workWithTimeApi = new WorkWithTimeApi();
        dbHelper = new DBHelper(this.getContext());

        if (!messageIsMyService) {
            canceledBtn.setVisibility(View.GONE);
        } else {
            if (!isRelevance() || messageIsCanceled) {
                canceledBtn.setVisibility(View.GONE);
            }
        }

        LinearLayout layout = view.findViewById(R.id.messageOrderElementLayout);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.setMargins(10, 10, 10, 15);
        params.gravity = Gravity.BOTTOM;
        layout.setLayoutParams(params);

        setData();
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        messageText.setText(text);
    }

    public void confirm() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Отказ");
        dialog.setMessage("Отказать в предоставлении услуги?");
        dialog.setCancelable(false);

        dialog.setPositiveButton(Html.fromHtml("<b><font color='#FF7F27'>Да</font></b>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                setIsCanceled();
            }
        });
        dialog.setNegativeButton(Html.fromHtml("<b><font color='#FF7F27'>Нет</font></b>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        confirm();
    }

    // изменить время сообщения !!!
    private void setIsCanceled() {
        //Отказываем юзеру в услуге за ЧАС до ее исполнения
        //Иначе даем возможность написать ревью

        // если разница между заказом и временем, которое сейчас меньше часа, отмена без review
        // isRelevance нужен, чтобы пользователь, как прошло время, не смог отменить заказ,
        // будучи на активити

        if (isRelevance()) {
            cancel();

            // отнимаем возможность оставить отзыв клиенту (потому что отказали ему в обслуживании)
            disableReviewForUser();

            //за час до
            if (!beforeOneHour()) {
                // отнимаем возможность оставить отзыв мастеру (потому что он отказал в обслуживании заранее)
                disableReviewForService();
            }
        }

        canceledBtn.setEnabled(false);
    }

    private void disableReviewForUser() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        DatabaseReference myRef = database.getReference(USERS)
                .child(userId)
                .child(ORDERS)
                .child(orderId)
                .child(REVIEWS)
                .child(reviewId);

        Map<String, Object> items = new HashMap<>();

        items.put(REVIEW, "-");
        myRef.updateChildren(items);
        updateReviewInLocalStorage(reviewId);
    }

    private String getServiceReviewId() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String query = "SELECT " + DBHelper.KEY_ID
                + " FROM " + DBHelper.TABLE_REVIEWS
                + " WHERE " + DBHelper.KEY_ORDER_ID_REVIEWS + " = ?"
                + " AND " + DBHelper.KEY_TYPE_REVIEWS + " = " + REVIEW_FOR_SERVICE;

        Cursor cursor = database.rawQuery(query, new String[]{orderId});

        String userReviewId = "";
        if (cursor.moveToFirst()) {
            userReviewId = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_ID));
        }

        cursor.close();
        return userReviewId;
    }

    private void disableReviewForService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // id отзыва об услуге
        String serviceReviewId = getServiceReviewId();
        String myId = getUserId();

        DatabaseReference myRef = database.getReference(USERS)
                .child(myId)
                .child(SERVICES)
                .child(serviceId)
                .child(WORKING_DAYS)
                .child(workingDayId)
                .child(WORKING_TIME)
                .child(workingTimeId)
                .child(ORDERS)
                .child(orderId)
                .child(REVIEWS)
                .child(serviceReviewId);

        Map<String, Object> items = new HashMap<>();

        items.put(REVIEW, "-");
        myRef.updateChildren(items);
        updateReviewInLocalStorage(reviewId);
    }

    private void updateReviewInLocalStorage(String reviewId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_REVIEW_REVIEWS, "-");

        database.update(DBHelper.TABLE_REVIEWS, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(reviewId)});
    }

    private boolean isRelevance() {
        String commonDate = messageWorkingDay + " " + messageWorkingTime;

        long orderDateLong = workWithTimeApi.getMillisecondsStringDate(commonDate);
        long sysdateLong = workWithTimeApi.getSysdateLong();

        return orderDateLong > sysdateLong;
    }

    private void cancel() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String myId = getUserId();
        DatabaseReference myRef;
        // if (messageIsMyService) {
        myRef = database.getReference(USERS)
                .child(myId)
                .child(SERVICES)
                .child(serviceId)
                .child(WORKING_DAYS)
                .child(workingDayId)
                .child(WORKING_TIME)
                .child(workingTimeId)
                .child(ORDERS)
                .child(orderId);

        Map<String, Object> items = new HashMap<>();

        items.put(IS_CANCELED, true);
        String newMessageTime = workWithTimeApi.getDateInFormatYMDHMS(new Date());
        items.put(TIME, newMessageTime);
        myRef.updateChildren(items);
        updateOrderInLocalStorage(newMessageTime);
    }

    private boolean beforeOneHour() {
        String commonDate = messageWorkingDay + " " + messageWorkingTime;
        long sysdateLong = workWithTimeApi.getSysdateLong();
        long orderDateLong = workWithTimeApi.getMillisecondsStringDate(commonDate);

        return orderDateLong - sysdateLong < 3600000;
    }

    private void updateOrderInLocalStorage(String newMessageTime) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_IS_CANCELED_ORDERS, "true");
        contentValues.put(DBHelper.KEY_MESSAGE_TIME_ORDERS, newMessageTime);

        database.update(DBHelper.TABLE_ORDERS, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(orderId)});
    }

    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}