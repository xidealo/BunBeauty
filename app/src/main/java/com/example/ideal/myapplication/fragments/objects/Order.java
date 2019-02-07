package com.example.ideal.myapplication.fragments.objects;

public class Order {
    private String id;
    private boolean isCanceled;
    private String messageId;
    private String workingTimeId;
    private String orderTime;


    public void setId(String _id){
        id = _id;
    }
    public void setIsCanceled(Boolean _isCanceled){
        isCanceled = _isCanceled;
    }
    public void setMessageId(String _messageId){
        messageId = _messageId;
    }
    public void setWorkingTimeId(String _workingTimeId){
        workingTimeId=_workingTimeId;
    }
    public void setOrderTime(String _orderTime){
        orderTime = _orderTime;
    }
    public String getId() {
        return id;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getWorkingTimeId() {
        return workingTimeId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public boolean getIsCanceled(){
        return isCanceled;
    }
}
