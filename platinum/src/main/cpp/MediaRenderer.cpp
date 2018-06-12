//
// Created by huzongyao on 2018/6/6.
//

#include "MediaRenderer.h"
#include "NdkLogger.h"

MediaRenderer::MediaRenderer() : mIsAdded(false) {
    LOGD("MediaRenderer::MediaRenderer");
    mMediaCallback = new MediaCallback();
}

NPT_Result
MediaRenderer::Start(const char *friendly_name, bool show_ip, const char *uuid,
                     unsigned int port, bool port_rebind) {
    LOGD("MediaRenderer::Start");
    LOGD("Name[%s], Show IP[%d], UUID[%s], Port[%u], Port Rebind[%d]",
         friendly_name, show_ip, uuid, port, port_rebind);
    PLT_MediaRenderer *render =
            new PLT_MediaRenderer(friendly_name, show_ip, uuid, port, port_rebind);
    render->SetDelegate(mMediaCallback);
    mDevice = render;
    mMediaCallback->setDevice(mDevice);
    mUPnP.AddDevice(mDevice);
    NPT_Result ret = mUPnP.Start();
    mIsAdded = true;
    return ret;
}

NPT_Result
MediaRenderer::Stop() {
    LOGD("MediaRenderer::Stop");
    if (mIsAdded) {
        mUPnP.RemoveDevice(mDevice);
    }
    return mUPnP.Stop();
}

MediaRenderer::~MediaRenderer() {
    LOGD("MediaRenderer::~MediaRenderer");
    delete mMediaCallback;
}
