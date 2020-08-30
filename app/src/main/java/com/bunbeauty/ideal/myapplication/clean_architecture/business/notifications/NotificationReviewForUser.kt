/*package com.example.ideal.myapplication.notifications;

public class NotificationReviewForService {
}*/
package com.bunbeauty.ideal.myapplication.clean_architecture.business.notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.ideal.myapplication.R

class NotificationReviewForUser(
    private val context: Context,
    private val userName: String
) : NotificationConstructor() {
    override fun createNotification() {
        //нужен, чтобы потом обратиться к нему и если что изменить, в нашем случае вроде как не нужен
        val notificationId = 0

        //создание notification
        val builder =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(R.drawable.bun_beauty_icon)
                .setContentTitle("Возможность оценить")
                .setContentText("У вас есть возможность оценить пользователя $userName")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("У вас есть возможность оценить пользователя $userName")
                )
                .setPriority(NotificationCompat.PRIORITY_MAX)
        val notificationManager = NotificationManagerCompat.from(context)

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build())
    }

    companion object {
        private const val CHANNEL_ID = "1"
    }
}