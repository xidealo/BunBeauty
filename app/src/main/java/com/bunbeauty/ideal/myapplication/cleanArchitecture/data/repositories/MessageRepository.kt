package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.MessageFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IMessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageRepository(private val messageFirebase: MessageFirebase) : BaseRepository(),
    IMessageRepository, MessagesCallback, MessageCallback {

    private lateinit var messagesCallback: MessagesCallback
    private lateinit var messageCallback: MessageCallback

    override fun insert(message: Message, insertMessageCallback: InsertMessageCallback) {
        launch {
            message.id = messageFirebase.getIdForNew(message)
            messageFirebase.insert(message)
            withContext(Dispatchers.Main) {
                insertMessageCallback.returnCreatedCallback(message)
            }
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

    override fun getByDialogId(
        dialog: Dialog,
        messageCallback: MessageCallback,
        messagesCallback: MessagesCallback
    ) {
        this.messagesCallback = messagesCallback
        this.messageCallback = messageCallback
        launch {
            messageFirebase.getByDialogId(dialog, messageCallback, messagesCallback)
        }
    }

    override fun getByIdLastMessage(dialog: Dialog, messageCallback: MessageCallback) {
        this.messageCallback = messageCallback
        launch {
            messageFirebase.getLastMessage(dialog, messageCallback)
        }
    }

    fun getIdForNew(message: Message): String = messageFirebase.getIdForNew(message)

    override fun returnList(objects: List<Message>) {
        messagesCallback.returnList(objects)
    }

    override fun returnElement(element: Message) {
        messageCallback.returnElement(element)
    }

}