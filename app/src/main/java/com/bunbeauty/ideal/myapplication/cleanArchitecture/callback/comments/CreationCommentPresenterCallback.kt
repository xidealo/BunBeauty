package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment

interface CreationCommentPresenterCallback {
    fun showCommentCreated(message: Message)
    fun createServiceComment(rating: Double, review: String)
    fun updateCommentMessage(userComment: UserComment)
}