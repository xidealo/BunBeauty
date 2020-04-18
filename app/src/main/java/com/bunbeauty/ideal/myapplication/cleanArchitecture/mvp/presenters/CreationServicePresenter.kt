package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.AddingServiceView
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi
import java.util.*

@InjectViewState
class CreationServicePresenter(private val creationServiceInteractor: CreationServiceInteractor) : MvpPresenter<AddingServiceView>() {

    companion object {
        private const val MAX_COUNT_OF_IMAGES = 10
        private const val WORKER = "worker"
    }

    fun addService(name: String, description: String, cost: String, category: String, address: String, tags: List<String>): Service? {
        val service = Service()

        if (isNameCorrect(name) && isCostCorrect(cost) && isAddressCorrect(address)
                && isCategoryCorrect(category) && isDescriptionCorrect(description)) {
            service.name = name
            service.description = description
            service.cost = cost
            service.category = category
            service.address = address
            service.rating = 0f
            service.countOfRates = 0
            service.premiumDate = Service.DEFAULT_PREMIUM_DATE
            service.creationDate = WorkWithTimeApi.getDateInFormatYMDHMS(Date())
            service.userId = creationServiceInteractor.getUserId()

            service.id = creationServiceInteractor.addService(service, tags)
            return service
        } else {
            return null
        }
    }

    fun addImages(fpathOfImages: List<String>, service: Service) {
        if (fpathOfImages.size < MAX_COUNT_OF_IMAGES) {
            creationServiceInteractor.addImages(fpathOfImages, service)
            viewState.showAllDone()
            viewState.hideMainBlocks()
            Thread.sleep(500)
            viewState.showPremiumBlock(service)
        } else {
            viewState.showMoreTenImages()
        }
    }

    private fun isNameCorrect(name: String): Boolean {
        if (name.isEmpty()) {
            viewState.showNameInputError("Введите имя сервиса")
            return false
        }

        if (!creationServiceInteractor.getIsNameInputCorrect(name)) {
            viewState.showNameInputError("Имя сервиса должно содержать только буквы")
            return false
        }
        if (!creationServiceInteractor.getIsNameLengthLessTwenty(name)) {
            viewState.showNameInputError("Имя сервиса должно быть меньше 20 символов")
            return false
        }
        return true
    }

    private fun isDescriptionCorrect(description: String): Boolean {

        if (description.isEmpty()) {
            viewState.showDescriptionInputError("Введите описание сервиса")
            return false
        }

        if (!creationServiceInteractor.getIsDescriptionInputCorrect(description)) {
            viewState.showDescriptionInputError("")
            return false
        }

        if (!creationServiceInteractor.getIsDescriptionLengthLessTwoHundred(description)) {
            viewState.showDescriptionInputError("Описание должно быть меньше 200 символов")
            return false
        }

        return true
    }

    private fun isCostCorrect(cost: String): Boolean {
        if (cost.isEmpty()) {
            viewState.showCostInputError("Введите цену")
            return false
        }

        if (!creationServiceInteractor.getIsCostInputCorrect(cost)) {
            viewState.showCostInputError("Цена должна содержать только цифры")
            return false
        }
        if (!creationServiceInteractor.getIsCostLengthLessTen(cost)) {
            viewState.showCostInputError("Цена должна быть меньше 10 символов")
            return false
        }
        return true
    }

    private fun isCategoryCorrect(category: String): Boolean {
        if (!creationServiceInteractor.getIsCategoryInputCorrect(category)) {
            viewState.showCategoryInputError("Выберите категорию")
            return false
        }
        return true
    }

    private fun isAddressCorrect(address: String): Boolean {

        if (address.isEmpty()) {
            viewState.showAddressInputError("Введите адрес")
            return false
        }

        if (!creationServiceInteractor.getIsAddressInputCorrect(address)) {
            viewState.showAddressInputError("")
            return false
        }
        if (!creationServiceInteractor.getIsAddressLengthThirty(address)) {
            viewState.showAddressInputError("Адрес должен быть меньше 30 символов")
            return false
        }
        return true
    }

}