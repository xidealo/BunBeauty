package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.baseSubscribers

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service

interface BaseDeleteAllCallback {
    fun returnAllDeletedCallback(deletedCount: Int)
    fun returnDeleteCallback(obj: Service)
}