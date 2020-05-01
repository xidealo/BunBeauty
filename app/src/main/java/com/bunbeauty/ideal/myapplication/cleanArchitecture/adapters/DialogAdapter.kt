package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.chatElements.DialogElement
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import java.util.*

class DialogAdapter(
    private val dialogList: List<Dialog>
) : RecyclerView.Adapter<DialogAdapter.DialogViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DialogViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.dialog_element
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)
        return DialogViewHolder(view)
    }

    override fun onBindViewHolder(dialogViewHolder: DialogViewHolder, i: Int) {
        dialogViewHolder.bind(dialogList[i])
    }

    override fun getItemCount(): Int {
        return dialogList.size
    }

    inner class DialogViewHolder(private val view: View) :
        ViewHolder(view) {
        fun bind(dialog: Dialog) {
            val dialogElement = DialogElement(view)
            dialogElement.createElement(dialog)
        }
    }
}