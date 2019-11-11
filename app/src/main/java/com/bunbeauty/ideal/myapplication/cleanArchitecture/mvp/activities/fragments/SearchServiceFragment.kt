package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.ideal.myapplication.R
import com.arellomobile.mvp.MvpAppCompatFragment
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.fragments.SearchServiceFragmentView
import com.bunbeauty.ideal.myapplication.helpApi.Search
import com.google.firebase.database.*


class SearchServiceFragment constructor() : MvpAppCompatFragment(), View.OnClickListener, SearchServiceFragmentView {

    private var city = NOT_CHOSEN
    private var searchBy = NAME_OF_SERVICE
    private lateinit var searchLineInput: EditText

    //fragment просто вызывает методы поиска мс, больше ничего не делает?
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_service, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(view)
    }

    private fun init(view: View){

        //создаём выпадающее меню на основе массива городов
        //Выпадающее меню
        val citySpinner = view.findViewById<Spinner>(R.id.citySearchServiceSpinner)
        citySpinner.prompt = NOT_CHOSEN

        //создаём выпадающее меню "Поиск по"
        val searchBySpinner = view.findViewById<Spinner>(R.id.searchBySearchServiceSpinner)
        searchBySpinner.prompt = NAME_OF_SERVICE

        searchLineInput = view.findViewById(R.id.searchLineSearchServiceInput)

        val findBtn = view.findViewById<TextView>(R.id.findServiceSearchServiceText)
        findBtn.setOnClickListener(this)
        //отслеживаем смену городов в выпадающем меню
        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, itemSelected: View, selectedItemPosition: Int, selectedId: Long) {
                val cityText = itemSelected as TextView
                city = cityText.text.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        searchBySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, itemSelected: View, selectedItemPosition: Int, selectedId: Long) {
                val searchByText = itemSelected as TextView
                searchBy = searchByText.text.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.findServiceSearchServiceText -> if (searchLineInput.text.toString().toLowerCase() != "") {
                //обпращаемся к презентору, метод который будет осуществлять поиск
                search()
            } else {
                //showServicesInHomeTown()
            }
            else -> {
            }
        }
    }

    private fun getServicesInThisCity(userCity: String) {

    }

    private fun search() {
        when (searchBy) {
            "название сервиса" -> searchByNameService()
            "имя и фамилия" -> searchByWorkerName()
        }
    }

    private fun searchByNameService() {
        val enteredText = searchLineInput.text.toString().toLowerCase()
        val search = Search(context)

        var usersQuery: Query = FirebaseDatabase.getInstance().getReference(User.USERS)
        if (city != NOT_CHOSEN) {
            usersQuery = usersQuery.orderByChild(User.CITY).equalTo(city)
        }

        usersQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(usersSnapshot: DataSnapshot) {
                if (usersSnapshot.value == null) {
                    attentionNothingFound()
                    return
                }

                val commonList = search.getServicesOfUsers(usersSnapshot,
                        enteredText, null, null, null, null)
                for (serviceData in commonList) {
                    //serviceList.add(serviceData[1] as Service)
                    //userList.add(serviceData[2] as User)
                }
                if (commonList.isEmpty()) {
                    attentionNothingFound()
                } else {
                    //serviceAdapter = new ServiceAdapter(serviceList.size(),serviceList,userList);
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                attentionBadConnection()
            }
        })
    }

    private fun searchByWorkerName() {
       /* val enteredText = searchLineInput.text.toString().toLowerCase()
        val search = Search()*/

        /*val userQuery = FirebaseDatabase.getInstance().getReference(User.USERS)
                .orderByChild(User.NAME)
                .equalTo(enteredText)*/
        /*userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(usersSnapshot: DataSnapshot) {

                if (usersSnapshot.value == null) {
                    attentionNothingFound()
                    return
                }

                val commonList = search.getServicesOfUsers(usersSnapshot, null,
                        enteredText,
                        city, null, null)
                for (serviceData in commonList) {
                    //serviceList.add(serviceData[1] as Service)
                    //userList.add(serviceData[2] as User)
                }
                *//*  if (serviceList.isEmpty()) {
                      attentionNothingFound()
                  } else {
                      //serviceAdapter = new ServiceAdapter(serviceList.size(),serviceList,userList);
                      recyclerView.adapter = serviceAdapter
                      hideLoading()
                  }*//*
            }

            override fun onCancelled(databaseError: DatabaseError) {
                attentionBadConnection()
            }
        })*/
    }

    override fun attentionNothingFound() {
        Toast.makeText(context, "Ничего не найдено", Toast.LENGTH_SHORT).show()
    }

    override fun attentionBadConnection() {
        Toast.makeText(context, "Плохое соединение", Toast.LENGTH_SHORT).show()
    }

    companion object {
        // сначала идут константы
        private val TAG = "DBInf"
        const val NOT_CHOSEN = "не выбран"
        const val NAME_OF_SERVICE = "название сервиса"
    }
}
