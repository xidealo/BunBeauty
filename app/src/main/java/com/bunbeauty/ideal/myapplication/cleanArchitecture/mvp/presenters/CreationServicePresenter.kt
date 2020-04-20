package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.creationService.CreationServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.AddingServiceView
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi
import java.util.*

@InjectViewState
class CreationServicePresenter(private val creationServiceInteractor: CreationServiceInteractor) :
    MvpPresenter<AddingServiceView>(), CreationServicePresenterCallback {

    fun addService(
        name: String,
        description: String,
        cost: String,
        category: String,
        address: String,
        tags: List<String>
    ) {
        val service = Service()
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
        creationServiceInteractor.addService(service, tags, this)
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

    override fun showNameInputError(error: String) {
        viewState.showNameInputError(error)
    }

    override fun showDescriptionInputError(error: String) {
        viewState.showDescriptionInputError(error)
    }

    override fun showCostInputError(error: String) {
        viewState.showCostInputError(error)
    }

    override fun showCategoryInputError(error: String) {
        viewState.showCategoryInputError(error)
    }

    override fun showAddressInputError(error: String) {
        viewState.showAddressInputError(error)
    }
    companion object {
        private const val MAX_COUNT_OF_IMAGES = 10
        private const val WORKER = "worker"
    }
}