package com.hzy.platinum.server.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.core.app.NotificationCompat;

import com.hzy.platinum.server.activity.MainActivity;
import com.hzy.platinum.server.R;
import com.hzy.platinum.server.player.MediaType;
import com.hzy.platinum.server.player.PlayerManager;
import com.hzy.platinum.server.utils.UUIDUtils;
import com.plutinosoft.platinum.NativeUpnpApi;

/**
 * Foreground service hosting the DLNA MediaRenderer (DMR).
 *
 * <ul>
 *     <li>Initializes the native UPnP stack and exposes this device as a renderer.</li>
 *     <li>Acquires a WiFi multicast lock so SSDP discovery works on locked/doze devices.</li>
 *     <li>Forwards renderer events to the bound {@link MainActivity} which drives the player.</li>
 * </ul>
 */
public class DLNAService extends Service {

    private static final String CHANNEL_ID = "dlna_renderer";
    private static final int NOTIFICATION_ID = 1001;

    public static final String ACTION_START = "com.hzy.platinum.server.action.START";
    public static final String ACTION_STOP = "com.hzy.platinum.server.action.STOP";

    private final IBinder mBinder = new LocalBinder();
    private WifiManager.MulticastLock mMulticastLock;
    private NativeUpnpApi mApi;
    private PlayerManager mPlayer;
    private String mDeviceUuid;
    private boolean mStarted;
    private final android.os.Handler mMainHandler = new android.os.Handler(Looper.getMainLooper());

    public class LocalBinder extends Binder {
        public DLNAService getService() {
            return DLNAService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }
        startRenderer();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDeviceUuid = UUIDUtils.getCachedUuid(this);
        createNotificationChannel();
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            mMulticastLock = wifi.createMulticastLock("dlna_renderer_lock");
            mMulticastLock.setReferenceCounted(false);
            mMulticastLock.acquire();
        }
        mPlayer = new PlayerManager();
    }

    @Override
    public void onDestroy() {
        if (mStarted && mApi != null) {
            mApi.stop();
        }
        if (mMulticastLock != null && mMulticastLock.isHeld()) {
            mMulticastLock.release();
        }
        if (mPlayer != null) {
            mPlayer.release();
        }
        super.onDestroy();
    }

    public NativeUpnpApi getApi() {
        return mApi;
    }

    public PlayerManager getPlayer() {
        return mPlayer;
    }

    public void setDeviceUuid(String uuid) {
        mDeviceUuid = uuid;
    }

    public String getDeviceUuid() {
        return mDeviceUuid;
    }

    private void startRenderer() {
        if (mStarted) return;
        mApi = NativeUpnpApi.INSTANCE.init(param -> {
            // Native callback is delivered on a native thread; dispatch later.
            NativeUpnpApi.dispatch(param, mRendererDelegate);
            return null;
        });
        int ret = mApi.addRenderer(buildName(), true, mDeviceUuid, 0);
        int startRet = mApi.start();
        mStarted = (ret == 0 && startRet == 0);
        startForeground(NOTIFICATION_ID, buildNotification(), foregroundType());
    }

    private NativeUpnpApi.NativeDelegate mRendererDelegate = new NativeUpnpApi.NativeDelegate() {
        @Override
        public void onRendererEvent(NativeUpnpApi.RendererEvent event) {
            final NativeUpnpApi.RendererEvent ev = event;
            mMainHandler.post(() -> handleRendererEvent(ev));
        }

        @Override
        public void onDeviceEvent(NativeUpnpApi.DeviceEvent event) {
            // not used on the renderer side
        }

        @Override
        public void onActionResult(String action, int code) {
            // not used on the renderer side
        }
    };

    private void handleRendererEvent(NativeUpnpApi.RendererEvent event) {
        if (mPlayer == null) return;
        switch (event.action) {
            case "SetAVTransportURI":
                if (event.currentUri != null && !event.currentUri.isEmpty()) {
                    MediaType type = MediaType.detect(event.currentUri, event.currentUriMetaData);
                    mPlayer.setAvTransportUri(event.currentUri, type);
                }
                break;
            case "Play":
                mPlayer.play();
                break;
            case "Pause":
                mPlayer.pause();
                break;
            case "Stop":
                mPlayer.stop();
                break;
            case "Seek":
                // event.target is "HH:MM:SS" or milliseconds; parse best-effort
                mPlayer.seekTo(parseTarget(event.target));
                break;
            case "SetVolume":
                try {
                    int vol = Integer.parseInt(event.target); // Target carries DesiredVolume
                    mPlayer.setVolume(vol / 100f);
                } catch (NumberFormatException ignore) {
                }
                break;
            case "SetMute":
                try {
                    boolean mute = Boolean.parseBoolean(event.target);
                    mPlayer.setVolume(mute ? 0f : 1f);
                } catch (Exception ignore) {
                }
                break;
            default:
                break;
        }
    }

    private static int parseTarget(String target) {
        if (target == null || target.isEmpty()) return 0;
        // "HH:MM:SS" form
        String[] parts = target.split(":");
        if (parts.length == 3) {
            try {
                int h = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]);
                int s = Integer.parseInt(parts[2]);
                return (h * 3600 + m * 60 + s) * 1000;
            } catch (NumberFormatException ignore) {
            }
        }
        try {
            return Integer.parseInt(target); // assume milliseconds
        } catch (NumberFormatException ignore) {
            return 0;
        }
    }

    private String buildName() {
        return "Platinum DMR " + (mDeviceUuid == null ? "" : mDeviceUuid.substring(0, Math.min(4, mDeviceUuid.length())));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, getString(R.string.channel_name), NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(getString(R.string.notification_text));
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(pi)
                .build();
    }

    private int foregroundType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK;
        }
        return 0;
    }
}
