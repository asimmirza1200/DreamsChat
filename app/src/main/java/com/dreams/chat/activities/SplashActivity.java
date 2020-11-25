package com.dreams.chat.activities;

import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.daasuu.ahp.AnimateHorizontalProgressBar;
import com.dreams.chat.R;
import com.dreams.chat.models.Group;
import com.dreams.chat.utils.Constants;
import com.dreams.chat.utils.Helper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.gson.Gson;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AnimateHorizontalProgressBar progressBar = (AnimateHorizontalProgressBar) findViewById(R.id.animate_progress_bar);
        progressBar.setMax(1500);
        progressBar.setProgressWithAnim(1500);
        final Helper helper = new Helper(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                final FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference ref = database.getReference("data/groups/group_+923438851054_1604304148946");
//
//// Attach a listener to read the data at our posts reference
//                ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Group main_group = dataSnapshot.getValue(Group.class);
//                        Constants.maingroup=main_group;
//                        Log.d("sdjslkjdlsk",new Gson().toJson(main_group));
                        startActivity(helper.isLoggedIn()?new Intent(SplashActivity.this, HomeActivity.class):new Intent(SplashActivity.this, SignInActivity.class));
                        finish();

//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        System.out.println("The read failed: " + databaseError.getCode());
//                    }
//                });
            }
        }, 1500);
    }
}
