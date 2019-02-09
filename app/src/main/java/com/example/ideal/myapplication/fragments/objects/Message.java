package com.example.ideal.myapplication.fragments.objects;

public class Message {

    // из message
    private String id;
    private String messageTime;

    // общие
    private boolean isCanceled;
    private String date;
    private String orderTime;
    private String serviceName;
    private String userName;
    private String timeId;
    private String orderId;

    // только order
    private String dialogId;
    private boolean isMyService;

    // только review
    private String type;
    private boolean isRate;
    //private String serviceId;


    // Set

    public void setId(String _id) {
        id = _id;
    }

    public void setMessageTime(String _time) {
        messageTime = _time;
    }

    public void setIsCanceled(boolean _isCanceled) {
        isCanceled = _isCanceled;
    }

    public void setIsMyService(boolean _isMyService) {
        isMyService = _isMyService;
    }

    public void setDate(String _date) {
        date = _date;
    }

    public void setOrderTime(String _orderTime) {
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

    public void setIsRate(boolean _isRate) {
        isRate = _isRate;
    }

    /*public void setServiceId(String _serviceId) {
        serviceId = _serviceId;
    }*/

    public void setDialogId(String _dialogId) {
        dialogId = _dialogId;
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

    public boolean getIsMyService() {
        return isMyService;
    }

    public String getDate() {
        return date;
    }

    public String getOrderTime() {
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

    /*public String getServiceId() {
        return serviceId;
    }*/

    public String getDialogId() {
        return dialogId;
    }

    public boolean getIsRate() {
        return isRate;
    }

    public String getType() {
        return type;
    }
}
