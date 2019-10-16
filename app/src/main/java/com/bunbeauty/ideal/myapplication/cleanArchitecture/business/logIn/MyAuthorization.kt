package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.logIn


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.logIn.RegistrationActivity
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithLocalStorageApi
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.DBHelper
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.dao.UserDao
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.profile.ProfileActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyAuthorization(private val context: Context,
                      private val myPhoneNumber: String,
                      private val userDao: UserDao) {

    companion object {
        private const val TAG = "DBInf"

        private const val ORDERS = "orders"
        private const val USER_ID = "user id"
        private const val IS_CANCELED = "is canceled"
        private const val COUNT_OF_RATES = "count of rates"
        private const val SUBSCRIBERS = "subscribers"

        private const val WORKING_DAY_ID = "working day id"
        private const val WORKING_TIME_ID = "working time id"
        private const val WORKING_DAYS = "working days"
        private const val WORKING_TIME = "working time"
        private const val TIME = "time"
        private const val DATE = "date"

        private const val SERVICE_ID = "service id"
        private const val WORKER_ID = "worker id"
    }

    private var dbHelper: DBHelper = DBHelper(context)
    private var counter: Int = 0

    private lateinit var serviceThread: Thread
    private lateinit var dayThread: Thread
    private lateinit var timeThread: Thread
    private lateinit var orderThread: Thread

    fun authorizeUser() {

        val userQuery = FirebaseDatabase
                .getInstance()
                .getReference(User.USERS)
                .orderByChild(User.PHONE)
                .equalTo(myPhoneNumber)

        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(usersSnapshot: DataSnapshot) {
                if (usersSnapshot.value == null) {
                    goToRegistration()
                } else {
                    // Получаем остальные данные о пользователе
                    val userName = getUserName(usersSnapshot)
                    if (userName == null) {
                        goToRegistration()
                    } else {

                        clearSQLite()

                        //loadProfileData(usersSnapshot)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                attentionBadConnection()
            }
        })
    }

   /* private fun loadProfileData(usersSnapshot: DataSnapshot) {
        val userSnapshot = usersSnapshot
                .children
                .iterator()
                .next()

        loadUserInfo(userSnapshot)

        val userId = userSnapshot.key
        //counter
        serviceThread = Thread(Runnable {
            LoadingProfileData.loadUserServices(userSnapshot.child(Service.SERVICES),
                    userId,
                    localDatabase)
            serviceThread!!.interrupt()
        })
        serviceThread!!.start()

        LoadingProfileData.addSubscriptionsCountInLocalStorage(userSnapshot, localDatabase)
        LoadingProfileData.addSubscribersCountInLocalStorage(userSnapshot, localDatabase)
        loadMyOrders(userSnapshot.child(ORDERS))

        //set listener for countOfRates
        val countOfRatesRef = FirebaseDatabase.getInstance()
                .getReference(User.USERS)
                .child(userId!!)
                .child(COUNT_OF_RATES)
        countOfRatesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(userSnapshotCountOfRates: DataSnapshot) {
                val countOfRates = userSnapshotCountOfRates.getValue(Long::class.java)!!.toString()
                updateCountOfRatesInLocalStorage(countOfRates, userId)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
        // set listener for count of Subscribers
        val countOfSubscribersRef = FirebaseDatabase.getInstance()
                .getReference(User.USERS)
                .child(userId)
        countOfSubscribersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {
                LoadingProfileData.addSubscribersCountInLocalStorage(userSnapshot, localDatabase)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }*/

    private fun loadUserInfo (usersSnapshot: DataSnapshot) {
        val userSnapshot = usersSnapshot
                .children
                .iterator()
                .next()

        val user = User()
        user.id = userSnapshot.key!!
        user.phone = myPhoneNumber
        user.name = userSnapshot
                .child(User.NAME)
                .getValue<String>(String::class.java)!!
        user.city = userSnapshot
                .child(User.CITY)
                .getValue<String>(String::class.java)!!
        user.countOfRates = userSnapshot
                .child(User.COUNT_OF_RATES)
                .getValue<Long>(Long::class.java)!!
        user.photoLink = userSnapshot
                .child(User.PHOTO_LINK)
                .getValue<String>(String::class.java)!!
        user.rating = userSnapshot
                .child(User.AVG_RATING)
                .getValue<Float>(Float::class.java)!!
        user.subscribersCount = userSnapshot
                .child(User.COUNT_OF_SUBSCRIBERS)
                .getValue<Long>(Long::class.java)!!
        user.subscriptionsCount = userSnapshot
                .child(User.COUNT_OF_SUBSCRIPTIONS)
                .getValue<Long>(Long::class.java)!!

        //userDao.insert(user)
    }

    private fun getUserName(usersSnapshot: DataSnapshot): String? {
        return usersSnapshot
                .children
                .iterator()
                .next()
                .child(User.NAME)
                .getValue<String>(String::class.java)
    }

    private fun updateCountOfRatesInLocalStorage(countOfRates: String, userId: String?) {
        Log.d(TAG, "updateCountOfRatesInLocalStorage: ")
        val contentValues = ContentValues()
        val database = dbHelper.readableDatabase

        contentValues.put(DBHelper.KEY_COUNT_OF_RATES_USERS, countOfRates)
        val hasSomeData = WorkWithLocalStorageApi.hasSomeData(DBHelper.TABLE_CONTACTS_USERS, userId)

        if (hasSomeData) {
            database.update(DBHelper.TABLE_CONTACTS_USERS, contentValues,
                    DBHelper.KEY_ID + " = ?",
                    arrayOf<String>({userId}.toString()))
        } else {
            contentValues.put(DBHelper.KEY_ID, userId)
            database.insert(DBHelper.TABLE_CONTACTS_USERS, null, contentValues)
        }
    }

    private fun loadMyOrders(_ordersSnapshot: DataSnapshot) {

        if (_ordersSnapshot.childrenCount == 0L) {
            goToProfile()
        }
        counter = 0
        val childrenCount = _ordersSnapshot.childrenCount
        for (orderSnapshot in _ordersSnapshot.children) {
            //получаем "путь" к мастеру, на сервис которого мы записаны
            val orderId = orderSnapshot.key
            val workerId = orderSnapshot.child(WORKER_ID).value.toString()
            val serviceId = orderSnapshot.child(SERVICE_ID).value.toString()
            val workingDayId = orderSnapshot.child(WORKING_DAY_ID).value.toString()
            val workingTimeId = orderSnapshot.child(WORKING_TIME_ID).value.toString()
            val serviceReference = FirebaseDatabase.getInstance()
                    .getReference(User.USERS)
                    .child(workerId)
                    .child(Service.SERVICES)
                    .child(serviceId)

            serviceReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(serviceSnapshot: DataSnapshot) {
                    //получаем данные для нашего ордера
                    val daySnapshot = serviceSnapshot.child(WORKING_DAYS)
                            .child(workingDayId)

                    if (isActualOrder(daySnapshot, workingTimeId, orderId)) {

                        dayThread = Thread(Runnable { addWorkingDaysInLocalStorage(daySnapshot, serviceId) })
                        dayThread.start()

                        val timeSnapshot = daySnapshot.child(WORKING_TIME)
                                .child(workingTimeId)
                        timeThread = Thread(Runnable { addTimeInLocalStorage(timeSnapshot, workingDayId) })
                        timeThread.start()

                        val orderSnapshot = timeSnapshot.child(ORDERS)
                                .child(orderId!!)
                        orderThread = Thread(Runnable { addOrderInLocalStorage(orderSnapshot, workingTimeId) })
                        orderThread.start()

                        val serviceName = serviceSnapshot.child(Service.NAME).getValue(String::class.java)
                        addServiceInLocalStorage(serviceId, serviceName, workerId)
                    }

                    counter++
                    if (counter.toLong() == childrenCount) {
                        if (dayThread.isAlive || timeThread.isAlive || orderThread.isAlive) {
                            try {
                                dayThread.join()
                                timeThread.join()
                                orderThread.join()
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }

                            goToProfile()
                        } else {
                            goToProfile()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    attentionBadConnection()
                }
            })
        }
    }

    private fun isActualOrder(daySnapshot: DataSnapshot, timeId: String, orderId: String?): Boolean {
        val date = daySnapshot.child(DATE).getValue(String::class.java)
        val time = daySnapshot.child(WORKING_TIME).child(timeId).child(TIME).getValue(String::class.java)
        val isCanceled = daySnapshot.child(WORKING_TIME)
                .child(timeId)
                .child(ORDERS)
                .child(orderId!!)
                .child(IS_CANCELED)
                .getValue(Boolean::class.java)!!

        //3600000 * 24 = 24 часа
        val commonDate = "$date $time"
        val orderDate = WorkWithTimeApi.getMillisecondsStringDate(commonDate) + 3600000
        val sysdate = WorkWithTimeApi.getSysdateLong()

        return if (orderDate < sysdate || isCanceled) {
            false
        } else true

    }

    private fun addServiceInLocalStorage(serviceId: String, serviceName: String?, workerId: String) {
        val database = dbHelper.readableDatabase

        val contentValues = ContentValues()
        contentValues.put(DBHelper.KEY_ID, serviceId)
        contentValues.put(DBHelper.KEY_NAME_SERVICES, serviceName)
        contentValues.put(DBHelper.KEY_USER_ID, workerId)

        database.insert(DBHelper.TABLE_CONTACTS_SERVICES, null, contentValues)
    }

    private fun addWorkingDaysInLocalStorage(workingDaySnapshot: DataSnapshot, serviceId: String) {
        val database = dbHelper.readableDatabase

        val contentValues = ContentValues()
        val dayId = workingDaySnapshot.key
        val date = workingDaySnapshot.child(DATE).value.toString()
        contentValues.put(DBHelper.KEY_ID, dayId)
        contentValues.put(DBHelper.KEY_DATE_WORKING_DAYS, date)
        contentValues.put(DBHelper.KEY_SERVICE_ID_WORKING_DAYS, serviceId)

        database.insert(DBHelper.TABLE_WORKING_DAYS, null, contentValues)

        dayThread.interrupt()
    }

    private fun addTimeInLocalStorage(timeSnapshot: DataSnapshot, workingDayId: String) {
        val database = dbHelper.readableDatabase

        val contentValues = ContentValues()
        val timeId = timeSnapshot.key
        contentValues.put(DBHelper.KEY_ID, timeId)
        contentValues.put(DBHelper.KEY_TIME_WORKING_TIME, timeSnapshot.child(TIME).value.toString())
        contentValues.put(DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME, workingDayId)

        database.insert(DBHelper.TABLE_WORKING_TIME, null, contentValues)

        timeThread.interrupt()
    }

    private fun addOrderInLocalStorage(orderSnapshot: DataSnapshot, timeId: String) {
        val database = dbHelper.readableDatabase

        val orderId = orderSnapshot.key
        val userId = orderSnapshot.child(USER_ID).value.toString()

        val contentValues = ContentValues()
        contentValues.put(DBHelper.KEY_ID, orderId)
        contentValues.put(DBHelper.KEY_USER_ID, userId)
        contentValues.put(DBHelper.KEY_IS_CANCELED_ORDERS, "false")
        contentValues.put(DBHelper.KEY_WORKING_TIME_ID_ORDERS, timeId)

        database.insert(DBHelper.TABLE_ORDERS, null, contentValues)

        orderThread.interrupt()
    }

    // Удаляет все данные о пользователях, сервисах, рабочих днях и рабочем времени из SQLite
    private fun clearSQLite() {

        val database = dbHelper.writableDatabase

        database.delete(DBHelper.TABLE_CONTACTS_USERS, null, null)
        database.delete(DBHelper.TABLE_CONTACTS_SERVICES, null, null)
        database.delete(DBHelper.TABLE_WORKING_DAYS, null, null)
        database.delete(DBHelper.TABLE_WORKING_TIME, null, null)

        database.delete(DBHelper.TABLE_PHOTOS, null, null)
        database.delete(DBHelper.TABLE_SUBSCRIBERS, null, null)

        database.delete(DBHelper.TABLE_REVIEWS, null, null)
        database.delete(DBHelper.TABLE_ORDERS, null, null)
        database.delete(DBHelper.TABLE_TAGS, null, null)
    }

    private fun attentionBadConnection() {
        Toast.makeText(context, "Плохое соединение", Toast.LENGTH_SHORT).show()
    }

    private fun goToRegistration() {
        val intent = Intent(context, RegistrationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(User.PHONE, myPhoneNumber)
        context!!.startActivity(intent)
    }

    private fun goToProfile() {
        // тоже самое необходимо прописать для перехода с регистрации
        //ContextCompat.startForegroundService(context, new Intent(context, MyService.class));
        val intent = Intent(context, ProfileActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context!!.startActivity(intent)
        //context.startService(new Intent(context, MyService.class));
    }

}
