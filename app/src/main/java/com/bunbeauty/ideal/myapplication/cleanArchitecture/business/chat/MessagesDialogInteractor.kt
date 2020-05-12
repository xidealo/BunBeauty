package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.chat.iChat.IMessagesDialogInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.dialog.UpdateDialogCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.DialogRepository

class MessagesDialogInteractor(
    private val intent: Intent,
    private val dialogRepository: DialogRepository
) : IMessagesDialogInteractor, UpdateDialogCallback {

    private lateinit var cacheDialog: Dialog
    private lateinit var cacheCompanionDialog: Dialog

    override fun getMyDialog(): Dialog {
        cacheDialog = intent.getSerializableExtra(Dialog.DIALOG) as Dialog
        if (!cacheDialog.isChecked) {
            dialogRepository.update(cacheDialog, this)
        }
        cacheDialog.isChecked = true
        return cacheDialog
    }

    override fun getCompanionDialog(): Dialog {
        cacheCompanionDialog = intent.getSerializableExtra(Dialog.COMPANION_DIALOG) as Dialog
        return cacheCompanionDialog
    }

    override fun updateCheckedDialog(message: Message) {
        cacheCompanionDialog.isChecked = false
        dialogRepository.update(cacheCompanionDialog, this)
    }

    override fun returnUpdatedCallback(obj: Dialog) {

    }

}