package com.example.ideal.myapplication.fragments.objects;

public class Order {
    private String id;
    private String workingTimeId;

    public void setId(String _id){
        id = _id;
    }
    public void setWorkingTimeId(String _workingTimeId){
        workingTimeId=_workingTimeId;
    }

    public String getId() {
        return id;
    }

    public String getWorkingTimeId() {
        return workingTimeId;
    }
}
