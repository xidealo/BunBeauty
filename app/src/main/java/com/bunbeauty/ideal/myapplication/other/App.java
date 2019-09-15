package com.bunbeauty.ideal.myapplication.other;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CANCEL_CHANNEL_ID = "1";
    public static final String ORDER_CHANNEL_ID = "2";
    public static final String SUBSCRIBER_CHANNEL_ID = "3";
    public static final String RATE_CHANNEL_ID = "4";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel cancelChannel = new NotificationChannel(
                    CANCEL_CHANNEL_ID,
                    "Cancel Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationChannel orderChannel = new NotificationChannel(
                    ORDER_CHANNEL_ID,
                    "Order Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationChannel subscriberChannel = new NotificationChannel(
                    SUBSCRIBER_CHANNEL_ID,
                    "Subscriber Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationChannel rateChannel = new NotificationChannel(
                    RATE_CHANNEL_ID,
                    "Rate Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(cancelChannel);
            manager.createNotificationChannel(orderChannel);
            manager.createNotificationChannel(subscriberChannel);
            manager.createNotificationChannel(rateChannel);
        }
    }
}
