package com.example.ideal.myapplication.fragments.objects;

public class Photo {

    private String photoId;
    private String photoLink;
    private String photoOwnerId;

    public void setPhotoId(String _photoId){
        photoId = _photoId;
    }
    public String getPhotoId() {
        return photoId;
    }
    public void setPhotoLink(String _photoLink){
        photoLink = _photoLink;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoOwnerId(String _photoOwnerId){
        photoOwnerId = _photoOwnerId;
    }

    public String getPhotoOwnerId() {
        return photoOwnerId;
    }
}
