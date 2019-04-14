package com.example.ideal.myapplication.fragments.chatElements;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private static final String RATING = "rating";
    private static final String MESSAGE_ID = "message id";
    private static final String TYPE = "type";

    private static final String REVIEW_FOR_SERVICE = "review for service";

    private static final String USERS = "users";
    private static final String SERVISES = "services";
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

    private WorkWithTimeApi workWithTimeApi;

    private TextView messageText;
    private Button canceledBtn;

    public MessageOrderElement() { }

    @SuppressLint("ValidFragment")
    public MessageOrderElement(Message message) {
        // для условий
        messageIsCanceled = message.getIsCanceled();
        messageIsMyService = message.getIsMyService();

        // содержание сообщения
        String messageUserName = message.getUserName();
        String messageServiceName = message.getServiceName();
        messageWorkingDay= message.getWorkingDay();
        messageWorkingTime = message.getWorkingTime();
        String messageTime = message.getMessageTime();

        // для Reference
        userId = message.getUserId();
        serviceId = message.getServiceId();
        workingDayId = message.getWorkingDayId();
        workingTimeId = message.getWorkingTimeId();
        orderId = message.getOrderId();

        if(messageIsCanceled) {
            if (messageIsMyService) {
                text = "Вы отказали пользователю " + messageUserName
                        + " в предоставлении услуги " + messageServiceName
                        + ". Сеанс на " + messageWorkingDay
                        + " в " + messageWorkingTime + " отменён."
                        + "\n (" + messageTime + ")";
            } else {
                text = "Пользователь " + messageUserName
                        + " отказал Вам в придоставлении услуги " + messageServiceName
                        + ". Сеанс на " + messageWorkingDay
                        + " в " + messageWorkingTime + " отменён."
                        + "\n (" + messageTime + ")";
            }
        } else {
            if (messageIsMyService) {
                text = "Пользователь " + messageUserName
                        + " записался к вам на услугу " + messageServiceName
                        + ". Сеанс состоится " + messageWorkingDay
                        + " в " + messageWorkingTime
                        + ". Вы можете отказаться, указав причину, однако, если вы сделаете это слишком поздно, у пользователя будет возможность оценить Ваш сервис."
                        + "\n (" + messageTime + ")";
            } else {
                text = "Вы записались к " + messageUserName
                        + " на услугу " + messageServiceName
                        + ". Сеанс состоится " + messageWorkingDay
                        + " в " + messageWorkingTime
                        + "\n (" + messageTime + ")";
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

        if (!messageIsMyService) {
            canceledBtn.setVisibility(View.GONE);
        } else {
            if(!isRelevance() || messageIsCanceled){
                canceledBtn.setEnabled(false);
            }
        }

        setData();
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        messageText.setText(text);
    }

    public void confirm() {
        AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        dialog.setTitle("Отказ");
        dialog.setMessage("Отказать в предоставлении услуги?");
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                setIsCanceled();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
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

        if(isRelevance()) {
            cancel();
            //за час до
            if (beforeOneHour()) {
                // создаём сообщение с возможность написать ревью
            }
        }

        canceledBtn.setEnabled(false);
    }

    private boolean isRelevance() {
        String commonDate = messageWorkingDay + " " + messageWorkingTime;

        long orderDateLong = workWithTimeApi.getMillisecondsStringDate(commonDate);
        long sysdateLong = workWithTimeApi.getSysdateLong();

        return orderDateLong > sysdateLong;
    }

    private void cancel(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String myId = getUserId();
        DatabaseReference myRef;
        if (messageIsMyService) {
            myRef = database.getReference(USERS)
                    .child(myId)
                    .child(SERVISES)
                    .child(serviceId)
                    .child(WORKING_DAYS)
                    .child(workingDayId)
                    .child(WORKING_TIME)
                    .child(workingTimeId)
                    .child(ORDERS)
                    .child(orderId);
        } else {
            myRef = database.getReference(USERS)
                    .child(userId)
                    .child(SERVISES)
                    .child(serviceId)
                    .child(WORKING_DAYS)
                    .child(workingDayId)
                    .child(WORKING_TIME)
                    .child(workingTimeId)
                    .child(ORDERS)
                    .child(orderId);
        }
        Log.d(TAG, "cancel: " + myRef);

        Map<String, Object> items = new HashMap<>();

        items.put(IS_CANCELED, true);
        String newMessageTime = workWithTimeApi.getDateInFormatYMDHMS(new Date());
        items.put(TIME, newMessageTime);
        myRef.updateChildren(items);
        updateLocalStorage(newMessageTime);
    }


    private void createReview(String messageId){
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference(REVIEWS);
        Map<String,Object> items = new HashMap<>();

        items.put(RATING, "0");
        items.put(TYPE, REVIEW_FOR_SERVICE);
        items.put(REVIEW, "");
        items.put(MESSAGE_ID, messageId);
        //items.put(WORKING_TIME_ID, messageWorkingTimeId);

        String reviewId =  myRef.push().getKey();
        myRef = database.getReference(REVIEWS).child(reviewId);
        myRef.updateChildren(items);
    }

    private boolean beforeOneHour() {
        String commonDate = messageWorkingDay + " " + messageWorkingTime;
        long sysdateLong = workWithTimeApi.getSysdateLong();
        long orderDateLong = workWithTimeApi.getMillisecondsStringDate(commonDate);

        return orderDateLong - sysdateLong < 3600000;
    }

    private void updateLocalStorage(String newMessageTime) {
        // isCancled
        DBHelper dbHelper = new DBHelper(this.getContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_IS_CANCELED_ORDERS, "true");
        contentValues.put(DBHelper.KEY_MESSAGE_TIME_ORDERS, newMessageTime);

        database.update(DBHelper.TABLE_ORDERS, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(orderId)});
        contentValues.clear();
    }

    private  String getUserId(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}