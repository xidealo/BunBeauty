package com.example.ideal.myapplication.fragments.objects;

public class Comment {
    private String userId;
    private String userName;
    private float rating;
    private String review;

    public void setUserId(String _id) {
        userId = _id;
    }

    public void setUserName(String _name) {
        userName = _name;
    }

    public void setRating(float _rating) {
        rating = _rating;
    }

    public void setReview(String _review) {
        review = _review;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public float getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }
}