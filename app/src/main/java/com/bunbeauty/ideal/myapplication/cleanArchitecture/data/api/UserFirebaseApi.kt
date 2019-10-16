package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import android.util.Log
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IUserRepository
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class UserFirebaseApi: IUserRepository {

    private val TAG = "data_layer"

    override fun insert(user: User) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(User.USERS).child(user.id)

        val items = HashMap<String, Any>()
        items[User.NAME] = user.name
        items[User.CITY] = user.city
        items[User.PHONE] = user.phone
        items[User.AVG_RATING] = user.rating
        items[User.COUNT_OF_RATES] = user.countOfRates
        items[User.PHOTO_LINK] = user.photoLink
        myRef.updateChildren(items)
        Log.d(TAG, "User inserting completed ")
    }

    override fun getById(id:String):User{

        return User()
    }

    override fun delete(user: User) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(user: User) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(): List<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
