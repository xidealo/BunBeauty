package com.example.ideal.myapplication.fragments.objects;

public class RatingReview {

    private String id;
    private String review;
    private String rating;
    private String workingTimeId;
    private String messageId;
    private String type;

    public void setReview(String _review){
        review=_review;
    }
    public void setId(String _id){
        id=_id;
    }
    public void setWorkingTimeId(String _workingTimeId){
        workingTimeId=_workingTimeId;
    }
    public void setMessageId(String _messageId){
        messageId=_messageId;
    }
    public void setRating(String _rating){
        rating=_rating;
    }
    public void setType(String _type){
        type = _type;
    }

    public String getReview() {
        return review;
    }

    public String getWorkingTimeId() {
        return workingTimeId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getRating() {
        return rating;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
