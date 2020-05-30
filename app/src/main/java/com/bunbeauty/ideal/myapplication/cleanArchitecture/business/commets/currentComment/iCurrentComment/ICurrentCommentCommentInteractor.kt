package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.commets.currentComment.iCurrentComment

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments.CurrentCommentPresenterCallback

interface ICurrentCommentCommentInteractor {
    fun getComment(currentCommentPresenterCallback: CurrentCommentPresenterCallback)
}