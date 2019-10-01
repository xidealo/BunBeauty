package com.bunbeauty.ideal.myapplication.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.presentation.profile.ProfileActivity;

public class NotificationOrder extends NotificationConstructor {

    private static final String TAG = "DBInf";
    private static final String CHANNEL_ID = "1";

    private Context context;
    private String name;
    private String serviceName;
    private String serviceId;
    private String workingDate;
    private String workingTime;

    public NotificationOrder(Context context, String name, String serviceName, String serviceId,
                             String workingDate, String workingTime) {
        this.context = context;
        this.name = name;
        this.serviceName = serviceName;
        this.serviceId = serviceId;
        this.workingDate = workingDate;
        this.workingTime = workingTime;
    }

    @Override
    public void createNotification() {
        int notificationId = 2;

        Intent intent = new Intent(context, ProfileActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT );

        //создание notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bun_beauty)
                .setContentIntent(pIntent)
                .setContentTitle("Новая запись!")
                .setContentText("Пользователь " + WorkWithStringsApi.doubleCapitalSymbols(name)
                        + " записался к вам на услугу " + WorkWithStringsApi.firstCapitalSymbol(serviceName)
                        + ". Сеанс состоится " + workingDate
                        + " в " + workingTime + ".")
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText("Пользователь " + WorkWithStringsApi.doubleCapitalSymbols(name)
                        + " записался к вам на услугу " + WorkWithStringsApi.firstCapitalSymbol(serviceName)
                        + ". Сеанс состоится " + workingDate
                        + " в " + workingTime + "."))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
    }

}
