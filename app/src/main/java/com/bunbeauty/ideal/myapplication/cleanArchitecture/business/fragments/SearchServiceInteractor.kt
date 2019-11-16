package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.SearchServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth

class SearchServiceInteractor(val userRepository: UserRepository) : IUserSubscriber {

    lateinit var searchServiceCallback: SearchServiceCallback
    var cities = arrayListOf<String>()

    fun setMyCity(cities: ArrayList<String>, searchServiceCallback: SearchServiceCallback) {
        this.searchServiceCallback = searchServiceCallback
        this.cities = cities
        userRepository.getById(getUserId(), this, false)
    }

    override fun returnUser(user: User) {
        searchServiceCallback.setCity(cities.indexOf(user.city))
    }

    override fun returnUsers(users: List<User>) {
        //log
    }

    fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid
}