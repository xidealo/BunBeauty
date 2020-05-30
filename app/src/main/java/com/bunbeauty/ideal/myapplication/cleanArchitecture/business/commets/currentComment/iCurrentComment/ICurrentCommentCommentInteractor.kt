package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.currentComment.iCurrentComment

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CurrentCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface ICurrentCommentCommentInteractor {
    fun createCurrentCommentScreen(currentCommentPresenterCallback: CurrentCommentPresenterCallback)
    fun getUserFromComment(): User
}