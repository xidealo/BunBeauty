package com.example.ideal.myapplication.fragments.objects;

public class Message {

    private String id;
    private String dialogId;
    private String messageTime;


    public void setId(String _id) {
        id = _id;
    }


    public void setDialogId(String _dialogId) {
        dialogId = _dialogId;
    }

    public void setMessageTime(String _time) {
        messageTime = _time;
    }

    public String getId() {
        return id;
    }

    public String getDialogId() {
        return dialogId;
    }

    public String getMessageTime() {
        return messageTime;
    }

}
