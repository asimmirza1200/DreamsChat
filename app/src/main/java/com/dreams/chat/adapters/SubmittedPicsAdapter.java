package com.dreams.chat.adapters;

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

public class SubmittedPicsAdapter extends RecyclerView.Adapter<SubmittedPicsAdapter.SubmittedPicsViewHolder> {
    public SubmittedPicsAdapter(Context context, List<SubmittedPicsModel> submittedPicsList) {
        this.context = context;
        this.submittedPicsList = submittedPicsList;
    }

    Context context;
    List<SubmittedPicsModel> submittedPicsList;
    View view;
    @NonNull
    @Override
    public SubmittedPicsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SubmittedPicsViewHolder(view= LayoutInflater.from(context).inflate(R.layout.item_rv_submitted_pics,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SubmittedPicsViewHolder submittedPicsViewHolder, int i) {
        Glide.with(context).load(submittedPicsList.get(i).getPicture_url()).into(submittedPicsViewHolder.ivSubmittedPics);
        submittedPicsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return submittedPicsList.size();
    }

    public class SubmittedPicsViewHolder extends RecyclerView.ViewHolder{
        ImageView ivSubmittedPics;
        public SubmittedPicsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSubmittedPics=itemView.findViewById(R.id.ivSubmittedPics);
        }
    }
}
