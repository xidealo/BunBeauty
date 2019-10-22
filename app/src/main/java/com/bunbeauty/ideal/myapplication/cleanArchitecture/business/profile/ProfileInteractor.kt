package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.BaseRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId

class ProfileInteractor(private val userRepository: UserRepository,
                        private val serviceRepository: ServiceRepository,
                        private val intent: Intent) : BaseRepository(),
        IProfileInteractor, IUserSubscriber {
    override fun loadProfile(ownerId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val OWNER_ID = "owner id"
    private val TOKEN = "token"

    lateinit var userSubscriber: IUserSubscriber

    override fun updateProfile(ownerId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCountOfRates(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isFirstEnter() = (intent.getStringExtra(OWNER_ID) == null)

    override fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    override fun getOwnerId() = intent.getStringExtra(OWNER_ID) ?: getUserId()

    override fun checkSubscription(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initFCM() {
        val token = FirebaseInstanceId.getInstance().token
        val reference = FirebaseDatabase.getInstance().reference
        reference.child(User.USERS)
                .child(getUserId())
                .child(TOKEN)
                .setValue(token)
    }

    override fun userIsOwner():Boolean {
        return getUserId() == getOwnerId()
    }

    override fun getProfileOwner(userSubscriber: IUserSubscriber) {
        this.userSubscriber = userSubscriber

        userRepository.getById(getOwnerId(), this)
    }

    override fun getProfileServiceList() {}/* = serviceRepository.getAllUserServices(getOwnerId(intent))*/

    override fun returnUser(user: User) {
        userSubscriber.returnUser(user)
    }

}