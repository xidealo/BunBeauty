package com.bunbeauty.ideal.myapplication.clean_architecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.ServiceCommentElement
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.UserCommentElement
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment

class ServiceCommentAdapter :
    RecyclerView.Adapter<ServiceCommentAdapter.ServiceCommentViewHolder>() {

    private val serviceCommentList: ArrayList<ServiceComment> = arrayListOf()

    fun addItem(serviceComment: ServiceComment) {
        val foundDialog = serviceCommentList.find { it.id == serviceComment.id }
        if (foundDialog == null) {
            serviceCommentList.add(serviceComment)
            serviceCommentList.sortByDescending { it.date }
            notifyItemInserted(serviceCommentList.size)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ServiceCommentViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.element_service_comment
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)

        return this.ServiceCommentViewHolder(view, context)
    }

    override fun onBindViewHolder(commentViewHolder: ServiceCommentViewHolder, i: Int) {
        commentViewHolder.bind(serviceCommentList[i])
    }

    override fun getItemCount() = serviceCommentList.size

    inner class ServiceCommentViewHolder(private val view: View, private val context: Context) :
        RecyclerView.ViewHolder(view) {

        fun bind(serviceComment: ServiceComment) {
            ServiceCommentElement(context, serviceComment, view)
        }
    }

}
