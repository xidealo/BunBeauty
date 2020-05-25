package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message

interface CreationCommentPresenterCallback {
    fun showCommentCreated(message: Message)
    fun updateCommentMessage(userComment: UserComment)
}