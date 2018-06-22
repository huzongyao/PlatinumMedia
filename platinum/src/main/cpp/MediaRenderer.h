//
// Created by huzongyao on 2018/6/6.
//

#ifndef PLATINUMMEDIA_MEDIARENDER_H
#define PLATINUMMEDIA_MEDIARENDER_H


#include <PltUPnP.h>
#include <jni.h>
#include "MediaCallback.h"

class MediaRenderer {
public:
    MediaRenderer();

    ~MediaRenderer();

    NPT_Result Start(const char *friendly_name,
                     bool show_ip     /* = false */,
                     const char *uuid        /* = NULL */,
                     unsigned int port        /* = 0 */,
                     bool port_rebind /* = false */);

    NPT_Result Stop();

    NPT_Result SetMediaDuration(const char *duration);

    NPT_Result SetTimePosition(const char *position);

    NPT_Result SetTransportState(const char *state);

private:
    PLT_UPnP mUPnP;
    PLT_DeviceHostReference mDevice;
    MediaCallback *mMediaCallback;
    bool mIsAdded;

    bool IsServiceAllOk();
};


#endif //PLATINUMMEDIA_MEDIARENDER_H
