package com.example.ideal.myapplication.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.other.Profile;

public class NotificationYouAreRated extends NotificationConstructor {

    private static final String TAG = "DBInf";
    private static final String CHANNEL_ID = "1";

    private Context context;
    private String name;

    public NotificationYouAreRated(Context _context, String _name) {
        context = _context;
        name = _name;
    }

    @Override
    public void createNotification() {
        int notificationId = 4;

        Intent intent = new Intent(context, Profile.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT );

        //создание notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bun_beauty)
                .setContentIntent(pIntent)
                .setContentTitle("Новая оценка!")
                .setContentText("Мастер " + name + " оценил вас!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }

}
