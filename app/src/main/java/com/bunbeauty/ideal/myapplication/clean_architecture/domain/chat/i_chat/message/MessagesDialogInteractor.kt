package com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.i_message.IMessagesDialogInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.dialog.UpdateDialogCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.DialogRepository

class MessagesDialogInteractor(
    private val dialogRepository: DialogRepository
) : IMessagesDialogInteractor, UpdateDialogCallback {

    private lateinit var cacheDialog: Dialog
    private lateinit var cacheCompanionDialog: Dialog

    override fun getMyDialog(intent: Intent): Dialog {
        cacheDialog = intent.getSerializableExtra(Dialog.DIALOG) as Dialog
        return cacheDialog
    }

    override fun getCompanionDialog(intent: Intent): Dialog {
        cacheCompanionDialog = intent.getSerializableExtra(Dialog.COMPANION_DIALOG) as Dialog
        return cacheCompanionDialog
    }

    override fun updateUncheckedDialog(message: Message) {
        cacheCompanionDialog.isChecked = false
        dialogRepository.update(cacheCompanionDialog, this)
    }

    override fun updateCheckedDialog() {
        cacheDialog.isChecked = true
        dialogRepository.update(cacheDialog, this)
    }


    override fun returnUpdatedCallback(obj: Dialog) {}
}