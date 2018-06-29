package com.hzy.platinum.media.event;

import com.hzy.platinum.media.media.MediaInfo;

/**
 * Created by huzongyao on 2018/6/22.
 * native events, post on work thread
 */

public class NativeAsyncEvent {
    public int type;
    public String param1;
    public String param2;
    public String param3;
    public MediaInfo mediaInfo;

    public NativeAsyncEvent(int type, String paran1, String param2, String param3) {
        this.type = type;
        this.param1 = paran1;
        this.param2 = param2;
        this.param3 = param3;
    }

    public NativeAsyncEvent(int type, MediaInfo mediaInfo) {
        this.type = type;
        this.mediaInfo = mediaInfo;
    }
}
