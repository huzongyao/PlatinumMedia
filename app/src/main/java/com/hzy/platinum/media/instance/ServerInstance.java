package com.hzy.platinum.media.instance;

import android.util.Log;

import com.hzy.platinum.media.event.ServerAsyncEvent;
import com.hzy.platinum.media.event.ServerStateEvent;
import com.plutinosoft.platinum.DLNABridge;
import com.plutinosoft.platinum.ServerParams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huzongyao on 2018/6/10.
 * the server instance
 */

public enum ServerInstance {

    INSTANCE;

    private static final String TAG = "ServerInstance";

    private volatile State mState = State.IDLE;
    private DLNABridge mDLNAServer;

    ServerInstance() {
        Log.d(TAG, "Init!");
    }

    public State getState() {
        return mState;
    }

    public void start(String friendlyName, boolean showIp, String uuid) {
        ServerParams params = new ServerParams(friendlyName, showIp, uuid);
        start(params);
    }

    public void start(ServerParams params) {
        EventBus.getDefault().register(this);
        ServerAsyncEvent event = new ServerAsyncEvent(ServerAsyncEvent.EVENT_START);
        event.setParam(params);
        EventBus.getDefault().post(event);
    }

    public void stop() {
        ServerAsyncEvent event = new ServerAsyncEvent(ServerAsyncEvent.EVENT_STOP);
        EventBus.getDefault().post(event);
    }

    public enum State {
        IDLE,
        STARTING,
        RUNNING,
        STOPPING
    }

    @SuppressWarnings("UnusedDeclaration")
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void startAsyncTask(ServerAsyncEvent event) {
        switch (event.getType()) {
            case ServerAsyncEvent.EVENT_START:
                startAsync(event);
                break;
            case ServerAsyncEvent.EVENT_STOP:
                stopAsync();
                break;
        }
    }

    private void setState(State state) {
        mState = state;
        EventBus.getDefault().post(new ServerStateEvent(state));
    }

    private void startAsync(ServerAsyncEvent event) {
        if (mState == State.IDLE) {
            Object param = event.getParam();
            if (param != null && param instanceof ServerParams) {
                ServerParams serverParam = (ServerParams) param;
                setState(State.STARTING);
                mDLNAServer = new DLNABridge();
                mDLNAServer.setCallback(CallbackInstance.INSTANCE.getCallback());
                mDLNAServer.start(serverParam);
                setState(State.RUNNING);
            }
        }
    }

    private void stopAsync() {
        if (mState == State.RUNNING) {
            setState(State.STOPPING);
            mDLNAServer.stop();
            mDLNAServer.destroy();
            setState(State.IDLE);
            EventBus.getDefault().unregister(this);
        }
    }
}
