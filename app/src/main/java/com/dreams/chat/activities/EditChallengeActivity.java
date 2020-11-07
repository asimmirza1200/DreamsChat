package com.dreams.chat.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.dreams.chat.BaseApplication;
import com.dreams.chat.R;
import com.dreams.chat.databinding.ActivityCreateChallengeBinding;
import com.dreams.chat.models.ChallengeModel;
import com.dreams.chat.models.Group;
import com.dreams.chat.models.RecipeModel;
import com.dreams.chat.models.SubmittedPicsModel;
import com.dreams.chat.models.User;
import com.dreams.chat.utils.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;
import static com.dreams.chat.utils.GeneralUtils.isNetworkAvailable;

public class EditChallengeActivity extends AppCompatActivity {
    ActivityCreateChallengeBinding binding;
    PostRecipeAdapter postRecipeAdapter;
    List<SubmittedPicsModel> post_recipe_list=new ArrayList<>();
    AwesomeValidation mAwesomeValidation;
    List<SubmittedPicsModel> dowload_url=new ArrayList<>();
    private ProgressDialog dialog;
    private RecipeModel recipeModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_challenge);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_challenge);
        mAwesomeValidation = new AwesomeValidation(COLORATION);
        mAwesomeValidation.setColor(Color.parseColor("#FFA500"));
         recipeModel= (RecipeModel) getIntent().getSerializableExtra("data");
        binding.startdatetxt.setText(recipeModel.getStartdate());
        binding.enddatetxt.setText(recipeModel.getEnddate());
        binding.etDescription.getEditText().setText(recipeModel.getContent());
        BaseApplication.getGroupRef().child(recipeModel.getGroup()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Group main_group = dataSnapshot.getValue(Group.class);
                binding.groupname.getEditText().setText(main_group.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate(binding.startdatetxt);
            }
        });
        binding.enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate(binding.enddatetxt);
            }
        });
        mAwesomeValidation.addValidation(this, binding.etDescription.getId(), "[0-9a-zA-Z\\s\\.]+", R.string.err_description);
        binding.btPostRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAwesomeValidation.validate()) {
                    if (isNetworkAvailable(EditChallengeActivity.this)) {
                        dialog = ProgressDialog.show(EditChallengeActivity.this, "Loading",
                                "Adding. Please wait...", true);
                        dialog.show();
                        Helper helper = new Helper(EditChallengeActivity.this);

                        User userMe = helper.getLoggedInUser();

                       createGroup("",userMe);

                    }
                }
            }
        });

    }
    private void createGroup(String groupImageUrl, User userMe) {


//        ArrayList<MyString> exitIds = new ArrayList<>();
//        group.setExitIDS(exitIds);

        BaseApplication.getGroupRef().child(recipeModel.getGroup()).child("name").setValue(binding.groupname.getEditText().getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                ChallengeModel sendRecipeChatModel=new ChallengeModel(userMe.getId(),userMe.getNameToDisplay(),System.currentTimeMillis(),"Challenge",binding.etDescription.getEditText().getText().toString(),binding.startdatetxt.getText().toString(),binding.enddatetxt.getText().toString(),userMe.getImage(),dowload_url,recipeModel.getGroup());

                FirebaseDatabase.getInstance().getReference().child("public").child(recipeModel.getKey())
                        .setValue(sendRecipeChatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        finish();
                        Toast.makeText(EditChallengeActivity.this, "Successfully Edited", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Unable to process request at this time", Toast.LENGTH_SHORT).show();

            }
        });


    }

    void selectDate(TextView textView){
        // Initialize
        SwitchDateTimeDialogFragment dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                "Select Date",
                "OK",
                "Cancel"
        );

// Assign values
        dateTimeDialogFragment.startAtCalendarView();

// Define new day and month format
        try {
            dateTimeDialogFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("dd MMMM", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e("TAG", e.getMessage());
        }

// Set listener
        dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                Format formatter = new SimpleDateFormat("dd-MM-yyy HH:mm aa");
                String s = formatter.format(date);
                textView.setText(s);

                // Date is get on positive button click
                // Do something
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Date is get on negative button click
            }
        });

// Show
        dateTimeDialogFragment.show(getSupportFragmentManager(), "dialog_time");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



}