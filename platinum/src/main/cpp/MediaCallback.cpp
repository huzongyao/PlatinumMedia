//
// Created by huzongyao on 2018/6/6.
//

#include "MediaCallback.h"
#include "NdkLogger.h"

NPT_Result MediaCallback::OnGetCurrentConnectionInfo(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnGetCurrentConnectionInfo");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnNext(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnNext");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnPause(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnPause");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnPlay(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnPlay");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnPrevious(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnPrevious");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnSeek(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnSeek");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnStop(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnStop");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnSetAVTransportURI(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnSetAVTransportURI");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnSetPlayMode(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnSetPlayMode");
    return NPT_SUCCESS;
}

NPT_Result MediaCallback::OnSetVolume(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnSetVolume");
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

NPT_Result MediaCallback::OnSetMute(PLT_ActionReference &action) {
    LOGD("MediaCallback::OnSetMute");
    return NPT_SUCCESS;
}
