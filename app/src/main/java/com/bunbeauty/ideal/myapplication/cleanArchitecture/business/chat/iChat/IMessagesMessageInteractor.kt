package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.chat.MessagesPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message

interface IMessagesMessageInteractor {
    fun getMyMessages(dialog: Dialog, messagesPresenterCallback: MessagesPresenterCallback)
    fun getMyMessagesLink(): List<Message>
    fun getCompanionMessages(dialog: Dialog, messagesPresenterCallback: MessagesPresenterCallback)
    fun sendMessage(message: Message, messagesPresenterCallback: MessagesPresenterCallback)
}