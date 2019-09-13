package com.example.ideal.myapplication.notifications;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "DBInf";
    private static final String FOLLOWING = "following";
    private static final String ORDER = "order";
    private static final String CANCEL = "cancel";
    private static final String SERVICE_RATED = "service rated";
    private static final String USER_RATED = "user rated";

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String dataType = remoteMessage.getData().get("data_type");

        switch (dataType) {
            case FOLLOWING:
                sendFollowingNotification(remoteMessage);
                break;

            case ORDER:
                sendOrderNotification(remoteMessage);
                break;

            case CANCEL:
                sendCancelNotification(remoteMessage);
                break;

            case SERVICE_RATED:
                sendServiceRatedNotification(remoteMessage);
                break;

            case USER_RATED:
                sendUserRatedNotification(remoteMessage);
                break;

            default:
                Log.d(TAG, "Invalid data type!");
        }
    }

    private void sendUserRatedNotification(RemoteMessage remoteMessage) {
        String workerName = remoteMessage.getData().get("name");

        NotificationYouAreRated notification = new NotificationYouAreRated(this,
                workerName);
        notification.createNotification();
    }

    private void sendServiceRatedNotification(RemoteMessage remoteMessage) {
        String userName = remoteMessage.getData().get("name");
        String serviceName = remoteMessage.getData().get("service_name");

        NotificationYourServiceIsRated notification = new NotificationYourServiceIsRated(this,
                userName,
                serviceName);
        notification.createNotification();
    }

    private void sendCancelNotification(RemoteMessage remoteMessage) {
        String workerName = remoteMessage.getData().get("name");
        String serviceName = remoteMessage.getData().get("service_name");
        String serviceId = remoteMessage.getData().get("service_id");
        String date = remoteMessage.getData().get("date");
        String time = remoteMessage.getData().get("time");

        NotificationCancel notification = new NotificationCancel(this,
                workerName,
                serviceName,
                serviceId,
                date,
                time);
        notification.createNotification();
    }

    private void sendOrderNotification(RemoteMessage remoteMessage) {
        String userName = remoteMessage.getData().get("name");
        String serviceName = remoteMessage.getData().get("service_name");
        String serviceId = remoteMessage.getData().get("service_id");
        String date = remoteMessage.getData().get("date");
        String time = remoteMessage.getData().get("time");

        NotificationOrder notification = new NotificationOrder(this,
                userName,
                serviceName,
                serviceId,
                date,
                time);
        notification.createNotification();
    }

    private void sendFollowingNotification(RemoteMessage remoteMessage) {
        String followerId = remoteMessage.getData().get("user_id");
        String followerName = remoteMessage.getData().get("name");

        NotificationSubscribers notification = new NotificationSubscribers(this,
                followerId,
                followerName);
        notification.createNotification();
    }
}