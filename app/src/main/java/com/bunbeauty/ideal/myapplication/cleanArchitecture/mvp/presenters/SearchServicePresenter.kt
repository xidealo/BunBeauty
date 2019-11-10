package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.SearchServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.SearchServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.SearchServiceView

class SearchServicePresenter(private val searchServiceInteractor: SearchServiceInteractor): MvpPresenter<SearchServiceView>(),
        SearchServiceCallback {

    fun createSerchServiceScreen() {

    }

    override fun returnSearchServiceData(mainScreenData: ArrayList<ArrayList<Any>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}