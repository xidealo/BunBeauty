package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.db.dao

import androidx.room.*
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User

@Dao
interface UserDao {

    fun getAll() : List<User>

    @Query("SELECT * FROM user WHERE id = :id")
    fun getById(id: Long) : List<User>

    @Insert
    fun insert(employee: User)

    @Update
    fun update(employee: User)

    @Delete
    fun delete(employee: User)
}