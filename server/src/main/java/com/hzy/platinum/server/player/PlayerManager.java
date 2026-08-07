package com.hzy.platinum.server.player;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Plays media pushed by a DLNA controller. State is driven entirely by the
 * renderer events delivered from the native stack ({@code SetAVTransportURI},
 * {@code Play}, {@code Pause}, {@code Stop}, {@code Seek}, {@code SetVolume}).
 *
 * <p>Three media types are supported:
 * <ul>
 *     <li>{@link MediaType#VIDEO} - decoded onto a {@link android.view.Surface}.</li>
 *     <li>{@link MediaType#AUDIO} - played without a surface.</li>
 *     <li>{@link MediaType#IMAGE} - fetched and surfaced as a {@link Bitmap}.</li>
 * </ul>
 */
public class PlayerManager implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnBufferingUpdateListener {

    public interface StateListener {
        void onMediaType(MediaType type);

        void onUri(String uri);

        void onStateChanged(String state);

        void onDuration(int durationMs);

        void onProgress(int positionMs);

        /** Called with the decoded bitmap for {@link MediaType#IMAGE} media. */
        void onImage(Bitmap bitmap);

        void onError(String message);
    }

    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private final Runnable mProgressTask = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                int pos = mMediaPlayer.getCurrentPosition();
                if (mListener != null) mListener.onProgress(pos);
            }
            mUiHandler.postDelayed(this, 500);
        }
    };

    @Nullable
    private MediaPlayer mMediaPlayer;
    @Nullable
    private android.view.Surface mSurface;
    private String mCurrentUri;
    private MediaType mType = MediaType.UNKNOWN;
    private StateListener mListener;

    public void setSurface(android.view.Surface surface) {
        mSurface = surface;
        if (mMediaPlayer != null && mType != MediaType.AUDIO) {
            mMediaPlayer.setSurface(surface);
        }
    }

    public void setListener(StateListener listener) {
        mListener = listener;
    }

    public void setAvTransportUri(String uri, MediaType type) {
        mCurrentUri = uri;
        mType = type == null ? MediaType.UNKNOWN : type;
        resetInternal();
        if (mListener != null) {
            mListener.onMediaType(mType);
            mListener.onUri(uri);
            mListener.onStateChanged("STOPPED");
        }
        // For images we can resolve the bitmap immediately (deferred to a worker thread).
        if (mType == MediaType.IMAGE) {
            loadImage(uri);
        }
    }

    public void play() {
        if (mCurrentUri == null || mCurrentUri.isEmpty()) {
            notifyError("没有可播放的媒体");
            return;
        }
        if (mType == MediaType.IMAGE) {
            // Nothing to "play" for an image beyond showing it; treat as PLAYING.
            notifyState("PLAYING");
            return;
        }
        if (mMediaPlayer == null) {
            createAndPrepare(mCurrentUri);
        } else if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            startProgress();
            notifyState("PLAYING");
        }
    }

    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            notifyState("PAUSED_PLAYBACK");
        }
    }

    public void stop() {
        resetInternal();
        notifyState("STOPPED");
    }

    public void seekTo(int positionMs) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(positionMs);
        }
    }

    public void setVolume(float volume) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    public MediaType getType() {
        return mType;
    }

    private void createAndPrepare(String uri) {
        mMediaPlayer = new MediaPlayer();
        if (mType != MediaType.AUDIO && mSurface != null) {
            mMediaPlayer.setSurface(mSurface);
        }
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        try {
            mMediaPlayer.setDataSource(mCurrentUri);
            mMediaPlayer.prepareAsync();
            notifyState("TRANSITIONING");
        } catch (IOException e) {
            notifyError(e.getMessage());
        }
    }

    private void loadImage(String uri) {
        new Thread(() -> {
            try {
                URL url = new URL(uri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
                conn.connect();
                try (InputStream is = conn.getInputStream()) {
                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
                    if (bitmap != null && mListener != null) {
                        mUiHandler.post(() -> mListener.onImage(bitmap));
                    } else if (mListener != null) {
                        mUiHandler.post(() -> mListener.onError("图片解码失败"));
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                if (mListener != null) {
                    mUiHandler.post(() -> mListener.onError("图片加载失败：" + e.getMessage()));
                }
            }
        }).start();
    }

    private void resetInternal() {
        mUiHandler.removeCallbacks(mProgressTask);
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.release();
            } catch (Exception ignore) {
            }
            mMediaPlayer = null;
        }
    }

    private void startProgress() {
        mUiHandler.removeCallbacks(mProgressTask);
        mUiHandler.postDelayed(mProgressTask, 500);
    }

    private void notifyState(String state) {
        if (mListener != null) mListener.onStateChanged(state);
    }

    private void notifyError(String message) {
        if (mListener != null) mListener.onError(message);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mListener != null) mListener.onDuration(mp.getDuration());
        mp.start();
        startProgress();
        notifyState("PLAYING");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        notifyState("STOPPED");
        mUiHandler.removeCallbacks(mProgressTask);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        notifyError("播放错误 what=" + what + " extra=" + extra);
        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        // percent is of the buffered portion of the stream
    }

    public void release() {
        resetInternal();
    }
}
