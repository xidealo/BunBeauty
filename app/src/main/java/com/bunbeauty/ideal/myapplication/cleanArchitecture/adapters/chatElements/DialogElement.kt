package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.chatElements

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.chat.MessagesActivity
import com.bunbeauty.ideal.myapplication.helpApi.CircularTransformation
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithStringsApi
import com.squareup.picasso.Picasso

class DialogElement(
    private val view: View,
    private val context: Context
) : View.OnClickListener {

    private lateinit var avatarDialogElementImage: ImageView
    private lateinit var nameDialogElementText: TextView
    private lateinit var lastMessageDialogElementText: TextView
    private lateinit var messageTimeDialogElementText: TextView
    private lateinit var dialogElementLayout: LinearLayout
    private lateinit var dialog: Dialog

    fun createElement(dialog: Dialog) {
        this.dialog = dialog
        onViewCreated(view)
        setData(dialog)
    }

    private fun onViewCreated(view: View) {
        avatarDialogElementImage = view.findViewById(R.id.avatarDialogElementImage)
        nameDialogElementText = view.findViewById(R.id.nameDialogElementText)
        lastMessageDialogElementText = view.findViewById(R.id.lastMessageDialogElementText)
        messageTimeDialogElementText = view.findViewById(R.id.messageTimeDialogElementText)
        dialogElementLayout = view.findViewById(R.id.dialogElementLayout)
        dialogElementLayout.setOnClickListener(this)
    }

    private fun setData(dialog: Dialog) {
        showAvatar(dialog.user)
        nameDialogElementText.text = "${dialog.user.name} ${dialog.user.surname}"
        lastMessageDialogElementText.text = dialog.lastMessage.message
        messageTimeDialogElementText.text = dialog.lastMessage.time
    }

    private fun showAvatar(user: User) {
        Picasso.get()
            .load(user.photoLink)
            .resize(65, 65)
            .centerCrop()
            .transform(CircularTransformation())
            .into(avatarDialogElementImage)
    }

    override fun onClick(v: View) {
        goToDialog()
    }

    private fun goToDialog() {
        val intent = Intent(context, MessagesActivity::class.java)
        intent.putExtra(Dialog.COMPANION_DIALOG, dialog)

        val myDialog = Dialog()
        intent.putExtra(Dialog.DIALOG, myDialog)
        context.startActivity(intent)
    }

    companion object {
        private const val TAG = "DBInf"
    }

}