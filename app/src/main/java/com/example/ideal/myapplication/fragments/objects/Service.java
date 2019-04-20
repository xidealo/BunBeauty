package com.example.ideal.myapplication.fragments.objects;

public class Service {

    private String id;
    private String name;
    private String description;
    private String cost;
    private String userId;
    private boolean isPremium;
    private String creationDate;
    private float averageRating;

    // id
    public String getId(){return id;}
    public void setId(String _id){
        id = _id;
    }

    // Имя
    public String getName(){return name;}
    public boolean setName(String _name){
        if(isCorrectData(_name)) {
            name = _name.trim();
            return true;
        }
        return false;
    }

    // Цена
    public boolean setCost(String _cost){
        if(isCorrectCost(_cost)) {
            cost = _cost;
            return true;
        }
        return false;
    }
    public String getCost(){return cost;}

    // Описание
    public String getDescription(){return description;}
    public void setDescription(String _description){
        description = _description;
    }

    // id владельца
    public String getUserId(){return userId;}
    public void setUserId(String _userId){
        userId = _userId;
    }

    // подключон ли премиум
    public boolean getIsPremium(){return isPremium;}
    public void setIsPremium(Boolean _isPremium){
        isPremium = _isPremium;
    }

    // Дата создания
    public String getCreationDate(){return creationDate;}
    public void setCreationDate(String _creationDate) {
        creationDate = _creationDate;
    }

    // Средний рейтинг
    public float getAverageRating(){return averageRating;}
    public void setAverageRating(float _averageRating) {
        averageRating = _averageRating;
    }

    protected boolean isCorrectData(String data){

        if(!data.matches("[a-zA-ZА-Яа-я\\s\\-]+")) return false;
        return true;
    }

    private boolean isCorrectCost(String cost){

        if(!cost.matches("[0-9]{1,8}")) return false;
        return true;
    }


}
