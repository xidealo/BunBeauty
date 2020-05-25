package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.userComment

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.baseSubscribers.BaseDeleteCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment

interface DeleteUserCommentCallback : BaseDeleteCallback<UserComment>