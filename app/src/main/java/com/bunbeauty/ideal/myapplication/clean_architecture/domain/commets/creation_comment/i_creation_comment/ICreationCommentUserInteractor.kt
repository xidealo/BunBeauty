package com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.creation_comment.i_creation_comment

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.CreationCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment

interface ICreationCommentUserInteractor {
    fun getUser(intent: Intent): User
    fun updateUser(
        user: User,
        userComment: UserComment,
        creationCommentPresenterCallback: CreationCommentPresenterCallback
    )
}