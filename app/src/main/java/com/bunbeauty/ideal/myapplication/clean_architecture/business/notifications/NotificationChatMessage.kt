package com.bunbeauty.ideal.myapplication.clean_architecture.business.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.firstCapitalSymbol
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.chat.MessagesActivity


class NotificationChatMessage(
    private val context: Context,
    private val myId: String,
    private val userId: String,
    private val name: String,
    private val surname: String,
    private val message: String,
    private val photoLink: String
) : NotificationConstructor() {

    override fun createNotification() {
        val notificationId = 5


        val intent = Intent(context, MessagesActivity::class.java).apply {
            putExtra(
                User.USER,
                User(id = userId, photoLink = photoLink, name = name, surname = surname)
            )
            putExtra(
                Dialog.DIALOG, Dialog(
                    id = myId,
                    ownerId = myId,
                    user = User(id = userId),
                    isChecked = false
                )
            )
            putExtra(
                Dialog.COMPANION_DIALOG, Dialog(
                    id = userId,
                    ownerId = userId,
                    user = User(id = myId)
                )
            )
        }

        val pIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        //создание notification
        val builder =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(R.drawable.bun_beauty_icon)
                .setLargeIcon(getBitmapFromURL(photoLink))
                .setContentIntent(pIntent)
                .setContentTitle("${name.firstCapitalSymbol()} ${surname.firstCapitalSymbol()}")
                .setContentText(message)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(message)
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)
        val notification = builder.build()
        notification.flags = Notification.FLAG_AUTO_CANCEL
        notificationManager.notify(notificationId, notification)
    }

    companion object {
        private const val CHANNEL_ID = "1"
    }
}