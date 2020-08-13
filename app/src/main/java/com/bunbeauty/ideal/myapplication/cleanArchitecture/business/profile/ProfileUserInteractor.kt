package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import android.content.Intent
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UpdateUsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IUserRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn.RegistrationActivity
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

        if (intent.hasExtra(User.USER)) {
            val user = intent.getSerializableExtra(User.USER) as User
            //такое может быть?
            if (user.id.isNotEmpty()) {
                returnElement(user)
            } else {
                userRepository.getById(
                    intent.getStringExtra(User.USER_ID) ?: User.getMyId(),
                    this,
                    true
                )
            }
        } else {
            userRepository.getById(
                intent.getStringExtra(User.USER_ID) ?: User.getMyId(),
                this,
                true
            )
        }
    }

    override fun returnElement(element: User?) {
        if (element == null) return

        cacheOwner = element
        profilePresenterCallback.returnProfileOwner(element)
        profilePresenterCallback.showCountOfSubscriber(element.subscribersCount)
        whoseProfile(element, profilePresenterCallback)
    }

    private fun whoseProfile(user: User, profilePresenterCallback: ProfilePresenterCallback) {
        if (isMyProfile(user.id, User.getMyId())) {
            profilePresenterCallback.showMyProfile(user)
            cacheUser = cacheOwner!!
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
        //if (isFromRegistration()) {
            FirebaseInstanceId.getInstance().instanceId
                .addOnSuccessListener { instanceIdResult ->
                    userRepository.setToken(instanceIdResult.token)
                }
        //}
    }

    private fun isFromRegistration() =
        (intent.getStringExtra(RegistrationActivity.REGISTRATION_ACTIVITY) ?: "").isNotEmpty()

    override fun updateMyProfileServices(
        profilePresenterCallback: ProfilePresenterCallback
    ) {
        if (cacheOwner != null) {
            if (isMyProfile(cacheOwner!!.id, User.getMyId())) {
                profilePresenterCallback.getProfileServiceList(cacheOwner!!.id)
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