package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.WorkWithStringsApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity


class NotificationChatMessage(
    private val context: Context,
    private val userId: String,
    private val name: String,
    private val message: String,
    private val photoLink: String
) : NotificationConstructor() {

    override fun createNotification() {
        val notificationId = 5
        val intent = Intent(context, ProfileActivity::class.java).apply {
            putExtra(User.USER_ID, userId)
        }
        val pIntent = PendingIntent.getActivity(
            context, 0,
            intent, PendingIntent.FLAG_CANCEL_CURRENT
        )

        //создание notification
        val builder =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(R.drawable.bun_beauty)
                .setLargeIcon(getBitmapFromURL(photoLink))
                .setContentIntent(pIntent)
                .setContentTitle("Сообщение от " + WorkWithStringsApi.doubleCapitalSymbols(name))
                .setContentText(message)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(message)
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notificationId, builder.build())
    }

    companion object {
        private const val CHANNEL_ID = "1"
    }
}