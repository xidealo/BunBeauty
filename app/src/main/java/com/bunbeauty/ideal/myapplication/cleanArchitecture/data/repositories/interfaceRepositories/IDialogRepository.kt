package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog

interface IDialogRepository {
    fun insert(dialog: Dialog, insertDialogCallback: InsertDialogCallback)
    fun insert(dialogs: List<Dialog>, insertDialogCallback: InsertDialogCallback)
    fun delete(dialog: Dialog, deleteDialogCallback: DeleteDialogCallback)
    fun update(dialog: Dialog, updateDialogCallback: UpdateDialogCallback)
    fun get(dialogsCallback: DialogsCallback)
    fun getByUserId(userId: String, dialogsCallback: DialogsCallback, dialogCallback: DialogCallback)
    fun getById(dialog: Dialog, dialogCallback: DialogCallback)
}