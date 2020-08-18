package com.bunbeauty.ideal.myapplication.clean_architecture.business.profile

import android.content.Intent
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.clean_architecture.business.profile.iProfile.IProfileUserInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.UpdateUsersCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.clean_architecture.data.repositories.interface_repositories.IUserRepository
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.activities.log_in.RegistrationActivity
import com.google.firebase.iid.FirebaseInstanceId


class ProfileUserInteractor(
    private val userRepository: IUserRepository,
    private val intent: Intent
) : BaseRepository(), IProfileUserInteractor, UserCallback, UpdateUsersCallback {

    private lateinit var profilePresenterCallback: ProfilePresenterCallback

    private var cacheOwner: User? = null

    override fun getCacheOwner(): User = cacheOwner!!

    override fun getCacheUser(): User = cacheUser

    override fun getProfileOwner(profilePresenterCallback: ProfilePresenterCallback) {
        this.profilePresenterCallback = profilePresenterCallback

        val user: User? = if (intent.hasExtra(User.USER)) {
            intent.getSerializableExtra(User.USER) as User
        } else {
            null
        }
        if (user != null && user.id.isNotEmpty()) {
            returnGottenObject(user)
        } else {
            userRepository.getById(
                intent.getStringExtra(User.USER_ID) ?: User.getMyId(),
                this,
                true
            )
        }
    }

    override fun returnGottenObject(user: User?) {
        if (user == null) return

        cacheOwner = user
        profilePresenterCallback.returnProfileOwner(user)
        profilePresenterCallback.showCountOfSubscriber(user.subscribersCount)
        whoseProfile(user, profilePresenterCallback)
    }

    private fun whoseProfile(user: User, profilePresenterCallback: ProfilePresenterCallback) {
        if (isMyProfile(user.id, User.getMyId())) {
            profilePresenterCallback.showMyProfile(user)
            cacheUser = cacheOwner!!
            profilePresenterCallback.getOrderList(user.id)
        } else {
            profilePresenterCallback.showAlienProfile(user)
        }
    }

    override fun isMyProfile(ownerId: String, myId: String) = ownerId == myId

    override fun updateUserFromEditUser(
        user: User,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        cacheOwner = user
        cacheUser = user

        profilePresenterCallback.returnProfileOwner(user)
        whoseProfile(user, profilePresenterCallback)
    }

    override fun initFCM() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            userRepository.setToken(instanceIdResult.token)
        }
    }

    private fun isFromRegistration() =
        (intent.getStringExtra(RegistrationActivity.REGISTRATION_ACTIVITY) ?: "").isNotEmpty()

    override fun updateMyProfileServices(
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        if (cacheOwner != null) {
            if (isMyProfile(cacheOwner!!.id, User.getMyId())) {
                profilePresenterCallback.getServiceList(cacheOwner!!.id)
            }
        }
    }

    override fun updateCountOfSubscribers(
        user: User,
        subscriber: Int,
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        user.subscribersCount += subscriber
        userRepository.update(user, this)
    }

    override fun returnUpdatedCallback(obj: User) {
        cacheOwner = obj
        profilePresenterCallback.showCountOfSubscriber(obj.subscribersCount)
    }

    override fun checkIconClick(profilePresenterCallback: ProfilePresenterCallback) {
        if (isMyProfile(cacheOwner!!.id, User.getMyId())) {
            profilePresenterCallback.goToEditProfile(cacheOwner!!)
        }
    }

    override fun updateBottomPanel(profilePresenterCallback: ProfilePresenterCallback) {
        if (cacheOwner == null) {
            return
        }

        if (isMyProfile(cacheOwner!!.id, User.getMyId())) {
            profilePresenterCallback.showUpdatedBottomPanel(R.id.navigation_profile)
        } else {
            profilePresenterCallback.showUpdatedBottomPanel()
        }
    }

    companion object {
        var cacheUser = User()
    }

}