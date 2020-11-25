package com.dreams.chat.activities;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dreams.chat.R;
import com.dreams.chat.models.SubmittedPicsModel;

import java.util.List;

public class PostRecipeAdapter extends RecyclerView.Adapter<PostRecipeAdapter.PostRecipeViewHolder> {
    Context context;
    List<SubmittedPicsModel> post_recipe_list;
    View view;

    public PostRecipeAdapter(Context context, List<SubmittedPicsModel> post_recipe_list) {
        this.context = context;
        this.post_recipe_list = post_recipe_list;
    }

    @NonNull
    @Override
    public PostRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(context).inflate(R.layout.item_rv_submitted_pics, viewGroup, false);
        return new PostRecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostRecipeViewHolder postRecipeViewHolder, int i) {
        Glide.with(context).load(post_recipe_list.get(i).getPicture_url()).into(postRecipeViewHolder.ivSubmittedPics);
        postRecipeViewHolder.closeimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post_recipe_list.remove(i);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return post_recipe_list.size();
    }

    public class PostRecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSubmittedPics,closeimg;
        public PostRecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSubmittedPics=itemView.findViewById(R.id.ivSubmittedPics);
            closeimg=itemView.findViewById(R.id.closebtn);
            closeimg.setVisibility(View.VISIBLE);

        }
    }
}
