package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService.IMainScreenUserInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.IUserCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.subscribers.user.IUsersCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.collections.ArrayList

class MainScreenUserInteractor(
    val userRepository: UserRepository
) : IMainScreenUserInteractor,
    IUserCallback, IUsersCallback {

    private lateinit var mainScreenPresenterCallback: MainScreenPresenterCallback
    var selectedCategory = ""
    var selectedTagsArray: ArrayList<String> = arrayListOf()

    //cache
    private var currentCountOfUsers = 0
    private var cachePremiumMainScreenData = ArrayList<ArrayList<Any>>()
    private var cacheUserList = arrayListOf<User>()

    private var searchByServiceName = false
    private var serviceName = ""

    fun createTags(category: String, mainScreenPresenterCallback: MainScreenPresenterCallback) {
        mainScreenPresenterCallback.createTags(category, selectedTagsArray)
    }

    override fun getMainScreenDataByUserName(
        city: String,
        userName: String,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    ) {
        this.mainScreenPresenterCallback = mainScreenPresenterCallback
        mainScreenPresenterCallback.showLoading()
        clearCache()
        userRepository.getByCityAndUserName(city, userName, this, true)
    }

    override fun getMainScreenDataByServiceName(
        city: String,
        serviceName: String,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    ) {
        clearCache()
        mainScreenPresenterCallback.showLoading()
        this.mainScreenPresenterCallback = mainScreenPresenterCallback
        searchByServiceName = true
        this.serviceName = serviceName
        userRepository.getByCity(city, this, true)
    }

    override fun getUsersByCity(city: String) {
        userRepository.getByCity(city, this, true)
    }

    override fun returnUser(user: User) {
        //here we can get out city
        getUsersByCity(user.city)
    }

    override fun returnUsers(users: List<User>) {
        cacheUserList.addAll(users)

        if (searchByServiceName) {
            for (user in users) {
                mainScreenPresenterCallback.getServicesByUserIdAndServiceName(user.id, serviceName)
            }
        } else {
            for (user in users) {
                mainScreenPresenterCallback.getServicesByUserId(user.id)
            }
        }
    }

    override fun convertCacheDataToMainScreenData(cacheMainScreenData: ArrayList<ArrayList<Any>>): ArrayList<ArrayList<Any>> {
        val mainScreenData = ArrayList<ArrayList<Any>>()
        for (i in cacheMainScreenData.indices) {
            //services
            mainScreenData.add(arrayListOf(cacheMainScreenData[i][1], cacheMainScreenData[i][2]))
        }
        return mainScreenData
    }

    override fun convertCacheDataToMainScreenData(
        category: String,
        cacheMainScreenData: ArrayList<ArrayList<Any>>
    ): ArrayList<ArrayList<Any>> {
        val mainScreenData = ArrayList<ArrayList<Any>>()
        for (i in cacheMainScreenData.indices) {
            //services 1 , users 2
            if ((cacheMainScreenData[i][1] as Service).category == category)
                mainScreenData.add(
                    arrayListOf(
                        cacheMainScreenData[i][1],
                        cacheMainScreenData[i][2]
                    )
                )
        }
        return mainScreenData
    }

    override fun convertCacheDataToMainScreenData(
        selectedTagsArray: ArrayList<String>,
        cacheMainScreenData: ArrayList<ArrayList<Any>>
    ): ArrayList<ArrayList<Any>> {
        val mainScreenData = ArrayList<ArrayList<Any>>()
        for (i in cacheMainScreenData.indices) {
            //services 1 , users 2
            for (j in selectedTagsArray.indices) {
                if ((cacheMainScreenData[i][1] as Service).tags.toString()
                        .contains(selectedTagsArray[j])
                )
                    mainScreenData.add(
                        arrayListOf(
                            cacheMainScreenData[i][1],
                            cacheMainScreenData[i][2]
                        )
                    )
            }
        }
        return mainScreenData
    }

    fun getUserByService(cacheUserList: ArrayList<User>, service: Service): User {
        for (user in cacheUserList) {
            if (service.userId == user.id)
                return user
        }
        return User()
    }

    //and than we have to get all services by this users
    fun isFirstEnter(id: String, idList: ArrayList<String>): Boolean {
        if (idList.contains(id)) {
            return false
        }
        idList.add(id)
        return true
    }

    override fun getCategories(mainScreenData: ArrayList<ArrayList<Any>>): MutableSet<String> {
        val setOfCategories = mutableSetOf<String>()
        for (i in mainScreenData.indices) {
            setOfCategories.add((mainScreenData[i][0] as Service).category)
        }
        return setOfCategories
    }

    private fun choosePremiumServices(
        premiumList: ArrayList<ArrayList<Any>>,
        cacheMainScreenData: ArrayList<ArrayList<Any>>
    ): ArrayList<ArrayList<Any>> {
        val random = Random()
        val limit = 3

        if (premiumList.size <= limit) {
            cacheMainScreenData.addAll(0, premiumList)
        } else {
            for (i in 0 until limit) {
                var premiumService: ArrayList<Any>
                do {
                    val index = random.nextInt(premiumList.size)
                    premiumService = premiumList[index]
                } while (cacheMainScreenData.contains(premiumService))
                cacheMainScreenData.add(0, premiumService)
            }
        }
        return cacheMainScreenData
    }

    override fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid

    override fun isSelectedCategory(category: String): Boolean {
        if (category == selectedCategory) {
            selectedCategory = ""
            return true
        }
        selectedCategory = category
        return false
    }

    private fun clearCache() {
        searchByServiceName = false
        serviceName = ""
        cacheUserList.clear()
        cachePremiumMainScreenData.clear()
        currentCountOfUsers = 0
    }


    companion object {
        //can be replaced by one var
        val cachedUserIds = arrayListOf<String>()
    }

}