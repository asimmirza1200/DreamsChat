package com.dreams.chat.adapters;

public class Comments {
    String id,comments,image;

    public Comments(String id, String comments, String image) {
        this.id = id;
        this.comments = comments;
        this.image = image;
    }

    public Comments() {
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
