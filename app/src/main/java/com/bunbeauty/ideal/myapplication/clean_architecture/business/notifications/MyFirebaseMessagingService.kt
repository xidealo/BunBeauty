package com.bunbeauty.ideal.myapplication.clean_architecture.business.notifications

import android.util.Log
import com.bunbeauty.ideal.myapplication.clean_architecture.Tag
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        when (remoteMessage.data["data_type"]) {
            FOLLOWING -> sendFollowingNotification(
                remoteMessage
            )
            CHAT_MESSAGE -> sendChatMessageNotification(remoteMessage)
            ORDER -> sendOrderNotification(remoteMessage)
            CANCEL -> sendCancelNotification(remoteMessage)
            SERVICE_RATED -> sendServiceRatedNotification(
                remoteMessage
            )
            USER_RATED -> sendUserRatedNotification(
                remoteMessage
            )
            else -> Log.d(
                Tag.TEST_TAG,
                "Invalid data type!"
            )
        }
    }

    private fun sendFollowingNotification(remoteMessage: RemoteMessage) {
        val notification =
            NotificationSubscribers(
                this,
                remoteMessage.data["user_id"]?: "",
                remoteMessage.data[User.NAME] ?: "",
                remoteMessage.data["photo_link"] ?: ""

            )
        notification.createNotification()
    }

    private fun sendChatMessageNotification(remoteMessage: RemoteMessage) {
        val notification =
            NotificationChatMessage(
                this,
                remoteMessage.data["my_id"]?: "",
                remoteMessage.data["user_id"]?: "",
                remoteMessage.data[User.NAME] ?: "",
                remoteMessage.data[User.SURNAME] ?: "",
                remoteMessage.data[Message.MESSAGE] ?: "",
                remoteMessage.data["photo_link"] ?: ""
            )
        notification.createNotification()
    }


    private fun sendUserRatedNotification(remoteMessage: RemoteMessage) {
        val workerName = remoteMessage.data["name"]
        val notification = NotificationYouAreRated(
            this,
            workerName
        )
        notification.createNotification()
    }

    private fun sendServiceRatedNotification(remoteMessage: RemoteMessage) {
        val userName = remoteMessage.data["name"]
        val serviceName = remoteMessage.data["service_name"]
        val notification = NotificationYourServiceIsRated(
            this,
            userName,
            serviceName
        )
        notification.createNotification()
    }

    private fun sendCancelNotification(remoteMessage: RemoteMessage) {
        val workerName = remoteMessage.data["name"]
        val serviceName = remoteMessage.data["service_name"]
        val serviceId = remoteMessage.data["service_id"]
        val date = remoteMessage.data["date"]
        val time = remoteMessage.data["time"]
        val notification = NotificationCancel(
            this,
            workerName,
            serviceName,
            serviceId,
            date,
            time
        )
        notification.createNotification()
    }

    private fun sendOrderNotification(remoteMessage: RemoteMessage) {
        val userName = remoteMessage.data["name"]
        val serviceName = remoteMessage.data["service_name"]
        val serviceId = remoteMessage.data["service_id"]
        val date = remoteMessage.data["date"]
        val time = remoteMessage.data["time"]
        val notification = NotificationOrder(
            this,
            userName,
            serviceName,
            serviceId,
            date,
            time
        )
        notification.createNotification()
    }

    companion object {
        private const val FOLLOWING = "following"
        private const val ORDER = "order"
        private const val CANCEL = "cancel"
        private const val SERVICE_RATED = "service rated"
        private const val USER_RATED = "user rated"
        private const val CHAT_MESSAGE = "chat message"
    }
}