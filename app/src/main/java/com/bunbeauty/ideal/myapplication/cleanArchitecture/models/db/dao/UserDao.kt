package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.dao

import androidx.room.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    fun findAll() : List<User>

    @Query("SELECT * FROM user WHERE id = :id")
    fun findById(id: Long) : List<User>

    @Insert
    fun insert(user: User) : Long

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)
}