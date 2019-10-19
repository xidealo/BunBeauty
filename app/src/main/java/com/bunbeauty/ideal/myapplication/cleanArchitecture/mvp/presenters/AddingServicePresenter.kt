package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.AddingServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.AddingServiceView
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi
import java.util.*

@InjectViewState
class AddingServicePresenter(private val addingServiceInteractor: AddingServiceInteractor) : MvpPresenter<AddingServiceView>() {

    fun addService(name: String, description: String, cost: String, category: String, address: String, tags: List<String>) {
        if (isNameCorrect(name) && isDescriptionCorrect(description) && isCostCorrect(cost)
                && isCategoryCorrect(category) && isAddressCorrect(address)) {
            val service = Service()
            service.name = name
            service.description = description
            service.cost = cost
            service.category = category
            service.address = address
            service.rating = 0f
            service.countOfRates = 0
            service.premiumDate = "1970-01-01 00:00:00"
            service.creationDate = WorkWithTimeApi.getDateInFormatYMDHMS(Date())
            service.userId = addingServiceInteractor.getUserId()

            addingServiceInteractor.addService(service, tags)
        }
    }

    fun addImage() {

    }

    private fun isNameCorrect(name: String): Boolean {

        if (name.isEmpty()) {
            viewState.showNameInputError("Введите имя сервиса")
            return false
        }

        if (!addingServiceInteractor.getIsNameInputCorrect(name)) {
            viewState.showNameInputError("Имя сервиса должно содержать только буквы")
            return false
        }
        if (!addingServiceInteractor.getIsNameLengthLessTwenty(name)) {
            viewState.showNameInputError("Имя сервиса должно быть меньше 20 символов")
            return false
        }
        return true
    }

    private fun isDescriptionCorrect(description: String): Boolean {

        if (description.isEmpty()) {
            viewState.showCostInputError("Введите описание сервиса")
            return false
        }

        if (!addingServiceInteractor.getIsDescriptionInputCorrect(description)) {
            viewState.showDescriptionInputError("")
            return false
        }

        if (!addingServiceInteractor.getIsDescriptionLengthLessTwoHunded(description)) {
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

        if (!addingServiceInteractor.getIsCostInputCorrect(cost)) {
            viewState.showCostInputError("Цена должна содержать только цифры")
            return false
        }
        if (!addingServiceInteractor.getIsCostLengthLessTen(cost)) {
            viewState.showCostInputError("Цена должна быть меньше 10 символов")
            return false
        }
        return true
    }

    private fun isCategoryCorrect(category: String): Boolean {
        if (!addingServiceInteractor.getIsCategoryInputCorrect(category)) {
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

        if (!addingServiceInteractor.getIsAddressInputCorrect(address)) {
            viewState.showAddressInputError("")
            return false
        }
        if (!addingServiceInteractor.getIsAddressLengthThirty(address)) {
            viewState.showAddressInputError("Адрес должен быть меньше 30 символов")
            return false
        }
        return true
    }

}