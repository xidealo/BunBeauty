package com.example.ideal.myapplication.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.example.ideal.myapplication.other.Profile;

public class NotificationSubscribers extends NotificationConstructor {

    private static final String TAG = "DBInf";
    private static final String CHANNEL_ID = "1";
    private static final String OWNER_ID = "owner id";

    private String userId;
    private String name;
    private Context context;

    public NotificationSubscribers(Context context, String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.context = context;
    }

    @Override
    public void createNotification() {

        //нужен, чтобы потом обратиться к нему и если что изменить, в нашем случае вроде как не нужен
        int notificationId = 1;

        Intent intent = new Intent(context, Profile.class);
        intent.putExtra(OWNER_ID, userId);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT );

        //создание notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bun_beauty)
                .setContentIntent(pIntent)
                .setContentTitle("Новый подписчик!")
                .setContentText("На вас подписался " + WorkWithStringsApi.doubleCapitalSymbols(name))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }
}
