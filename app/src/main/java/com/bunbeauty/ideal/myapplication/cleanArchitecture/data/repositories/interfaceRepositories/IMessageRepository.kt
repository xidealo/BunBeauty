package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.DeleteMessageCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.InsertMessageCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.MessagesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.UpdateMessageCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message

interface IMessageRepository {
    fun insert(message: Message, insertMessageCallback: InsertMessageCallback)
    fun delete(message: Message, deleteMessageCallback: DeleteMessageCallback)
    fun update(message: Message, updateMessageCallback: UpdateMessageCallback)
    fun get(messagesCallback: MessagesCallback)

    fun getByDialogId(dialog: Dialog, messagesCallback: MessagesCallback)
}