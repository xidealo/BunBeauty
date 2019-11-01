package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.profile.ProfileInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.ServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ProfileCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ProfileView
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ServiceView
import com.google.firebase.auth.FirebaseAuth

@InjectViewState
class ServicePresenter(private val serviceInteractor: ServiceInteractor):
        MvpPresenter<ServiceView>(), ServiceCallback {

    private val TAG = "DBInf"

    fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun isMyService(): Boolean {
        return true//FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun getService() {
        serviceInteractor.getService(this)
    }

    override fun callbackGetService(service: Service) {
        viewState.showServiceInfo(service)
    }
}