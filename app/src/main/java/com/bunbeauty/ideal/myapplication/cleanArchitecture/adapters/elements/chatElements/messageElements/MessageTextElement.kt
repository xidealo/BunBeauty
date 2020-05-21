package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.chatElements.messageElements

import android.view.View
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.chat.MessagesPresenter

class MessageTextElement(
    messagesPresenter: MessagesPresenter
) : MessageElement(messagesPresenter) {

    override fun setVisibility() {
        rateMessageElementButton.visibility = View.GONE
    }

}