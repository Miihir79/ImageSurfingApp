package com.mihir.imageSurfing.model;

public class ImageModel {

    private UrlModel urls;

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
