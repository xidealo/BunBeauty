package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.DialogCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.DialogsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

    fun getDialogsByUserId(userId: String, dialogsCallback: DialogsCallback) {

        val dialogsRef = FirebaseDatabase.getInstance()
            .getReference(User.USERS)
            .child(userId)
            .child(Dialog.DIALOGS)

        dialogsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dialogsSnapshot: DataSnapshot) {
                dialogsCallback.returnList(returnDialogList(dialogsSnapshot, userId))
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