package com.bunbeauty.ideal.myapplication.clean_architecture.business.api.server_time

interface ServerTimeCallback {
    fun onSuccess(timestamp: Long)
    fun onFailed()
}