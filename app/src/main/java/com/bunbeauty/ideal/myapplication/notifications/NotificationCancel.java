package com.bunbeauty.ideal.myapplication.notifications;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity;

public class NotificationCancel extends NotificationConstructor {

    public static final String TAG = "DBInf";
    private static final String CHANNEL_ID = "1";

    private Context context;
    private String workerName;
    private String serviceName;
    private String serviceId;
    private String orderTime;
    private String orderDate;

    public NotificationCancel(Context context, String workerName, String serviceName,
                              String serviceId, String orderDate, String orderTime) {
        this.context = context;
        this.workerName = workerName;
        this.serviceName = serviceName;
        this.serviceId = serviceId;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
    }

    @Override
    public void createNotification() {
        int notificationId = 1;

        Intent intent = new Intent(context, ProfileActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT );

        //создание notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bun_beauty)
                .setContentIntent(pIntent)
                .setContentTitle("Отказ в предоставлении услуги")
                .setContentText("Мастер " + workerName
                        + " отказал Вам в предоставлении услуги " + serviceName
                        + ". Сеанс на " + orderDate + " в " + orderTime + " отменён.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Мастер " + workerName
                                + " отказал Вам в предоставлении услуги " + serviceName
                                + ". Сеанс на " + orderDate + " в " + orderTime + " отменён."))
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }

}
