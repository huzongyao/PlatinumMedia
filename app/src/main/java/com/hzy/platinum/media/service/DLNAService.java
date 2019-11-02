package com.hzy.platinum.media.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import com.hzy.platinum.media.R;
import com.hzy.platinum.media.activity.MainActivity;
import com.hzy.platinum.media.event.NativeAsyncEvent;
import com.hzy.platinum.media.instance.NotificationHelper;
import com.hzy.platinum.media.instance.ServerInstance;
import com.hzy.platinum.media.media.MediaUtils;
import com.plutinosoft.platinum.CallbackTypes;
import com.plutinosoft.platinum.ServerParams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huzongyao on 2018/6/7.
 * The service that manage the server instance
 */

public class DLNAService extends Service {

    public static final String EXTRA_SERVER_PARAMS = "EXTRA_SERVER_PARAMS";

    private static final String TAG = "DLNAService";
    private WifiManager.MulticastLock mMulticastLock;
    private Notification mNotification;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        acquireMulticastLock();
        buildNotification();
        EventBus.getDefault().register(this);
    }

    private void buildNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        mNotification = NotificationHelper.INSTANCE
                .getNotification(intent, getString(R.string.server_notification_title),
                        getString(R.string.server_notification_text));
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
        if (intent != null) {
            ServerParams params = intent.getParcelableExtra(EXTRA_SERVER_PARAMS);
            if (params != null) {
                ServerInstance.INSTANCE.start(params);
                NotificationHelper.INSTANCE.notify(mNotification);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressWarnings("UnusedDeclaration")
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onServerStateChange(NativeAsyncEvent event) {
        switch (event.type) {
            case CallbackTypes.CALLBACK_EVENT_ON_PLAY:
                MediaUtils.startPlayMedia(this, event.mediaInfo);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (mMulticastLock != null) {
            mMulticastLock.release();
            mMulticastLock = null;
        }
        EventBus.getDefault().unregister(this);
        ServerInstance.INSTANCE.stop();
        NotificationHelper.INSTANCE.cancel();
        super.onDestroy();
    }
}
