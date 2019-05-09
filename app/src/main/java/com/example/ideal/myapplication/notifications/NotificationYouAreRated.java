package com.example.ideal.myapplication.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.ideal.myapplication.R;

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
        //нужен, чтобы потом обратиться к нему и если что изменить, в нашем случае вроде как не нужен
        int notificationId = 1;
        Log.d(TAG, "createReviewNotification: ");

        //создание notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bun_beauty)
                .setContentTitle("BunBeauty")
                .setContentText("Мастер " + name + " оценил вас!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }

}
