package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.*
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
) : BaseRepository(), IDialogRepository {

    override fun insert(dialog: Dialog, insertDialogCallback: InsertDialogCallback) {
        launch {
            //dialog.id = dialogFirebase.getIdForNew(dialog.ownerId)
            //dialogDao.insert(dialog)
            dialogFirebase.insert(dialog)
            withContext(Dispatchers.Main) {
                insertDialogCallback.returnCreatedCallback(dialog)
            }
        }
    }

    override fun insert(dialogs: List<Dialog>, insertDialogCallback: InsertDialogCallback) {
        launch {
            for (dialog in dialogs) {
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
        launch {
            dialogFirebase.update(dialog)
            updateDialogCallback.returnUpdatedCallback(dialog)
        }
    }

    override fun get(dialogsCallback: DialogsCallback) {

    }

    override fun getByUserId(
        userId: String,
        dialogsCallback: DialogsCallback,
        dialogChangedCallback: DialogChangedCallback,
        dialogCallback: DialogCallback
    ) {
        dialogFirebase.getDialogsByUserId(
            userId,
            dialogsCallback,
            dialogChangedCallback,
            dialogCallback
        )
    }

    override fun getById(dialog: Dialog, dialogCallback: DialogCallback) {
        launch {
            dialogFirebase.getById(dialog, dialogCallback)
        }
    }

}