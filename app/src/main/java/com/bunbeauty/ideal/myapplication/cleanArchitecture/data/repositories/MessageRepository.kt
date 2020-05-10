package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.DeleteMessageCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.InsertMessageCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.MessagesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.UpdateMessageCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.MessageFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IMessageRepository
import kotlinx.coroutines.launch

class MessageRepository(private val messageFirebase: MessageFirebase) : BaseRepository(),
    IMessageRepository, MessagesCallback {

    private lateinit var messagesCallback: MessagesCallback

    override fun insert(message: Message, insertMessageCallback: InsertMessageCallback) {
        launch {
            message.id = messageFirebase.getIdForNew(message)
            messageFirebase.insert(message)
        }
    }

    override fun delete(message: Message, deleteMessageCallback: DeleteMessageCallback) {
        launch {

        }
    }

    override fun update(message: Message, updateMessageCallback: UpdateMessageCallback) {
        launch {

        }
    }

    override fun get(messagesCallback: MessagesCallback) {
        launch {

        }
    }

    override fun getByDialogId(dialog: Dialog, messagesCallback: MessagesCallback) {
        this.messagesCallback = messagesCallback
        launch {
            messageFirebase.getByDialogId(dialog, messagesCallback)
        }
    }

    override fun getById(message: Message, messagesCallback: MessagesCallback) {
        launch {
            messageFirebase.getById(message, messagesCallback)
        }
    }

    fun getIdForNew(message: Message): String = messageFirebase.getIdForNew(message)

    override fun returnList(objects: List<Message>) {
        messagesCallback.returnList(objects)
    }

}