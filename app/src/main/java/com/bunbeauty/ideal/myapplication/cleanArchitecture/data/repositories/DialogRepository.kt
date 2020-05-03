package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.DeleteDialogCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.DialogsCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.InsertDialogCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.UpdateDialogCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.DialogFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.DialogDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IDialogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DialogRepository(
    private val dialogDao: DialogDao,
    private val dialogFirebase: DialogFirebase
) : BaseRepository(), IDialogRepository, DialogsCallback {

    private lateinit var dialogsCallback: DialogsCallback

    override fun insert(dialog: Dialog, insertDialogCallback: InsertDialogCallback) {
        launch {
            dialog.id = dialogFirebase.getIdForNew(dialog.ownerId)
            //dialogDao.insert(dialog)
            dialogFirebase.insert(dialog)
            withContext(Dispatchers.Main) {
                insertDialogCallback.returnCreatedCallback(dialog)
            }
        }
    }

    override fun insert(dialogs: List<Dialog>, insertDialogCallback: InsertDialogCallback) {
        launch {
            val id = dialogFirebase.getIdForNew(dialogs.first().ownerId)
            //dialogDao.insert(dialog)
            for (dialog in dialogs) {
                dialog.id = id
                dialogFirebase.insert(dialog)
            }
            withContext(Dispatchers.Main) {
                insertDialogCallback.returnCreatedCallback(dialogs.first())
            }
        }
    }

    override fun delete(dialog: Dialog, deleteDialogCallback: DeleteDialogCallback) {

    }

    override fun update(dialog: Dialog, updateDialogCallback: UpdateDialogCallback) {

    }

    override fun get(dialogsCallback: DialogsCallback) {
        this.dialogsCallback = dialogsCallback
    }

    override fun getByUserId(userId: String, dialogsCallback: DialogsCallback) {
        this.dialogsCallback = dialogsCallback
        launch {
            dialogFirebase.getDialogsByUserId(userId, dialogsCallback)
        }
    }

    override fun returnList(objects: List<Dialog>) {
        dialogsCallback.returnList(objects)
    }

}