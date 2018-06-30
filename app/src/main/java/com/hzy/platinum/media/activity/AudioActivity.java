package com.hzy.platinum.media.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.devbrackets.android.exomedia.AudioPlayer;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.hzy.platinum.media.R;
import com.hzy.platinum.media.media.MediaInfo;
import com.hzy.platinum.media.media.MediaUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huzongyao on 2018/6/30.
 */

public class AudioActivity extends AppCompatActivity
        implements OnPreparedListener {

    @BindView(R.id.audio_player_image)
    ImageView mAudioPlayerImage;
    @BindView(R.id.audio_player_loading)
    ProgressBar mAudioPlayerLoading;
    @BindView(R.id.audio_player_position)
    TextView mAudioPlayerPosition;
    @BindView(R.id.audio_player_duration)
    TextView mAudioPlayerDuration;
    @BindView(R.id.audio_player_seek)
    SeekBar mAudioPlayerSeek;
    @BindView(R.id.audio_player_previous)
    ImageButton mAudioPlayerPrevious;
    @BindView(R.id.audio_player_play_pause)
    ImageButton mAudioPlayerPlayPause;
    @BindView(R.id.audio_player_next)
    ImageButton mAudioPlayerNext;

    private MediaInfo mMediaInfo;
    private RequestManager mGlide;
    private AudioPlayer mAudioPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_audio);
        ButterKnife.bind(this);
        mMediaInfo = getIntent().getParcelableExtra(MediaUtils.EXTRA_MEDIA_INFO);
        initAudioPlayer();
        startPlayCurrentMedia();
    }

    private void initAudioPlayer() {
        mAudioPlayer = new AudioPlayer(getApplicationContext());
        mAudioPlayer.setOnPreparedListener(this);
    }

    private void startPlayCurrentMedia() {
        mGlide = Glide.with(this);
        if (mMediaInfo != null) {
            Uri artUri = Uri.parse(mMediaInfo.albumArtURI);
            mGlide.load(artUri).into(mAudioPlayerImage);
            Uri uri = Uri.parse(mMediaInfo.url);
            mAudioPlayer.setDataSource(uri);
            mAudioPlayer.prepareAsync();
        }
    }

    private void updatePlayPauseImage(boolean isPlaying) {
        int resId = isPlaying ? R.drawable.exomedia_ic_pause_white
                : R.drawable.exomedia_ic_play_arrow_white;
        mAudioPlayerPlayPause.setImageResource(resId);
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
        if (!mAudioPlayer.isPlaying()) {
            mAudioPlayer.start();
        }
    }
}
