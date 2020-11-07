package com.dreams.chat.models;

import java.util.List;

public class ChallengeModel {
    private  String id;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getstartdate() {
        return startdate;
    }

    public void setstartdate(String startdate) {
        this.startdate = startdate;
    }

    public ChallengeModel() {
    }

    public String getenddate() {
        return enddate;
    }

    public void setenddate(String enddate) {
        this.enddate = enddate;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public ChallengeModel(String id,String name, long date, String type, String content, String startdate, String enddate, String profile_picture, List<SubmittedPicsModel> post_recipe_list,String group) {
        this.name = name;
        this.group=group;
        this.date = date;
        this.post_recipe_list = post_recipe_list;
        this.type = type;
        this.content = content;
        this.id = id;

        this.startdate = startdate;
        this.enddate = enddate;
        this.profile_picture=profile_picture;
    }
    List<SubmittedPicsModel> post_recipe_list;

    String name;
    long date;
    String type;
    String content;
    String startdate;
    String enddate;
    String profile_picture;
    String group;

    public String getGroup() {
        return group;
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



}
