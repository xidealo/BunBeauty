package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.service_comment

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers.BaseGetListCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment

interface ServiceCommentsCallback : BaseGetListCallback<ServiceComment>