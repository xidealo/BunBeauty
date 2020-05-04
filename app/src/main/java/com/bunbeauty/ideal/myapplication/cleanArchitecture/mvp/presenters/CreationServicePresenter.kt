package com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.CreationServiceServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.iCreateService.ICreationServicePhotoInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.business.createService.iCreateService.ICreationServiceTagInteractor
import com.bunbeauty.ideal.myapplication.cleanArchitecture.callback.creationService.CreationServicePresenterCallback
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.cleanArchitecture.mvp.views.AddingServiceView
import com.bunbeauty.ideal.myapplication.helpApi.WorkWithTimeApi
import java.util.*

@InjectViewState
class CreationServicePresenter(
    private val creationServiceServiceInteractor: CreationServiceServiceServiceInteractor,
    private val creationServiceTagInteractor: ICreationServiceTagInteractor,
    private val creationServicePhotoInteractor: ICreationServicePhotoInteractor
) :
    MvpPresenter<AddingServiceView>(), CreationServicePresenterCallback {

    fun addService(
        name: String,
        description: String,
        cost: Long,
        category: String,
        address: String,
        tags: List<String>,
        fpathOfImages: List<String>
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
        service.userId = User.getMyId()
        creationServiceServiceInteractor.addService(service, tags, fpathOfImages, this)
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

    override fun showServiceCreated(service: Service) {
        viewState.hideMainBlock()
        viewState.showPremiumBlock(service)
        viewState.showMessage("Сервис создан!")
    }

    override fun addTags(service: Service) {
        creationServiceTagInteractor.addTags(service)
    }

    override fun addPhotos(service: Service) {
        creationServicePhotoInteractor.addImages(service)
    }
}