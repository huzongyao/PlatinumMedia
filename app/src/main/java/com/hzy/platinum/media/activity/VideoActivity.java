package com.hzy.platinum.media.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.hzy.platinum.media.R;
import com.hzy.platinum.media.media.MediaInfo;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huzongyao on 2018/6/29.
 */

public class VideoActivity extends AppCompatActivity {

    public static final String EXTRA_MEDIA_INFO = "EXTRA_MEDIA_INFO";

    @BindView(R.id.video_view)
    VideoView mVideoView;

    MediaInfo mMediaInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        mVideoView.setHandleAudioFocus(false);
        mMediaInfo = getIntent().getParcelableExtra(EXTRA_MEDIA_INFO);
        if (mMediaInfo != null) {
            Uri uri = Uri.parse(mMediaInfo.url);
            mVideoView.setVideoURI(uri);
            mVideoView.start();
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
