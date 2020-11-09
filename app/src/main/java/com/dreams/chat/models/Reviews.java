package com.dreams.chat.models;

public class Reviews {
    String comment,rating,id;

    public Reviews(String comment, String rating, String id) {
        this.comment = comment;
        this.rating = rating;
        this.id = id;
    }

    public Reviews() {
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
