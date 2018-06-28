package com.hzy.platinum.media.event;

/**
 * Created by huzongyao on 2018/6/22.
 * native events, post on work thread
 */

public class NativeAsyncEvent {
    private int type;
    private String param1;
    private String param2;
    private String param3;

    public NativeAsyncEvent(int type, String paran1, String param2, String param3) {
        this.type = type;
        this.param1 = paran1;
        this.param2 = param2;
        this.param3 = param3;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }
}
