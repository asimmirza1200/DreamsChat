package com.dreams.chat.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.dreams.chat.R;
import com.dreams.chat.databinding.ActivityAddRecipeBinding;
import com.dreams.chat.models.SubmittedPicsModel;
import com.dreams.chat.utils.SanImagePicker;
import com.dreams.chat.utils.Sources;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;
import static com.dreams.chat.utils.FileUtils.getRealPathFromURI;
import static com.dreams.chat.utils.GeneralUtils.isNetworkAvailable;

public class AddRecipeActivity extends AppCompatActivity {
    ActivityAddRecipeBinding binding;
    PostRecipeAdapter postRecipeAdapter;
    List<SubmittedPicsModel> post_recipe_list=new ArrayList<>();
    AwesomeValidation mAwesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_recipe);
        binding.rvSelectedPics.setAdapter(postRecipeAdapter = new PostRecipeAdapter(this, post_recipe_list));
        mAwesomeValidation = new AwesomeValidation(COLORATION);
        mAwesomeValidation.setColor(Color.parseColor("#FFA500"));
        mAwesomeValidation.addValidation(this, binding.etDescription.getId(), "[0-9a-zA-Z\\s\\.]+", R.string.err_description);
        binding.btPostRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAwesomeValidation.validate()) {
                    if (isNetworkAvailable(AddRecipeActivity.this)) {
                        Intent intent = new Intent();
                        intent.putExtra("content", binding.etDescription.getEditText().getText().toString());
                        intent.putExtra("images", new Gson().toJson(post_recipe_list));
                        setResult(123, intent);
                        finish();

                    }
                }
            }
        });
        binding.btAddPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageOption();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void selectImageOption() {
        final CharSequence[] items = {getResources().getString(R.string.Capture_Photo), getResources().getString(R.string.Choose_from_Gallery), getResources().getString(R.string.Cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.Add_photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int item) {
                dialog = dialog;
                File f = null;
                if (items[item].equals(getResources().getString(R.string.Capture_Photo))) {
                    cameraIntent();
                    dialog.dismiss();

                } else if (items[item].equals(getResources().getString(R.string.Choose_from_Gallery))) {
                    galleryIntent();
                    dialog.dismiss();

                } else if (items[item].equals(getResources().getString(R.string.Cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void galleryIntent() {
        pickImageFromSource(Sources.GALLERY);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void cameraIntent() {
        pickImageFromSource(Sources.CAMERA);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("CheckResult")
    private void pickImageFromSource(Sources camera) {
        SanImagePicker.with(getApplicationContext()).requestImage(camera).subscribe(new Consumer<Uri>() {
            @Override
            public void accept(Uri uri) throws Exception {
                AddRecipeActivity.this.onImagePicked(uri);
            }
        });
    }

    String selectedImagePath;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void onImagePicked(Object uri) {
        String realPathofImage = getRealPathFromURI((Uri) uri, AddRecipeActivity.this);
        post_recipe_list.add(new SubmittedPicsModel(realPathofImage));
        postRecipeAdapter.notifyDataSetChanged();

//        selectedImagePath = getPath(getApplicationContext(), (Uri) uri);
//        imgFile = new File(realPathofImage);

    }
}