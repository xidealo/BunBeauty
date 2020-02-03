package com.bunbeauty.ideal.myapplication.chat

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatActivity
import com.bunbeauty.ideal.myapplication.adapters.DialogAdapter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.DBHelper
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Dialog
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.interfaces.IBottomPanel
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.fragments.general.TopPanel
import com.bunbeauty.ideal.myapplication.helpApi.LoadingUserElementData
import com.bunbeauty.ideal.myapplication.helpApi.PanelBuilder
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithLocalStorageApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class Dialogs : MvpAppCompatActivity(), IBottomPanel {
    private var userId: String? = null
    private var userCount = 0
    private var counter = 0
    private var LSApi: WorkWithLocalStorageApi? = null
    private var dbHelper: DBHelper? = null
    private var database: SQLiteDatabase? = null
    private var progressBar: ProgressBar? = null
    private var manager: FragmentManager? = null
    private var dialogList: ArrayList<Dialog>? = null
    private var recyclerView: RecyclerView? = null
    private var dialogAdapter: DialogAdapter? = null
    private var noDialogsText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialogs)
        init()
        val topPanel = TopPanel()
        topPanel.title = "Диалоги"
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.topPanelDialogsLayout, topPanel)
        transaction.commit()
        createBottomPanel(supportFragmentManager, R.id.bottomPanelDialogsLayout)
    }

    private fun init() {
        userId = getUserId()
        recyclerView = findViewById(R.id.resultsDialogsRecycleView)
        progressBar = findViewById(R.id.progressBarDialogs)
        noDialogsText = findViewById(R.id.noDialogsDialogsText)
        dialogList = ArrayList()
        val layoutManager = LinearLayoutManager(this)
        /*recyclerView.setLayoutManager(layoutManager)*/
        manager = supportFragmentManager
        dbHelper = DBHelper(this)
        database = dbHelper!!.readableDatabase
        LSApi = WorkWithLocalStorageApi(database)
    }

    override fun onResume() {
        super.onResume()
        dialogList!!.clear()

        //Каждый раз загружаем
        loadOrders()
    }

    private fun loadOrders() { // берем все мои записи
        val orderReference = FirebaseDatabase.getInstance()
                .getReference(USERS)
                .child(userId!!)
                .child(ORDERS)
        orderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(ordersSnapshot: DataSnapshot) {
                if (ordersSnapshot.childrenCount == 0L) {
                    loadMyServiceOrders()
                }
                counter = 0
                val childrenCount = ordersSnapshot.childrenCount
                for (myOrderSnapshot in ordersSnapshot.children) { //получаем "путь" к мастеру, на сервис которого мы записаны
                    val orderId = myOrderSnapshot.key
                    val workerId = myOrderSnapshot.child(WORKER_ID).value.toString()
                    val serviceId = myOrderSnapshot.child(SERVICE_ID).value.toString()
                    val workingDayId = myOrderSnapshot.child(WORKING_DAY_ID).value.toString()
                    val workingTimeId = myOrderSnapshot.child(WORKING_TIME_ID).value.toString()
                    val serviceReference = FirebaseDatabase.getInstance()
                            .getReference(USERS)
                            .child(workerId)
                            .child(SERVICES)
                            .child(serviceId)
                            .child(WORKING_DAYS)
                            .child(workingDayId)
                            .child(WORKING_TIME)
                            .child(workingTimeId)
                            .child(ORDERS)
                            .child(orderId!!)
                    serviceReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(orderSnapshot: DataSnapshot) {
                            val messageTime = orderSnapshot.child(MESSAGE_TIME).getValue(String::class.java)!!
                            WorkWithLocalStorageApi.addDialogInfoInLocalStorage(serviceId,
                                    workingDayId,
                                    workingTimeId,
                                    orderId,
                                    userId,
                                    messageTime,
                                    workerId)
                            val userReference = FirebaseDatabase.getInstance()
                                    .getReference(USERS)
                                    .child(workerId)
                            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnapshot: DataSnapshot) {
                                    LoadingUserElementData.loadUserNameAndPhoto(userSnapshot, database)
                                    counter++
                                    if (counter.toLong() == childrenCount) {
                                        loadMyServiceOrders()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {}
                            })
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                myDialogs
            }
        })
    }

    private fun loadMyServiceOrders() {
        userCount = 0
        counter = 0
        val servicesReference = FirebaseDatabase.getInstance()
                .getReference(USERS)
                .child(userId!!)
                .child(SERVICES)
        servicesReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(servicesSnapshot: DataSnapshot) {
                for (serviceSnapshot in servicesSnapshot.children) {
                    val serviceId = serviceSnapshot.key
                    for (workingDaySnapshot in serviceSnapshot.child(WORKING_DAYS).children) {
                        val workingDayId = workingDaySnapshot.key
                        for (workingTimeSnapshot in workingDaySnapshot.child(WORKING_TIME).children) {
                            val workingTimeId = workingTimeSnapshot.key
                            for (orderSnapshot in workingTimeSnapshot.child(ORDERS).children) {
                                userCount++
                                val orderId = orderSnapshot.key
                                val orderUserId = orderSnapshot.child(USER_ID).getValue(String::class.java)!!
                                val messageTime = orderSnapshot.child(MESSAGE_TIME).getValue(String::class.java)!!
                                // добавляем данные в Local Storage
                                WorkWithLocalStorageApi.addDialogInfoInLocalStorage(serviceId,
                                        workingDayId,
                                        workingTimeId,
                                        orderId,
                                        orderUserId,
                                        messageTime,
                                        null)
                                val userReference = FirebaseDatabase.getInstance()
                                        .getReference(USERS)
                                        .child(orderUserId)
                                userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(userSnapshot: DataSnapshot) {
                                        LoadingUserElementData.loadUserNameAndPhoto(userSnapshot, database)
                                        counter++
                                        if (counter == userCount) {
                                            myDialogs
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {}
                                })
                            }
                        }
                    }
                }
                if (userCount == 0) {
                    myDialogs
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }// Если нет создаём и заносим в соданные// Если ко мне записывались на услугу
// Проверяем не создан ли уже диалог с клиентом
// Если нет создаём и заносим в соданные// Если я записывался на услугу
// Проверяем не создан ли уже диалог с владельцем сервиса

    // Проверка где лежит мой id
    private val myDialogs: Unit
        private get() {
            val dialogsCursor = createDialogsCursor()
            if (dialogsCursor.moveToFirst()) {
                val createdDialogs = ArrayList<String>()
                val indexOrderId = dialogsCursor.getColumnIndex(ORDER_ID)
                val indexOwnerId = dialogsCursor.getColumnIndex(OWNER_ID)
                val indexMessageTime = dialogsCursor.getColumnIndex(DBHelper.KEY_MESSAGE_TIME_ORDERS)
                do {
                    val orderId = dialogsCursor.getString(indexOrderId)
                    val ownerId = dialogsCursor.getString(indexOwnerId)
                    val messageTime = dialogsCursor.getString(dialogsCursor.getColumnIndex(DBHelper.KEY_MESSAGE_TIME_ORDERS))
                    // Проверка где лежит мой id
                    if (userId == orderId) { // Если я записывался на услугу
// Проверяем не создан ли уже диалог с владельцем сервиса
                        if (!createdDialogs.contains(ownerId)) { // Если нет создаём и заносим в соданные
                            createDialogWithUser(ownerId, messageTime)
                            createdDialogs.add(ownerId)
                        }
                    } else { // Если ко мне записывались на услугу
// Проверяем не создан ли уже диалог с клиентом
                        if (!createdDialogs.contains(orderId)) { // Если нет создаём и заносим в соданные
                            createDialogWithUser(orderId, messageTime)
                            createdDialogs.add(orderId)
                        }
                    }
                } while (dialogsCursor.moveToNext())
                dialogAdapter = DialogAdapter(dialogList!!.size, dialogList)
                recyclerView!!.adapter = dialogAdapter
                progressBar!!.visibility = View.GONE
                recyclerView!!.visibility = View.VISIBLE
                dialogsCursor.close()
            } else {
                setNoDialogs()
                dialogsCursor.close()
            }
        }

    private fun createDialogsCursor(): Cursor {
        val database = dbHelper!!.readableDatabase
        //берем все мои ордеры
        val dialogsQuery = ("SELECT DISTINCT "
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + " AS " + ORDER_ID + ", "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID + " AS " + OWNER_ID + ", "
                + DBHelper.KEY_MESSAGE_TIME_ORDERS
                + " FROM "
                + DBHelper.TABLE_ORDERS + ", "
                + DBHelper.TABLE_WORKING_TIME + ", "
                + DBHelper.TABLE_WORKING_DAYS + ", "
                + DBHelper.TABLE_CONTACTS_SERVICES
                + " WHERE "
                + DBHelper.KEY_WORKING_TIME_ID_ORDERS
                + " = "
                + DBHelper.TABLE_WORKING_TIME + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_WORKING_DAYS_ID_WORKING_TIME
                + " = "
                + DBHelper.TABLE_WORKING_DAYS + "." + DBHelper.KEY_ID
                + " AND "
                + DBHelper.KEY_SERVICE_ID_WORKING_DAYS
                + " = "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_ID
                + " AND ("
                + DBHelper.TABLE_ORDERS + "." + DBHelper.KEY_USER_ID + " = ? "
                + " OR "
                + DBHelper.TABLE_CONTACTS_SERVICES + "." + DBHelper.KEY_USER_ID + " = ?)"
                + " ORDER BY " + DBHelper.KEY_MESSAGE_TIME_ORDERS + " DESC")
        return database.rawQuery(dialogsQuery, arrayOf(userId, userId))
    }

    private fun createDialogWithUser(userId: String, messageTime: String) {
        val cursor = LSApi!!.getUser(userId)
        if (cursor.moveToNext()) {
            val userName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NAME_USERS))
            val dialog = Dialog()
            /*dialog.userId = userId;
            dialog.userName = userName;
            dialog.messageTime = messageTime;*/dialogList!!.add(dialog)
        }
    }

    private fun setNoDialogs() {
        progressBar!!.visibility = View.GONE
        noDialogsText!!.visibility = View.VISIBLE
    }

    private fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    companion object {
        private const val TAG = "DBInf"
        private const val USERS = "users"
        private const val SERVICES = "services"
        private const val WORKING_DAYS = "working days"
        private const val WORKING_TIME = "working time"
        private const val ORDERS = "orders"
        private const val MESSAGE_TIME = "time"
        private const val WORKER_ID = "worker id"
        private const val SERVICE_ID = "service id"
        private const val WORKING_DAY_ID = "working day id"
        private const val WORKING_TIME_ID = "working time id"
        private const val USER_ID = "user id"
        private const val OWNER_ID = "owner_id"
        private const val ORDER_ID = "order_id"
    }
}