package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile

import android.app.Activity
import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.iProfile.IProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ProfileCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api.ProfileFirebase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.ProfileLocalDatabase
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.presentation.profile.ProfileActivity
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.logIn.ProfileRepository
import com.bunbeauty.ideal.myapplication.createService.AddingService
import com.bunbeauty.ideal.myapplication.reviews.Comments
import com.bunbeauty.ideal.myapplication.subscriptions.Subscribers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*

class ProfileInteractor(private val profileActivity: ProfileActivity) : IProfileInteractor, ProfileCallback {

    private val SERVICE_OWNER_ID = "service owner id"
    private val TYPE = "type"
    private val OWNER_ID = "owner id"
    private val STATUS = "status"
    private val TOKEN = "token"

    private val profileLocalDatabase = ProfileLocalDatabase()

    override fun loadProfile(ownerId: String) {
        val profileFirebase = ProfileFirebase(this)
        profileFirebase.loadProfileData(ownerId)
    }

    override fun callbackGetProfileData(user: User) {
        // отдельный поток
        profileLocalDatabase.addUserInLocalStorage(user)
        //
        profileActivity.showProfileData(user)
    }

    override fun callbackGetService(service: Service) {
        profileLocalDatabase.addServiceInLocalStorage(service)
    }

    override fun updateProfile(ownerId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCountOfRates(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    override fun getOwnerId(intent: Intent): String? {
        return intent.getStringExtra(OWNER_ID)
    }

    override fun checkSubscription(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun goToAddService(activity: Activity) {
        val intent = Intent(activity, AddingService::class.java)
        activity.startActivity(intent)
    }

    override fun goToSubscribers(activity: Activity, status: String) {
        val intent = Intent(activity, Subscribers::class.java)
        intent.putExtra(STATUS, status)
        activity.startActivity(intent)
    }

    override fun goToUserComments(activity: Activity, status: String, ownerId: String) {
        val intent = Intent(activity, Comments::class.java)
        intent.putExtra(SERVICE_OWNER_ID, ownerId)
        intent.putExtra(User.COUNT_OF_RATES, getCountOfRates())
        intent.putExtra(TYPE, status)
        activity.startActivity(intent)
    }

    override fun initFCM(ownerId: String) {
        val token = FirebaseInstanceId.getInstance().token
        val reference = FirebaseDatabase.getInstance().reference
        reference.child(User.USERS)
                .child(ownerId)
                .child(TOKEN)
                .setValue(token)
    }

    override fun isFirstEnter(intent: Intent):Boolean {
        return getOwnerId(intent) == null
    }

    override fun userIsOwner(intent: Intent):Boolean {
        return getUserId() == getOwnerId(intent)
    }


}