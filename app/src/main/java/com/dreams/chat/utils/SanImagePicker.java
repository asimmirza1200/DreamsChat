package com.dreams.chat.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.util.List;

import io.reactivex.subjects.PublishSubject;


public class SanImagePicker {

    @SuppressLint("StaticFieldLeak")
    private static SanImagePicker instance;


    public static synchronized SanImagePicker with(Context context) {
        if (instance == null) {
            instance = new SanImagePicker(context.getApplicationContext());
        }
        return instance;
    }

    private Context context;
    private PublishSubject<Uri> publishSubject;
    private PublishSubject<List<Uri>> publishSubjectMultipleImages;

    private SanImagePicker(Context context) {
        this.context = context;
    }

    public PublishSubject<Uri> getActiveSubscription() {
        return publishSubject;
    }

    public PublishSubject<Uri> requestImage(Sources imageSource) {
        publishSubject = PublishSubject.create();
        startImagePickHiddenActivity(imageSource.ordinal(), false);
        return publishSubject;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public PublishSubject<List<Uri>> requestMultipleImages() {
        publishSubjectMultipleImages = PublishSubject.create();
        startImagePickHiddenActivity(Sources.GALLERY.ordinal(), true);
        return publishSubjectMultipleImages;
    }

    void onImagePicked(Uri uri) {
        if (publishSubject != null) {
            publishSubject.onNext(uri);
            publishSubject.onComplete();
        }
    }

    void onImagesPicked(List<Uri> uris) {
        if (publishSubjectMultipleImages != null) {
            publishSubjectMultipleImages.onNext(uris);
            publishSubjectMultipleImages.onComplete();
        }
    }

    private void startImagePickHiddenActivity(int imageSource, boolean allowMultipleImages) {
        Intent intent = new Intent(context, HiddenActivity.class);
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(HiddenActivity.ALLOW_MULTIPLE_IMAGES, allowMultipleImages);
        intent.putExtra(HiddenActivity.IMAGE_SOURCE, imageSource);
        context.startActivity(intent);
    }

}

