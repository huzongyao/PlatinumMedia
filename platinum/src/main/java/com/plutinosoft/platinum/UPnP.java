package com.plutinosoft.platinum;

public class UPnP {

    private final long cSelf;

    public UPnP() {
        cSelf = _init();
    }

    public int start() {
        return _start(cSelf);
    }

    public int stop() {
        return _stop(cSelf);
    }

    // C glue
    private static native long _init();

    private static native int _start(long self);

    private static native int _stop(long self);

    static {
        System.loadLibrary("platinum-jni");
    }
}
