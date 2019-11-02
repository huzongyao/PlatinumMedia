package com.hzy.platinum.media.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.devbrackets.android.exomedia.AudioPlayer;
import com.hzy.platinum.media.R;
import com.hzy.platinum.media.media.MediaUtils;
import com.hzy.platinum.media.view.MusicDiskView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by huzongyao on 2018/6/30.
 * To play audio media
 */

public class AudioActivity extends BasePlayActivity
        implements SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.audio_player_loading)
    ProgressBar mAudioPlayerLoading;
    @BindView(R.id.audio_player_position)
    TextView mAudioPlayerPosition;
    @BindView(R.id.audio_player_duration)
    TextView mAudioPlayerDuration;
    @BindView(R.id.audio_player_seek)
    SeekBar mAudioPlayerSeek;
    @BindView(R.id.audio_player_play_pause)
    ImageButton mAudioPlayerPlayPause;
    @BindView(R.id.main_container)
    RelativeLayout mMainContainer;
    @BindView(R.id.audio_player_controls_container)
    LinearLayout mControlsContainer;
    @BindView(R.id.audio_player_image)
    MusicDiskView mAudioPlayerImage;

    private RequestManager mGlide;
    private AudioPlayer mAudioPlayer;
    private PhotoViewTarget mPhotoViewTarget;
    private boolean mShouldSetDuration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        ButterKnife.bind(this);
        initAudioPlayer();
        setCurrentMediaAndPlay();
    }

    private void initAudioPlayer() {
        mGlide = Glide.with(this);
        mPhotoViewTarget = new PhotoViewTarget(mAudioPlayerImage);
        mAudioPlayer = new AudioPlayer(getApplicationContext());
        mAudioPlayer.setOnPreparedListener(this);
        mAudioPlayer.setOnBufferUpdateListener(this);
        mAudioPlayerSeek.setOnSeekBarChangeListener(this);
        mAudioPlayer.setOnCompletionListener(this);
    }

    @Override
    void setCurrentMediaAndPlay() {
        if (mMediaInfo != null) {
            if (!TextUtils.isEmpty(mMediaInfo.albumArtURI)) {
                Uri artUri = Uri.parse(mMediaInfo.albumArtURI);
                mGlide.load(artUri).into(mPhotoViewTarget);
            } else {
                mAudioPlayerImage.setImageResource(R.mipmap.ic_launcher);
            }
            Uri uri = Uri.parse(mMediaInfo.url);
            mShouldSetDuration = true;
            mAudioPlayer.setDataSource(uri);
            mAudioPlayer.prepareAsync();
        }
    }

    @OnClick(R.id.audio_player_play_pause)
    public void onAudioPlayPauseClicked() {
        if (mAudioPlayer.isPlaying()) {
            mAudioPlayer.pause();
        } else {
            mAudioPlayer.start();
        }
        updateUIPlayPause();
    }

    @Override
    protected void onDestroy() {
        if (mAudioPlayer.isPlaying()) {
            mAudioPlayer.stopPlayback();
        }
        updateUIPlayPause();
        mAudioPlayer.release();
        super.onDestroy();
    }

    private void updateUIPlayPause() {
        boolean isPlaying = mAudioPlayer.isPlaying();
        int resId = isPlaying ? R.drawable.exomedia_ic_pause_white
                : R.drawable.exomedia_ic_play_arrow_white;
        mAudioPlayerPlayPause.setImageResource(resId);
        if (isPlaying) {
            mAudioPlayerImage.start();
        } else {
            mAudioPlayerImage.stop();
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
        if (!mAudioPlayer.isPlaying()) {
            mAudioPlayerLoading.setVisibility(View.GONE);
            mAudioPlayer.start();
            updateUIPlayPause();
        }
    }

    @Override
    protected void onMediaPause() {
        if (mAudioPlayer.isPlaying()) {
            mAudioPlayer.pause();
        }
    }

    @Override
    public void onBufferingUpdate(int percent) {
        long duration = mAudioPlayer.getDuration();
        if (duration > 0 && mShouldSetDuration) {
            mShouldSetDuration = false;
            mAudioPlayerSeek.setMax((int) duration);
            mAudioPlayerDuration.setText(MediaUtils.formatMs(duration));
        }
        int bufferProgress = (int) (mAudioPlayer.getDuration() * percent);
        mAudioPlayerSeek.setSecondaryProgress(bufferProgress);
        mAudioPlayerSeek.setProgress((int) mAudioPlayer.getCurrentPosition());
        mAudioPlayerPosition.setText(MediaUtils.formatMs(mAudioPlayer.getCurrentPosition()));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        mAudioPlayerPosition.setText(MediaUtils.formatMs(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        mAudioPlayer.seekTo(progress);
    }

    @Override
    public void onCompletion() {
        mAudioPlayer.seekTo(0);
        updateUIPlayPause();
    }

    private class PhotoViewTarget extends DrawableImageViewTarget {
        PhotoViewTarget(ImageView view) {
            super(view);
        }

        @Override
        public void onResourceReady(@NonNull Drawable resource,
                                    @Nullable Transition<? super Drawable> transition) {
            super.onResourceReady(resource, transition);
            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
            if (bitmap != null) {
                Palette.from(bitmap).generate(palette -> {
                    Palette.Swatch swatch = palette.getLightVibrantSwatch();
                    if (swatch != null) {
                        mMainContainer.setBackgroundColor(swatch.getRgb());
                    }
                    swatch = palette.getDarkVibrantSwatch();
                    if (swatch != null) {
                        mControlsContainer.setBackgroundColor(swatch.getRgb());
                    }
                });
            }
        }
    }
}
