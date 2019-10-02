package com.bunbeauty.ideal.myapplication.adapters.chatElements;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Message;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.bunbeauty.ideal.myapplication.reviews.Review;


public class MessageReviewElement implements View.OnClickListener {

    private static final String TAG = "DBInf";
    private static final String TYPE = "type";
    private static final String REVIEW_FOR_USER = "review for user";
    private static final String REVIEW_ID = "review id";
    private static final String USER_ID = "user id";
    private static final String SERVICE_ID = "service id";

    private String messageType;
    private String messageRatingReview;
    private String messageUserId;
    private String messageServiceId;
    private String text;
    private String messageTime;
    private String reviewId;
    private TextView messageText;
    private TextView timeText;
    private Context context;
    private View view;

    public MessageReviewElement(Message message, View view, Context context) {

        // для условий
        boolean isCanceled = message.getIsCanceled();
        messageType = message.getType();
        messageRatingReview = message.getRatingReview();

        // содержание сообщения
        // нету в сообщении
        this.messageUserId = message.getUserId();
        this.messageServiceId = message.getServiceId();
        String messageUserName = message.getUserName();
        String messageServiceName = message.getServiceName();
        String workingDayUserFormat = WorkWithStringsApi.dateToUserFormat(message.getWorkingDay());
        String messageWorkingTime = message.getWorkingTime();
        messageTime = message.getMessageTime();
        this.context = context;
        this.view = view;

        reviewId = message.getReviewId();

        if (messageType.equals(REVIEW_FOR_USER)) {
            text =
                    workingDayUserFormat + " в " + messageWorkingTime
                            + " Вы предоставляли услугу " + messageServiceName
                            + " пользователю " + messageUserName
                            + ".\nПожалуйста, оставьте отзыв об этом пользователе."
                            + " Вы также сможете увидеть отзыв, о себе,"
                            + " как только пройдет 72 часа.";
        } else {
            if (isCanceled) {
                text =
                        "Пользователь " + messageUserName
                                + " отказал Вам в придоставлении услуги " + messageServiceName
                                + " в последний момент. Сеанс на " + workingDayUserFormat
                                + " в " + messageWorkingTime
                                + " отменён. Вы можете оценить качество данного сервиса.";
            } else {
                text =
                        workingDayUserFormat + " в " + messageWorkingTime
                                + " Вы получали услугу " + messageServiceName
                                + " у пользователя " + messageUserName
                                + ".\nПожалуйста, оставьте отзыв о данной услуге, чтобы улучшить качество сервиса."
                                + " Вы также сможете увидеть отзыв, о себе,"
                                + " как только пройдет 72 часа.";
            }
        }
    }

    public void createElement(){
        onViewCreated(view);
    }

    private void onViewCreated(@NonNull View view) {
        messageText = view.findViewById(R.id.messageMessageElementText);
        timeText = view.findViewById(R.id.timeMessageElementText);
        Button reviewBtn = view.findViewById(R.id.canceledMessageElementBtn);
        reviewBtn.setText("ОЦЕНИТЬ");
        // Проверяем стоит ли оценка
        if (isRate()) {
            reviewBtn.setVisibility(View.GONE);
        } else {
            reviewBtn.setVisibility(View.VISIBLE);
            reviewBtn.setOnClickListener(this);
        }

        LinearLayout layout = view.findViewById(R.id.messageElementLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        layout.setLayoutParams(params);

        setData();
    }

    //если рейтинг не 0, значит считаем, что оценен
    private boolean isRate() {
        return !messageRatingReview.equals("0");
    }

    private void setData() {
        timeText.setText(messageTime);
        messageText.setText(text);
    }

    @Override
    public void onClick(View v) {
        goToReview();
    }

    private void goToReview() {
        Intent intent = new Intent(context, Review.class);
        intent.putExtra(TYPE, messageType);
        intent.putExtra(USER_ID, messageUserId);
        intent.putExtra(SERVICE_ID, messageServiceId);
        intent.putExtra(REVIEW_ID, reviewId);
        context.startActivity(intent);
    }
}