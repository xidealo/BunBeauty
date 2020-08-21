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

class DialogAdapter : RecyclerView.Adapter<DialogAdapter.DialogViewHolder>() {

    private val dialogList:ArrayList<Dialog> = arrayListOf()

    fun setData(dialogList: List<Dialog>) {
        this.dialogList.clear()
        this.dialogList.addAll(dialogList)
        notifyDataSetChanged()
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

    override fun getItemCount(): Int {
        return dialogList.size
    }

    inner class DialogViewHolder(private val view: View, private val context: Context) :
        ViewHolder(view) {
        fun bind(dialog: Dialog) {
            val dialogElement = DialogElement(view, context)
            dialogElement.createElement(dialog)
        }
    }


}