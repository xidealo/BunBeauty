package com.example.ideal.myapplication.notifications;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.example.ideal.myapplication.searchService.GuestService;

public class NotificationCancel extends NotificationConstructor {

    public static final String TAG = "DBInf";
    private static final String CHANNEL_ID = "1";
    private static final String SERVICE_ID = "service id";

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
        //нужен, чтобы потом обратиться к нему и если что изменить, в нашем случае вроде как не нужен
        int notificationId = 1;

        Intent intent = new Intent(context, GuestService.class);
        intent.putExtra(SERVICE_ID, serviceId);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT );

        //создание notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bun_beauty)
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

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }

}
