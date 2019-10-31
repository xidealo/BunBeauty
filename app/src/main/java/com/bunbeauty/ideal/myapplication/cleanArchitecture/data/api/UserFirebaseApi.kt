package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import android.util.Log
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class UserFirebaseApi {

    private val TAG = "data_layer"

    fun delete(user: User) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun update(user: User) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun get(): List<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun insert(user: User) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference(User.USERS).child(user.id)

        val items = HashMap<String, Any>()
        items[User.PHONE] = user.phone
        items[User.NAME] = user.name
        items[User.CITY] = user.city
        items[User.AVG_RATING] = user.rating
        items[User.COUNT_OF_RATES] = user.countOfRates
        items[User.PHOTO_LINK] = user.photoLink
        items[User.COUNT_OF_SUBSCRIBERS] = user.subscribersCount
        items[User.COUNT_OF_SUBSCRIPTIONS] = user.subscriptionsCount
        myRef.updateChildren(items)
        Log.d(TAG, "User has been inserted")
    }

    fun getById(id: String, callback: IUserSubscriber) {
        Log.d(TAG, "GET BY ID LOL 1 : ")

        val database = FirebaseDatabase.getInstance()
        val userRef = database
                .getReference(User.USERS)
                .child(id)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(usersSnapshot: DataSnapshot) {
                Log.d(TAG, "GET BY ID LOL 2")
                val user = getUserFromSnapshot(usersSnapshot)
                callback.returnUser(user)
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })
    }

    fun getByPhoneNumber(phoneNumber: String, callback: IUserSubscriber) {
        val userQuery = FirebaseDatabase.getInstance().getReference(User.USERS).orderByChild(User.PHONE).equalTo(phoneNumber)

        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(usersSnapshot: DataSnapshot) {
                var user = User()
                if (usersSnapshot.childrenCount > 0L) {
                    user = getUserFromSnapshot(usersSnapshot.children.iterator().next())
                }
                callback.returnUser(user)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Some error
            }
        })
    }

    private fun getUserFromSnapshot(userSnapshot: DataSnapshot): User {
        val user = User()

        user.id = userSnapshot.key!!
        user.name = userSnapshot.child(User.NAME).getValue<String>(String::class.java)!!
        user.city = userSnapshot.child(User.CITY).getValue<String>(String::class.java)!!
        user.phone = userSnapshot.child(User.PHONE).getValue<String>(String::class.java)!!
        user.photoLink = userSnapshot.child(User.PHOTO_LINK).getValue<String>(String::class.java)!!
        user.countOfRates = userSnapshot.child(User.COUNT_OF_RATES).getValue<Long>(Long::class.java)!!
        user.rating = userSnapshot.child(User.AVG_RATING).getValue<Float>(Float::class.java)!!
        user.subscriptionsCount = userSnapshot.child(User.COUNT_OF_SUBSCRIPTIONS).getValue<Long>(Long::class.java)!!
        user.subscribersCount = userSnapshot.child(User.COUNT_OF_SUBSCRIBERS).getValue<Long>(Long::class.java)!!

        return user
    }
}
