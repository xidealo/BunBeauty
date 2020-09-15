package com.bunbeauty.ideal.myapplication.clean_architecture.domain.sessions

import android.util.Log
import com.bunbeauty.ideal.myapplication.clean_architecture.Tag
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.sessions.i_sessions.ISessionsMessageInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.message.InsertMessageCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.MessageRepository

class SessionsMessageInteractor(private val messageRepository: MessageRepository) :
    ISessionsMessageInteractor, InsertMessageCallback {

    override fun sendMessage(message: Message) {
        messageRepository.insert(message, this)
    }

    override fun returnCreatedCallback(obj: Message) {
        Log.d(Tag.TEST_TAG, "message ${obj.id} was created")
    }

    override fun getId(userId: String, dialogId: String): String {
        return messageRepository.getIdForNew(userId, dialogId)
    }
}