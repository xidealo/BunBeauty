package com.example.ideal.myapplication.notifications;


import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.ideal.myapplication.R;

public class NotificationCancel extends NotificationConstructor {

    public static final String TAG = "DBInf";
    private static final String CHANNEL_ID = "1";

    private Context context;
    private String workerName;
    private String serviceName;
    private String orderTime;
    private String orderDate;

    public NotificationCancel(Context _context, String _workerName, String _serviceName, String _orderDate, String _orderTime) {
        context = _context;
        workerName = _workerName;
        serviceName = _serviceName;
        orderDate = _orderDate;
        orderTime = _orderTime;
    }

    @Override
    public void createNotification() {
        //нужен, чтобы потом обратиться к нему и если что изменить, в нашем случае вроде как не нужен
        int notificationId = 1;

        //создание notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bun_beauty)
                .setContentTitle("Отказ в предоставлении услуги")
                .setContentText("Мастер " + workerName
                        + " отказал Вам в предоставлении услуги " + serviceName
                        + ". Сеанс на " + orderDate + " в " + orderTime + " отменён.")
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }

}
