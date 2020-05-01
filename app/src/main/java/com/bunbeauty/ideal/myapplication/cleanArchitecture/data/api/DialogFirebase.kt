package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.DialogsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DialogFirebase {

    fun insert(dialog: Dialog) {
        val database = FirebaseDatabase.getInstance()
        val dialogRef = database
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

            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })
    }

    fun getIdForNew(userId: String) = FirebaseDatabase.getInstance().getReference(User.USERS)
            .child(userId)
            .child(Dialog.DIALOGS).push().key!!

}