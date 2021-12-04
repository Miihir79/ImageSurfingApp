package com.mihir.imageSurfing.model;

public class ImageModel{

    private UrlModel urls;

    private UserModel user;

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public ImageModel(UserModel user) {
        this.user = user;
    }

    public UrlModel getUrls() {
        return urls;
    }

    public void setUrls(UrlModel urls) {
        this.urls = urls;
    }

    public ImageModel(UrlModel urls) {
        this.urls = urls;
    }
}
