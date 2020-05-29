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
    IMessageRepository {

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
            messageFirebase.update(message)
            withContext(Dispatchers.Main) {
                updateMessageCallback.returnUpdatedCallback(message)
            }
        }
    }

    override fun get(messagesCallback: MessagesCallback) {
        launch {

        }
    }

    override fun getByDialogId(
        dialog: Dialog,
        messageCallback: MessageCallback,
        messagesCallback: MessagesCallback,
        updateMessageCallback: UpdateMessageCallback
    ) {
        launch {
            messageFirebase.getByDialogId(
                dialog,
                messageCallback,
                messagesCallback,
                updateMessageCallback
            )
        }
    }

    override fun getByIdLastMessage(dialog: Dialog, messageCallback: MessageCallback) {
        launch {
            messageFirebase.getLastMessage(dialog, messageCallback)
        }
    }

    fun getIdForNew(message: Message): String = messageFirebase.getIdForNew(message)

}