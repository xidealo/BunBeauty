package com.bunbeauty.ideal.myapplication.clean_architecture.business.sessions.i_sessions

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message

interface ISessionsMessageInteractor {
    fun sendUserReviewMessage(message: Message)
    fun sendServiceReviewMessage(message: Message)
    fun getId(userId: String, dialogId: String): String
}