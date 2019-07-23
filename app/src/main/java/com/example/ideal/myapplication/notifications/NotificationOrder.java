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
import com.example.ideal.myapplication.chat.Dialogs;
import com.example.ideal.myapplication.helpApi.WorkWithStringsApi;
import com.example.ideal.myapplication.searchService.GuestService;

public class NotificationOrder extends NotificationConstructor {

    private static final String TAG = "DBInf";
    private static final String CHANNEL_ID = "1";
    private static final String SERVICE_ID = "service id";

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
        //нужен, чтобы потом обратиться к нему и если что изменить, в нашем случае вроде как не нужен
        int notificationId = 1;

        Intent intent = new Intent(context, GuestService.class);
        intent.putExtra(SERVICE_ID, serviceId);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT );

        //создание notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bun_beauty)
                //.setContentIntent(pIntent) // Возможна ошибка информация о владельце отсутствует в БД
                .setContentTitle("Новая запись!")
                .setContentText("Пользователь " + WorkWithStringsApi.doubleCapitalSymbols(name)
                        + " записался к вам на услугу " + WorkWithStringsApi.firstCapitalSymbol(serviceName)
                        + ". Сеанс состоится " + workingDate
                        + " в " + workingTime + ".")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }

}
