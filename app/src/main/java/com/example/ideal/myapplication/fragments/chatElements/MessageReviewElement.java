package com.example.ideal.myapplication.fragments.chatElements;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.example.ideal.myapplication.reviews.Review;


public class MessageReviewElement extends Fragment implements View.OnClickListener {

    private static final String TAG = "DBInf";

    //private static final String SERVICE_ID = "service id";
    private static final String MESSAGE_ID = "message id";
    private static final String TYPE = "type";
    //private static final String IS_MY_SERVICE = "is my service";

    private static final String REVIEW_FOR_USER = "review for user";


    String text;

    String messageTime;
    Boolean messageIsCanceled;
    Boolean messageIsRate;
    String messageType;
    String messageDate;
    String messageOrderTime;
    String messageServiceName;
    String messageUserName;

    String messageId;

    TextView messageText;
    Button reviewBtn;

    public MessageReviewElement() {
    }

    @SuppressLint("ValidFragment")
    public MessageReviewElement(Message message) {
        messageTime = message.getMessageTime();
        //messageIsMyService = message.getIsMyService();
        messageIsCanceled = message.getIsCanceled();
        messageIsRate = message.getIsRate();
        messageType = message.getType();
        messageDate = message.getDate();
        messageOrderTime = message.getOrderTime();
        messageServiceName = message.getServiceName();
        messageUserName = message.getUserName();

        //serviceId = message.getServiceId();
        messageId = message.getId();

        if(messageType.equals(REVIEW_FOR_USER)) {
            text =
                    messageDate + " в " + messageOrderTime
                    + " Вы предоставляли услугу " + messageServiceName
                    + " пользователю " + messageUserName
                    + ".\nПожалуйста, оставьте отзыв об этом пользователе, чтобы улучшить качество сервиса."
                    + " Вы также сможете увидеть отзыв, о себе,"
                    + " как только пользователь оставит его или пройдет 72 часа."
                    + "\n (" + messageTime + ")";
        } else {
            if(messageIsCanceled) {
                text =
                        "Пользователь " + messageUserName
                        + " отказал Вам в придоставлении услуги " + messageServiceName
                        + " в последний момент. Сеанс на " + messageDate
                        + " в " + messageOrderTime
                        + " отменён. Вы можете оценить качество данного сервиса."
                        + "\n (" + messageTime + ")";
            } else {
                text =
                        messageDate + " в " + messageOrderTime
                        + " Вы получали услугу " + messageServiceName
                        + " у пользователя " + messageUserName
                        + ".\nПожалуйста, оставьте отзыв о данной услуге, чтобы улучшить качество сервиса."
                        + " Вы также сможете увидеть отзыв, о себе,"
                        + " как только пользователь оставит его или пройдет 72 часа."
                        + "\n (" + messageTime + ")";
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_review_element, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        messageText = view.findViewById(R.id.messageMessageReviewElementText);
        reviewBtn = view.findViewById(R.id.reviewMessageReviewElementBtn);

        // Проверяем стоит ли оценка
        if(messageIsRate) {
            reviewBtn.setEnabled(false);
        } else {
            reviewBtn.setOnClickListener(this);
        }

        setData();
    }

    private void setData() {
        messageText.setText(text);
    }

    @Override
    public void onClick(View v) {
        goToReview();
    }

    private void goToReview() {
        Intent intent = new Intent(this.getContext(), Review.class);
        intent.putExtra(TYPE, messageType);
        intent.putExtra(MESSAGE_ID, messageId);
        startActivity(intent);
    }
}