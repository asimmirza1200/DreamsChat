package com.dreams.chat.models;

import com.dreams.chat.adapters.Comments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RecipeModel implements Serializable {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
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

    public RecipeModel() {
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

    public RecipeModel(String id,String name, long date, String type, String content, String comments_count, String likes, String profile_picture, List<SubmittedPicsModel> post_recipe_list) {
        this.name = name;
        this.date = date;
        this.post_recipe_list = post_recipe_list;
        this.type = type;
        this.id=id;
        this.content = content;
        this.comments_count = comments_count;
        this.likes = likes;
        this.profile_picture=profile_picture;
    }
    List<SubmittedPicsModel> post_recipe_list;
    List<String> usersId=new ArrayList<>();
    ArrayList<Comments> commentsArrayList=new ArrayList<>();;
    String name;
    long date;
    String type,id,key;

    String content;
    String comments_count;
    String likes;
    String profile_picture;
    String startdate;
    String enddate;
    String group;

    public ArrayList<Comments> getCommentsArrayList() {
        return commentsArrayList;
    }

    public void setCommentsArrayList(ArrayList<Comments> commentsArrayList) {
        this.commentsArrayList = commentsArrayList;
    }

    public List<String> getUsersId() {
        return usersId;
    }

    public void setUsersId(List<String> usersId) {
        this.usersId = usersId;
    }

    public String getGroup() {
        return group;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<SubmittedPicsModel> getPost_recipe_list() {
        return post_recipe_list;
    }

    public void setPost_recipe_list(List<SubmittedPicsModel> post_recipe_list) {
        this.post_recipe_list = post_recipe_list;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }
}
