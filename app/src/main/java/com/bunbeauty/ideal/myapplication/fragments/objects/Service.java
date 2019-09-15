package com.bunbeauty.ideal.myapplication.fragments.objects;

import java.util.ArrayList;

public class Service {

    private String id;
    private String name;
    private float averageRating;
    private String description;
    private String cost;
    private String userId;
    private String category;
    private ArrayList<String> tags;
    private boolean isPremium;
    private String creationDate;
    private String address;
    private long countOfRates;

    // id
    public String getId(){return id;}
    public void setId(String _id){
        id = _id;
    }

    // Кол-во оценок
    public long getCountOfRates() {
        return countOfRates;
    }
    public void setCountOfRates(long countOfRates) {
        this.countOfRates = countOfRates;
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

    //категория
    public void setCategory(String category) {
        this.category = category;
    }
    public String getCategory() {
        return category;
    }

    //Теги
    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
    public ArrayList<String> getTags() {
        return tags;
    }

    //адерс
    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddress() {
        return address;
    }
}
