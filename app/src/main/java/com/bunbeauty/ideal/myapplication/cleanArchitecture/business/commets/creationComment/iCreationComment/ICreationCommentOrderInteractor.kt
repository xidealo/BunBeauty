package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message

interface ICreationCommentOrderInteractor {
    fun getOrderById(message: Message, creationCommentPresenterCallback: CreationCommentPresenterCallback)
}