package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.userComment

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.baseSubscribers.BaseInsertCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment

interface InsertUserCommentCallback : BaseInsertCallback<UserComment>