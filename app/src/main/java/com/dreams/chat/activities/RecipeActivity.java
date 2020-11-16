package com.dreams.chat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dreams.chat.BuildConfig;
import com.dreams.chat.R;
import com.dreams.chat.adapters.SubmittedPicsAdapter;
import com.dreams.chat.models.Group;
import com.dreams.chat.models.Message;
import com.dreams.chat.models.RecipeModel;
import com.dreams.chat.models.Reviews;

import java.io.Serializable;
import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {
    ImageView profileimg,shere;
    TextView name,date,content,timeleft,comment,like;
    RecyclerView imgrecycler;
    ArrayList<Reviews> reviewsArrayList;
    SubmittedPicsAdapter submittedPicsAdapter;
    public static Intent newIntent( Context context,RecipeModel recipeModel) {
        //intent contains user to chat with and message forward list if any.
        Intent intent = new Intent(context, RecipeActivity.class);
        intent.putExtra("data", recipeModel);

//        intent.putExtra(EXTRA_DATA_GROUP1, group);
        //intent.removeExtra(EXTRA_DATA_USER);

        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        profileimg=findViewById(R.id.ivProfilePicture);
        name=findViewById(R.id.senderName);
        date=findViewById(R.id.tvDate);
        content=findViewById(R.id.tvContent);
        timeleft=findViewById(R.id.timeLeft);
        comment=findViewById(R.id.tvCommentsCount);
        like=findViewById(R.id.tvLikeCount);
        imgrecycler=findViewById(R.id.rvSubmittedPics);
        shere=findViewById(R.id.ivShare);
        Intent intent = getIntent();
        RecipeModel recipemodel = (RecipeModel) intent.getSerializableExtra("data");
        imgrecycler.setAdapter(submittedPicsAdapter = new SubmittedPicsAdapter(RecipeActivity.this, recipemodel.getPost_recipe_list()));

        name.setText(recipemodel.getName());
//        date.setText((int) recipemodel.getDate());
        content.setText(recipemodel.getContent());
        comment.setText(recipemodel.getComments_count());
        like.setText(recipemodel.getLikes());
        Glide.with(this).load(recipemodel.getProfile_picture()).placeholder(R.drawable.ic_placeholder).into(profileimg);
        shere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            }
        });

    }
}