package com.dreams.chat.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.realm.Realm;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.dreams.chat.R;
import com.dreams.chat.activities.AddRecipeActivity;
import com.dreams.chat.activities.CreateChallengeActivity;
import com.dreams.chat.activities.HomeActivity;
import com.dreams.chat.activities.MyPostsActivity;
import com.dreams.chat.adapters.AdapterSubmittedPicsRecipe;
import com.dreams.chat.models.Contact;
import com.dreams.chat.models.Group;
import com.dreams.chat.models.RecipeModel;
import com.dreams.chat.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private RelativeLayout createPost;
    private static String OPTIONS_MORE = "optionsmore";


    public static ArrayList<User> myUsers = new ArrayList<>();
    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView =view.findViewById(R.id.recycler_view);
       // createPost =view.findViewById(R.id.createpost);
        ImageView users_image = view.findViewById(R.id.users_image);

        users_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OptionsFragment.newInstance(null).show(getFragmentManager(), OPTIONS_MORE);

            }
        });
        ImageView more =view. findViewById(R.id.more);


        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getContext(), more);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(getContext(), MyPostsActivity.class);

                        startActivity(intent);
                        return true;
                    }
                });

                popup.show();
            }
        });
//        createPost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(getContext());
//                builder.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT);
//                builder.setTitle("Select Type!");
//                builder.setItems(new String[]{"Create Challenge", "Add Recipe"}, new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int index) {
//                        if (index==0){
//                            Intent recipe_intent=new Intent(getContext(), CreateChallengeActivity.class);
//                            startActivity(recipe_intent);
//                        }else {
//                            Intent recipe_intent=new Intent(getContext(), AddRecipeActivity.class);
//                            startActivity(recipe_intent);
//                        }
//                        dialogInterface.dismiss();
//                    }
//                });
//                builder.show();
//            }
//        });
        mySwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_lay);
        mySwipeRefreshLayout.setRefreshing(false);
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        getData();
        return view;
    }
    void  getData(){


        FirebaseDatabase.getInstance().getReference().child("public")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<RecipeModel> list=new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            RecipeModel recipe = snapshot.getValue(RecipeModel.class);//I'm assuming you have a Recipe class
                            recipe.setKey(snapshot.getKey());
                            list.add(recipe);
                        }
                        Collections.reverse(list);
                        AdapterSubmittedPicsRecipe postadapter = new AdapterSubmittedPicsRecipe(list, getContext());
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(postadapter);
                        mySwipeRefreshLayout.setRefreshing(false);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mySwipeRefreshLayout.setRefreshing(false);

                    }
                });

    }
}