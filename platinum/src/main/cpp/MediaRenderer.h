//
// Created by huzongyao on 2018/6/6.
//

#ifndef PLATINUMMEDIA_MEDIARENDER_H
#define PLATINUMMEDIA_MEDIARENDER_H


#include <PltUPnP.h>
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

private:
    PLT_UPnP mUPnP;
    PLT_DeviceHostReference mDevice;
    MediaCallback *mMediaCallback;
    bool mIsAdded;
};


#endif //PLATINUMMEDIA_MEDIARENDER_H
