package com.plutinosoft.platinum;

import android.util.Log;

/**
 * Created by huzongyao on 2018/6/6.
 * To connect to the native through jni
 */

public class DLNABridge {

    private static final String TAG = "DLNAServer";
    private long mInstanceId;
    private static DLNACallback mCallback;

    public DLNABridge() {
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

    public int destroy() {
        if (mInstanceId != 0L) {
            int ret = nDestroy(mInstanceId);
            mInstanceId = 0L;
            return ret;
        }
        return NtpResult.NPT_ERROR_INVALID_STATE;
    }

    public void setCallback(DLNACallback callback) {
        mCallback = callback;
    }

    /**
     * Called By native
     *
     * @param type   event type
     * @param param1 param1
     * @param param2 param2
     */
    @SuppressWarnings("UnusedDeclaration")
    private static void onNEvent(int type, String param1, String param2, String param3) {
        if (mCallback != null) {
            mCallback.onEvent(type, param1, param2, param3);
        }
    }

    private static native long nInit();

    private static native int nStart(long self, String friendly_name, boolean show_ip,
                                     String uuid, long port, boolean port_rebind);

    private static native int nExecute(long self, int cmd, String param1, String param2,
                                       String param3);

    private static native int nStop(long self);

    private static native int nDestroy(long self);

    static {
        System.loadLibrary("platinum-jni");
    }
}