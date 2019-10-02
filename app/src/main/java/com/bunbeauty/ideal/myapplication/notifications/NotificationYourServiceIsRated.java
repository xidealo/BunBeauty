
package com.bunbeauty.ideal.myapplication.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.presentation.profile.ProfileActivity;

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
        int notificationId = 5;

        Intent intent = new Intent(context, ProfileActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT );

        //создание notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bun_beauty)
                .setContentIntent(pIntent)
                .setContentTitle("Новая оценка!")
                .setContentText("Клиент " + name + " оценил ваш сервис \"" + serviceName + "\"")
                .setStyle(new NotificationCompat.BigTextStyle()
                      .bigText("Клиент " + name + " оценил ваш сервис \"" + serviceName + "\" Оценка будет доступна через 72 часа"))
                .setContentIntent(pIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }
}
