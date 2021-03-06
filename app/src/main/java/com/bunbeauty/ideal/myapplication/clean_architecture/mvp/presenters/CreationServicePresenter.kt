package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters

import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.create_service.CreationServiceServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.create_service.iCreateService.ICreationServiceTagInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.photo.IPhotoCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.photo.IPhotoInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.creation_service.CreationServicePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Tag
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.User
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.CreationServiceView

@InjectViewState
class CreationServicePresenter(
    private val creationServiceServiceInteractor: CreationServiceServiceServiceInteractor,
    private val creationServiceTagInteractor: ICreationServiceTagInteractor,
    private val photoInteractor: IPhotoInteractor
) :
    MvpPresenter<CreationServiceView>(), CreationServicePresenterCallback, IPhotoCallback {

    fun addService(
        name: String,
        description: String,
        cost: Long,
        address: String,
        durationHours: Int,
        durationMinutes: Int,
        category: String,
        tags: ArrayList<Tag>
    ) {
        val service = Service(
            name = name,
            description = description,
            cost = cost,
            category = category,
            address = address,
            duration = durationHours + durationMinutes * 0.5f,
            userId = User.getMyId()
        )
        service.tags = tags
        creationServiceServiceInteractor.addService(service, this)
    }

    override fun addPhotos(service: Service) {
        photoInteractor.savePhotos(photoInteractor.getPhotoLinkList(), service, this)
    }

    override fun addTags(service: Service) {
        creationServiceTagInteractor.addTags(service)
    }

    override fun showServiceCreated(service: Service) {
        viewState.hideMainBlock()
        viewState.showPremiumBlock(service)
        viewState.showMessage("Услуга успешно создана!")
    }

    fun createPhoto(uri: Uri) {
        val photo = Photo()
        photo.link = uri.toString()
        photoInteractor.addPhoto(photo)
        viewState.updatePhotoFeed(photoInteractor.getPhotoLinkList())
    }

    fun removePhoto(photo: Photo) {
        photoInteractor.removePhoto(photo)
        viewState.updatePhotoFeed(photoInteractor.getPhotoLinkList())
    }

    fun getPhotoLinkList() = photoInteractor.getPhotoLinkList()

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
        viewState.showMessage(error)
    }

    override fun showAddressInputError(error: String) {
        viewState.showAddressInputError(error)
    }

    override fun showDurationInputError(error: String) {
        viewState.showMessage(error)
    }

    override fun returnPhotos(photos: List<Photo>) {}

    override fun returnCreatedPhotoLink(uri: Uri) {}
}