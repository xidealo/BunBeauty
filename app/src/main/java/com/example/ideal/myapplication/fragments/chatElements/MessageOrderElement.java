package com.example.ideal.myapplication.fragments.chatElements;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static android.provider.Telephony.BaseMmsColumns.MESSAGE_ID;


public class MessageOrderElement extends Fragment implements View.OnClickListener {

    private static final String TAG = "DBInf";

    private static final String ORDERS = "orders";
    private static final String IS_CANCELED = "is canceled";

    private static final String MESSAGES = "messages";
    private static final String MESSAGE_TIME = "message time";
    private static final String DIALOG_ID = "dialog id";

    private static final String REVIEWS = "reviews";
    private static final String REVIEW = "review";
    private static final String RATING = "rating";
    private static final String MESSAGE_ID = "message id";
    private static final String WORKING_TIME_ID = "working time id";
    private static final String TYPE = "type";

    private static final String WORKING_TIME = "working time";
    private static final String USER_ID = "user id";

    private static final String REVIEW_FOR_SERVICE = "review for service";

    private String text;

    private String messageTime;
    private Boolean messageIsCanceled;
    private Boolean messageIsMyService;
    private String messageDate;
    private String messageOrderTime;
    private String messageServiceName;
    private String messageUserName;
    private String messageWorkingTimeId;
    private String messageOrderId;
    private String messageDialogId;

    private WorkWithTimeApi workWithTimeApi;

    private TextView messageText;
    private Button canceledBtn;

    public MessageOrderElement() { }

    @SuppressLint("ValidFragment")
    public MessageOrderElement(Message message) {
        messageTime = message.getMessageTime();
        messageIsCanceled = message.getIsCanceled();
        messageIsMyService = message.getIsMyService();
        messageDate = message.getDate();
        messageOrderTime = message.getOrderTime();
        messageServiceName = message.getServiceName();
        messageUserName = message.getUserName();
        messageWorkingTimeId = message.getTimeId();
        messageOrderId = message.getOrderId();
        messageDialogId = message.getDialogId();

        if(messageIsCanceled) {
            if (messageIsMyService) {
                text = "Вы отказали пользователю " + messageUserName
                        + " в предоставлении услуги " + messageServiceName
                        + ". Сеанс на " + messageDate
                        + " в " + messageOrderTime + "отменён."
                        + "\n (" + messageTime + ")";
            } else {
                text = "Пользователь " + messageUserName
                        + " отказал Вам в придоставлении услуги " + messageServiceName
                        + ". Сеанс на " + messageDate
                        + " в " + messageOrderTime + "отменён."
                        + "\n (" + messageTime + ")";
            }
        } else {
            if (messageIsMyService) {
                text = "Пользователь " + messageUserName
                        + " записался на услугу " + messageServiceName
                        + ". Сеанс состоится " + messageDate
                        + " в " + messageOrderTime
                        + ". Вы можете отказаться, указав причину, однако, если вы сделаете это слишком поздно, у пользователя будет возможность оценить Ваш сервис."
                        + "\n (" + messageTime + ")";
            } else {
                text = "Вы записались к " + messageUserName
                        + " на услугу " + messageServiceName
                        + ". Сеанс состоится " + messageDate
                        + " в " + messageOrderTime
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

        if((!isRelevance()) || (!messageIsMyService)){
            canceledBtn.setVisibility(View.INVISIBLE);
        } else {
            if(messageIsCanceled) {
                canceledBtn.setEnabled(false);
            }
        }

        setData();
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        messageText.setText(text);
    }

    @Override
    public void onClick(View v) {
        setIsCanceled();
    }

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
                createMessage();
            }
        }
    }

    private boolean isRelevance() {
        String commonDate = messageDate + " " + messageOrderTime;

        Long orderDateLong = workWithTimeApi.getMillisecondsStringDate(commonDate);
        Long sysdateLong = workWithTimeApi.getSysdateLong();

        return orderDateLong > sysdateLong;
    }

    private void cancel(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(ORDERS).child(messageOrderId);
        Map<String, Object> items = new HashMap<>();

        items.put(IS_CANCELED, true);
        myRef.updateChildren(items);
        clearPhone();
        updateLocalStorage();
    }

    private void createMessage() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(MESSAGES);
        Map<String,Object> items = new HashMap<>();

        String dateNow = workWithTimeApi.getCurDateInFormatHMS();

        items.put(MESSAGE_TIME, dateNow);
        items.put(DIALOG_ID, messageDialogId);

        String messageId =  myRef.push().getKey();
        createReview(messageId);
        myRef = database.getReference(MESSAGES).child(messageId);
        myRef.updateChildren(items);
    }

    private void createReview(String messageId){
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference(REVIEWS);
        Map<String,Object> items = new HashMap<>();

        items.put(RATING, "0");
        items.put(TYPE, REVIEW_FOR_SERVICE);
        items.put(REVIEW, "");
        items.put(MESSAGE_ID, messageId);
        items.put(WORKING_TIME_ID, messageWorkingTimeId);

        String reviewId =  myRef.push().getKey();
        myRef = database.getReference(REVIEWS).child(reviewId);
        myRef.updateChildren(items);
    }

    private boolean beforeOneHour() {
        String commonDate = messageDate + " " + messageOrderTime;
        Long sysdateLong = workWithTimeApi.getSysdateLong();
        Long orderDateLong = workWithTimeApi.getMillisecondsStringDate(commonDate);

        return orderDateLong - sysdateLong < 3600000;
    }

    private void clearPhone() {
        //получить id message
        //получить date (id working days)
        //сделать query по date в working time и получить id времени
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database
                .getReference(WORKING_TIME)
                .child(messageWorkingTimeId);

        Map<String, Object> items = new HashMap<>();
        items.put(USER_ID, "0");
        myRef.updateChildren(items);

        canceledBtn.setEnabled(false);
    }

    private void updateLocalStorage() {
        // isCancled
        DBHelper dbHelper = new DBHelper(this.getContext());
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.KEY_IS_CANCELED_ORDERS, "true");

        database.update(DBHelper.TABLE_ORDERS, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(messageOrderId)});
        contentValues.clear();

        // userId
        contentValues.put(DBHelper.KEY_USER_ID, "0");
        database.update(DBHelper.TABLE_WORKING_TIME, contentValues,
                DBHelper.KEY_ID + " = ?",
                new String[]{String.valueOf(messageWorkingTimeId)});

    }
}