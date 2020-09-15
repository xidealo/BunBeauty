package com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.server_time

import com.google.firebase.functions.FirebaseFunctions


object ServerTime {
    fun getServerTime(serverTimeCallback: ServerTimeCallback) {
        FirebaseFunctions.getInstance().getHttpsCallable("getTime")
            .call()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val timestamp = task.result!!.data as Long
                    serverTimeCallback.onSuccess(timestamp)
                } else {
                    serverTimeCallback.onFailed()
                }
            }
    }
}