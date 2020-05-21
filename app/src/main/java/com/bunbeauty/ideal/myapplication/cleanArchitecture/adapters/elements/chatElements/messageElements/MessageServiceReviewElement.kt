package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.chatElements.messageElements

import android.content.Context
import android.view.View
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.chat.MessagesPresenter

class MessageServiceReviewElement(
    private val messagesPresenter: MessagesPresenter,
    private val context: Context
) : MessageElement(messagesPresenter), View.OnClickListener {

    override fun setVisibility() {
        super.setVisibility()
        rateMessageElementButton.visibility = View.VISIBLE
        rateMessageElementButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

    }

}