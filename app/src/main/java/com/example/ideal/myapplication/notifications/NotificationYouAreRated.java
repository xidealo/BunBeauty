package com.example.ideal.myapplication.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.other.Profile;

public class NotificationYouAreRated extends NotificationConstructor {

    private static final String TAG = "DBInf";
    private static final String CHANNEL_ID = "1";
    private static final String OWNER_ID = "owner id";

    private Context context;
    private String name;
    private String workerId;

    public NotificationYouAreRated(Context _context, String _name) {
        context = _context;
        name = _name;
        /*workerId = _workerId;*/
    }

    @Override
    public void createNotification() {
        //нужен, чтобы потом обратиться к нему и если что изменить, в нашем случае вроде как не нужен

        Intent intent = new Intent(context, Profile.class);
      /*  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(OWNER_ID, workerId);*/
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        int notificationId = 1;

        //создание notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bun_beauty)
                .setContentTitle("Новая оценка!")
                .setContentText("Мастер " + name + " оценил вас!")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }

}
