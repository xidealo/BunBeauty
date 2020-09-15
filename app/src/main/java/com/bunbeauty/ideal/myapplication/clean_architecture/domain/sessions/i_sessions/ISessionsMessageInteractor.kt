package com.bunbeauty.ideal.myapplication.clean_architecture.domain.sessions.i_sessions

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message

interface ISessionsMessageInteractor {
    fun sendMessage(message: Message)
    fun getId(userId: String, dialogId: String): String
}