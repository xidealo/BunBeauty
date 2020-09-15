package com.bunbeauty.ideal.myapplication.clean_architecture.domain.commets.current_comment.iCurrentComment

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.CurrentCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface ICurrentCommentCommentInteractor {
    fun createCurrentCommentScreen(currentCommentPresenterCallback: CurrentCommentPresenterCallback)
    fun getUserFromComment(): User
}