package com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.current_comment

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.business.commets.current_comment.iCurrentComment.ICurrentCommentCommentInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.comments.CurrentCommentPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.ServiceComment
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.comment.UserComment

class CurrentCommentCommentInteractor(
    private val intent: Intent
) : ICurrentCommentCommentInteractor {

    private lateinit var cacheUser: User

    override fun createCurrentCommentScreen(currentCommentPresenterCallback: CurrentCommentPresenterCallback) {

        if (intent.hasExtra(UserComment.USER_COMMENT)) {
            cacheUser = (intent.getSerializableExtra(
                UserComment.USER_COMMENT
            ) as UserComment).user

            currentCommentPresenterCallback.setUserComment(
                intent.getSerializableExtra(
                    UserComment.USER_COMMENT
                ) as UserComment
            )
        } else {
            cacheUser = (intent.getSerializableExtra(
                ServiceComment.SERVICE_COMMENT
            ) as ServiceComment).user

            currentCommentPresenterCallback.setServiceComment(
                intent.getSerializableExtra(
                    ServiceComment.SERVICE_COMMENT
                ) as ServiceComment
            )
        }
    }

    override fun getUserFromComment() = cacheUser
}