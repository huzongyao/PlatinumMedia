package com.hzy.platinum.media.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.hzy.platinum.media.R;
import com.hzy.platinum.media.service.MediaRendererService;

/**
 * Created by huzongyao on 2018/6/6.
 */

public class SimpleActivity extends AppCompatActivity {

    private Button mButtonStart;
    private boolean mIsRunning = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        mButtonStart = findViewById(R.id.button_start);
        mButtonStart.setOnClickListener(v -> {
            if (mIsRunning) {
                Intent intent = new Intent(this, MediaRendererService.class);
                stopService(intent);
                mButtonStart.setText("Start");
                mIsRunning = false;
            } else {
                Intent intent = new Intent(this, MediaRendererService.class);
                startService(intent);
                mButtonStart.setText("Stop");
                mIsRunning = true;
            }
        });
    }
}
