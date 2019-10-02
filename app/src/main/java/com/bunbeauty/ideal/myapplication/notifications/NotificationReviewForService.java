/*package com.example.ideal.myapplication.notifications;

public class NotificationReviewForService {
}*/
package com.bunbeauty.ideal.myapplication.notifications;

import android.content.Context;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.ideal.myapplication.R;

public class NotificationReviewForService extends NotificationConstructor {

    private Context context;
    private static final String CHANNEL_ID = "1";

    private String serviceName;

    public NotificationReviewForService(Context _context, String _serviceName) {
        context = _context;
        serviceName = _serviceName;
    }

    @Override
    public void createNotification() {
        //нужен, чтобы потом обратиться к нему и если что изменить, в нашем случае вроде как не нужен
        int notificationId = 0;

        //создание notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.bun_beauty)
                .setContentTitle("Возможность оценить")
                .setContentText("У вас есть возможность оценить услугу \"" + serviceName + "\"")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("У вас есть возможность оценить услугу \"" + serviceName + "\""))
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }

}
