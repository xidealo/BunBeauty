package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface IDeleteServiceCallback {
    fun returnDeleteCallback(service: Service)
}