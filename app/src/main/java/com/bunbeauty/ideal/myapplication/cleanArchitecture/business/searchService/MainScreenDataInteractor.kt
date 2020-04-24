package com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService

import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.FiguringServicePoints
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.searchService.iSearchService.IMainScreenDataInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.MainScreenPresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi
import java.util.HashMap

class MainScreenDataInteractor : IMainScreenDataInteractor {

    private var cacheMainScreenData = ArrayList<ArrayList<Any>>()
    private lateinit var mainScreenPresenterCallback: MainScreenPresenterCallback


    override fun getMainScreenData(mainScreenPresenterCallback: MainScreenPresenterCallback) {
    /*    this.mainScreenPresenterCallback = mainScreenPresenterCallback
        mainScreenPresenterCallback.showLoading()
        if (isFirstEnter(getUserId(), MainScreenUserInteractor.cachedUserIds)) {
            userRepository.getById(getUserId(), this, false)
        } else {
            clearCache()
            userRepository.getById(getUserId(), this, false)
        }*/
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

    override fun createMainScreenData() {
     /*   for (service in cacheServiceList) {
            addToServiceList(service, getUserByService(cacheUserList, service))
        }

        cacheMainScreenData = choosePremiumServices(cachePremiumMainScreenData, cacheMainScreenData)

        if (createMainScreenWithCategory) {
            createMainScreenWithCategory = false
            mainScreenPresenterCallback.returnMainScreenDataWithCreateCategory(
                convertCacheDataToMainScreenData(cacheMainScreenData)
            )
        } else {
            mainScreenPresenterCallback.returnMainScreenData(
                convertCacheDataToMainScreenData(
                    cacheMainScreenData
                )
            )
        }*/
    }

    // Добавляем конкретную услугу в список в сообветствии с её коэфициентом
    private fun addToServiceList(service: Service, user: User) {
       /* val coefficients = HashMap<String, Float>()
        coefficients[Service.CREATION_DATE] = 0.25f
        coefficients[Service.COST] = 0.07f
        coefficients[Service.AVG_RATING] = 0.53f
        coefficients[Service.COUNT_OF_RATES] = 0.15f
        //Проверку на премиум вынести, этот метод не должен делать 2 дейсвтия (можно сразу проверять в выборе рандомных премиум сервисов)
        val isPremium = WorkWithTimeApi.checkPremium(service.premiumDate)

        if (isPremium) {
            cachePremiumMainScreenData.add(0, arrayListOf(1f, service, user))
        } else {
            val creationDatePoints = FiguringServicePoints.figureCreationDatePoints(
                service.creationDate,
                coefficients[Service.CREATION_DATE]!!
            )
            val costPoints = FiguringServicePoints.figureCostPoints(
                (service.cost).toLong(),
                serviceRepository.getMaxCost().cost.toLong(),
                coefficients[Service.COST]!!
            )

            val ratingPoints = FiguringServicePoints.figureRatingPoints(
                service.rating,
                coefficients[Service.AVG_RATING]!!
            )
            val countOfRatesPoints = FiguringServicePoints.figureCountOfRatesPoints(
                service.countOfRates,
                serviceRepository.getMaxCountOfRates().countOfRates,
                coefficients[Service.COUNT_OF_RATES]!!
            )

            val points = creationDatePoints + costPoints + ratingPoints + countOfRatesPoints
            sortAddition(arrayListOf(points, service, user))
        }*/
    }

    private fun sortAddition(serviceData: ArrayList<Any>) {
  /*      for (i in cacheServiceList.indices) {
            if (cacheMainScreenData.size != 0) {
                if (cacheMainScreenData[i][0].toString().toFloat() < (serviceData[0]).toString()
                        .toFloat()
                ) {
                    cacheMainScreenData.add(i, serviceData)
                    return
                }
            }
            break
        }
        cacheMainScreenData.add(cacheMainScreenData.size, serviceData)*/
    }


}