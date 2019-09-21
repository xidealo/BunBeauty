package com.bunbeauty.ideal.myapplication.entity;

public class Comment implements Comparable<Comment> {
    private String userId;
    private String userName;
    private float rating;
    private String review;
    private String serviceName;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUserId(String _id) {
        userId = _id;
    }

    public void setServiceName(String _serviceName) {
        serviceName = _serviceName;
    }

    public String getServiceName() {
        return serviceName;
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

    @Override
    public int compareTo(Comment otherComment) {
        return time.compareTo(otherComment.time);
    }
}
