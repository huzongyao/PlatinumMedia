//
// Created by huzongyao on 2018/6/6.
//

#include "MediaCallback.h"
#include "NdkLogger.h"

MediaCallback::MediaCallback() {
    LOGD("MediaCallback::MediaCallback");
}

MediaCallback::~MediaCallback() {
    LOGD("MediaCallback::~MediaCallback");
}

NPT_Result MediaCallback::OnGetCurrentConnectionInfo(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnGetCurrentConnectionInfo");
    if (NPT_FAILED(action->VerifyArgumentValue("ConnectionID", "0"))) {
        action->SetError(706, "No Such Connection.");
        return NPT_FAILURE;
    }
    if (NPT_FAILED(action->SetArgumentValue("RcsID", "0"))) {
        return NPT_FAILURE;
    }
    if (NPT_FAILED(action->SetArgumentValue("AVTransportID", "0"))) {
        return NPT_FAILURE;
    }
    if (NPT_FAILED(action->SetArgumentOutFromStateVariable("ProtocolInfo"))) {
        return NPT_FAILURE;
    }
    if (NPT_FAILED(action->SetArgumentValue("PeerConnectionManager", "/"))) {
        return NPT_FAILURE;
    }
    if (NPT_FAILED(action->SetArgumentValue("PeerConnectionID", "-1"))) {
        return NPT_FAILURE;
    }
    if (NPT_FAILED(action->SetArgumentValue("Direction", "Input"))) {
        return NPT_FAILURE;
    }
    if (NPT_FAILED(action->SetArgumentValue("Status", "Unknown"))) {
        return NPT_FAILURE;
    }
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnSetAVTransportURI(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnSetAVTransportURI");
    NPT_String uri;
    action->GetArgumentValue("CurrentURI", uri);
    NPT_String metadata;
    action->GetArgumentValue("CurrentURIMetaData", metadata);
    PLT_Service *serviceAVT;
    mDevice->FindServiceByType("urn:schemas-upnp-org:service:AVTransport:1", serviceAVT);
    serviceAVT->SetStateVariable("AVTransportURI", uri);
    serviceAVT->SetStateVariable("AVTransportURIMetaData", metadata);
    serviceAVT->SetStateVariable("CurrentTrackURI", uri);
    serviceAVT->SetStateVariable("CurrentTrackMetadata", metadata);
    serviceAVT->SetStateVariable("TransportState", "TRANSITIONING");
    LOGD("URL[%s]", uri.GetChars());
    LOGD("MetaData[%s]", metadata.GetChars());
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnNext(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnNext");
    NPT_String NextURI;
    action->GetArgumentValue("NextURI", NextURI);
    NPT_String NextURIMetaData;
    action->GetArgumentValue("NextURIMetaData", NextURIMetaData);
    LOGD("URL[%s]", NextURI.GetChars());
    LOGD("MetaData[%s]", NextURIMetaData.GetChars());
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnPause(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnPause");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnPlay(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnPlay");
    NPT_String Speed;
    action->GetArgumentValue("Speed", Speed);
    LOGD("Play Speed[%s]", Speed.GetChars());
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnPrevious(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnPrevious");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnSeek(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnSeek");
    NPT_String Unit;
    action->GetArgumentValue("Unit", Unit);
    NPT_String Target;
    action->GetArgumentValue("Target", Target);
    LOGE("Seek Unit[%s], Target[%s]", Unit.GetChars(), Target.GetChars());
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnStop(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnStop");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnSetPlayMode(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnSetPlayMode");
    NPT_String NewPlayMode;
    action->GetArgumentValue("NewPlayMode", NewPlayMode);
    LOGD("NewPlayMode[%s]", NewPlayMode.GetChars());
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnSetVolume(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnSetVolume");
    NPT_String Channel;
    action->GetArgumentValue("Channel", Channel);
    NPT_String DesiredMute;
    action->GetArgumentValue("DesiredMute", DesiredMute);
    PLT_Service *serviceRC;
    mDevice->FindServiceByType("urn:schemas-upnp-org:service:RenderingControl:1", serviceRC);
    if (DesiredMute == "0") {
        serviceRC->SetStateVariable("Mute", "0");
    } else {
        serviceRC->SetStateVariable("Mute", "1");
    }
    LOGD("DesiredMute[%s]", DesiredMute.GetChars());
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnSetMute(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnSetMute");
    NPT_String Channel;
    action->GetArgumentValue("Channel", Channel);
    NPT_String DesiredMute;
    action->GetArgumentValue("DesiredMute", DesiredMute);
    PLT_Service *serviceRC;
    mDevice->FindServiceByType("urn:schemas-upnp-org:service:RenderingControl:1", serviceRC);
    if (DesiredMute == "0") {
        serviceRC->SetStateVariable("Mute", "0");
    } else {
        serviceRC->SetStateVariable("Mute", "1");
    }
    LOGD("DesiredMute[%s]", DesiredMute.GetChars());
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnSetVolumeDB(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnSetVolumeDB");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnGetVolumeDBRange(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnGetVolumeDBRange");
    return NPT_SUCCESS;
}

void MediaCallback::setDevice(PLT_DeviceHostReference device) {
    mDevice = device;
}
