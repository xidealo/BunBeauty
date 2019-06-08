package com.example.ideal.myapplication.notifications;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.chat.Messages;

public class NotificationCancel extends NotificationConstructor {

    private static final String TAG = "DBInf";
    private static final String CHANNEL_ID = "1";
    private static final String USER_ID = "user id";

    private Context context;
    private String workerId;
    private String workerName;
    private String serviceName;
    private String orderTime;
    private String orderDate;

    public NotificationCancel(Context _context, String _workerId, String _workerName,
                              String _serviceName, String _orderDate, String _orderTime) {
        context = _context;
        workerId = _workerId;
        workerName = _workerName;
        serviceName = _serviceName;
        orderDate = _orderDate;
        orderTime = _orderTime;
    }

    @Override
    public void createNotification() {
        //нужен, чтобы потом обратиться к нему и если что изменить, в нашем случае вроде как не нужен
        int notificationId = 1;

        // создание PendingIntent для перехода в нужный активити при нажатии
        Intent intent = new Intent(context, Messages.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(USER_ID, workerId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        //создание notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bun_beauty)
                .setContentTitle("Отказ в предоставлении услуги")
                .setContentText("Мастер " + workerName
                        + " отказал Вам в предоставлении услуги " + serviceName
                        + ". Сеанс на " + orderDate + " в " + orderTime + " отменён.")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(notificationId, builder.build());
    }
}
