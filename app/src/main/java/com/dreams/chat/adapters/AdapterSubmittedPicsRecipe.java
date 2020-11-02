package com.dreams.chat.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dreams.chat.R;
import com.dreams.chat.models.RecipeModel;
import com.dreams.chat.models.SubmittedPicsModel;

import java.util.List;

public class AdapterSubmittedPicsRecipe extends RecyclerView.Adapter<AdapterSubmittedPicsRecipe.RecipeViewHolder> {
    List<RecipeModel> recipe_list;
    Context context;
    View view;
    List<SubmittedPicsModel> submittedPicsList;
    SubmittedPicsAdapter submittedPicsAdapter;

    public AdapterSubmittedPicsRecipe(List<RecipeModel> recipe_list, List<SubmittedPicsModel> submittedPicsList,
                                      Context context) {
        this.context = context;
        this.recipe_list = recipe_list;
        this.submittedPicsList=submittedPicsList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view = LayoutInflater.from(context).inflate(R.layout.item_recipe, viewGroup, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return recipe_list.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, int i) {
        Glide.with(context).load(recipe_list.get(i).getProfile_picture()).into(recipeViewHolder.ivProfilePicture);
        recipeViewHolder.tvName.setText(recipe_list.get(i).getName());
        recipeViewHolder.tvDate.setText(recipe_list.get(i).getDate());
        recipeViewHolder.tvType.setText(recipe_list.get(i).getType());
        recipeViewHolder.tvContent.setText(recipe_list.get(i).getContent());
        recipeViewHolder.tvCommentsCount.setText(recipe_list.get(i).getComments_count());

        recipeViewHolder.rvSubmittedPics.setAdapter(submittedPicsAdapter = new SubmittedPicsAdapter(context,submittedPicsList));
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvType, tvContent, tvCommentsCount, tvLikeCount;
        ImageView ivProfilePicture, ivComments, ivLike, ivShare;
        RecyclerView rvSubmittedPics;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvType = itemView.findViewById(R.id.tvType);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvCommentsCount = itemView.findViewById(R.id.tvCommentsCount);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);

            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            rvSubmittedPics = itemView.findViewById(R.id.rvSubmittedPics);
        }
    }
}
