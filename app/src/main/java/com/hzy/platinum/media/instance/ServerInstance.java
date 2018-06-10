package com.hzy.platinum.media.instance;

import com.plutinosoft.platinum.DLNAServer;

/**
 * Created by huzongyao on 2018/6/10.
 */

public enum ServerInstance {

    INSTANCE;

    private volatile State mState = State.IDLE;
    private DLNAServer mDLNAServer;

    ServerInstance() {
        mDLNAServer = new DLNAServer();
    }

    public State getState() {
        return mState;
    }

    public void start(String friendlyName, boolean showIp, String uuid) {
        mState = State.INITIALIZING;
        mDLNAServer.start(friendlyName, showIp, uuid);
    }

    public void stop() {
        mDLNAServer.stop();
    }

    public enum State {
        IDLE,
        INITIALIZING,
        INITIALIZED,
        STARTING,
        RUNNING,
        STOPING
    }
}
