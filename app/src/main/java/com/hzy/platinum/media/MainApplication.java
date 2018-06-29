package com.hzy.platinum.media;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.hzy.platinum.media.media.MediaUtils;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by huzongyao on 2018/6/6.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        Utils.init(this);
        MediaUtils.configureExoMedia(this);
    }
}
