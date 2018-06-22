//
// Created by huzongyao on 2018/6/6.
//

#include "MediaCallback.h"
#include "NdkLogger.h"
#include "DLNAServer.h"
#include "CallbackTypes.h"

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
    const char *cUri = uri.GetChars();
    const char *cMeta = metadata.GetChars();
    //LOGD("URL[%s]", cUri);
    //LOGD("MetaData[%s]", cMeta);
    DoJavaCallback(CALLBACK_EVENT_ON_SET_AV_TRANSPORT_URI, cUri, cMeta);
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnNext(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnNext");
    NPT_String NextURI;
    action->GetArgumentValue("NextURI", NextURI);
    NPT_String NextURIMetaData;
    action->GetArgumentValue("NextURIMetaData", NextURIMetaData);
    const char *cUri = NextURI.GetChars();
    const char *cMeta = NextURIMetaData.GetChars();
    //LOGD("URL[%s]", cUri);
    //LOGD("MetaData[%s]", cMeta);
    DoJavaCallback(CALLBACK_EVENT_ON_NEXT, cUri, cMeta);
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnPause(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnPause");
    DoJavaCallback(CALLBACK_EVENT_ON_PAUSE, "", "");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnPlay(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnPlay");
    NPT_String Speed;
    action->GetArgumentValue("Speed", Speed);
    const char *cSpeed = Speed.GetChars();
    //LOGD("Play Speed[%s]", cSpeed);
    DoJavaCallback(CALLBACK_EVENT_ON_PLAY, cSpeed, "");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnPrevious(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnPrevious");
    DoJavaCallback(CALLBACK_EVENT_ON_PREVIOUS, "", "");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnSeek(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnSeek");
    NPT_String Unit;
    action->GetArgumentValue("Unit", Unit);
    NPT_String Target;
    action->GetArgumentValue("Target", Target);
    const char *cUnit = Unit.GetChars();
    const char *cTarget = Target.GetChars();
    //LOGE("Seek Unit[%s], Target[%s]", cUnit, cTarget);
    DoJavaCallback(CALLBACK_EVENT_ON_SEEK, cUnit, cTarget);
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnStop(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnStop");
    DoJavaCallback(CALLBACK_EVENT_ON_STOP, "", "");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnSetPlayMode(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnSetPlayMode");
    NPT_String NewPlayMode;
    action->GetArgumentValue("NewPlayMode", NewPlayMode);
    const char *cPlayMode = NewPlayMode.GetChars();
    //LOGD("NewPlayMode[%s]", cPlayMode);
    DoJavaCallback(CALLBACK_EVENT_ON_SET_PLAY_MODE, cPlayMode, "");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnSetVolume(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnSetVolume");
    NPT_String Channel;
    action->GetArgumentValue("Channel", Channel);
    NPT_String DesiredVolume;
    action->GetArgumentValue("DesiredVolume", DesiredVolume);
    PLT_Service *serviceRC;
    mDevice->FindServiceByType("urn:schemas-upnp-org:service:RenderingControl:1", serviceRC);
    serviceRC->SetStateVariable("Volume", DesiredVolume);
    if (DesiredVolume == "0") {
        serviceRC->SetStateVariable("Mute", "1");
    } else {
        serviceRC->SetStateVariable("Mute", "0");
    }
    const char *cChannel = Channel.GetChars();
    const char *cVolume = DesiredVolume.GetChars();
    //LOGD("Channel[%s], DesiredVolume[%s]", cChannel, cVolume);
    DoJavaCallback(CALLBACK_EVENT_ON_SET_VOLUME, cChannel, cVolume);
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
    const char *cMute = DesiredMute.GetChars();
    //LOGD("DesiredMute[%s]", cMute);
    DoJavaCallback(CALLBACK_EVENT_ON_SET_MUTE, cMute, "");
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

NPT_Result MediaCallback::DoJavaCallback(int type, const char *param1, const char *param2) {
    if (g_vm == NULL) {
        LOGE("g_vm = NULL!!!");
        return NPT_FAILURE;
    }
    int status;
    JNIEnv *env = NULL;
    bool isAttach = false;
    status = g_vm->GetEnv((void **) &env, JNI_VERSION_1_6);
    if (status != JNI_OK) {
        status = g_vm->AttachCurrentThread(&env, NULL);
        if (status < 0) {
            LOGE("callback_handler: failed to attach , current thread, status = %d", status);
            return NPT_FAILURE;
        }
        isAttach = true;
    }
    jstring jParam1 = NULL;
    jstring jParam2 = NULL;
    jclass inflectClass = g_callbackClass;
    jmethodID inflectMethod = g_callbackMethod;
    if (inflectClass == NULL || inflectMethod == NULL) {
        goto end;
    }
    //LOGD("TYPE: %d\nPARAM1: %s\nPARAM1: %s", type, param1, param2);
    jParam1 = env->NewStringUTF(param1);
    jParam2 = env->NewStringUTF(param2);
    env->CallStaticVoidMethod(inflectClass, inflectMethod, type, jParam1, jParam2);
    env->DeleteLocalRef(jParam1);
    env->DeleteLocalRef(jParam2);
    end:
    if (env->ExceptionOccurred()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
    }
    if (isAttach) {
        g_vm->DetachCurrentThread();
    }
    return NPT_SUCCESS;
}
