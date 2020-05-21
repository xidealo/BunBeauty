package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.chatElements.messageElements

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.comments.CreationCommentActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.chat.MessagesPresenter

class MessageUserReviewElement(
    messagesPresenter: MessagesPresenter,
    private val context: Context
) : MessageElement(messagesPresenter), View.OnClickListener {

    override fun setVisibility() {
        super.setVisibility()
        rateMessageElementButton.visibility = View.VISIBLE
        rateMessageElementButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        goToCreationComment()
    }

    private fun goToCreationComment() {
        val intent = Intent(context, CreationCommentActivity::class.java)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(0, 0)
    }
}