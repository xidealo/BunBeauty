package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import android.content.Intent
import com.android.ideal.myapplication.R
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UserCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.interfaceRepositories.IUserRepository
import com.google.firebase.auth.FirebaseAuth

class ProfileUserInteractor(
    private val userRepository: IUserRepository,
    private val intent: Intent
) : BaseRepository(), IProfileUserInteractor, UserCallback {

    private lateinit var profilePresenterCallback: ProfilePresenterCallback

    private var cacheOwner: User? = null

    override fun getCacheOwner(): User = cacheOwner!!

    override fun getCacheUser(): User = cacheUser

    override fun getProfileOwner(profilePresenterCallback: ProfilePresenterCallback) {
        this.profilePresenterCallback = profilePresenterCallback

        if (intent.hasExtra(User.USER)) {
            val user = intent.getSerializableExtra(User.USER) as User
            if (user.id.isNotEmpty()) {
                returnElement(user)
            } else {
                userRepository.getById(
                    User.getMyId(),
                    this,
                    true
                )
            }
        } else {
            userRepository.getByPhoneNumber(
                FirebaseAuth.getInstance().currentUser!!.phoneNumber!!,
                this,
                true
            )
        }
    }

    override fun returnElement(element: User) {
        cacheOwner = element
        profilePresenterCallback.returnProfileOwner(element)
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

    override fun updateUser(user: User, profilePresenterCallback: ProfilePresenterCallback) {
        cacheOwner = user
        cacheUser = user

        profilePresenterCallback.returnProfileOwner(user)
        whoseProfile(user, profilePresenterCallback)
    }

    override fun initFCM() {
        /*if (fromRegistartion()) {
            al token = FirebaseInstanceId . getInstance ().token
            val reference = FirebaseDatabase.getInstance().reference
            reference.child(User.USERS)
                .child(User.getMyId())
                .child(TOKEN)
                .setValue(token)
        }*/
    }

    override fun checkIconClick(profilePresenterCallback: ProfilePresenterCallback) {
        if (isMyProfile(cacheOwner!!.id, User.getMyId())) {
            profilePresenterCallback.goToEditProfile(cacheOwner!!)
        }
    }

    override fun getCountOfRates(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkSubscription(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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