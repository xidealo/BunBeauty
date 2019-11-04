package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.MainScreenInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.MainScreenView
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

@InjectViewState
class MainScreenPresenter(private val mainScreenInteractor: MainScreenInteractor) : MvpPresenter<MainScreenView>(),
        MainScreenCallback, CoroutineScope {

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private var currentCountOfUsers = 0
    private var mainServiceList = arrayListOf<Service>()
    private var mainUserList = arrayListOf<User>()
    fun createMainScreen() {
        mainScreenInteractor.getMyUser(this)
    }

    override fun getUsersByCity(user: User) {
        mainScreenInteractor.getUsersByCity(user.city, this)
    }

    override fun getServicesByUserId(users: List<User>) {
        launch {
            setListenerCountOfReturnServices(users.size)
        }

        mainUserList.addAll(users)

        for (user in users) {
            mainScreenInteractor.getServicesByUserId(user.id, this)
        }
    }

    override fun returnServicesList(serviceList: List<Service>) {
        currentCountOfUsers++
        mainServiceList.addAll(serviceList)
    }

    override suspend fun setListenerCountOfReturnServices(countOfUsers: Int) {
        while (countOfUsers != currentCountOfUsers) {
            delay(500)
        }

        mainScreenInteractor.addToServiceList(mainServiceList[0], mainUserList[0])
        viewState.hideLoading()

        //viewState.showMainScreen(mainServiceList,mainUserList)
    }

    fun createMainScreenWithCategory(category: String) {

    }

}