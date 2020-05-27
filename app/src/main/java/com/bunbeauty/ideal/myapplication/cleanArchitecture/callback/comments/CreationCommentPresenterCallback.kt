package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment

interface CreationCommentPresenterCallback {
    fun showCommentCreated(message: Message)
    fun createServiceComment(order: Order)
    fun createUserComment(rating: Double, review: String)
    fun getOrderForServiceComment(message: Message, rating: Double, review: String)
    fun updateUserCommentMessage(userComment: UserComment)
    fun updateServiceCommentMessage(serviceComment: ServiceComment)
}