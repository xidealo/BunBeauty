package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.repo

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import io.reactivex.Completable

interface UserRepo {

    fun findById(id: Int): List<User>

    fun findAll(): List<User>

    fun insert(user: User): Completable

    fun delete(user: User)

}