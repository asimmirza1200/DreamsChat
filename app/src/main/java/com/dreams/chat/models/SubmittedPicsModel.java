package com.dreams.chat.models;

import java.io.Serializable;

public class SubmittedPicsModel implements Serializable {
    String picture_url;
    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public SubmittedPicsModel(String picture_url) {
        this.picture_url = picture_url;
    }

    public SubmittedPicsModel() {
    }
}
