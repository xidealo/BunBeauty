
package com.example.ideal.myapplication.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.chat.Messages;

public class NotificationReviewForUser extends NotificationConstructor {

    private static final String CHANNEL_ID = "1";
    private static final String USER_ID = "user id";

    private Context context;
    private String userId;
    private String userName;

    public NotificationReviewForUser(Context _context, String _userId, String _userName) {
        context = _context;
        userId = _userId;
        userName = _userName;
    }

    @Override
    public void createNotification() {
        //нужен, чтобы потом обратиться к нему и если что изменить, в нашем случае вроде как не нужен
        int notificationId = 1;

        // создание PendingIntent для перехода в нужный активити при нажатии
        Intent intent = new Intent(context, Messages.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(USER_ID, userId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        //создание notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bun_beauty)
                .setContentTitle("Возможность оценить")
                .setContentText("У вас есть возможность оценить пользователя " + userName)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(notificationId, builder.build());
    }

}
