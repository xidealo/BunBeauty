package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService

import android.content.Intent
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.FiguringServicePoints
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService.IMainScreenDataInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.MainScreenData
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainScreenDataInteractor(
    private val intent: Intent,
    private val figuringServicePoints: FiguringServicePoints
) : IMainScreenDataInteractor {

    var cacheMainScreenData = ArrayList<ArrayList<MainScreenData>>()
    private var cachePremiumMainScreenData = ArrayList<ArrayList<MainScreenData>>()
    private var createMainScreenWithCategory = true
    private var cacheServiceList = ArrayList<Service>()

    private lateinit var mainScreenPresenterCallback: MainScreenPresenterCallback

    override fun getMainScreenData(mainScreenPresenterCallback: MainScreenPresenterCallback) {
        this.mainScreenPresenterCallback = mainScreenPresenterCallback
        mainScreenPresenterCallback.showLoading()
        mainScreenPresenterCallback.getUsersByCity((intent.getSerializableExtra(User.USER) as User).city)
    }

    override fun getMainScreenData(
        selectedTagsArray: ArrayList<String>,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    ) {
        /*   mainScreenPresenterCallback.returnMainScreenData(
               convertCacheDataToMainScreenData(
                   selectedTagsArray,
                   cacheMainScreenData
               )
           )*/
    }

    override fun getMainScreenData(
        category: String,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    ) {
        //can add if which will check cache size and if 0 will load from FB
        /* mainScreenPresenterCallback.showLoading()
         mainScreenPresenterCallback.returnMainScreenData(
             convertCacheDataToMainScreenData(
                 category,
                 cacheMainScreenData
             )
         )*/
    }

    override fun createMainScreenData(
        cacheUserList: ArrayList<User>,
        cacheServiceList: ArrayList<Service>
    ) {
        this.cacheServiceList.addAll(cacheServiceList)

        for (service in cacheServiceList) {
            addToServiceList(service, getUserByService(cacheUserList, service))
        }

        cacheMainScreenData = choosePremiumServices(cachePremiumMainScreenData, cacheMainScreenData)

        if (createMainScreenWithCategory) {
            createMainScreenWithCategory = false
            mainScreenPresenterCallback.showMainScreenData(
                cacheMainScreenData
            )
            //TODO(Send only services)
            mainScreenPresenterCallback.createCategoryFeed(cacheMainScreenData)
        } else {
            mainScreenPresenterCallback.returnMainScreenData(
                cacheMainScreenData
            )
        }
    }

    private fun getUserByService(cacheUserList: ArrayList<User>, service: Service): User {
        for (user in cacheUserList) {
            if (service.userId == user.id)
                return user
        }
        return User()
    }

    // Добавляем конкретную услугу в список в соответствии с её коэфициентом
    private fun addToServiceList(service: Service, user: User) {
        val coefficients = HashMap<String, Float>()
        coefficients[Service.CREATION_DATE] = 0.25f
        coefficients[Service.COST] = 0.07f
        coefficients[Service.AVG_RATING] = 0.53f
        coefficients[Service.COUNT_OF_RATES] = 0.15f
        //Проверку на премиум вынести, этот метод не должен делать 2 дейсвтия (можно сразу проверять в выборе рандомных премиум сервисов)
        val isPremium = WorkWithTimeApi.checkPremium(service.premiumDate)

        if (isPremium) {
            cachePremiumMainScreenData.add(0, arrayListOf(MainScreenData(1f, user, service)))
        } else {
            val creationDatePoints = figuringServicePoints.figureCreationDatePoints(
                service.creationDate,
                coefficients[Service.CREATION_DATE]!!
            )
            val costPoints = figuringServicePoints.figureCostPoints(
                (service.cost).toLong(),
                /* serviceRepository.getMaxCost().cost.toLong()*/1000,
                coefficients[Service.COST]!!
            )

            val ratingPoints = figuringServicePoints.figureRatingPoints(
                service.rating,
                coefficients[Service.AVG_RATING]!!
            )
            val countOfRatesPoints = figuringServicePoints.figureCountOfRatesPoints(
                service.countOfRates,
                /*serviceRepository.getMaxCountOfRates().countOfRates*/10,
                coefficients[Service.COUNT_OF_RATES]!!
            )

            val points = creationDatePoints + costPoints + ratingPoints + countOfRatesPoints
            sortAddition(arrayListOf(MainScreenData(points, user, service)))
        }
    }

    private fun sortAddition(serviceData: ArrayList<MainScreenData>) {
        for (i in cacheServiceList.indices) {
            if (cacheMainScreenData.size != 0) {
                if (cacheMainScreenData[i][0].weight < (serviceData[0]).weight
                ) {
                    cacheMainScreenData.add(i, serviceData)
                    return
                }
            }
            break
        }
        cacheMainScreenData.add(cacheMainScreenData.size, serviceData)
    }

    private fun choosePremiumServices(
        premiumList: ArrayList<ArrayList<MainScreenData>>,
        cacheMainScreenData: ArrayList<ArrayList<MainScreenData>>
    ): ArrayList<ArrayList<MainScreenData>> {
        val random = Random()
        val limit = 3

        if (premiumList.size <= limit) {
            cacheMainScreenData.addAll(0, premiumList)
        } else {
            for (i in 0 until limit) {
                var premiumService: ArrayList<MainScreenData>
                do {
                    val index = random.nextInt(premiumList.size)
                    premiumService = premiumList[index]
                } while (cacheMainScreenData.contains(premiumService))
                cacheMainScreenData.add(0, premiumService)
            }
        }
        return cacheMainScreenData
    }

/*    override fun convertCacheDataToMainScreenData(cacheMainScreenData: ArrayList<ArrayList<MainScreenData>>): ArrayList<ArrayList<MainScreenData>> {
        val mainScreenData = ArrayList<ArrayList<MainScreenData>>()
        for (i in cacheMainScreenData.indices) {
            mainScreenData.add(arrayListOf(cacheMainScreenData[i][0], cacheMainScreenData[i][1]))
        }
        return mainScreenData
    }*/

}