package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.MessageCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.MessagesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.UpdateMessageCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.google.firebase.database.*

class MessageFirebase {

    fun insert(message: Message) {
        val messageRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(message.userId)
            .child(Dialog.DIALOGS)
            .child(message.dialogId)
            .child(Message.MESSAGES)
            .child(message.id)

        val items = HashMap<String, Any>()
        items[Message.MESSAGE] = message.message
        items[Message.TIME] = ServerValue.TIMESTAMP
        if (message.orderId.isNotEmpty())
            items[Message.ORDER_ID] = message.orderId

        items[Message.TYPE] = message.type

        messageRef.updateChildren(items)
    }

    fun update(message: Message) {
        val messageRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(message.userId)
            .child(Dialog.DIALOGS)
            .child(message.dialogId)
            .child(Message.MESSAGES)
            .child(message.id)

        val items = HashMap<String, Any>()
        items[Message.MESSAGE] = message.message
        items[Message.TIME] = ServerValue.TIMESTAMP
        if (message.orderId.isNotEmpty())
            items[Message.ORDER_ID] = message.orderId

        items[Message.TYPE] = message.type

        messageRef.updateChildren(items)
    }

    fun getByDialogId(
        dialog: Dialog,
        messageCallback: MessageCallback,
        messagesCallback: MessagesCallback,
        updateMessageCallback: UpdateMessageCallback
    ) {
        val messageRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(dialog.ownerId)
            .child(Dialog.DIALOGS)
            .child(dialog.id)
            .child(Message.MESSAGES)

        messageRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(messagesSnapshot: DataSnapshot) {

                val messages = arrayListOf<Message>()
                if (messagesSnapshot.childrenCount > 0L) {
                    for (messageSnapshot in messagesSnapshot.children) {
                        val message = getMessageFromSnapshot(messageSnapshot, dialog.ownerId)
                        message.dialogId = dialog.id
                        message.userId = dialog.ownerId
                        messages.add(message)
                    }
                }
                messagesCallback.returnList(messages)

                messageRef.addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                    }

                    override fun onChildChanged(messageSnapshot: DataSnapshot, p1: String?) {
                        updateMessageCallback.returnUpdatedCallback(
                            getMessageFromSnapshot(
                                messageSnapshot,
                                dialog.ownerId
                            )
                        )
                    }

                    override fun onChildAdded(messageSnapshot: DataSnapshot, previousId: String?) {
                        if (messages.isNotEmpty()) {
                            if (previousId == messages.last().id) {
                                val addedMessage =
                                    getMessageFromSnapshot(messageSnapshot, dialog.ownerId)
                                addedMessage.dialogId = dialog.id
                                addedMessage.userId = dialog.ownerId
                                messages.add(addedMessage)
                                messageCallback.returnElement(addedMessage)
                            }
                        } else {
                            val addedMessage =
                                getMessageFromSnapshot(messageSnapshot, dialog.ownerId)
                            addedMessage.dialogId = dialog.id
                            addedMessage.userId = dialog.ownerId
                            messages.add(addedMessage)
                            messageCallback.returnElement(addedMessage)
                        }
                    }

                    override fun onChildRemoved(p0: DataSnapshot) {

                    }
                })
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    fun getLastMessage(dialog: Dialog, messageCallback: MessageCallback) {
        val messageRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(dialog.ownerId)
            .child(Dialog.DIALOGS)
            .child(dialog.id)
            .child(Message.MESSAGES).orderByChild(Message.TIME).limitToLast(1)

        messageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(messagesSnapshot: DataSnapshot) {
                var message = Message()
                if (messagesSnapshot.childrenCount > 0) {
                    message =
                        getMessageFromSnapshot(
                            messagesSnapshot.children.iterator().next(),
                            dialog.ownerId
                        )
                }
                message.dialogId = dialog.id
                message.userId = dialog.ownerId
                messageCallback.returnElement(message)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Some error
            }
        })
    }

    private fun getMessageFromSnapshot(messageSnapshot: DataSnapshot, userId: String): Message {
        val message = Message()
        message.id = messageSnapshot.key!!
        message.message = messageSnapshot.child(Message.MESSAGE).value as? String ?: ""
        message.time = messageSnapshot.child(Message.TIME).value as? Long ?: 0
        message.type = messageSnapshot.child(Message.TYPE).getValue(Int::class.java) ?: 0
        message.userId = userId
        return message
    }

    fun getIdForNew(message: Message) = FirebaseDatabase.getInstance().getReference(User.USERS)
        .child(message.userId)
        .child(Dialog.DIALOGS)
        .child(message.dialogId)
        .child(Message.MESSAGES).push().key!!
}