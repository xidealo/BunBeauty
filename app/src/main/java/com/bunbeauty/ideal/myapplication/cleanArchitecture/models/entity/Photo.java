package com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity;

public class Photo {

    private String photoId;
    private String photoLink;
    private String photoOwnerId;

    public String getPhotoId() {
        return photoId;
    }
    public void setPhotoId(String _photoId){
        photoId = _photoId;
    }

    public String getPhotoLink() {
        return photoLink;
    }
    public void setPhotoLink(String _photoLink){
        photoLink = _photoLink;
    }

    public String getPhotoOwnerId() {
        return photoOwnerId;
    }
    public void setPhotoOwnerId(String _photoOwnerId){
        photoOwnerId = _photoOwnerId;
    }
}
