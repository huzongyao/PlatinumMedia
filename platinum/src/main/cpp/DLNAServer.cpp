//
// Created by tangbull on 2018/6/27.
//

#include "DLNAServer.h"
#include "NdkLogger.h"
#include "MediaRenderer.h"


DLNAServer::DLNAServer() {
    LOGD("DLNAServer::DLNAServer()");
}

DLNAServer::~DLNAServer() {
    LOGD("DLNAServer::~DLNAServer()");
}

NPT_Result
DLNAServer::Start(const char *friendly_name, bool show_ip, const char *uuid,
                  unsigned int port, bool port_rebind) {
    MediaRenderer *renderer = new MediaRenderer(friendly_name, show_ip, uuid, port, port_rebind);
    mDevice = renderer;
    mUPnP.AddDevice(mDevice);
    return mUPnP.Start();
}

NPT_Result DLNAServer::Stop() {
    if (mDevice.IsNull() || !mUPnP.IsRunning()) {
        return NPT_FAILURE;
    }
    mUPnP.Stop();
    return 0;
}
