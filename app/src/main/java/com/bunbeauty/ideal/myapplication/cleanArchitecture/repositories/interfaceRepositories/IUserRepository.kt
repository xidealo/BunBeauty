package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IUserRepository {
    fun insert(user: User)
    fun delete(user: User)
    fun deleteById(id: String)
    fun update(user: User)
    fun get(): List<User>

    fun getById(id:String, userSubscriber: IUserCallback, isFirstEnter:Boolean)
    fun getByPhoneNumber(phoneNumber:String, userSubscriber: IUserCallback, isFirstEnter: Boolean)
    fun getByCity(city:String, userSubscriber: IUserCallback, isFirstEnter:Boolean)
    fun getByName(name:String, userSubscriber: IUserCallback, isFirstEnter:Boolean)
    fun getByCityAndUserName(city: String, userName:String, userSubscriber: IUserCallback, isFirstEnter: Boolean)
}