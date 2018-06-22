package com.hzy.platinum.media.instance;

import com.hzy.platinum.media.event.NativeAsyncEvent;
import com.plutinosoft.platinum.DLNACallback;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huzongyao on 2018/6/21.
 * To receive the messages from native
 */

public enum CallbackInstance {

    INSTANCE;

    private final DLNACallback mCallback;

    CallbackInstance() {
        mCallback = (type, param1, param2) ->
                EventBus.getDefault().post(new NativeAsyncEvent(type, param1, param2));
    }

    public DLNACallback getCallback() {
        return mCallback;
    }
}
