package com.example.ideal.myapplication.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.other.Profile;

public class NotificationOrder extends NotificationConstructor {

    private static final String TAG = "DBInf";
    private static final String CHANNEL_ID = "1";
    private static final String OWNER_ID = "owner id";

    private Context context;
    private String name;
    private String userId;
    private String serviceName;
    private String workingDate;
    private String workingTime;

    public NotificationOrder(Context _context, String _userId, String _name, String _serviceName,
                             String _workingDate, String _workingTime) {
        context = _context;
        name = _name;
        userId = _userId;
        serviceName = _serviceName;
        workingDate = _workingDate;
        workingTime = _workingTime;
    }

    @Override
    public void createNotification() {
        // нужен, чтобы потом обратиться к нему и если что изменить, в нашем случае вроде как не нужен
        int notificationId = 1;

        // создание PendingIntent для перехода в нужный активити при нажатии
        Intent intent = new Intent(context, Profile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(OWNER_ID, userId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        //создание notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bun_beauty)
                .setContentTitle("BunBeauty")
                .setContentText("Пользователь " + name
                        + " записался к вам на услугу " + serviceName
                        + ". Сеанс состоится " + workingDate
                        + " в " + workingTime + ".")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(notificationId, builder.build());
    }

}
