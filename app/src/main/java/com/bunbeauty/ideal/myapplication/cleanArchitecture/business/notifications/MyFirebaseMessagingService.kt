package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val dataType = remoteMessage.data["data_type"]
        when (dataType) {
            FOLLOWING -> sendFollowingNotification(
                remoteMessage
            )
            ORDER -> sendOrderNotification(remoteMessage)
            CANCEL -> sendCancelNotification(remoteMessage)
            SERVICE_RATED -> sendServiceRatedNotification(
                remoteMessage
            )
            USER_RATED -> sendUserRatedNotification(
                remoteMessage
            )
            else -> Log.d(
                TAG,
                "Invalid data type!"
            )
        }
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

    private fun sendFollowingNotification(remoteMessage: RemoteMessage) {
        val notification =
            NotificationSubscribers(this, remoteMessage.data["name"]!!)
        notification.createNotification()
    }

    companion object {
        private const val TAG = "DBInf"
        private const val FOLLOWING = "following"
        private const val ORDER = "order"
        private const val CANCEL = "cancel"
        private const val SERVICE_RATED = "service rated"
        private const val USER_RATED = "user rated"
    }
}