package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.profile.ProfilePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.service.IServicesCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.IUserCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId

class ProfileInteractor(
    private val userRepository: UserRepository,
    private val serviceRepository: ServiceRepository,
    private val intent: Intent
) : BaseRepository(),
    IProfileInteractor, IUserCallback, IServicesCallback {

    private val TAG = "DBInf"
    private lateinit var profilePresenterCallback: ProfilePresenterCallback
    lateinit var currentUser: User

    var services = mutableListOf<Service>()

    override fun getProfileOwner(profilePresenterCallback: ProfilePresenterCallback) {
        this.profilePresenterCallback = profilePresenterCallback
        if (intent.hasExtra(User.USER)) {
            returnUser(intent.getSerializableExtra(User.USER) as User)
            whoseProfile((intent.getSerializableExtra(User.USER) as User), profilePresenterCallback)
        } else {
            userRepository.getById(
                getUserId(),
                this,
                isFirstEnter(getUserId(), cachedUserIds)
            )
        }
    }

    override fun getServicesLink() = services


    override fun returnUser(user: User) {
        profilePresenterCallback.setUserProfile(user)
        currentUser = user
        getProfileServiceList(user.id)
        whoseProfile(user, profilePresenterCallback)
        setRating(user.rating, profilePresenterCallback)
    }

    fun setRating(rating: Float, profilePresenterCallback: ProfilePresenterCallback) {
        if (rating > 0) {
            profilePresenterCallback.showRating(rating)
        } else {
            profilePresenterCallback.showWithoutRating()
        }
    }

    private fun getProfileServiceList(userId: String) {
        serviceRepository.getServicesByUserId(
            userId,
            this,
            isFirstEnter(userId, cachedUserIdsForServices)
        )
    }

    override fun returnServices(serviceList: List<Service>) {
        services.addAll(serviceList)
        profilePresenterCallback.setServiceListWithOwner(serviceList, currentUser)
    }

    override fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid
    override fun isFirstEnter() = (intent.hasExtra(User.USER))


    private fun whoseProfile(user: User, profilePresenterCallback: ProfilePresenterCallback) {
        if (user.id == getUserId()) {
            profilePresenterCallback.showMyProfile(user)
            cacheCurrentUser = currentUser
        } else {
            profilePresenterCallback.showAlienProfile(user)
        }
    }

    override fun initFCM() {
        if (isFirstEnter()) {
            val token = FirebaseInstanceId.getInstance().token
            val reference = FirebaseDatabase.getInstance().reference
            reference.child(User.USERS)
                .child(getUserId())
                .child(TOKEN)
                .setValue(token)
        }
    }

    private fun isFirstEnter(id: String, idList: ArrayList<String>): Boolean {
        if (idList.contains(id)) {
            return false
        }
        idList.add(id)
        return true
    }

    override fun updateProfile(ownerId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadProfile(ownerId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCountOfRates(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkSubscription(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        const val OWNER_ID = "owner id"
        const val TOKEN = "token"
        val cachedUserIds = arrayListOf<String>()
        val cachedUserIdsForServices = arrayListOf<String>()
        var cacheCurrentUser = User()
    }
}