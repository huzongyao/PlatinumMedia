package com.hzy.platinum.media.instance;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.Utils;
import com.hzy.platinum.media.R;

/**
 * Created by huzongyao on 2018/6/8.
 * To manage the notifications
 */

public enum NotificationHelper {

    INSTANCE;

    public static final String PRIMARY_CHANNEL = "default";
    private static final int DEFAULT_NOTIFICATION_ID = 10001;
    private NotificationManager mManager;

    NotificationHelper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Context context = Utils.getApp();
            NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL,
                    context.getString(R.string.notification_channel_default),
                    NotificationManager.IMPORTANCE_DEFAULT);
            getManager().createNotificationChannel(channel);
        }
    }

    public Notification getNotification(Intent intent, String title, String body) {
        PendingIntent pendingIntent = PendingIntent.getActivity(Utils.getApp(),
                0, intent, 0);
        return new NotificationCompat.Builder(Utils.getApp(), PRIMARY_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOngoing(true)
                .build();
    }

    public void notify(Notification notification) {
        getManager().notify(DEFAULT_NOTIFICATION_ID, notification);
    }

    public void cancel() {
        getManager().cancel(DEFAULT_NOTIFICATION_ID);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager)
                    Utils.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
}
