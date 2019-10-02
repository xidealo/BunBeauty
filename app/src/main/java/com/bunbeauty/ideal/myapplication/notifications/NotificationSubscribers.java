package com.bunbeauty.ideal.myapplication.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.ui.activities.profile.ProfileActivity;

public class NotificationSubscribers extends NotificationConstructor {

    private static final String TAG = "DBInf";
    private static final String CHANNEL_ID = "1";

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
        int notificationId = 3;

        Intent intent = new Intent(context, ProfileActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT );

        //создание notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bun_beauty)
                .setContentIntent(pIntent)
                .setContentTitle("Новый подписчик!")
                .setContentText("На вас подписался " + WorkWithStringsApi.doubleCapitalSymbols(name))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("На вас подписался " + WorkWithStringsApi.doubleCapitalSymbols(name)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }
}
