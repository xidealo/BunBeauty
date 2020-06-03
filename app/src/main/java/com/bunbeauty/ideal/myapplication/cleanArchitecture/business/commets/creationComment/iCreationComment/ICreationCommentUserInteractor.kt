package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.creationComment.iCreationComment

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment

interface ICreationCommentUserInteractor {
    fun getUser(): User
    fun updateUser(
        user: User,
        userComment: UserComment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    )
}