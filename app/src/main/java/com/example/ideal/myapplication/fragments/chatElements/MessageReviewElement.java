package com.example.ideal.myapplication.fragments.chatElements;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    private static final String MESSAGE_ID = "message id";
    private static final String TYPE = "type";

    private static final String REVIEW_FOR_USER = "review for user";

    private String text;

    private String messageTime;
    private Boolean messageIsCanceled;
    private String messageType;
    private String messageDate;
    private String messageOrderTime;
    private String messageServiceName;
    private String messageUserName;
    private String messageRatingReview;

    private String messageId;

    private TextView messageText;
    private Button reviewBtn;

    public MessageReviewElement() {
    }

    @SuppressLint("ValidFragment")
    public MessageReviewElement(Message message) {
        messageTime = message.getMessageTime();
        messageIsCanceled = message.getIsCanceled();
        messageType = message.getType();
        messageOrderTime = message.getServiceTime();
        messageServiceName = message.getServiceName();
        messageUserName = message.getUserName();
        messageRatingReview = message.getRatingReview();
        messageDate = message.getMessageDate();

        messageId = message.getId();

        if (messageType.equals(REVIEW_FOR_USER)) {
            text =
                    messageDate + " в " + messageOrderTime
                            + " Вы предоставляли услугу " + messageServiceName
                            + " пользователю " + messageUserName
                            + ".\nПожалуйста, оставьте отзыв об этом пользователе, чтобы улучшить качество сервиса."
                            + " Вы также сможете увидеть отзыв, о себе,"
                            + " как только пользователь оставит его или пройдет 72 часа."
                            + "\n (" + messageTime + ")";
        } else {
            if (messageIsCanceled) {
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
        if (isMessageRate()) {
            reviewBtn.setEnabled(false);
        } else {
            reviewBtn.setOnClickListener(this);
        }
        setData();
    }

    //если рейтинг не 0, значит считаем, что оценен
    private boolean isMessageRate() {
        return !messageRatingReview.equals("0");
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