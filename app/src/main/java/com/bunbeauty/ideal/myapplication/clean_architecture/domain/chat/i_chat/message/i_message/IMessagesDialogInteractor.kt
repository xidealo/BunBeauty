package com.bunbeauty.ideal.myapplication.clean_architecture.domain.chat.i_chat.message.i_message

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message

interface IMessagesDialogInteractor {
    fun getMyDialog(intent: Intent): Dialog
    fun getCompanionDialog(intent: Intent): Dialog
    fun updateUncheckedDialog(message: Message)
    fun updateCheckedDialog()
}