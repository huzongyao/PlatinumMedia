package com.hzy.platinum.media.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hzy.platinum.media.activity.MainActivity;
import com.hzy.platinum.media.instance.NotificationHelper;

/**
 * Created by huzongyao on 2018/6/7.
 */

public class MediaRendererService extends Service {

    private static final String TAG = "MediaRendererService";
    private WifiManager.MulticastLock mMulticastLock;
    private Notification mNotification;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        acquireMulticastLock();
        buildNotification();
    }

    private void buildNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        mNotification = NotificationHelper.INSTANCE
                .getNotification(intent, "title", "content");
    }

    private void acquireMulticastLock() {
        WifiManager wifiManager = (WifiManager)
                getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            mMulticastLock = wifiManager.createMulticastLock(TAG);
            mMulticastLock.acquire();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationHelper.INSTANCE.notify(mNotification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mMulticastLock != null) {
            mMulticastLock.release();
            mMulticastLock = null;
        }
        NotificationHelper.INSTANCE.cancel();
        super.onDestroy();
    }
}
