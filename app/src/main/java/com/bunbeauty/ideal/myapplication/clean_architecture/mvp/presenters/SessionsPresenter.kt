package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters

import android.content.Intent
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.WorkWithTimeApi
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.sessions.SessionsInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.sessions.SessionsOrderInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.domain.sessions.i_sessions.ISessionsMessageInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.callback.sessions.SessionsPresenterCallback
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Message
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.Order
import com.bunbeauty.ideal.myapplication.clean_architecture.data.db.models.entity.schedule.WorkingDay
import com.bunbeauty.ideal.myapplication.clean_architecture.mvp.views.SessionsView
import java.util.*

@InjectViewState
class SessionsPresenter(
    private val sessionsInteractor: SessionsInteractor,
    private val sessionsOrderInteractor: SessionsOrderInteractor,
    private val sessionsMessageInteractor: ISessionsMessageInteractor,
    private val intent: Intent
) : MvpPresenter<SessionsView>(), SessionsPresenterCallback {

    fun getSchedule() {
        viewState.showLoading()
        sessionsInteractor.getSchedule(intent, this)
    }

    override fun showDays(days: List<WorkingDay>) {
        viewState.hideLoading()
        viewState.showDays(days)
    }

    override fun showNoAvailableSessions() {
        viewState.hideLoading()
        viewState.showNoAvailableSessions()
    }

    fun getSessions(day: WorkingDay) {
        clearSessions()
        viewState.showTime(sessionsInteractor.getSessions(day))
    }

    fun clearSessions() {
        sessionsInteractor.sessionList.clear()
        sessionsInteractor.clearSelectedSession(this)
        viewState.clearSessionsLayout()
    }

    fun updateTime(time: String) {
        sessionsInteractor.updateTime(time, this)
    }

    override fun clearTime(time: String) {
        viewState.clearTime(time)
    }

    override fun selectTime(selectedTime: String) {
        viewState.selectTime(selectedTime)
    }

    override fun enableMakeAppointmentButton() {
        viewState.enableMakeAppointmentButton()
    }

    override fun disableMakeAppointmentButton() {
        viewState.disableMakeAppointmentButton()
    }

    fun makeAppointment() {
        sessionsOrderInteractor.makeAppointment(
            this,
            sessionsInteractor.getService(intent),
            sessionsInteractor.selectedSession!!
        )
    }

    override fun updateSchedule(order: Order) {
        sessionsInteractor.updateSchedule(order, this)
    }

    override fun showMadeAppointment() {
        viewState.showMessage("Вы записаны")
        viewState.goBack()
    }

    override fun sendMessages(order: Order) {
        val infoMessageId = sessionsMessageInteractor.getId(order.clientId, order.masterId)
        val myInfoMessageId = Message(
            id = infoMessageId,
            type = Message.TEXT_STATUS,
            message = "Новая запись на услугу ${order.serviceName}",
            dialogId = order.clientId,
            userId = order.masterId,
            ownerId = order.clientId
        )
        val companionInfoMessageId = Message(
            id = infoMessageId,
            type = Message.TEXT_STATUS,
            message = "Новая запись на услугу ${order.serviceName}",
            dialogId = order.masterId,
            userId = order.clientId,
            ownerId = order.clientId
        )
        sessionsMessageInteractor.sendMessage(myInfoMessageId)
        sessionsMessageInteractor.sendMessage(companionInfoMessageId)

        val userReviewId = sessionsMessageInteractor.getId(order.clientId, order.masterId)
        val myMessageUserReview = Message(
            id = userReviewId,
            type = Message.USER_REVIEW_STATUS,
            message = "Вы сможете оставить отзыв о клиенте после ${
                WorkWithTimeApi.getDateInFormatYMDHMS(
                    Date(order.session.finishTime)
                )
            }.",
            dialogId = order.clientId,
            userId = order.masterId,
            orderId = order.id,
            ownerId = order.masterId,
            finishOrderTime = order.session.finishTime
        )
        val companionMessageUserReview = Message(
            id = userReviewId,
            type = Message.USER_REVIEW_STATUS,
            message = "Вы сможете оставить отзыв о клиенте после ${
                WorkWithTimeApi.getDateInFormatYMDHMS(
                    Date(order.session.finishTime)
                )
            }.",
            dialogId = order.masterId,
            userId = order.clientId,
            orderId = order.id,
            ownerId = order.masterId,
            finishOrderTime = order.session.finishTime
        )
        sessionsMessageInteractor.sendMessage(myMessageUserReview)
        sessionsMessageInteractor.sendMessage(companionMessageUserReview)

        val serviceReviewId = sessionsMessageInteractor.getId(order.masterId, order.clientId)
        val myMessageServiceReview = Message(
            id = serviceReviewId,
            type = Message.SERVICE_REVIEW_STATUS,
            message = "Вы сможете оставить отзыв об услуге ${order.serviceName} после ${
                WorkWithTimeApi.getDateInFormatYMDHMS(
                    Date(order.session.finishTime)
                )
            }.",
            dialogId = order.clientId,
            userId = order.masterId,
            orderId = order.id,
            ownerId = order.clientId,
            finishOrderTime = order.session.finishTime
        )
        val companionMessageServiceReview = Message(
            id = serviceReviewId,
            type = Message.SERVICE_REVIEW_STATUS,
            message = "Вы сможете оставить отзыв об услуге ${order.serviceName} после ${
                WorkWithTimeApi.getDateInFormatYMDHMS(
                    Date(order.session.finishTime)
                )
            }.",
            dialogId = order.masterId,
            userId = order.clientId,
            orderId = order.id,
            ownerId = order.clientId,
            finishOrderTime = order.session.finishTime
        )
        sessionsMessageInteractor.sendMessage(myMessageServiceReview)
        sessionsMessageInteractor.sendMessage(companionMessageServiceReview)

        //send message cancel order
        val cancelMessageId = sessionsMessageInteractor.getId(order.masterId, order.clientId)
        val myMessageCancel = Message(
            id = cancelMessageId,
            type = Message.CANCEL_STATUS,
            message = "К вам на услугу ${order.serviceName} на время c ${
                WorkWithTimeApi.getDateInFormatYMDHMS(
                    Date(order.session.startTime)
                )
            } по ${
                WorkWithTimeApi.getDateInFormatYMDHMS(
                    Date(order.session.finishTime)
                )
            } записался клиент",
            dialogId = order.clientId,
            userId = order.masterId,
            orderId = order.id,
            ownerId = order.masterId,
            finishOrderTime = order.session.finishTime
        )
        val companionMessageCancel = Message(
            id = cancelMessageId,
            type = Message.CANCEL_STATUS,
            message = "К вам на услугу ${order.serviceName} на время c ${
                WorkWithTimeApi.getDateInFormatYMDHMS(
                    Date(order.session.startTime)
                )
            } по ${
                WorkWithTimeApi.getDateInFormatYMDHMS(
                    Date(order.session.finishTime)
                )
            } записался клиент",
            dialogId = order.masterId,
            userId = order.clientId,
            orderId = order.id,
            ownerId = order.masterId,
            finishOrderTime = order.session.finishTime
        )
        sessionsMessageInteractor.sendMessage(myMessageCancel)
        sessionsMessageInteractor.sendMessage(companionMessageCancel)
    }

}