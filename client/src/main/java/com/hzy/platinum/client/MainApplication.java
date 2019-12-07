package com.hzy.platinum.client;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
