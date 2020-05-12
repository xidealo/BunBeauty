package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.DialogCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.DialogsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.google.firebase.database.*

class DialogFirebase {

    fun insert(dialog: Dialog) {
        val dialogRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(dialog.ownerId)
            .child(Dialog.DIALOGS)
            .child(dialog.id)

        val items = HashMap<String, Any>()
        items[Dialog.IS_CHECKED] = dialog.isChecked
        items[Dialog.COMPANION_ID] = dialog.user.id
        dialogRef.updateChildren(items)
    }

    fun update(dialog: Dialog) {
        val dialogRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(dialog.ownerId)
            .child(Dialog.DIALOGS)
            .child(dialog.id)

        val items = HashMap<String, Any>()
        items[Dialog.IS_CHECKED] = dialog.isChecked
        items[Dialog.COMPANION_ID] = dialog.user.id
        items[Dialog.MESSAGE_ID] = dialog.lastMessage.id
        dialogRef.updateChildren(items)
    }

    fun getDialogsByUserId(
        userId: String,
        dialogsCallback: DialogsCallback,
        dialogCallback: DialogCallback
    ) {

        val dialogsRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(userId)
            .child(Dialog.DIALOGS)

        dialogsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dialogsSnapshot: DataSnapshot) {

                val dialogs = arrayListOf<Dialog>()
                for (dialogSnapshot in dialogsSnapshot.children) {
                    dialogs.add(getDialogFromSnapshot(dialogSnapshot, userId))
                }

                dialogsCallback.returnList(dialogs)

                dialogsRef.addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                        TODO("Not yet implemented")
                    }
                    //прислать коллбэк апдейта?
                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

                    override fun onChildAdded(dialogSnapshot: DataSnapshot, previousId: String?) {
                        if (dialogs.isNotEmpty()) {
                            if (previousId == dialogs.last().id) {
                                val addedDialog = getDialogFromSnapshot(dialogSnapshot, userId)
                                addedDialog.user.id = userId
                                dialogs.add(addedDialog)
                                dialogCallback.returnElement(addedDialog)
                            }
                        } else {
                            val addedDialog = getDialogFromSnapshot(dialogSnapshot, userId)
                            addedDialog.user.id = userId
                            dialogs.add(addedDialog)
                            dialogCallback.returnElement(addedDialog)
                        }
                    }

                    override fun onChildRemoved(p0: DataSnapshot) {
                        TODO("Not yet implemented")
                    }

                })
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })
    }

    fun getById(dialog: Dialog, dialogCallback: DialogCallback) {
        val dialogsRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(dialog.ownerId)
            .child(Dialog.DIALOGS)
            .child(dialog.id)

        dialogsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dialogSnapshot: DataSnapshot) {
                dialogCallback.returnElement(getDialogFromSnapshot(dialogSnapshot, dialog.ownerId))
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })
    }

    private fun returnDialogList(
        dialogsSnapshot: DataSnapshot,
        userId: String
    ): ArrayList<Dialog> {
        val dialogs = arrayListOf<Dialog>()
        for (dialogSnapshot in dialogsSnapshot.children) {
            dialogs.add(getDialogFromSnapshot(dialogSnapshot, userId))
        }
        return dialogs
    }

    private fun getDialogFromSnapshot(dialogSnapshot: DataSnapshot, userId: String): Dialog {
        val dialog = Dialog()
        dialog.id = dialogSnapshot.key!!
        dialog.ownerId = userId
        dialog.isChecked = dialogSnapshot.child(Dialog.IS_CHECKED).value as? Boolean ?: true
        dialog.user.id = dialogSnapshot.child(Dialog.COMPANION_ID).value as? String ?: ""
        dialog.lastMessage.id = dialogSnapshot.child(Dialog.MESSAGE_ID).value as? String ?: ""

        return dialog
    }

    fun getIdForNew(userId: String) = FirebaseDatabase.getInstance().getReference(User.USERS)
        .child(userId)
        .child(Dialog.DIALOGS).push().key!!

}