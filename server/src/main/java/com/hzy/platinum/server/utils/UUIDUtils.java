package com.hzy.platinum.server.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by huzongyao on 2018/6/12.
 * Generates and caches a stable device UUID so the renderer keeps the same
 * UPnP UDN across restarts (DLNA controllers remember the device by UDN).
 */
public class UUIDUtils {

    private static final String PREFS = "dlna_prefs";
    private static final String KEY_UUID = "device_uuid";

    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getCachedUuid(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String uuid = sp.getString(KEY_UUID, null);
        if (uuid == null) {
            uuid = getRandomUUID();
            sp.edit().putString(KEY_UUID, uuid).apply();
        }
        return uuid;
    }
}
