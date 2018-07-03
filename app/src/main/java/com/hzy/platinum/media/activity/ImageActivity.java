package com.hzy.platinum.media.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hzy.platinum.media.R;
import com.hzy.platinum.media.media.MediaInfo;
import com.hzy.platinum.media.media.MediaUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by huzongyao on 2018/7/2.
 */

public class ImageActivity extends AppCompatActivity {

    @BindView(R.id.photo_view)
    PhotoView mPhotoView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private MediaInfo mMediaInfo;
    private RequestManager mGlide;
    private PhotoViewTarget mPhotoViewTarget;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        mGlide = Glide.with(this);
        mPhotoViewTarget = new PhotoViewTarget(mPhotoView);
        startPlayCurrentMedia(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startPlayCurrentMedia(intent);
    }

    private void startPlayCurrentMedia(Intent intent) {
        mMediaInfo = intent.getParcelableExtra(MediaUtils.EXTRA_MEDIA_INFO);
        if (mMediaInfo != null) {
            Uri uri = Uri.parse(mMediaInfo.url);
            mGlide.load(uri).into(mPhotoViewTarget);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
