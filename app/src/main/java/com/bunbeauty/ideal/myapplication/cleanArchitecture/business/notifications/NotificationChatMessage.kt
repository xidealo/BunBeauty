package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.WorkWithStringsApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity

class NotificationChatMessage(
    private val context: Context,
    private val name: String,
    private val chatMessage: String
) : NotificationConstructor() {

    override fun createNotification() {
        val notificationId = 5
        val intent = Intent(context, ProfileActivity::class.java)
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
                .setContentIntent(pIntent)
                .setContentTitle("Сообщение от " + WorkWithStringsApi.doubleCapitalSymbols(name))
                .setContentText(chatMessage)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(chatMessage)
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notificationId, builder.build())

    }

    companion object {
        private const val CHANNEL_ID = "1"
    }
}