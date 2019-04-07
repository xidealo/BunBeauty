package com.example.ideal.myapplication.fragments.objects;

public class Message {

    // из message
    private String id;
    private String messageTime;
    private String userId;
    private String workingTimeId;
    private String messageDate;

    // общие
    private boolean isCanceled;
    private String orderTime;

    private String serviceName;
    private String userName;
    private String timeId;
    private String orderId;

    // только order
    private boolean isMyService;

    // только review
    private String type;
    private String ratingReview;

    public void setId(String _id) {
        id = _id;
    }
    public void setUserId(String _userId){
        userId = _userId;
    }
    public String getUserId() {
        return userId;
    }
    public void setMessageTime(String _time) {
        messageTime = _time;
    }
    public void setMessageDate(String _date) {
        messageDate = _date;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setWorkingTimeId(String _workingTimeId) {
        workingTimeId = _workingTimeId;
    }

    public void setIsCanceled(boolean _isCanceled) {
        isCanceled = _isCanceled;
    }

    public void setIsMyService(boolean _isMyService) {
        isMyService = _isMyService;
    }
    public boolean getIsMyService() {
        return isMyService;
    }

    public void setRatingReview(String _ratingReview) {
        ratingReview = _ratingReview;
    }
    public String getRatingReview() {
        return ratingReview;
    }

    public void setServiceTime(String _orderTime) {
        orderTime = _orderTime;
    }

    public void setServiceName(String _serviceName) {
        serviceName = _serviceName;
    }

    public void setUserName(String _userName) {
        userName = _userName;
    }

    public void setTimeId(String _timeId) {
        timeId = _timeId;
    }

    public void setOrderId(String _orderId) {
        orderId = _orderId;
    }


    public void setType(String _type) {
        type = _type;
    }

    // Get

    public String getId() {
        return id;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public boolean getIsCanceled() {
        return isCanceled;
    }

    public String getServiceTime() {
        return orderTime;
    }

    public String getServiceName(){
        return serviceName;
    }

    public String getUserName(){
        return userName;
    }

    public String getTimeId() {
        return timeId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getType() {
        return type;
    }
}
