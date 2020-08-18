package com.bunbeauty.ideal.myapplication.clean_architecture.business.chat.i_chat

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message

interface IMessagesDialogInteractor {
    fun getMyDialog(): Dialog
    fun getCompanionDialog(): Dialog
    fun updateUncheckedDialog(message: Message)
    fun updateCheckedDialog()
    fun setUnchecked()
}