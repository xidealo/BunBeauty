package com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user_comment

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.base_subscribers.BaseGetCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment

interface UserCommentCallback : BaseGetCallback<UserComment>