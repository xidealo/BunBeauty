package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.comments

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment

interface CurrentCommentView : MvpView {
    fun setUserComment(userComment: UserComment)
    fun setServiceComment(serviceComment: ServiceComment)
}