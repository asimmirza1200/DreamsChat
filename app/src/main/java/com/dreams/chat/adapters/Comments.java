package com.dreams.chat.adapters;

public class Comments {
    String id,comments,image,rate="",type="";

//    public Comments(String id, String comments, String image) {
//        this.id = id;
//        this.comments = comments;
//        this.image = image;
//    }
    public Comments(String id, String comments, String image, String rate, String type) {
        this.id = id;
        this.comments = comments;
        this.image = image;
        this.type = type;
        this.rate = rate;

    }
    public Comments() {
    }

    public String getRate() {
        return rate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
