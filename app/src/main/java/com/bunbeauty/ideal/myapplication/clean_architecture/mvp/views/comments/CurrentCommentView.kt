package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.comments

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment

interface CurrentCommentView : MvpView {
    fun setUserComment(userComment: UserComment)
    fun setServiceComment(serviceComment: ServiceComment)
}