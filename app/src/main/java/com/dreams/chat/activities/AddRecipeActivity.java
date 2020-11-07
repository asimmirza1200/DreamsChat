package com.dreams.chat.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.dreams.chat.R;
import com.dreams.chat.databinding.ActivityAddRecipeBinding;
import com.dreams.chat.models.Attachment;
import com.dreams.chat.models.AttachmentTypes;
import com.dreams.chat.models.RecipeModel;
import com.dreams.chat.models.SubmittedPicsModel;
import com.dreams.chat.models.User;
import com.dreams.chat.utils.FirebaseUploader;
import com.dreams.chat.utils.Helper;
import com.dreams.chat.utils.SanImagePicker;
import com.dreams.chat.utils.Sources;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    List<SubmittedPicsModel> dowload_url=new ArrayList<>();
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_recipe);
        binding.rvSelectedPics.setAdapter(postRecipeAdapter = new PostRecipeAdapter(this, post_recipe_list));
        mAwesomeValidation = new AwesomeValidation(COLORATION);
        mAwesomeValidation.setColor(Color.parseColor("#FFA500"));
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mAwesomeValidation.addValidation(this, binding.etDescription.getId(), "[0-9a-zA-Z\\s\\.]+", R.string.err_description);
        binding.btPostRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAwesomeValidation.validate()) {
                    if (isNetworkAvailable(AddRecipeActivity.this)) {
                         dialog = ProgressDialog.show(AddRecipeActivity.this, "Uploading Images",
                                "Adding. Please wait...", true);
                         dialog.show();
                       upload(new File(post_recipe_list.get(0).getPicture_url()), 0);
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
    private void upload(final File fileToUpload, final int index) {
        if (!fileToUpload.exists())
            return;
        final String fileName = Uri.fromFile(fileToUpload).getLastPathSegment();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(getString(R.string.app_name)).child(AttachmentTypes.getTypeName(AttachmentTypes.RECIPE)).child(fileName);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //If file is already uploaded
                Attachment attachment1 = new Attachment();
                attachment1.setName(fileName);
                attachment1.setUrl(uri.toString());
                attachment1.setBytesCount(fileToUpload.length());
                dowload_url.add(new SubmittedPicsModel(uri.toString()));
                if (index+1==post_recipe_list.size()){
                    Helper helper = new Helper(AddRecipeActivity.this);

                    User userMe = helper.getLoggedInUser();


                    RecipeModel sendRecipeChatModel=new RecipeModel(userMe.getId(),userMe.getNameToDisplay(),System.currentTimeMillis(),"Recipe",binding.etDescription.getEditText().getText().toString(),"0","0",userMe.getImage(),dowload_url);

                    FirebaseDatabase.getInstance().getReference().child("public").push()
                            .setValue(sendRecipeChatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                            finish();
                            Toast.makeText(AddRecipeActivity.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    upload(new File(post_recipe_list.get(index+1).getPicture_url()), index+1);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //Elase upload and then send message
                FirebaseUploader firebaseUploader = new FirebaseUploader(new FirebaseUploader.UploadListener() {
                    @Override
                    public void onUploadFail(String message) {
                        dialog.dismiss();
                        Log.e("DatabaseException", message);
                    }

                    @Override
                    public void onUploadSuccess(String downloadUrl) {
                        Attachment attachment1=new Attachment() ;
                        attachment1.setName(fileToUpload.getName());
                        attachment1.setUrl(downloadUrl);
                        attachment1.setBytesCount(fileToUpload.length());
                        dowload_url.add(new SubmittedPicsModel(downloadUrl));

                        if (index+1==post_recipe_list.size()){
                            Helper helper = new Helper(AddRecipeActivity.this);

                            User userMe = helper.getLoggedInUser();


                            RecipeModel sendRecipeChatModel=new RecipeModel(userMe.getId(),userMe.getNameToDisplay(),System.currentTimeMillis(),"Recipe",binding.etDescription.getEditText().getText().toString(),"0","0",userMe.getImage(),dowload_url);

                            FirebaseDatabase.getInstance().getReference().child("public").push()
                                    .setValue(sendRecipeChatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    finish();
                                    Toast.makeText(AddRecipeActivity.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            upload(new File(post_recipe_list.get(index+1).getPicture_url()), index+1);

                        }


                    }

                    @Override
                    public void onUploadProgress(int progress) {

                    }

                    @Override
                    public void onUploadCancelled() {
                        dialog.dismiss();
                    }
                }, storageReference);
                firebaseUploader.uploadOthers(getApplicationContext(), fileToUpload);
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