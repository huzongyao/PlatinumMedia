package com.hzy.platinum.media.activity;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hzy.platinum.media.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by huzongyao on 2018/7/2.
 * To play image media
 */

public class ImageActivity extends BasePlayActivity {

    @BindView(R.id.photo_view)
    PhotoView mPhotoView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private RequestManager mGlide;
    private PhotoViewTarget mPhotoViewTarget;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        mGlide = Glide.with(this);
        mPhotoViewTarget = new PhotoViewTarget(mPhotoView);
        setCurrentMediaAndPlay();
    }

    @Override
    void setCurrentMediaAndPlay() {
        if (mMediaInfo != null) {
            Uri uri = Uri.parse(mMediaInfo.url);
            mGlide.load(uri).into(mPhotoViewTarget);
        }
    }

    private class PhotoViewTarget extends DrawableImageViewTarget {
        PhotoViewTarget(ImageView view) {
            super(view);
        }

        @Override
        public void onLoadStarted(@Nullable Drawable placeholder) {
            super.onLoadStarted(placeholder);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onResourceReady(@NonNull Drawable resource,
                                    @Nullable Transition<? super Drawable> transition) {
            super.onResourceReady(resource, transition);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
