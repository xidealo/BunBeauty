package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.FiguringServicePoints
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService.IMainScreenInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IServiceSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.IUserSubscriber
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.ServiceRepository
import com.bunbeauty.ideal.myapplication.cleanArchitecture.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class MainScreenInteractor(val userRepository: UserRepository,
                           val serviceRepository: ServiceRepository) : IMainScreenInteractor,
        IUserSubscriber, IServiceSubscriber, CoroutineScope {
    lateinit var mainScreenCallback: MainScreenCallback

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private var currentCountOfUsers = 0
    private var cacheScreenData = ArrayList<ArrayList<Any>>()
    private var cacheServiceList = arrayListOf<Service>()
    private var cacheUserList = arrayListOf<User>()

    override fun getMainScreenData(mainScreenCallback: MainScreenCallback) {
        this.mainScreenCallback = mainScreenCallback
        userRepository.getById(getUserId(), this, false)
    }

    override fun getUsersByCity(city: String) {
        userRepository.getByCity(city, this, isFirstEnter(getUserId(), cachedUserIds))
    }

    override fun getServicesByUserId(id: String) {
        serviceRepository.getServicesByUserId(id, this, true)
    }

    override fun returnUser(user: User) {
        //here we can get out city
        getUsersByCity(user.city)
    }

    override fun returnUsers(users: List<User>) {
        launch {
            setListenerCountOfReturnServices(users.size)
        }

        cacheUserList.addAll(users)

        for (user in users) {
            getServicesByUserId(user.id)
        }
    }

    override fun returnServiceList(serviceList: List<Service>) {
        currentCountOfUsers++
        cacheServiceList.addAll(serviceList)
    }

    override suspend fun setListenerCountOfReturnServices(countOfUsers: Int) {
        while (countOfUsers != currentCountOfUsers) {
            delay(500)
        }

        for (service in cacheServiceList) {
            addToServiceList(service, getUserByService(service))
        }

        val mainScreenData = ArrayList<ArrayList<Any>>()
        for (i in cacheScreenData.indices) {
            //services
            mainScreenData.add(arrayListOf(cacheScreenData[i][1], cacheScreenData[i][2]))
        }

        mainScreenCallback.returnMainScreenData(mainScreenData)
    }

    private fun getUserByService(service: Service): User {
        for (user in cacheUserList) {
            if (service.userId == user.id)
                return user
        }
        return User()
    }

    override fun returnService(service: Service) {
        //log
    }

    //and than we have to get all services by this users
    private fun isFirstEnter(id: String, idList: ArrayList<String>): Boolean {
        if (idList.contains(id)) {
            return false
        }
        idList.add(id)
        return true
    }

    // Добавляем конкретную услугу в список в сообветствии с её коэфициентом
    private fun addToServiceList(service: Service, user: User) {
        val coefficients = HashMap<String, Float>()
        coefficients[Service.CREATION_DATE] = 0.25f
        coefficients[Service.COST] = 0.07f
        coefficients[Service.AVG_RATING] = 0.53f
        coefficients[Service.COUNT_OF_RATES] = 0.15f

        //boolean isPremium = service.getPremiumDate();

        //if (isPremium) {
        /*points = 1f
        premiumList.add(0, arrayOf(points, service, user))*/
        //} else {

        val creationDatePoints = FiguringServicePoints.figureCreationDatePoints(service.creationDate, coefficients[Service.CREATION_DATE]!!)
        val costPoints = FiguringServicePoints.figureCostPoints((service.cost).toLong(),
                serviceRepository.getMaxCost().cost.toLong(),
                coefficients[Service.COST]!!)

        val ratingPoints = FiguringServicePoints.figureRatingPoints(service.rating, coefficients[Service.AVG_RATING]!!)
        val countOfRatesPoints = FiguringServicePoints.figureCountOfRatesPoints(service.countOfRates,
                serviceRepository.getMaxCountOfRates().countOfRates,
                coefficients[Service.COUNT_OF_RATES]!!)

        val points = creationDatePoints + costPoints + ratingPoints + countOfRatesPoints
        sortAddition(arrayListOf(points, service, user))

        //}
    }

    private fun sortAddition(serviceData: ArrayList<Any>) {
        for (i in cacheServiceList.indices) {
            if (cacheScreenData.size != 0) {
                if (cacheScreenData[i][0].toString().toFloat() < (serviceData[0]).toString().toFloat()) {
                    cacheScreenData.add(i, serviceData)
                    return
                }
            }
            break
        }
        cacheScreenData.add(cacheScreenData.size, serviceData)
    }

    override fun getCategories(mainScreenData: ArrayList<ArrayList<Any>>): MutableSet<String> {
        val setOfCategories = mutableSetOf<String>()
        for (i in mainScreenData[1].indices){
            setOfCategories.add((mainScreenData[i][1] as Service).category)
        }
        return setOfCategories
    }
    override fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid

    companion object {
        //can be replaced by one var
        val cachedUserIds = arrayListOf<String>()
    }
}