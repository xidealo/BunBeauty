package com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.creation_comment.iCreationComment

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment

interface ICreationCommentUserInteractor {
    fun getUser(): User
    fun updateUser(
        user: User,
        userComment: UserComment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    )
}