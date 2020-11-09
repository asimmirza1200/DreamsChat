package com.dreams.chat.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.internal.IOException;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dreams.chat.R;
import com.dreams.chat.activities.ChatActivity;
import com.dreams.chat.activities.CreateChallengeActivity;
import com.dreams.chat.activities.EditChallengeActivity;
import com.dreams.chat.activities.HomeActivity;
import com.dreams.chat.activities.MyPostsActivity;
import com.dreams.chat.activities.SignInActivity;
import com.dreams.chat.models.Group;
import com.dreams.chat.models.RecipeModel;
import com.dreams.chat.models.Reviews;
import com.dreams.chat.models.SubmittedPicsModel;
import com.dreams.chat.models.User;
import com.dreams.chat.utils.ConfirmationDialogFragment;
import com.dreams.chat.utils.Constants;
import com.dreams.chat.utils.Helper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdapterSubmittedPicsRecipe extends RecyclerView.Adapter<AdapterSubmittedPicsRecipe.RecipeViewHolder> {
    List<RecipeModel> recipe_list;
    Context context;
    View view;
    SubmittedPicsAdapter submittedPicsAdapter;

    public AdapterSubmittedPicsRecipe(List<RecipeModel> recipe_list,
                                      Context context) {
        this.context = context;
        this.recipe_list = recipe_list;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i==0){
            view = LayoutInflater.from(context).inflate(R.layout.item_recipe, viewGroup, false);
            return new RecipeViewHolder(view);
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.item_challenge, viewGroup, false);
            return new RecipeViewHolder(view);
        }

    }

    @Override
    public int getItemCount() {
        return recipe_list.size();
    }
    public void onShowPopup(View v,ArrayList<Comments> commentsList,int i){

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        View inflatedView = layoutInflater.inflate(R.layout.popup_layout, null, false);
        // find the ListView in the popup layout
        RecyclerView listView = (RecyclerView) inflatedView.findViewById(R.id.commentsListView);
        LinearLayout headerView = (LinearLayout)inflatedView.findViewById(R.id.headerLayout);
        EditText writeComment = (EditText) inflatedView.findViewById(R.id.writeComment);

        TextView toptext = (TextView) inflatedView.findViewById(R.id.toptext);
       toptext.setText(commentsList.size()+" Comments and "+recipe_list.get(i).getLikes()+" people  Like this recipe");
        Button send = (Button) inflatedView.findViewById(R.id.send);
       send.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (!writeComment.getText().toString().isEmpty()){
                   Helper helper = new Helper(context);
                   User userMe = helper.getLoggedInUser();
                   commentsList.add(new Comments(userMe.getNameToDisplay(),writeComment.getText().toString(),userMe.getImage(),"",""));
                   FirebaseDatabase.getInstance().getReference().child("public").child(recipe_list.get(i).getKey()).child("commentsArrayList").setValue(commentsList).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           notifyDataSetChanged();
                           writeComment.setText("");
                           listView.scrollToPosition(commentsList.size()-1);
                           toptext.setText(commentsList.size()+" Comments and "+recipe_list.get(i).getLikes()+"  Like this recipe");

                       }
                   });
               }else {
                   Toast.makeText(context, "Enter Comment", Toast.LENGTH_SHORT).show();
               }

           }
       });
        // get device size
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
//        mDeviceHeight = size.y;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;


        // fill the data to the list items
        setSimpleList(listView,commentsList);


        // set height depends on the device size
        PopupWindow popWindow = new PopupWindow(inflatedView, width, height - 50, true);
        // set a background drawable with rounders corners
        popWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popup_bg));

        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        popWindow.setAnimationStyle(R.style.PopupAnimation);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0,100);
    }
    public void onShowPopupReview(View v, ArrayList<Reviews> commentsList, int i){

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        View inflatedView = layoutInflater.inflate(R.layout.popup_layout, null, false);
        // find the ListView in the popup layout
        RecyclerView listView = (RecyclerView) inflatedView.findViewById(R.id.commentsListView);
        LinearLayout headerView = (LinearLayout)inflatedView.findViewById(R.id.headerLayout);
        LinearLayout headerView1 = (LinearLayout)inflatedView.findViewById(R.id.comment_section);
        headerView1.setVisibility(View.GONE);
        EditText writeComment = (EditText) inflatedView.findViewById(R.id.writeComment);

        TextView toptext = (TextView) inflatedView.findViewById(R.id.toptext);
        ImageView img=inflatedView.findViewById(R.id.ratingimg);
        img.setVisibility(View.VISIBLE);
        TextView toptext1=(TextView) inflatedView.findViewById(R.id.rating);
        toptext1.setVisibility(View.VISIBLE);
        double rating=0.0;
        for (int j=0;j<commentsList.size();j++)
        {
            if (commentsList.get(j).getRating().equals("leave"))
            {
                commentsList.remove(j);
            }
            else
            {
                rating= rating+Double.parseDouble(commentsList.get(j).getRating());

            }
        }

        toptext.setText(commentsList.size()+" People review on this challage ");
        toptext1.setText(String.valueOf(rating/commentsList.size()));
        Button send = (Button) inflatedView.findViewById(R.id.send);

        // get device size
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
//        mDeviceHeight = size.y;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;


        // fill the data to the list items
        setSimpleList2(listView,commentsList);


        // set height depends on the device size
        PopupWindow popWindow = new PopupWindow(inflatedView, width, height - 50, true);
        // set a background drawable with rounders corners
        popWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popup_bg));

        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        popWindow.setAnimationStyle(R.style.PopupAnimation);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0,100);
    }
    void setSimpleList2(RecyclerView listView, ArrayList<Reviews> reviewsArrayList){

        ArrayList<Comments> commentsList=new ArrayList<>();
        for (int j = 0; j < reviewsArrayList.size(); j++) {
            commentsList.add(new Comments(reviewsArrayList.get(j).getId(),reviewsArrayList.get(j).getComment(),reviewsArrayList.get(j).getImage(),reviewsArrayList.get(j).getRating(),reviewsArrayList.get(j).getType()));


        }

        listView.setAdapter(new CommentsListAdapter(context,commentsList));
    }

    void setSimpleList(RecyclerView listView, ArrayList<Comments> commentsList){



        listView.setAdapter(new CommentsListAdapter(context,commentsList));
    }
    void share(RecipeModel recipeModel){
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Please wait... Preparing images");
        ArrayList<Uri> files = new ArrayList<Uri>();
        pd.show();


        for (int i = 0; i < recipeModel.getPost_recipe_list().size(); i++) {

            int finalI = i;
            Picasso.get().load(recipeModel.getPost_recipe_list().get(i).getPicture_url()).into(new Target() {
                @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if (finalI+1 ==recipeModel.getPost_recipe_list().size()) {
                        files.add(getImageUri(bitmap, Bitmap.CompressFormat.PNG,100));

                        pd.dismiss();

                        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                        intent.putExtra(Intent.EXTRA_TEXT, recipeModel.getContent());

                        intent.setType("*/*");
                        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                        context.startActivity(Intent.createChooser(intent, "Share Recipe"));
                    }else {
                        files.add(getImageUri(bitmap, Bitmap.CompressFormat.PNG,100));
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    pd.dismiss();

                }

                @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
            });
        }

    }
    public Uri getImageUri(Bitmap src, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);

        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), src, "title", null);
        return Uri.parse(path);
    }
    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, int i) {
        if (recipe_list.get(i).getType().equalsIgnoreCase("recipe")) {
            Glide.with(context).load(recipe_list.get(i).getProfile_picture()).placeholder(R.drawable.ic_placeholder).into(recipeViewHolder.ivProfilePicture);
            recipeViewHolder.tvName.setText(recipe_list.get(i).getName());
            recipeViewHolder.tvDate.setText(getDate(recipe_list.get(i).getDate(), "dd/MM/yyyy"));
            recipeViewHolder.tvType.setText(recipe_list.get(i).getType());
            recipeViewHolder.tvContent.setText(recipe_list.get(i).getContent());
            recipeViewHolder.tvLikeCount.setText(recipe_list.get(i).getLikes());

            recipeViewHolder.tvCommentsCount.setText(String.valueOf(recipe_list.get(i).getCommentsArrayList().size()));
            recipeViewHolder.ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    share(recipe_list.get(i));
                }
            });

            recipeViewHolder.rvSubmittedPics.setAdapter(submittedPicsAdapter = new SubmittedPicsAdapter(context, recipe_list.get(i).getPost_recipe_list()));
            recipeViewHolder.ivComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onShowPopup(recipeViewHolder.ivComments,recipe_list.get(i).getCommentsArrayList(),i);
                }
            });
            boolean check=false;
            Helper helper = new Helper(context);
            User userMe = helper.getLoggedInUser();

            for (int j = 0; j < recipe_list.get(i).getUsersId().size(); j++) {
                if (recipe_list.get(i).getUsersId().get(j).equals(userMe.getId())){
                    check=true;
                }
            }

            if (check){
                recipeViewHolder.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_favorite_24));

            }else {
                recipeViewHolder.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_heart));

            }
            recipeViewHolder.ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean check=false;
                    Helper helper = new Helper(context);
                    int index=-1;
                    User userMe = helper.getLoggedInUser();
                    for (int j = 0; j < recipe_list.get(i).getUsersId().size(); j++) {
                        if (recipe_list.get(i).getUsersId().get(j).equals(userMe.getId())){
                            check=true;
                            index=j;
                        }
                    }
                    if (!check) {
                        recipeViewHolder.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_favorite_24));

                        FirebaseDatabase.getInstance().getReference().child("public").child(recipe_list.get(i).getKey()).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                FirebaseDatabase.getInstance().getReference().child("public").child(recipe_list.get(i).getKey()).child("likes").setValue(String.valueOf(Integer.valueOf(dataSnapshot.getValue(String.class)) + 1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        recipe_list.get(i).setLikes(String.valueOf(Integer.valueOf(dataSnapshot.getValue(String.class)) + 1));
                                        notifyDataSetChanged();

                                    }
                                });
                                recipe_list.get(i).getUsersId().add(userMe.getId());
                                FirebaseDatabase.getInstance().getReference().child("public").child(recipe_list.get(i).getKey()).child("usersId").setValue(recipe_list.get(i).getUsersId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }else {
                        recipeViewHolder.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_heart));

                        int finalIndex = index;
                        FirebaseDatabase.getInstance().getReference().child("public").child(recipe_list.get(i).getKey()).child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                FirebaseDatabase.getInstance().getReference().child("public").child(recipe_list.get(i).getKey()).child("likes").setValue(String.valueOf(Integer.valueOf(dataSnapshot.getValue(String.class)) - 1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        recipe_list.get(i).setLikes(String.valueOf(Integer.valueOf(dataSnapshot.getValue(String.class)) -1));
                                        notifyDataSetChanged();

                                    }
                                });
                                FirebaseDatabase.getInstance().getReference().child("public").child(recipe_list.get(i).getKey()).child("usersId").child(String.valueOf(finalIndex)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        recipe_list.get(i).getUsersId().remove(finalIndex);
                                        notifyDataSetChanged();


                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            });

        }else {
            Glide.with(context).load(recipe_list.get(i).getProfile_picture()).placeholder(R.drawable.ic_placeholder).into(recipeViewHolder.ivProfilePicture);
            recipeViewHolder.tvName.setText(recipe_list.get(i).getName());
            recipeViewHolder.tvDate.setText("Start Date: "+recipe_list.get(i).getStartdate());
            recipeViewHolder.endtvDate.setText("End Date: "+recipe_list.get(i).getEnddate().substring(0,10));

            recipeViewHolder.tvType.setText(recipe_list.get(i).getType());
            recipeViewHolder.tvContent.setText(recipe_list.get(i).getContent());
            Helper helper = new Helper(context);

            User userMe = helper.getLoggedInUser();

            if (userMe.getId().equals(recipe_list.get(i).getName())) {
                recipeViewHolder.more.setVisibility(View.VISIBLE);
            }else {
                recipeViewHolder.more.setVisibility(View.GONE);

            }
            boolean check=false;

            for (int j = 0; j < recipe_list.get(i).getReviewsList().size(); j++) {
                if (recipe_list.get(i).getReviewsList().get(j).getId().equals(userMe.getId())){
                    check=true;
                }
            }
            if (check){
                recipeViewHolder.acceptchallenge.setVisibility(View.GONE);
                recipeViewHolder.Seeinfo.setVisibility(View.VISIBLE);
            }else {
                recipeViewHolder.acceptchallenge.setVisibility(View.VISIBLE);
                recipeViewHolder.Seeinfo.setVisibility(View.GONE);

            }
            recipeViewHolder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(context, recipeViewHolder.more);

                    //Inflating the Popup using xml file
                    popup.getMenuInflater()
                            .inflate(R.menu.edit_menu, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId()==R.id.editchallenges){
                                Intent intent = new Intent(context, EditChallengeActivity.class);
                                intent.putExtra("data",recipe_list.get(i));
                                context.startActivity(intent);
                            }else {
                                ConfirmationDialogFragment confirmationDialogFragment = ConfirmationDialogFragment.newInstance("Logout",
                                        "Are you sure you want to delete?",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                FirebaseDatabase.getInstance().getReference().child("public").child(recipe_list.get(i).getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        recipe_list.remove(i);
                                                        notifyDataSetChanged();
                                                        Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        },
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                            }
                                        });
                                confirmationDialogFragment.show(((AppCompatActivity)context).getSupportFragmentManager(), "CONFIRM_TAG");
                            }

                            return true;
                        }
                    });

                    popup.show();
                }
            });

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("data/groups/"+recipe_list.get(i).getGroup());

