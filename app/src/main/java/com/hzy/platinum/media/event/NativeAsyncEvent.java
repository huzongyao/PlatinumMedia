package com.hzy.platinum.media.event;

/**
 * Created by huzongyao on 2018/6/22.
 * native events, post on work thread
 */

public class NativeAsyncEvent {
    private int type;
    private String paran1;
    private String param2;

    public NativeAsyncEvent(int type, String paran1, String param2) {
        this.type = type;
        this.paran1 = paran1;
        this.param2 = param2;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getParan1() {
        return paran1;
    }

    public void setParan1(String paran1) {
        this.paran1 = paran1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }
}
