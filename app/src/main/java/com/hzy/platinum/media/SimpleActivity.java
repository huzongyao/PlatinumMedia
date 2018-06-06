package com.hzy.platinum.media;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.plutinosoft.platinum.UPnP;

/**
 * Created by huzongyao on 2018/6/6.
 */

public class SimpleActivity extends AppCompatActivity {

    private Button mButtonStart;
    private UPnP mUpnP;
    private boolean mIsRunning = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        mButtonStart = findViewById(R.id.button_start);
        mUpnP = new UPnP();
        mButtonStart.setOnClickListener(v -> {
            if (mIsRunning) {
                mUpnP.stop();
                mButtonStart.setText("Start");
                mIsRunning = false;
            } else {
                int result = mUpnP.start();
                if (result == 0) {
                    mButtonStart.setText("Stop");
                    mIsRunning = true;
                }
            }
        });
    }
}
