package com.plutinosoft.platinum;

import android.util.Log;

/**
 * Created by huzongyao on 2018/6/6.
 */

public class DLNAServer {

    private static final String TAG = "DLNAServer";
    private final long mInstanceId;

    public DLNAServer() {
        mInstanceId = nInit();
        if (mInstanceId == 0L) {
            Log.e(TAG, "Server init failed!");
        }
    }

    public int start(String friendlyName, boolean showIp, String uuid) {
        return start(friendlyName, showIp, uuid, 0, false);
    }

    public int start(String friendlyName, boolean showIp, String uuid,
                     long port, boolean portRebind) {
        if (mInstanceId != 0L) {
            return nStart(mInstanceId, friendlyName, showIp, uuid, port, portRebind);
        }
        return -1;
    }

    public int stop() {
        if (mInstanceId != 0L) {
            return nStop(mInstanceId);
        }
        return -1;
    }

    /*public int command() {
        return nCommand(mInstanceId);
    }*/

    private native long nInit();

    private native int nStart(long self, String friendly_name, boolean show_ip,
                              String uuid, long port, boolean port_rebind);

    private native int nStop(long self);

    //private native int nCommand(long self);

    static {
        System.loadLibrary("platinum-jni");
    }
}