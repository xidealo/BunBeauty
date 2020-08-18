package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.photo

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers.BaseDeleteCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo

interface DeletePhotoCallback : BaseDeleteCallback<Photo>