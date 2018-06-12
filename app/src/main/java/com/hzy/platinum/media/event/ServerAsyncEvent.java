package com.hzy.platinum.media.event;

/**
 * Created by huzongyao on 2018/6/11.
 * To send command from main thread to the work thread
 */

public class ServerAsyncEvent {

    public static final int EVENT_START = 0x0001;
    public static final int EVENT_STOP = 0x0010;

    private int type;
    private Object param;

    public ServerAsyncEvent(int type) {
        this.type = type;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
