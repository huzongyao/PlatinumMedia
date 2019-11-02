package com.hzy.platinum.media.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.devbrackets.android.exomedia.listener.OnBufferUpdateListener;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.hzy.platinum.media.event.NativeAsyncEvent;
import com.hzy.platinum.media.media.MediaInfo;
import com.hzy.platinum.media.media.MediaUtils;
import com.plutinosoft.platinum.CallbackTypes;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huzongyao on 2018/7/4.
 */

@SuppressLint("Registered")
public abstract class BasePlayActivity extends AppCompatActivity
        implements OnPreparedListener, OnCompletionListener,
        OnBufferUpdateListener {

    protected MediaInfo mMediaInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        parseIntent(getIntent());
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(intent);
        setCurrentMediaAndPlay();
    }

    private void parseIntent(Intent intent) {
        mMediaInfo = intent.getParcelableExtra(MediaUtils.EXTRA_MEDIA_INFO);
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

    @SuppressWarnings("UnusedDeclaration")
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onServerStateChange(NativeAsyncEvent event) {
        switch (event.type) {
            case CallbackTypes.CALLBACK_EVENT_ON_PAUSE:
                onMediaPause();
                break;
            case CallbackTypes.CALLBACK_EVENT_ON_PLAY:
                break;
            case CallbackTypes.CALLBACK_EVENT_ON_SET_VOLUME:
                Log.e("TAG", "" + event.param1);
                break;
            default:
                break;
        }
    }

    protected void onMediaPause() {
    }

    @Override
    public void onBufferingUpdate(int percent) {
    }

    @Override
    public void onCompletion() {
    }

    @Override
    public void onPrepared() {
    }

    /**
     * Set current media source and start to play
     */
    abstract void setCurrentMediaAndPlay();
}
