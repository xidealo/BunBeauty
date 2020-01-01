package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ISearchServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.IUserCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth

class SearchServiceInteractor(val userRepository: UserRepository) : IUserCallback {

    lateinit var searchServiceCallback: ISearchServiceCallback
    var cities = arrayListOf<String>()

    fun setMyCity(cities: ArrayList<String>, searchServiceCallback: ISearchServiceCallback) {
        this.searchServiceCallback = searchServiceCallback
        this.cities = cities
        userRepository.getById(getUserId(), this, false)
    }

    override fun returnUser(user: User) {
        searchServiceCallback.setCity(cities.indexOf(user.city))
    }

    fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid
}