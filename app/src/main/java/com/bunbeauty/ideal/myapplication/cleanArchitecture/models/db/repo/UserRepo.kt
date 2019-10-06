package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.repo

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

interface UserRepo {

    fun findById(id: Int): List<User>

    fun findAll(): List<User>

    fun insert(user: User): Long

    fun delete(user: User)

}