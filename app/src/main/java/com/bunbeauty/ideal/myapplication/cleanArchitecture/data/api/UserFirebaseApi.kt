package com.bunbeauty.ideal.myapplication.cleanArchitecture.data.api

import android.util.Log
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.interfaceRepositories.IUserRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class UserFirebaseApi : IUserRepository {

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

    override fun getById(id: String): User {
        val database = FirebaseDatabase.getInstance()
        val userRef = database
                .getReference(User.USERS)
                .child(id)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(userSnpshot: DataSnapshot) {
                val user = User()

                user.name = userSnpshot.child(User.NAME).getValue<String>(String::class.java)!!
                user.city = userSnpshot.child(User.CITY).getValue<String>(String::class.java)!!
                user.phone = userSnpshot.child(User.PHONE).getValue<String>(String::class.java)!!
                user.photoLink = userSnpshot.child(User.PHOTO_LINK).getValue<String>(String::class.java)!!
                user.countOfRates = userSnpshot.child(User.COUNT_OF_RATES).getValue<Long>(Long::class.java)!!
                user.rating = userSnpshot.child(User.AVG_RATING).getValue<Float>(Float::class.java)!!
                user.subscriptionsCount = userSnpshot.child(User.COUNT_OF_SUBSCRIPTIONS).getValue<Long>(Long::class.java)!!
                user.subscribersCount = userSnpshot.child(User.COUNT_OF_SUBSCRIBERS).getValue<Long>(Long::class.java)!!
            }

            override fun onCancelled(error: DatabaseError) {
                // Some error
            }
        })

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

    fun getByPhoneNumberFromFirebase(phoneNumber: String) {
        val userQuery = FirebaseDatabase.getInstance().getReference(User.USERS).orderByChild(User.PHONE).equalTo(phoneNumber)

        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(usersSnapshot: DataSnapshot) {
                if (usersSnapshot.childrenCount == 0L) {
                    // NULL
                } else {
                    // Получаем остальные данные о пользователе
                    val userSnapshot = usersSnapshot.children.iterator().next()
                    val name = userSnapshot.child(User.NAME).value
                    //RETURN USER CALLBACK
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun getByPhoneNumber(phoneNumber: String) : User = User()


}
