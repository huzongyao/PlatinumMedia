package com.hzy.platinum.media.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
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
    private MediaInfo mMediaInfo;
    private RequestManager mGlide;

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
        mMediaInfo = getIntent().getParcelableExtra(MediaUtils.EXTRA_MEDIA_INFO);
        startPlayCurrentMedia();
    }

    private void startPlayCurrentMedia() {
        if (mMediaInfo != null) {
            Uri uri = Uri.parse(mMediaInfo.url);
            mGlide.load(uri).into(mPhotoView);
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
}
