package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters.fragments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments.SearchServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.SearchServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.fragments.SearchServiceFragmentView

@InjectViewState
class SearchServicePresenter(private val searchServiceInteractor: SearchServiceInteractor):
        MvpPresenter<SearchServiceFragmentView>(), SearchServiceCallback {

    fun setMyCity(cities:ArrayList<String>){
        searchServiceInteractor.setMyCity(cities, this)
    }

    override fun setCity(position: Int) {
        viewState.showMyCity(position)
    }
}