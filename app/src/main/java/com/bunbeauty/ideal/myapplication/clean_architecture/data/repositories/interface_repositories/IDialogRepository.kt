package com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories

import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.dialog.*
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog

interface IDialogRepository {
    fun insert(dialog: Dialog, insertDialogCallback: InsertDialogCallback)
    fun insert(dialogs: List<Dialog>, insertDialogCallback: InsertDialogCallback)
    fun delete(dialog: Dialog, deleteDialogCallback: DeleteDialogCallback)
    fun update(dialog: Dialog, updateDialogCallback: UpdateDialogCallback)
    fun get(dialogsCallback: DialogsCallback)
    fun getByUserId(
        userId: String,
        dialogsCallback: DialogsCallback,
        dialogChangedCallback: DialogChangedCallback,
        dialogCallback: DialogCallback
    )

    fun getById(dialog: Dialog, dialogCallback: DialogCallback)

    fun removeObservers()
}