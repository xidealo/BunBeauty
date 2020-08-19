package com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.creation_comment.i_creation_comment

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message

interface ICreationCommentOrderInteractor {
    fun getOrderById(message: Message, creationCommentPresenterCallback: CreationCommentPresenterCallback)
}