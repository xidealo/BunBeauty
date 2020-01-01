package com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories

import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User

interface IUserRepository {
    fun insert(user: User, iInsertUsersCallback: IInsertUsersCallback)
    fun insertInRoom(user: User)
    fun delete(user: User, iDeleteUsersCallback: IDeleteUsersCallback)
    fun update(user: User, iUpdateUsersCallback: IUpdateUsersCallback)
    fun get(iUsersCallback: IUsersCallback)

    fun getById(id:String, userSubscriber: IUserCallback, isFirstEnter:Boolean)
    fun getByPhoneNumber(phoneNumber:String, userSubscriber: IUserCallback, isFirstEnter: Boolean)
    fun getByCity(city:String, usersSubscriber: IUsersCallback, isFirstEnter:Boolean)
    fun getByName(name:String, usersSubscriber: IUsersCallback, isFirstEnter:Boolean)
    fun getByCityAndUserName(city: String, userName:String, usersSubscriber: IUsersCallback, isFirstEnter: Boolean)
}