package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.service.ServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.ServiceView
import com.google.firebase.auth.FirebaseAuth

@InjectViewState
class ServicePresenter(private val serviceInteractor: ServiceInteractor) :
        MvpPresenter<ServiceView>(), IUserServiceCallback {

    private val TAG = "DBInf"

    fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun isMyService(ownerId: String) = (getUserId() == ownerId)

    fun getUserAndService() {
        serviceInteractor.getService(this)
    }

    override fun returnUserAndService(user: User, service: Service) {
        viewState.showServiceInfo(user, service)
    }

    fun setTopPanel(ownerId: String, ownerPhotoLink: String, serviceId: String, serviceName: String) {
        if (isMyService(ownerId)) {
            viewState.showTopPanelForMyService(serviceId, serviceName)
        } else {
            viewState.showTopPanelForAlienService(serviceName, ownerPhotoLink, ownerId)
        }
    }

    fun setPremium(ownerId: String, premiumDate: String) {
        if (isMyService(ownerId)) {
            viewState.showPremium(serviceInteractor.isPremium(premiumDate))
        }
    }
}