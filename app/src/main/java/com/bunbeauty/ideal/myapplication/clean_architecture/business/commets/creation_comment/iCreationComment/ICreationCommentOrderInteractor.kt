package com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.creation_comment.iCreationComment

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message

interface ICreationCommentOrderInteractor {
    fun getOrderById(message: Message, creationCommentPresenterCallback: CreationCommentPresenterCallback)
}