package com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment

interface ServiceCommentsPresenterCallback {
    fun getUser(serviceComment: ServiceComment)
    fun setUserOnServiceComment(user: User)
    fun updateServiceComments(serviceComments: List<ServiceComment>)
    fun showEmptyScreen()
}
