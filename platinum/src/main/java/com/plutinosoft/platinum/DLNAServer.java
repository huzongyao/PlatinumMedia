package com.plutinosoft.platinum;

import android.util.Log;

/**
 * Created by huzongyao on 2018/6/6.
 */

public class DLNAServer {

    private static final String TAG = "DLNAServer";
    private long mInstanceId;

    public DLNAServer() {
        mInstanceId = nInit();
        if (mInstanceId == 0L) {
            Log.e(TAG, "Server init failed!");
        }
    }

    public int start(ServerParams params) {
        return start(params.getFriendlyName(), params.isShowIp(), params.getUuid());
    }

    public int start(String friendlyName, boolean showIp, String uuid) {
        return start(friendlyName, showIp, uuid, 0, false);
    }

    public int start(String friendlyName, boolean showIp, String uuid,
                     long port, boolean portRebind) {
        if (mInstanceId != 0L) {
            return nStart(mInstanceId, friendlyName, showIp, uuid, port, portRebind);
        }
        return NtpResult.NPT_ERROR_INVALID_STATE;
    }

    public int stop() {
        if (mInstanceId != 0L) {
            return nStop(mInstanceId);
        }
        return NtpResult.NPT_ERROR_INVALID_STATE;
    }

    public int destory() {
        if (mInstanceId != 0L) {
            int ret = nDestory(mInstanceId);
            mInstanceId = 0L;
            return ret;
        }
        return NtpResult.NPT_ERROR_INVALID_STATE;
    }

    /*public int command() {
        return nCommand(mInstanceId);
    }*/

    private native long nInit();

    private native int nStart(long self, String friendly_name, boolean show_ip,
                              String uuid, long port, boolean port_rebind);

    private native int nStop(long self);

    private native int nDestory(long self);

    //private native int nCommand(long self);

    static {
        System.loadLibrary("platinum-jni");
    }
}