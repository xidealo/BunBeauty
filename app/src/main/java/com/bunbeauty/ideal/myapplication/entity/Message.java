package com.bunbeauty.ideal.myapplication.entity;

public class Message {

    // общие
    private boolean isMyService;
    private boolean isCanceled;
    private String messageTime;
    private String serviceName;
    private String userName;
    private String workingTime;
    private String workingDay;

    //для Reference
    private String userId;
    private String serviceId;
    private String workingDayId;
    private String workingTimeId;
    private String orderId;
    private String reviewId;
    private String status;

    private String type;
    private String ratingReview;

    // время сообщения
    public void setMessageTime(String _time) {
        messageTime = _time;
    }
    public String getMessageTime() {
        return messageTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // id отправителя
    public void setUserId(String _userId){
        userId = _userId;
    }
    public String getUserId() {
        return userId;
    }

    // id услуги
    public void setServiceId(String _serviceId){
        serviceId = _serviceId;
    }
    public String getServiceId() {
        return serviceId;
    }

    // id рабочего дня
    public void setWorkingDayId(String _workingDayId) {
        workingDayId = _workingDayId;
    }
    public String getWorkingDayId() {
        return workingDayId;
    }

    // id рабочего времени
    public void setWorkingTimeId(String _workingTimeId) {
        workingTimeId = _workingTimeId;
    }
    public String getWorkingTimeId() {
        return workingTimeId;
    }

    // id записи
    public void setOrderId(String _orderId) {
        orderId = _orderId;
    }
    public String getOrderId() {
        return orderId;
    }

    // id отзыва
    public void setReviewId(String _reviewId) {
        reviewId = _reviewId;
    }
    public String getReviewId() {
        return reviewId;
    }

    // отменён ли?
    public void setIsCanceled(boolean _isCanceled) {
        isCanceled = _isCanceled;
    }
    public boolean getIsCanceled() {
        return isCanceled;
    }

    // мой лиэто сервис
    public void setIsMyService(boolean _isMyService) {
        isMyService = _isMyService;
    }
    public boolean getIsMyService() {
        return isMyService;
    }

    // текст ревью
    public void setRatingReview(String _ratingReview) {
        ratingReview = _ratingReview;
    }
    public String getRatingReview() {
        return ratingReview;
    }

    // название услуги
    public void setServiceName(String _serviceName) {
        serviceName = _serviceName;
    }
    public String getServiceName(){
        return serviceName;
    }

    // имя отправителя
    public void setUserName(String _userName) {
        userName = _userName;
    }
    public String getUserName(){
        return userName;
    }

    // тип ревью
    public void setType(String _type) {
        type = _type;
    }
    public String getType() {
        return type;
    }

    // время сеанса
    public void setWorkingTime(String _time) {
        workingTime = _time;
    }
    public String getWorkingTime() {
        return workingTime;
    }

    // дата сеанса
    public void setWorkingDay(String _day) {
        workingDay = _day;
    }
    public String getWorkingDay() {
        return workingDay;
    }
}
