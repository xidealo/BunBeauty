package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IUserRepository {
    fun insert(user: User)
    fun delete(user: User)
    fun deleteById(id: String)
    fun update(user: User)
    fun get(): List<User>

    fun getById(id:String, userSubscriber: IUserSubscriber, isFirstEnter:Boolean)
    fun getByPhoneNumber(phoneNumber:String, userSubscriber: IUserSubscriber)
    fun getByCity(city:String, userSubscriber: IUserSubscriber, isFirstEnter:Boolean)
}