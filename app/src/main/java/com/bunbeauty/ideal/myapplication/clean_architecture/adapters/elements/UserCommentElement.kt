package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements

import android.content.Context
import android.content.Intent
import android.view.View
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.business.CircularTransformation
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments.CurrentCommentActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.element_user_comment.view.*

class UserCommentElement(private val context: Context, private val userComment: UserComment) {

    fun createElement(view: View) {
        view.element_user_comment_ll.setOnClickListener {
            goToCurrentComment()
        }
        Picasso.get()
            .load(userComment.user.photoLink)
            .resize(
                context.resources.getDimensionPixelSize(R.dimen.photo_avatar_width),
                context.resources.getDimensionPixelSize(R.dimen.photo_avatar_width)
            )
            .centerCrop()
            .transform(CircularTransformation())
            .into(view.element_user_comment_iv_avatar)

        view.element_user_comment_tv_name.text =
            "${userComment.user.name}  ${userComment.user.surname}"
        view.element_user_comment_tv_review.text = userComment.review
        view.element_user_comment_rb_rating.rating = userComment.rating
    }

    private fun goToCurrentComment() {
        val intent = Intent(context, CurrentCommentActivity::class.java).apply {
            putExtra(UserComment.USER_COMMENT, userComment)
        }
        context.startActivity(intent)
    }

}