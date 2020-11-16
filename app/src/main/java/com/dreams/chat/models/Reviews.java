package com.dreams.chat.models;

import java.io.Serializable;
public class Reviews implements Serializable {
    String comment,rating="",id,image,type="";

    public Reviews(String comment, String rating, String id,String image,String type) {
        this.comment = comment;
        this.rating = rating;
        this.id = id;
        this.image=image;
        this.type=type;

    }

    public Reviews() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
