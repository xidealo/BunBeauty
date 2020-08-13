package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.DialogCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.DialogChangedCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.DialogsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.google.firebase.database.*

class DialogFirebase {

    fun insert(dialog: Dialog) {
        val dialogRef = FirebaseDatabase.getInstance()
            .getReference(Dialog.DIALOGS)
            .child(dialog.id)
            .child(dialog.user.id)

        val dialogItems = HashMap<String, Any>()
        dialogItems[Dialog.IS_CHECKED] = dialog.isChecked
        dialogRef.updateChildren(dialogItems)

        val items = HashMap<String, Any>()
        items[Dialog.IS_CHECKED] = dialog.isChecked
        dialogRef.updateChildren(items)
    }

    fun update(dialog: Dialog) {
        val dialogRef = FirebaseDatabase.getInstance()
            .getReference(Dialog.DIALOGS)
            .child(dialog.id) // user id
            .child(dialog.user.id)

        val items = HashMap<String, Any>()
        items[Dialog.IS_CHECKED] = dialog.isChecked
        dialogRef.updateChildren(items)
    }

    fun getDialogsByUserId(
        userId: String,
        dialogsCallback: DialogsCallback,
        dialogChangedCallback: DialogChangedCallback,
        dialogCallback: DialogCallback
    ) {

        val dialogsRef = FirebaseDatabase.getInstance()
            .getReference(Dialog.DIALOGS)
            .child(userId)

        dialogsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dialogsSnapshot: DataSnapshot) {

                val dialogs = arrayListOf<Dialog>()
                for (dialogSnapshot in dialogsSnapshot.children) {
                    dialogs.add(getDialogFromSnapshot(dialogSnapshot, userId))
                }

                dialogsCallback.returnList(dialogs)

                dialogsRef.addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                    }

                    override fun onChildChanged(dialogSnapshot: DataSnapshot, p1: String?) {
                        val changedDialog = getDialogFromSnapshot(dialogSnapshot, userId)
                        changedDialog.user.id = userId
                        dialogChangedCallback.returnChanged(changedDialog)
                    }

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
        dialog.id = userId
        dialog.ownerId = userId
        dialog.isChecked = dialogSnapshot.child(Dialog.IS_CHECKED).value as? Boolean ?: true
        dialog.user.id = dialogSnapshot.key!!

        return dialog
    }

    fun getIdForNew(userId: String) = FirebaseDatabase.getInstance().getReference(User.USERS)
        .child(userId)
        .child(Dialog.DIALOGS).push().key!!

}