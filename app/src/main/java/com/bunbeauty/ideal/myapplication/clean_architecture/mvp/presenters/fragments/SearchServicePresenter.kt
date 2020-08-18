package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.fragments

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.fragments.SearchServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.ISearchServiceCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.fragments.SearchServiceFragmentView

@InjectViewState
class SearchServicePresenter(private val searchServiceInteractor: SearchServiceInteractor):
        MvpPresenter<SearchServiceFragmentView>(), ISearchServiceCallback {

    fun setMyCity(cities:ArrayList<String>){
        searchServiceInteractor.setMyCity(cities, this)
    }

    override fun setCity(position: Int) {
        viewState.showMyCity(position)
    }
}