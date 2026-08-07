//
// Created by huzongyao on 2026/8/7.
// DLNA DMR delegate: forwards AVTransport / RenderingControl actions to Java.
//

#ifndef PLATINUMMEDIA_ANDMEDIARENDERER_H
#define PLATINUMMEDIA_ANDMEDIARENDERER_H

#include "Platinum.h"
#include "PltMediaRenderer.h"

class AndUPnP;

/**
 * Bridges PLT_MediaRendererDelegate callbacks to the Java layer via AndUPnP::notifyJEvent.
 * Each action is serialized as a JSON string and delivered through onEvent(String).
 */
class AndMediaRenderer : public PLT_MediaRendererDelegate {
public:
    explicit AndMediaRenderer(AndUPnP *upnp, const NPT_String &uuid);

    ~AndMediaRenderer() override;

    // ConnectionManager
    NPT_Result OnGetCurrentConnectionInfo(PLT_ActionReference &action) override;

    // AVTransport
    NPT_Result OnNext(PLT_ActionReference &action) override;
    NPT_Result OnPause(PLT_ActionReference &action) override;
    NPT_Result OnPlay(PLT_ActionReference &action) override;
    NPT_Result OnPrevious(PLT_ActionReference &action) override;
    NPT_Result OnSeek(PLT_ActionReference &action) override;
    NPT_Result OnStop(PLT_ActionReference &action) override;
    NPT_Result OnSetAVTransportURI(PLT_ActionReference &action) override;
    NPT_Result OnSetPlayMode(PLT_ActionReference &action) override;

    // RenderingControl
    NPT_Result OnSetVolume(PLT_ActionReference &action) override;
    NPT_Result OnSetVolumeDB(PLT_ActionReference &action) override;
    NPT_Result OnGetVolumeDBRange(PLT_ActionReference &action) override;
    NPT_Result OnSetMute(PLT_ActionReference &action) override;

private:
    AndUPnP *mUpnp;
    NPT_String mUuid;

    NPT_Result forward(const char *action, PLT_ActionReference &actionRef);
};

#endif //PLATINUMMEDIA_ANDMEDIARENDERER_H
