package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters.log_in

import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.photo.IPhotoInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.editing.service.IEditServiceServiceInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.editing.service.IEditServiceTagInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.photo.IPhotoCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.service.EditServicePresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Photo
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Service
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Tag
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.log_in.EditServiceView

@InjectViewState
class EditServicePresenter(
    private val editServiceServiceInteractor: IEditServiceServiceInteractor,
    private val photoInteractor: IPhotoInteractor,
    private val editServiceTagInteractor: IEditServiceTagInteractor
) :
    MvpPresenter<EditServiceView>(), EditServicePresenterCallback, IPhotoCallback {

    fun createEditServiceScreen() {
        editServiceServiceInteractor.createEditServiceScreen(this)
    }

    fun save(
        name: String,
        address: String,
        description: String,
        cost: Long,
        category: String,
        tags: ArrayList<Tag>
    ) {
        val service = editServiceServiceInteractor.getCacheService()
        service.name = name
        service.address = address
        service.description = description
        service.cost = cost
        service.category = category
        service.tags = tags
        editServiceServiceInteractor.update(service, this)
    }

    fun delete() {
        val service = editServiceServiceInteractor.getCacheService()
        editServiceServiceInteractor.delete(service, this)
    }

    fun getCacheService() = editServiceServiceInteractor.getCacheService()

    fun getPhotosLink() = photoInteractor.getPhotosLink()

    override fun showEditService(service: Service) {
        viewState.showEditService(service)
        editServiceTagInteractor.setCachedServiceTags(service.tags)
        photoInteractor.getPhotos(service, this)
    }

    override fun addPhoto(photo: Photo) {
        photoInteractor.addPhoto(photo)
    }

    override fun goToService(service: Service) {
        photoInteractor.savePhotos(photoInteractor.getPhotosLink(), service, this)
        photoInteractor.deleteImagesFromService(photoInteractor.getDeletePhotosLink())
        viewState.goToService(service)
    }

    override fun goToProfile(service: Service) {
        photoInteractor.deletePhotosFromStorage(
            Service.SERVICE_PHOTO,
            photoInteractor.getPhotosLink()
        )
        viewState.showMessage("Услуга успешно удалена")
        viewState.goToProfile(service)
    }

    override fun nameEditServiceInputError() {
        viewState.setNameEditServiceInputError("Допустимы только буквы и тире")
    }

    override fun nameEditServiceInputErrorEmpty() {
        viewState.setNameEditServiceInputError("Введите своё имя")
    }

    override fun nameEditServiceInputErrorLong() {
        viewState.setNameEditServiceInputError("Слишком длинное имя")
    }

    override fun saveTags(service: Service) {
        editServiceTagInteractor.saveTags(service)
    }

    override fun returnPhotos(photos: List<Photo>) {
        viewState.updatePhotoFeed()
        viewState.hideLoading()
    }

    override fun returnCreatedPhotoLink(uri: Uri) {}

    fun removePhoto(photo: Photo) {
        photoInteractor.removePhoto(photo)
        viewState.updatePhotoFeed()
    }

    fun createPhoto(resultUri: Uri) {
        val photo = Photo()
        photo.link = resultUri.toString()
        photoInteractor.addPhoto(photo)
        viewState.updatePhotoFeed()
    }
}