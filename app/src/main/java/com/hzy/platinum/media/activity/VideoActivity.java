package com.hzy.platinum.media.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.hzy.platinum.media.R;
import com.hzy.platinum.media.media.MediaInfo;
import com.hzy.platinum.media.media.MediaUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huzongyao on 2018/6/29.
 */

public class VideoActivity extends AppCompatActivity
        implements OnPreparedListener {

    @BindView(R.id.video_view)
    VideoView mVideoView;

    private MediaInfo mMediaInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        initVideoPlayer();
        mMediaInfo = getIntent().getParcelableExtra(MediaUtils.EXTRA_MEDIA_INFO);
        startPlayCurrentMedia();
    }

    private void initVideoPlayer() {
        mVideoView.setHandleAudioFocus(false);
        mVideoView.setOnPreparedListener(this);
    }

    private void startPlayCurrentMedia() {
        if (mMediaInfo != null) {
            VideoControls videoControls = mVideoView.getVideoControls();
            if (videoControls != null) {
                videoControls.setTitle(mMediaInfo.title);
            }
            Uri uri = Uri.parse(mMediaInfo.url);
            mVideoView.setVideoURI(uri);
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

    @Override
    public void onPrepared() {
        if (!mVideoView.isPlaying()) {
            mVideoView.start();
        }
    }
}
