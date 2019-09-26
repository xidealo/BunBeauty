package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity;

public class User {

    public static final String PHONE = "phone";
    public static final String USERS = "users";

    public static final String NAME = "name";
    public static final String CITY = "city";
    public static final String PHOTO_LINK = "photo link";
    public static final String AVG_RATING = "avg rating";
    public static final String COUNT_OF_RATES = "count of rates";

    private String phone;
    private String name;
    private String city;
    private String id;
    private float rating;
    private long countOfRates;

    public long getCountOfRates() {
        return countOfRates;
    }

    public void setCountOfRates(long countOfRates) {
        this.countOfRates = countOfRates;
    }

    public void setPhone(String _phone) {
        phone = _phone;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean setName(String _name) {
        if (isCorrectName(_name)) {
            name = _name;
            return true;
        }
        return false;
    }

    public boolean setCity(String _city) {
        if (isCorrectCity(_city)) {
            city = _city;
            return true;
        }
        return false;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    private boolean isCorrectName(String name) {

        if (!name.matches("[a-zA-ZА-Яа-я\\-]+\\s[a-zA-ZА-Яа-я\\-]+")) return false;

        return true;
    }

    private boolean isCorrectCity(String city) {

        if (!city.matches("[a-zA-ZА-Яа-я\\-]+")) return false;

        return true;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getRating() {
        return rating;
    }
}
