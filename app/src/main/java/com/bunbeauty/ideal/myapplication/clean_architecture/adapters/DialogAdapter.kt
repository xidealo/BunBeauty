package com.bunbeauty.ideal.myapplication.clean_architecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.chatElements.DialogElement
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message

class DialogAdapter : RecyclerView.Adapter<DialogAdapter.DialogViewHolder>() {

    private val dialogList: ArrayList<Dialog> = arrayListOf()

    fun addItem(dialog: Dialog) {
        val foundDialog = dialogList.find { it.user.id == dialog.user.id }
        if (foundDialog == null) {
            dialogList.add(dialog)
            dialogList.sortByDescending { it.lastMessage.time }
            notifyItemInserted(dialogList.size)
        } else {
            val index = dialogList.indexOf(foundDialog)
            dialogList[index] = dialog
            dialogList.sortByDescending { it.lastMessage.time }
            notifyItemChanged(index)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DialogViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.element_dialog
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)
        return DialogViewHolder(view, context)
    }

    override fun onBindViewHolder(dialogViewHolder: DialogViewHolder, i: Int) {
        dialogViewHolder.bind(dialogList[i])
    }

    override fun getItemCount() = dialogList.size

    inner class DialogViewHolder(private val view: View, private val context: Context) :
        ViewHolder(view) {
        fun bind(dialog: Dialog) {
           DialogElement(view, context, dialog)
        }
    }


}