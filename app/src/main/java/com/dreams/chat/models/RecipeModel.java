package com.dreams.chat.models;

public class RecipeModel {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getComments_count() {
        return comments_count;
    }

    public void setComments_count(String comments_count) {
        this.comments_count = comments_count;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public RecipeModel(String name, String date, String type, String content, String comments_count, String likes, String profile_picture,boolean isLiked) {
        this.name = name;
        this.date = date;
        this.type = type;
        this.content = content;
        this.comments_count = comments_count;
        this.likes = likes;
        this.profile_picture=profile_picture;
        this.isLiked=isLiked;
    }

    String name,date,type,content,comments_count,likes,profile_picture;

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    boolean isLiked;
}
