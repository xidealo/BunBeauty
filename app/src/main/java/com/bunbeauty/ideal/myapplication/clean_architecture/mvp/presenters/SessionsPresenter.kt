package com.bunbeauty.ideal.myapplication.clean_architecture.mvp.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.bunbeauty.ideal.myapplication.clean_architecture.business.WorkWithTimeApi
import com.bunbeauty.ideal.myapplication.clean_architecture.business.sessions.SessionsInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.sessions.SessionsOrderInteractor
import com.bunbeauty.ideal.myapplication.clean_architecture.business.sessions.i_sessions.ISessionsMessageInteractor
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
    private val sessionsMessageInteractor: ISessionsMessageInteractor
) : MvpPresenter<SessionsView>(), SessionsPresenterCallback {

    fun getSchedule() {
        sessionsInteractor.getSchedule(this)
    }

    override fun showDays(days: Set<WorkingDay>) {
        viewState.showDays(days.toList())
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
            sessionsInteractor.getService(),
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
        val userReviewId = sessionsMessageInteractor.getId(order.clientId, order.masterId)
        val myMessageUserReview = Message(
            id = userReviewId,
            type = Message.USER_REVIEW_MESSAGE_STATUS,
            message = "На услуге ${order.serviceName} был клиент. Вы сможете оставить отзыв о клиенте после ${
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
            type = Message.USER_REVIEW_MESSAGE_STATUS,
            message = "На услуге ${order.serviceName} был клиент. Вы сможете оставить отзыв о клиенте после ${
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
        sessionsMessageInteractor.sendUserReviewMessage(myMessageUserReview)
        sessionsMessageInteractor.sendUserReviewMessage(companionMessageUserReview)

        val serviceReviewId = sessionsMessageInteractor.getId(order.masterId, order.clientId)
        val myMessageServiceReview = Message(
            id = serviceReviewId,
            type = Message.SERVICE_REVIEW_MESSAGE_STATUS,
            message = "Вам была оказана услуга ${order.serviceName}. Вы сможете оставить отзыв об услуге после ${
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
            type = Message.SERVICE_REVIEW_MESSAGE_STATUS,
            message = "Вам была оказана услуга ${order.serviceName}. Вы сможете оставить отзыв об услуге после ${
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
        sessionsMessageInteractor.sendServiceReviewMessage(myMessageServiceReview)
        sessionsMessageInteractor.sendServiceReviewMessage(companionMessageServiceReview)

        //send message cancel order
        val cancelMessageId = sessionsMessageInteractor.getId(order.masterId, order.clientId)
        val myMessageCancel = Message(
            id = cancelMessageId,
            type = Message.CANCEL_MESSAGE_STATUS,
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
            type = Message.CANCEL_MESSAGE_STATUS,
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
        sessionsMessageInteractor.sendServiceReviewMessage(myMessageCancel)
        sessionsMessageInteractor.sendServiceReviewMessage(companionMessageCancel)
    }

}