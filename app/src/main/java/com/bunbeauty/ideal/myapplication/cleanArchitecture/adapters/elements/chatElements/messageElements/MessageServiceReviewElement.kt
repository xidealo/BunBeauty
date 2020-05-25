package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements.chatElements.messageElements

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.chat.MessagesActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.comments.CreationCommentActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.chat.MessagesPresenter

class MessageServiceReviewElement(
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
        intent.putExtra(Message.MESSAGE, message)
        (context as Activity).startActivityForResult(
            intent,
            MessagesActivity.REQUEST_MESSAGE_USER_REVIEW
        )
        context.overridePendingTransition(0, 0)
    }


}