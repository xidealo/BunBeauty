package com.example.ideal.myapplication.fragments.objects;

public class Service {

    private String id;
    private String name;
    private String description;
    private String cost;
    private String userId;
    private boolean isPremium;

    public boolean setName(String _name){
        if(isCorrectData(_name)) {
            name = _name.trim();
            return true;
        }
        return false;
    }
    public void setId(String _id){
        id = _id;
    }
    public void setDescription(String _description){
        description = _description;
    }

    public void setIsPremium(Boolean _isPremium){
        isPremium = _isPremium;
    }

    public boolean setCost(String _cost){
        if(isCorrectCost(_cost)) {
            cost = _cost;
            return true;
        }
        return false;
    }
    public void setUserId(String _userId){
        userId = _userId;
    }

    public String getId(){return id;}
    public String getName(){return name;}
    public String getDescription(){return description;}
    public String getCost(){return cost;}
    public String getUserId(){return userId;}
    public Boolean getIsPremium(){return isPremium;}
    protected boolean isCorrectData(String data){

        if(!data.matches("[a-zA-ZА-Яа-я\\s\\-]+")) return false;
        return true;
    }

    private boolean isCorrectCost(String cost){

        if(!cost.matches("[0-9]{1,8}")) return false;
        return true;
    }

}
