package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.userComment

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.baseSubscribers.BaseGetListCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment

interface UserCommentsCallback : BaseGetListCallback<UserComment>