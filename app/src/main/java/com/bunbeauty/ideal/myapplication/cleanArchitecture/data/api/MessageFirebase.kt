package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.message.MessagesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
        items[Message.TIME] = message.time
        messageRef.updateChildren(items)
    }

    fun getByDialogId(dialog: Dialog, messagesCallback: MessagesCallback) {

        val dialogRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(dialog.ownerId)
            .child(Dialog.DIALOGS)
            .child(dialog.id)
            .child(Message.MESSAGES)

        dialogRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(messagesSnapshot: DataSnapshot) {
                //val currentDialog = dialog
                val messages = arrayListOf<Message>()
                if (messagesSnapshot.childrenCount > 0L) {
                    for (messageSnapshot in messagesSnapshot.children) {
                        val message = getDialogFromSnapshot(messageSnapshot)
                        message.dialogId = dialog.id
                        message.userId = dialog.ownerId
                        messages.add(message)
                    }
                }
                messagesCallback.returnList(messages)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Some error
            }
        })
    }

    private fun getDialogFromSnapshot(messageSnapshot: DataSnapshot): Message {
        val message = Message()
        message.id = messageSnapshot.key!!
        message.message = messageSnapshot.child(Message.MESSAGE).value as? String ?: ""
        message.time = messageSnapshot.child(Message.TIME).value as? String ?: ""

        return message
    }

    fun getIdForNew(message: Message) = FirebaseDatabase.getInstance().getReference(User.USERS)
        .child(message.userId)
        .child(Dialog.DIALOGS)
        .child(message.dialogId)
        .child(Message.MESSAGES).push().key!!
}