package com.bunbeauty.ideal.myapplication.clean_architecture

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.AppComponent
import com.bunbeauty.ideal.myapplication.clean_architecture.di.component.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()

        appComponent.inject(this)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val cancelChannel = NotificationChannel(
                CANCEL_CHANNEL_ID,
                "Cancel Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val orderChannel = NotificationChannel(
                ORDER_CHANNEL_ID,
                "Order Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val subscriberChannel = NotificationChannel(
                SUBSCRIBER_CHANNEL_ID,
                "Subscriber Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val rateChannel = NotificationChannel(
                RATE_CHANNEL_ID,
                "Rate Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)

            manager?.createNotificationChannel(cancelChannel)
            manager?.createNotificationChannel(orderChannel)
            manager?.createNotificationChannel(subscriberChannel)
            manager?.createNotificationChannel(rateChannel)
        }
    }

    companion object {
        const val CANCEL_CHANNEL_ID = "1"
        const val ORDER_CHANNEL_ID = "2"
        const val SUBSCRIBER_CHANNEL_ID = "3"
        const val RATE_CHANNEL_ID = "4"
    }
}