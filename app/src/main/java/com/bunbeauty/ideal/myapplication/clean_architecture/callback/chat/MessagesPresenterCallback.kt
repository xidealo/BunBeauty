package com.bunbeauty.ideal.myapplication.clean_architecture.callback.chat

import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order

interface MessagesPresenterCallback {
    fun sendMessage(messageText: String)
    fun showMessage(message: Message, isSmoothScrollingToPosition: Boolean)
    fun deleteOrderFromSchedule(order: Order)
    fun deleteOrder(message: Message)
    fun updateMessageAdapter(message: Message)
    fun removeMessageAdapter(message: Message)
    fun showMoveToStart()
    fun setUnchecked()
    fun showEmptyScreen()
    fun updateUncheckedDialog(message: Message)
    fun showCompanionUserInfo(fullName: String, photoLink: String)
}