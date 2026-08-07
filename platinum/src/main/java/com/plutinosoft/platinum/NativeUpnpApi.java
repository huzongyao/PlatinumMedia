package com.plutinosoft.platinum;

import androidx.annotation.Keep;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * JNI wrapper around the Platinum UPnP native stack.
 *
 * <p>Two roles are supported:
 * <ul>
 *     <li><b>DMR (renderer / server)</b>: {@link #addRenderer} exposes this device as a
 *     DLNA MediaRenderer. AVTransport/RenderingControl commands received from controllers
 *     are delivered to {@link NativeCallable#onEvent(String)} as JSON of the form
 *     {@code {"uuid":..,"action":"Play"|"Stop"|"SetAVTransportURI"|...,"CurrentURI":..}}.
 *     <li><b>DMC (controller / client)</b>: {@link #addController} starts device discovery.
 *     Discovered renderers and action results are delivered to {@link NativeCallable#onEvent}
 *     as {@code {"type":"device","action":"added"|"removed","uuid":..,"name":..}} and
 *     {@code {"type":"action","action":..,"code":..}}.
 * </ul>
 */
@Keep
public enum NativeUpnpApi {
    INSTANCE;

    static {
        System.loadLibrary("platinum");
    }

    private long mInstanceId = 0;
    private NativeCallable mCallable = null;

    public interface NativeCallable {
        /** Called from native (any thread). Return value is currently ignored. */
        String onEvent(String param);
    }

    public interface NativeDelegate {
        void onRendererEvent(RendererEvent event);

        void onDeviceEvent(DeviceEvent event);

        void onActionResult(String action, int code);
    }

    /** AVTransport / RenderingControl command received by the DMR. */
    public static class RendererEvent {
        public String uuid;
        public String action;
        public String instanceId;
        public String currentUri;
        public String currentUriMetaData;
        public String speed;
        public String target;
        public String unit;
        public String channel;
    }

    /** A renderer discovered (or lost) by the DMC. */
    public static class DeviceEvent {
        public String action; // "added" | "removed" | "ready"
        public String uuid;
        public String name;
    }

    public NativeUpnpApi init(NativeCallable callable) {
        mCallable = callable;
        if (mInstanceId == 0) {
            mInstanceId = nInit();
            nSetCallback(mInstanceId, callable);
        }
        return this;
    }

    public long getInstanceId() {
        return mInstanceId;
    }

    /** Expose this device as a DLNA MediaRenderer. */
    public int addRenderer(String friendlyName, boolean showIp, String uuid, int port) {
        JSONObject json = new JSONObject();
        try {
            json.put("friendly_name", friendlyName);
            json.put("show_ip", showIp);
            json.put("uuid", uuid);
            json.put("port", port);
            json.put("port_rebind", false);
        } catch (JSONException ignore) {
        }
        return nAddRenderer(mInstanceId, json.toString());
    }

    /** Start a DLNA control point that discovers renderers. */
    public int addController(boolean showIp, int port) {
        JSONObject json = new JSONObject();
        try {
            json.put("show_ip", showIp);
            json.put("port", port);
            json.put("port_rebind", false);
        } catch (JSONException ignore) {
        }
        return nAddController(mInstanceId, json.toString());
    }

    /** Send a control command to a discovered renderer (DMC side). */
    public int invokeAction(JSONObject action) {
        return nInvokeAction(mInstanceId, action.toString());
    }

    public int start() {
        return nStart(mInstanceId);
    }

    public int stop() {
        return nStop(mInstanceId);
    }

    /** Parse a native event JSON and dispatch to the appropriate delegate callback. */
    public static void dispatch(String json, NativeDelegate delegate) {
        if (json == null || delegate == null) return;
        try {
            JSONObject obj = new JSONObject(json);
            if (obj.has("type")) {
                String type = obj.getString("type");
                if ("device".equals(type)) {
                    DeviceEvent ev = new DeviceEvent();
                    ev.action = obj.optString("action");
                    ev.uuid = obj.optString("uuid");
                    ev.name = obj.optString("name");
                    delegate.onDeviceEvent(ev);
                } else if ("action".equals(type)) {
                    delegate.onActionResult(obj.optString("action"), obj.optInt("code"));
                }
            } else if (obj.has("action")) {
                RendererEvent ev = new RendererEvent();
                ev.uuid = obj.optString("uuid");
                ev.action = obj.optString("action");
                ev.instanceId = obj.optString("InstanceID", "0");
                ev.currentUri = obj.optString("CurrentURI");
                ev.currentUriMetaData = obj.optString("CurrentURIMetaData");
                ev.speed = obj.optString("Speed", "1");
                ev.target = obj.optString("Target");
                ev.unit = obj.optString("Unit", "REL_TIME");
                ev.channel = obj.optString("Channel", "Master");
                delegate.onRendererEvent(ev);
            }
        } catch (JSONException e) {
            // ignore malformed events
        }
    }

    public static native String getVersionString();

    private static native long nInit();

    private static native int nSetCallback(long iid, NativeCallable callable);

    private static native int nAddRenderer(long iid, String param);

    private static native int nAddController(long iid, String param);

    private static native int nInvokeAction(long iid, String param);

    private static native int nStart(long iid);

    private static native int nStop(long iid);
}
