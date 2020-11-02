package com.dreams.chat.models;

import java.util.List;

public class SendRecipeChatModel {
    String content;
    List<SubmittedPicsModel> post_recipe_list;
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<SubmittedPicsModel> getPost_recipe_list() {
        return post_recipe_list;
    }

    public void setPost_recipe_list(List<SubmittedPicsModel> post_recipe_list) {
        this.post_recipe_list = post_recipe_list;
    }


    public SendRecipeChatModel(String content, List<SubmittedPicsModel> post_recipe_list) {
        this.content = content;
        this.post_recipe_list = post_recipe_list;
    }


}
