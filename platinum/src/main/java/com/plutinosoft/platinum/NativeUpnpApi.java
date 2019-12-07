package com.plutinosoft.platinum;

import org.json.JSONObject;

public enum NativeUpnpApi implements NativeCallable {
    INSTANCE;

    private volatile long mInstanceId = 0;
    private NativeDelegate mDelegate;

    /**
     * the delegate will process the native event
     *
     * @param d delegate
     */
    public synchronized void setDelegate(NativeDelegate d) {
        mDelegate = d;
    }

    public void removeDelegate() {
        mDelegate = null;
    }

    /**
     * this interface should be called by native
     *
     * @param param param
     * @return result
     */
    @Override
    public final String onEvent(String param) {
        if (mDelegate != null) {
            try {
                return mDelegate.onEvent(param);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * init the instance
     */
    public synchronized void init() {
        if (mInstanceId == 0) {
            mInstanceId = nInit();
            nSetCallback(mInstanceId, this);
        }
    }

    public void addRenderer(String name, boolean showIp, String uuid, int port) {
        if (mInstanceId != 0) {
            try {
                JSONObject jObj = new JSONObject();
                jObj.put("friendly_name", name);
                jObj.put("show_ip", showIp);
                jObj.put("uuid", uuid);
                jObj.put("port", port);
                jObj.put("port_rebind", false);
                nAddRenderer(mInstanceId, jObj.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        if (mInstanceId != 0) {
            nStart(mInstanceId);
        }
    }

    public void stop() {
        if (mInstanceId != 0) {
            nStop(mInstanceId);
        }
    }

    private static native int nStart(long iid);

    private static native int nStop(long iid);

    private static native int nSetCallback(long iid, NativeCallable callable);

    public static native int nAddRenderer(long iid, String param);

    private static native long nInit();

    public static native String getVersionString();

    static {
        System.loadLibrary("platinum");
    }
}
