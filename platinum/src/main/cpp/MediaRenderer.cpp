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


NPT_Result MediaRenderer::SetMediaDuration(const char *duration) {
    LOGD("MediaRenderer::SetMediaDuration[%s]", duration);
    PLT_Service *serviceAVT = NULL;
    if (IsServiceAllOk()) {
        NPT_String type;
        type = "urn:schemas-upnp-org:service:AVTransport:*";
        mDevice->FindServiceByType(type, serviceAVT);
        NPT_Result ret;
        ret = serviceAVT->SetStateVariable("CurrentMediaDuration", duration);
        if (ret == NPT_SUCCESS)
            ret = serviceAVT->SetStateVariable("CurrentTrackDuration", duration);
        return ret;
    }
    return NPT_FAILURE;
}

NPT_Result MediaRenderer::SetTimePosition(const char *position) {
    LOGD("MediaRenderer::SetTimePosition[%s]", position);
    PLT_Service *serviceAVT = NULL;
    if (IsServiceAllOk()) {
        NPT_String type;
        type = "urn:schemas-upnp-org:service:AVTransport:*";
        mDevice->FindServiceByType(type, serviceAVT);
        NPT_Result ret;
        ret = serviceAVT->SetStateVariable("RelativeTimePosition", position);
        return ret;
    }
    return NPT_FAILURE;
}

NPT_Result MediaRenderer::SetTransportState(const char *state) {
    LOGD("MediaRenderer::SetTransportState[%s]", state);
    PLT_Service *serviceAVT = NULL;
    if (IsServiceAllOk()) {
        NPT_String type;
        type = "urn:schemas-upnp-org:service:AVTransport:*";
        mDevice->FindServiceByType(type, serviceAVT);
        NPT_Result ret;
        ret = serviceAVT->SetStateVariable("TransportState", state);
        return ret;
    }
    return NPT_FAILURE;
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

bool MediaRenderer::IsServiceAllOk() {
    PLT_Service *serviceAVT = NULL;
    PLT_Service *serviceCMR;
    PLT_Service *serviceRC;
    NPT_String type;
    if (!mDevice->GetType().StartsWith("urn:schemas-upnp-org:device:MediaRenderer")) {
        return false;
    }
    type = "urn:schemas-upnp-org:service:AVTransport:*";
    if (NPT_FAILED(mDevice->FindServiceByType(type, serviceAVT))) {
        LOGW("Service %s not found!", (const char *) type);
        return false;
    }
    type = "urn:schemas-upnp-org:service:ConnectionManager:*";
    if (NPT_FAILED(mDevice->FindServiceByType(type, serviceCMR))) {
        LOGW("Service %s not found!", (const char *) type);
        return false;
    }
    type = "urn:schemas-upnp-org:service:RenderingControl:*";
    if (NPT_FAILED(mDevice->FindServiceByType(type, serviceRC))) {
        LOGW("Service %s not found!", (const char *) type);
        return false;
    }
    return true;
}

