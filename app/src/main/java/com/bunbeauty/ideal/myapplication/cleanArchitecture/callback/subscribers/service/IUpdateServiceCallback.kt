package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface IUpdateServiceCallback {
    fun returnUpdateCallback(service: Service)
}