package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.elements

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.CircularTransformation
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.comment.UserComment
import com.squareup.picasso.Picasso

class UserCommentElement(
    private val context: Context
) : View.OnClickListener {

    private lateinit var avatarUserCommentElementImage: ImageView
    private lateinit var nameUserCommentElementText: TextView
    private lateinit var reviewUserCommentElementText: TextView
    private lateinit var ratingUserCommentElementBar: RatingBar


    fun createElement(view: View) {
        avatarUserCommentElementImage = view.findViewById(R.id.avatarUserCommentElementImage)
        nameUserCommentElementText = view.findViewById(R.id.nameUserCommentElementText)
        reviewUserCommentElementText = view.findViewById(R.id.reviewUserCommentElementText)
        ratingUserCommentElementBar = view.findViewById(R.id.ratingUserCommentElementBar)
    }

    fun setData(userComment: UserComment) {
        Picasso.get()
            .load(userComment.user.photoLink)
            .resize(65, 65)
            .centerCrop()
            .transform(CircularTransformation())
            .into(avatarUserCommentElementImage)

        nameUserCommentElementText.text = "${userComment.user.name}  ${userComment.user.surname}"
        reviewUserCommentElementText.text = userComment.review
        ratingUserCommentElementBar.rating = userComment.rating.toFloat()
    }

    override fun onClick(v: View) {
        goToCurrentComment()
    }

    private fun goToCurrentComment() {

    }

}