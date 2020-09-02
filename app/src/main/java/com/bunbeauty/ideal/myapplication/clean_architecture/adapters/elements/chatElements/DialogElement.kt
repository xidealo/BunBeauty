package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.chatElements

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.business.CircularTransformation
import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithStringsApi
import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithTimeApi
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.cutStringWithDots
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.cutStringWithLineBreak
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.gone
import com.bunbeauty.ideal.myapplication.clean_architecture.business.api.visible
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.chat.MessagesActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.element_dialog.view.*
import java.util.*

class DialogElement(
    view: View,
    context: Context,
    dialog: Dialog
) {

    init {
        setData(dialog, view, context)
        view.element_dialog_mc_main.setOnClickListener {
            goToDialog(dialog, context)
        }
    }

    private fun setData(dialog: Dialog, view: View, context: Context) {
        showAvatar(dialog.user, view, context)
        view.element_dialog_tv_name.text = "${dialog.user.name} ${dialog.user.surname}"
        view.element_dialog_tv_last_message.text =
            dialog.lastMessage.message.cutStringWithDots(23).cutStringWithLineBreak(2)
        view.element_dialog_tv_time.text =
            WorkWithTimeApi.getDateInFormatYMDHMS(Date(dialog.lastMessage.time)).substring(11, 16)

        if (!dialog.isChecked) {
            view.element_dialog_tv_is_checked.visible()
        } else {
            view.element_dialog_tv_is_checked.gone()
        }
    }

    private fun showAvatar(user: User, view: View, context: Context) {
        Picasso.get()
            .load(user.photoLink)
            .resize(
                context.resources.getDimensionPixelSize(R.dimen.photo_avatar_width),
                context.resources.getDimensionPixelSize(R.dimen.photo_avatar_width)
            )
            .centerCrop()
            .transform(CircularTransformation())
            .into(view.element_dialog_iv_avatar)
    }

    private fun goToDialog(dialog: Dialog, context: Context) {
        val companionDialog = Dialog()
        companionDialog.id = dialog.user.id
        companionDialog.ownerId = dialog.user.id
        companionDialog.user.id = dialog.ownerId

        val intent = Intent(context, MessagesActivity::class.java)
        intent.putExtra(Dialog.DIALOG, dialog)
        intent.putExtra(User.USER, dialog.user)
        intent.putExtra(Dialog.COMPANION_DIALOG, companionDialog)
        context.startActivity(intent)
        (context as Activity).overridePendingTransition(0, 0)
    }
}