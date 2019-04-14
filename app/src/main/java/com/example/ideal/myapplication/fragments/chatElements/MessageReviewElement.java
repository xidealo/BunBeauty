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
import com.google.firebase.database.DatabaseReference;


public class MessageReviewElement extends Fragment implements View.OnClickListener {

    private static final String TAG = "DBInf";
    private static final String TYPE = "type";
    private static final String REVIEW_FOR_USER = "review for user";
    private static final String REVIEW_ID = "review id";

    private String messageType;
    private String messageRatingReview;

    private String text;

    private String reviewId;

    private TextView messageText;
    private Button reviewBtn;

    public MessageReviewElement() {
    }

    @SuppressLint("ValidFragment")
    public MessageReviewElement(Message message) {

        // для условий
        boolean isCanceled = message.getIsCanceled();
        messageType = message.getType();
        messageRatingReview = message.getRatingReview();
        
        // содержание сообщения
        String messageUserName = message.getUserName();
        String messageServiceName = message.getServiceName();
        String messageWorkingDay= message.getWorkingDay();
        String messageWorkingTime = message.getWorkingTime();
        String messageTime = message.getMessageTime();

        reviewId = message.getReviewId();

        if (messageType.equals(REVIEW_FOR_USER)) {
            text =
                    messageWorkingDay + " в " + messageWorkingTime
                            + " Вы предоставляли услугу " + messageServiceName
                            + " пользователю " + messageUserName
                            + ".\nПожалуйста, оставьте отзыв об этом пользователе."
                            + " Вы также сможете увидеть отзыв, о себе,"
                            + " как только пользователь оставит его или пройдет 72 часа."
                            + "\n (" + messageTime + ")";
        } else {
            if (isCanceled) {
                text =
                        "Пользователь " + messageUserName
                                + " отказал Вам в придоставлении услуги " + messageServiceName
                                + " в последний момент. Сеанс на " + messageWorkingDay
                                + " в " + messageWorkingTime
                                + " отменён. Вы можете оценить качество данного сервиса."
                                + "\n (" + messageTime + ")";
            } else {
                text =
                        messageWorkingDay + " в " + messageWorkingTime
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

        Log.d(TAG, "onViewCreated: " + messageRatingReview);

        // Проверяем стоит ли оценка
        if (isRate()) {
            reviewBtn.setEnabled(false);
        } else {
            reviewBtn.setOnClickListener(this);
        }
        setData();
    }

    //если рейтинг не 0, значит считаем, что оценен
    private boolean isRate() {
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
        intent.putExtra(REVIEW_ID, reviewId);
        startActivity(intent);
    }
}