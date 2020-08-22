package com.bunbeauty.ideal.myapplication.clean_architecture.adapters.elements

import android.content.Context
import android.content.Intent
import android.view.View
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.business.CircularTransformation
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.comments.CurrentCommentActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.element_service_comment.view.*

class ServiceCommentElement(
    private val context: Context,
    private val serviceComment: ServiceComment
) {

    fun createElement(view: View) {
        view.element_service_comment_ll.setOnClickListener {
            goToCurrentComment()
        }

        Picasso.get()
            .load(serviceComment.user.photoLink)
            .resize(
                context.resources.getDimensionPixelSize(R.dimen.photo_avatar_width),
                context.resources.getDimensionPixelSize(R.dimen.photo_avatar_width)
            )
            .centerCrop()
            .transform(CircularTransformation())
            .into(view.element_service_comment_iv_avatar)

        view.element_service_comment_tv_name.text =
            "${serviceComment.user.name}  ${serviceComment.user.surname}"
        view.element_service_comment_tv_review.text = serviceComment.review
        view.element_service_comment_rb_rating.rating = serviceComment.rating
    }

    private fun goToCurrentComment() {
        val intent = Intent(context, CurrentCommentActivity::class.java).apply {
            putExtra(ServiceComment.SERVICE_COMMENT, serviceComment)
        }
        context.startActivity(intent)
    }

}