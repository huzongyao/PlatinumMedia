//
// Created by tangbull on 2018/6/6.
//

#ifndef PLATINUMMEDIA_MEDIACALLBACK_H
#define PLATINUMMEDIA_MEDIACALLBACK_H


#include <Platinum.h>

class MediaCallback : public PLT_MediaRendererDelegate {
public:
    virtual NPT_Result OnGetCurrentConnectionInfo(PLT_ActionReference &action);

    virtual NPT_Result OnNext(PLT_ActionReference &action);

    virtual NPT_Result OnPause(PLT_ActionReference &action);

    virtual NPT_Result OnPlay(PLT_ActionReference &action);

    virtual NPT_Result OnPrevious(PLT_ActionReference &action);

    virtual NPT_Result OnSeek(PLT_ActionReference &action);

    virtual NPT_Result OnStop(PLT_ActionReference &action);

    virtual NPT_Result OnSetAVTransportURI(PLT_ActionReference &action);

    virtual NPT_Result OnSetPlayMode(PLT_ActionReference &action);

    virtual NPT_Result OnSetVolume(PLT_ActionReference &action);

    virtual NPT_Result OnSetVolumeDB(PLT_ActionReference &action);

    virtual NPT_Result OnGetVolumeDBRange(PLT_ActionReference &action);

    virtual NPT_Result OnSetMute(PLT_ActionReference &action);

};


#endif //PLATINUMMEDIA_MEDIACALLBACK_H
