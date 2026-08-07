package com.hzy.platinum.server.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hzy.platinum.server.R;
import com.hzy.platinum.server.player.MediaType;
import com.hzy.platinum.server.player.PlayerManager;
import com.hzy.platinum.server.service.DLNAService;

public class MainActivity extends AppCompatActivity implements PlayerManager.StateListener {

    private TextView mStatusText;
    private TextView mUriText;
    private TextView mTypeText;
    private SurfaceView mSurfaceView;
    private ImageView mImageView;
    private TextView mAudioPlaceholder;
    private SeekBar mSeekBar;
    private SeekBar mVolumeBar;

    private DLNAService mService;
    private boolean mBound = false;
    private int mDuration = 0;

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            DLNAService.LocalBinder local = (DLNAService.LocalBinder) binder;
            mService = local.getService();
            mBound = true;
            mService.getPlayer().setSurface(mSurfaceView.getHolder().getSurface());
            mService.getPlayer().setListener(MainActivity.this);
            mStatusText.setText(R.string.status_running);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatusText = findViewById(R.id.text_status);
        mUriText = findViewById(R.id.text_uri);
        mTypeText = findViewById(R.id.text_type);
        mSurfaceView = findViewById(R.id.surface_view);
        mImageView = findViewById(R.id.image_view);
        mAudioPlaceholder = findViewById(R.id.audio_placeholder);
        mSeekBar = findViewById(R.id.seek_bar);
        mVolumeBar = findViewById(R.id.volume_bar);

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (mBound && mService != null) {
                    mService.getPlayer().setSurface(holder.getSurface());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });

        ((Button) findViewById(R.id.btn_play)).setOnClickListener(v -> {
            if (mBound) mService.getPlayer().play();
        });
        ((Button) findViewById(R.id.btn_pause)).setOnClickListener(v -> {
            if (mBound) mService.getPlayer().pause();
        });
        ((Button) findViewById(R.id.btn_stop)).setOnClickListener(v -> {
            if (mBound) mService.getPlayer().stop();
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mBound && mDuration > 0) {
                    int pos = (int) (progress / 1000f * mDuration);
                    mService.getPlayer().seekTo(pos);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mBound) {
                    mService.getPlayer().setVolume(progress / 100f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, DLNAService.class);
        intent.setAction(DLNAService.ACTION_START);
        startForegroundService(intent);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onMediaType(MediaType type) {
        runOnUiThread(() -> applyUiForType(type));
    }

    @Override
    public void onUri(String uri) {
        runOnUiThread(() -> mUriText.setText(getString(R.string.current_media, uri)));
    }

    @Override
    public void onStateChanged(String state) {
        runOnUiThread(() -> mStatusText.setText(
                getString(R.string.status_running) + " [" + state + "]"));
    }

    @Override
    public void onDuration(int durationMs) {
        mDuration = durationMs;
    }

    @Override
    public void onProgress(int positionMs) {
        if (mDuration > 0) {
            final int progress = (int) (positionMs / (float) mDuration * 1000);
            runOnUiThread(() -> mSeekBar.setProgress(progress));
        }
    }

    @Override
    public void onImage(Bitmap bitmap) {
        runOnUiThread(() -> mImageView.setImageBitmap(bitmap));
    }

    @Override
    public void onError(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    private void applyUiForType(MediaType type) {
        boolean hasTimeline = (type == MediaType.VIDEO || type == MediaType.AUDIO);
        mSeekBar.setVisibility(hasTimeline ? View.VISIBLE : View.GONE);

        switch (type) {
            case VIDEO:
                mTypeText.setText(getString(R.string.media_type_video));
                mSurfaceView.setVisibility(View.VISIBLE);
                mImageView.setVisibility(View.GONE);
                mAudioPlaceholder.setVisibility(View.GONE);
                break;
            case AUDIO:
                mTypeText.setText(getString(R.string.media_type_audio));
                mSurfaceView.setVisibility(View.GONE);
                mImageView.setVisibility(View.GONE);
                mAudioPlaceholder.setVisibility(View.VISIBLE);
                break;
            case IMAGE:
                mTypeText.setText(getString(R.string.media_type_image));
                mSurfaceView.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
                mAudioPlaceholder.setVisibility(View.GONE);
                break;
            default:
                mTypeText.setText("");
                mSurfaceView.setVisibility(View.VISIBLE);
                mImageView.setVisibility(View.GONE);
                mAudioPlaceholder.setVisibility(View.GONE);
                break;
        }
    }
}
