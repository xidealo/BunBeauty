package com.bunbeauty.ideal.myapplication.clean_architecture.domain.search_service

import android.content.Intent
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.api.FiguringServicePointsApi
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.WorkWithTimeApi
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.search_service.i_search_service.IMainScreenDataInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.MainScreenPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.MainScreenData
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.google.android.material.chip.Chip
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainScreenDataInteractor(
    private val figuringServicePointsApi: FiguringServicePointsApi
) : IMainScreenDataInteractor {

    var cacheMainScreenData = ArrayList<MainScreenData>()
    private var constCacheMainScreenData = ArrayList<MainScreenData>()
    private var cachePremiumMainScreenData = ArrayList<MainScreenData>()
    private var createMainScreenWithCategory = true
    private var cacheServiceList = ArrayList<Service>()
    var selectedCategory = ""

    var selectedTagsArray: ArrayList<String> = arrayListOf()

    private lateinit var mainScreenPresenterCallback: MainScreenPresenterCallback

    override fun getMainScreenData(
        intent: Intent,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    ) {
        this.mainScreenPresenterCallback = mainScreenPresenterCallback
        mainScreenPresenterCallback.showLoading()
        mainScreenPresenterCallback.getUsersByCity((intent.getSerializableExtra(User.USER) as User).city)
    }

    override fun getMainScreenData(
        category: String,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    ) {
        this.mainScreenPresenterCallback = mainScreenPresenterCallback
        selectedTagsArray.clear()

        mainScreenPresenterCallback.showLoading()
        mainScreenPresenterCallback.clearTags()
        mainScreenPresenterCallback.createTags(category, selectedTagsArray)
        mainScreenPresenterCallback.showTags()

        mainScreenPresenterCallback.returnMainScreenData(
            sortDataWithCategory(
                category,
                constCacheMainScreenData
            )
        )
    }

    override fun getMainScreenData(
        tagText: Chip,
        selectedTagsArray: ArrayList<String>,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    ) {
        this.mainScreenPresenterCallback = mainScreenPresenterCallback
        if (selectedTagsArray.contains(tagText.text.toString())) {
            //disable
            mainScreenPresenterCallback.disableTag(tagText)
            selectedTagsArray.remove(tagText.text.toString())
        } else {
            //enable
            mainScreenPresenterCallback.enableTag(tagText)
            selectedTagsArray.add(tagText.text.toString())
        }

        if (selectedTagsArray.size == 0) {
            getMainScreenData(selectedCategory, mainScreenPresenterCallback)
        } else {
            mainScreenPresenterCallback.returnMainScreenData(
                sortDataWithTags(
                    selectedTagsArray,
                    constCacheMainScreenData
                )
            )
        }
    }

    override fun showCurrentMainScreen(mainScreenPresenterCallback: MainScreenPresenterCallback) {
        this.mainScreenPresenterCallback = mainScreenPresenterCallback
        cacheMainScreenData.clear()
        cacheMainScreenData.addAll(constCacheMainScreenData)
        mainScreenPresenterCallback.returnMainScreenData(cacheMainScreenData)
    }

    override fun createMainScreenData(
        cacheUserList: ArrayList<User>,
        cacheServiceList: ArrayList<Service>,
        maxCost: Long,
        maxCountOfRates: Long
    ) {
        this.cacheServiceList.addAll(cacheServiceList)

        for (service in cacheServiceList) {
            addToServiceList(
                service,
                getUserByService(cacheUserList, service),
                maxCost,
                maxCountOfRates
            )
        }

        cacheMainScreenData = choosePremiumServices(cachePremiumMainScreenData, cacheMainScreenData)
        constCacheMainScreenData.addAll(cacheMainScreenData)

        if (createMainScreenWithCategory) {
            createMainScreenWithCategory = false
            mainScreenPresenterCallback.showMainScreenData(
                cacheMainScreenData
            )
            mainScreenPresenterCallback.createCategoryFeed(cacheMainScreenData.map { it.service })
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

    private fun sortDataWithCategory(
        category: String,
        constCacheMainScreenData: ArrayList<MainScreenData>
    ): ArrayList<MainScreenData> {
        cacheMainScreenData.clear()
        cacheMainScreenData.addAll(constCacheMainScreenData.filter { it.service.category == category })
        return cacheMainScreenData
    }

    private fun sortDataWithTags(
        tags: ArrayList<String>,
        constCacheMainScreenData: ArrayList<MainScreenData>
    ): ArrayList<MainScreenData> {
        val foundData = mutableSetOf<MainScreenData>()
        cacheMainScreenData.clear()

        for (tag in tags) {
            foundData.addAll(constCacheMainScreenData.filter { it -> it.service.tags.any { it.tag == tag } })
        }
        cacheMainScreenData.addAll(foundData)
        return cacheMainScreenData
    }

    // Добавляем конкретную услугу в список в соответствии с её коэфициентом
    private fun addToServiceList(
        service: Service,
        user: User,
        maxCost: Long,
        maxCountOfRates: Long
    ) {
        val coefficients = HashMap<String, Float>()
        coefficients[Service.CREATION_DATE] = 0.25f
        coefficients[Service.COST] = 0.07f
        coefficients[Service.AVG_RATING] = 0.53f
        coefficients[Service.COUNT_OF_RATES] = 0.15f

        //Проверку на премиум вынести, этот метод не должен делать 2 дейсвтия (можно сразу проверять в выборе рандомных премиум сервисов)
        if (Service.checkPremium(service.premiumDate)) {
            cachePremiumMainScreenData.add(0, MainScreenData(1f, user, service))
        }

        val creationDatePoints = figuringServicePointsApi.figureCreationDatePoints(
            service.creationDate,
            coefficients[Service.CREATION_DATE]!!
        )
        val costPoints = figuringServicePointsApi.figureCostPoints(
            (service.cost),
            maxCost,
            coefficients[Service.COST]!!
        )
        val ratingPoints = figuringServicePointsApi.figureRatingPoints(
            service.rating,
            coefficients[Service.AVG_RATING]!!
        )
        val countOfRatesPoints = figuringServicePointsApi.figureCountOfRatesPoints(
            service.countOfRates,
            maxCountOfRates,
            coefficients[Service.COUNT_OF_RATES]!!
        )

        val points = creationDatePoints + costPoints + ratingPoints + countOfRatesPoints
        sortAddition(MainScreenData(points, user, service))

    }

    private fun sortAddition(serviceData: MainScreenData) {
        for (i in cacheServiceList.indices) {
            if (cacheMainScreenData.size != 0) {
                if (cacheMainScreenData[i].weight < (serviceData).weight
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
        premiumList: ArrayList<MainScreenData>,
        cacheMainScreenData: ArrayList<MainScreenData>
    ): ArrayList<MainScreenData> {
        val random = Random()
        val limit = 3

        if (premiumList.size <= limit) {
            cacheMainScreenData.addAll(0, premiumList)
        } else {
            for (i in 0 until limit) {
                var premiumService: MainScreenData
                do {
                    val index = random.nextInt(premiumList.size)
                    premiumService = premiumList[index]
                } while (cacheMainScreenData.contains(premiumService))
                cacheMainScreenData.add(0, premiumService)
            }
        }
        return cacheMainScreenData
    }

    override fun isSelectedCategory(category: String): Boolean {
        if (category == selectedCategory) {
            selectedCategory = ""
            return true
        }
        selectedCategory = category
        return false
    }

    fun getMainScreenDataByName(
        newText: String?,
        mainScreenPresenterCallback: MainScreenPresenterCallback
    ) {
        if (newText != null) {
            this.mainScreenPresenterCallback = mainScreenPresenterCallback
            cacheMainScreenData.clear()
            cacheMainScreenData.addAll(constCacheMainScreenData.filter {
                it.service.name.toLowerCase(Locale.ROOT).contains(newText) ||
                        it.user.name.toLowerCase(Locale.ROOT).contains(newText)
            })
            mainScreenPresenterCallback.returnMainScreenData(cacheMainScreenData)
        }
    }

}