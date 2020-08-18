package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.comments

import com.arellomobile.mvp.MvpView
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User

interface CreationCommentView : MvpView {
    fun showCommentCreated(message: Message, user: User)
}