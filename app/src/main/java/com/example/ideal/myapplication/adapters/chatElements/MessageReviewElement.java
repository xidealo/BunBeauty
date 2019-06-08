package com.example.ideal.myapplication.adapters.chatElements;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.fragments.objects.Message;
import com.example.ideal.myapplication.reviews.Review;


public class MessageReviewElement implements View.OnClickListener {

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
    private Context context;

    public MessageReviewElement(Message message, View view, Context context) {

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
        this.context = context;

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
        onViewCreated(view);
    }

    private void onViewCreated(@NonNull View view) {
        messageText = view.findViewById(R.id.messageMessageOrderElementText);
        reviewBtn = view.findViewById(R.id.canceledMessageOrderElementBtn);
        reviewBtn.setText("ОЦЕНИТЬ");
        // Проверяем стоит ли оценка
        if (isRate()) {
            reviewBtn.setVisibility(View.GONE);
        } else {
            reviewBtn.setVisibility(View.VISIBLE);
            reviewBtn.setOnClickListener(this);
        }

        LinearLayout layout = view.findViewById(R.id.messageOrderElementLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);
        layout.setLayoutParams(params);

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
        Intent intent = new Intent(context, Review.class);
        intent.putExtra(TYPE, messageType);
        intent.putExtra(REVIEW_ID, reviewId);
        context.startActivity(intent);
    }
}