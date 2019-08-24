
package com.example.ideal.myapplication.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.other.Profile;

public class NotificationYourServiceIsRated extends NotificationConstructor {

    private static final String TAG = "DBInf";
    private static final String CHANNEL_ID = "1";

    private Context context;
    private String name;
    private String serviceName;

    public NotificationYourServiceIsRated(Context _context, String _name, String _serviceName) {
        context = _context;
        name = _name;
        serviceName = _serviceName;
    }

    @Override
    public void createNotification() {
        //нужен, чтобы потом обратиться к нему и если что изменить, в нашем случае вроде как не нужен
        Intent intent = new Intent(context, Profile.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        int notificationId = 1;

        //создание notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bun_beauty)
                .setContentTitle("Новая оценка!")
                .setContentText("Клиент " + name + " оценил ваш сервис \"" + serviceName + "\"")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Клиент " + name + " оценил ваш сервис \"" + serviceName + "\" Оценка будет доступна через 72 часа"))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }
}
