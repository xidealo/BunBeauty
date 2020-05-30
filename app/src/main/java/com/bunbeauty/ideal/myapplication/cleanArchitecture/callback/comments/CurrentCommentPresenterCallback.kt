package com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.comments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment

interface CurrentCommentPresenterCallback {
    fun setUserComment(userComment: UserComment)
    fun setServiceComment(serviceComment: ServiceComment)
}