package com.bunbeauty.ideal.myapplication.clean_architecture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements.UserCommentElement
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment

class UserCommentAdapter : RecyclerView.Adapter<UserCommentAdapter.UserCommentViewHolder>() {

    private val commentList: ArrayList<UserComment> = arrayListOf()

    fun setData(commentList: List<UserComment>) {
        this.commentList.clear()
        this.commentList.addAll(commentList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): UserCommentViewHolder {
        val context = viewGroup.context
        val layoutIdForListItem = R.layout.element_user_comment
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false)

        return this.UserCommentViewHolder(view, context)
    }

    override fun onBindViewHolder(commentViewHolder: UserCommentViewHolder, i: Int) {
        commentViewHolder.bind(commentList[i])
    }

    override fun getItemCount() = commentList.size

    inner class UserCommentViewHolder(private val view: View, private val context: Context) :
        RecyclerView.ViewHolder(view) {

        fun bind(userComment: UserComment) {
            val commentElement = UserCommentElement(context)
            commentElement.createElement(view)
            commentElement.setData(userComment)
        }
    }

}
