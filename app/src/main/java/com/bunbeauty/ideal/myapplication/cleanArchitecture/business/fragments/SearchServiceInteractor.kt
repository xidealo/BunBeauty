package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.fragments

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.ISearchServiceCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.UsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth

class SearchServiceInteractor(val userRepository: UserRepository) : UsersCallback {

    lateinit var searchServiceCallback: ISearchServiceCallback
    var cities = arrayListOf<String>()

    fun setMyCity(cities: ArrayList<String>, searchServiceCallback: ISearchServiceCallback) {
        this.searchServiceCallback = searchServiceCallback
        this.cities = cities
        userRepository.getById(getUserId(), this, false)
    }



    fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid

    override fun returnUsers(users: List<User>) {
        //searchServiceCallback.setCity(cities.indexOf(user.city))
    }
}