// Attach a listener to read the data at our posts reference
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Group main_group = dataSnapshot.getValue(Group.class);

                    if (main_group.getUserIds() != null) {
                        boolean status = false;
                        for (int j = 0; j < main_group.getUserIds().size(); j++) {
                            if (main_group.getUserIds().get(j).equals(userMe.getId())) {
                                status = true;
                            }
                        }
                        if (status) {
                            recipeViewHolder.acceptchallenge.setText("Go To Challenge");
                            //recipeViewHolder.Seeinfo.setVisibility(View.GONE);

                        }else {
                            recipeViewHolder.acceptchallenge.setText("Accept Challenge");
                          //  recipeViewHolder.Seeinfo.setVisibility(View.GONE);

                        }

                    } else {
                        recipeViewHolder.acceptchallenge.setText("Accept Challenge");
                        //recipeViewHolder.Seeinfo.setVisibility(View.GONE);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            recipeViewHolder.Seeinfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onShowPopupReview(recipeViewHolder.Seeinfo,recipe_list.get(i).getReviewsList(),i);

                }
            });






            recipeViewHolder.acceptchallenge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("data/groups/"+recipe_list.get(i).getGroup());
                    Toast.makeText(context, "Wait" , Toast.LENGTH_SHORT).show();

// Attach a listener to read the data at our posts reference
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Group main_group = dataSnapshot.getValue(Group.class);

                            if (main_group.getUserIds()!=null){
                                boolean status=false;
                                for (int j = 0; j < main_group.getUserIds().size(); j++) {
                                    if (main_group.getUserIds().get(j).equals(userMe.getId())){
                                        status=true;
                                    }
                                }
                                if (!status){
                                    main_group.getUserIds().add(userMe.getId());
                                    Toast.makeText(context, "Challenge Accepted" , Toast.LENGTH_SHORT).show();

                                }


                            }else {

                                main_group.setUserIds(new ArrayList<>());
                                main_group.getUserIds().add(userMe.getId());
                                Toast.makeText(context, "Challenge Accepted" , Toast.LENGTH_SHORT).show();

                            }

                            ref.setValue(main_group).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    context.startActivity(ChatActivity.newIntent(context, null,main_group ,recipe_list.get(i)));
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });

                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        return recipe_list.get(position).getType().equalsIgnoreCase("recipe")?0:1;
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate,endtvDate, tvType, tvContent, tvCommentsCount, tvLikeCount,acceptchallenge,Seeinfo;
        ImageView ivProfilePicture, more,ivComments, ivLike, ivShare;
        RecyclerView rvSubmittedPics;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            endtvDate = itemView.findViewById(R.id.endtvDate);
            acceptchallenge = itemView.findViewById(R.id.accept);
            Seeinfo=itemView.findViewById(R.id.SeeInfo);
            ivComments = itemView.findViewById(R.id.ivComments);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivShare = itemView.findViewById(R.id.ivShare);

            tvType = itemView.findViewById(R.id.tvType);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvCommentsCount = itemView.findViewById(R.id.tvCommentsCount);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvName = itemView.findViewById(R.id.senderName);
            more = itemView.findViewById(R.id.more);

            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            rvSubmittedPics = itemView.findViewById(R.id.rvSubmittedPics);
        }
    }
}
