package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.chatElements.messageElements

import android.view.View
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.chat.MessagesPresenter

class MessageTextElement: MessageElement() {

    override fun setVisibility(message: Message) {
        rateMessageElementButton.visibility = View.GONE
    }

}