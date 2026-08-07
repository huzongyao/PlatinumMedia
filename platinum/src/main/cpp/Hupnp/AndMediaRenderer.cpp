//
// Created by huzongyao on 2026/8/7.
//

#include "AndMediaRenderer.h"
#include "AndUPnP.h"

using namespace nlohmann;

static NPT_String
readArg(PLT_ActionReference &action, const char *name) {
    NPT_String value;
    if (action.IsNull()) return value;
    NPT_String tmp;
    if (NPT_SUCCEEDED(action->GetArgumentValue(name, tmp))) {
        value = tmp;
    }
    return value;
}

AndMediaRenderer::AndMediaRenderer(AndUPnP *upnp, const NPT_String &uuid)
        : mUpnp(upnp), mUuid(uuid) {
}

AndMediaRenderer::~AndMediaRenderer() = default;

NPT_Result
AndMediaRenderer::forward(const char *action, PLT_ActionReference &actionRef) {
    if (!mUpnp) return NPT_ERROR_INVALID_STATE;

    NPT_String uri = readArg(actionRef, "CurrentURI");
    NPT_String uriMetaData = readArg(actionRef, "CurrentURIMetaData");
    NPT_String speed = readArg(actionRef, "Speed");
    NPT_String target = readArg(actionRef, "Target");
    NPT_String unit = readArg(actionRef, "Unit");
    NPT_String channel = readArg(actionRef, "Channel");
    NPT_String instanceId = readArg(actionRef, "InstanceID");

    // Build a compact JSON payload understood by NativeUpnpApi.
    // {"uuid":"...","action":"Play","InstanceID":"0","CurrentURI":"...",...}
    json j;
    j["uuid"] = (const char *) mUuid;
    j["action"] = action;
    j["InstanceID"] = (const char *) instanceId;
    if (!uri.IsEmpty()) j["CurrentURI"] = (const char *) uri;
    if (!uriMetaData.IsEmpty()) j["CurrentURIMetaData"] = (const char *) uriMetaData;
    if (!speed.IsEmpty()) j["Speed"] = (const char *) speed;
    if (!target.IsEmpty()) j["Target"] = (const char *) target;
    if (!unit.IsEmpty()) j["Unit"] = (const char *) unit;
    if (!channel.IsEmpty()) j["Channel"] = (const char *) channel;

    mUpnp->notifyJEvent(j.dump());
    return NPT_SUCCESS;
}

NPT_Result
AndMediaRenderer::OnGetCurrentConnectionInfo(PLT_ActionReference &action) {
    return NPT_SUCCESS;
}

NPT_Result
AndMediaRenderer::OnNext(PLT_ActionReference &action) {
    return forward("Next", action);
}

NPT_Result
AndMediaRenderer::OnPause(PLT_ActionReference &action) {
    return forward("Pause", action);
}

NPT_Result
AndMediaRenderer::OnPlay(PLT_ActionReference &action) {
    return forward("Play", action);
}

NPT_Result
AndMediaRenderer::OnPrevious(PLT_ActionReference &action) {
    return forward("Previous", action);
}

NPT_Result
AndMediaRenderer::OnSeek(PLT_ActionReference &action) {
    return forward("Seek", action);
}

NPT_Result
AndMediaRenderer::OnStop(PLT_ActionReference &action) {
    return forward("Stop", action);
}

NPT_Result
AndMediaRenderer::OnSetAVTransportURI(PLT_ActionReference &action) {
    return forward("SetAVTransportURI", action);
}

NPT_Result
AndMediaRenderer::OnSetPlayMode(PLT_ActionReference &action) {
    return forward("SetPlayMode", action);
}

NPT_Result
AndMediaRenderer::OnSetVolume(PLT_ActionReference &action) {
    NPT_String desired;
    if (NPT_SUCCEEDED(action->GetArgumentValue("DesiredVolume", desired))) {
        // Reuse the "Target" JSON field to carry the numeric volume value.
        if (!mUpnp) return NPT_ERROR_INVALID_STATE;
        json j;
        j["uuid"] = (const char *) mUuid;
        j["action"] = "SetVolume";
        j["Target"] = (const char *) desired;
        j["Channel"] = (const char *) readArg(action, "Channel");
        mUpnp->notifyJEvent(j.dump());
        return NPT_SUCCESS;
    }
    return forward("SetVolume", action);
}

NPT_Result
AndMediaRenderer::OnSetVolumeDB(PLT_ActionReference &action) {
    return NPT_SUCCESS;
}

NPT_Result
AndMediaRenderer::OnGetVolumeDBRange(PLT_ActionReference &action) {
    return NPT_SUCCESS;
}

NPT_Result
AndMediaRenderer::OnSetMute(PLT_ActionReference &action) {
    NPT_String desired;
    if (NPT_SUCCEEDED(action->GetArgumentValue("DesiredMute", desired)) && mUpnp) {
        json j;
        j["uuid"] = (const char *) mUuid;
        j["action"] = "SetMute";
        j["Target"] = (const char *) desired;
        j["Channel"] = (const char *) readArg(action, "Channel");
        mUpnp->notifyJEvent(j.dump());
        return NPT_SUCCESS;
    }
    return forward("SetMute", action);
}
